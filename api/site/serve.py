#!/usr/bin/env python3
"""Local documentation server and guarded request proxy for the PW API Atlas.

The site in this directory is an independent, read-only explorer for the API
inventory recovered from the PW Android APK. It is deliberately not a clone of
the app and does not bundle credentials.

What this server does
---------------------
* Serves the static explorer and the catalog / OpenAPI / CSV artifacts.
* Exposes ``POST /api/execute``: a same-origin, allowlisted proxy that builds a
  request from a catalog operation. The host must be one of the catalog's base
  URLs. A token supplied by the user is forwarded once and never logged.
* Exposes the manual, rate-limited sign-in helpers ``POST /api/login/otp`` and
  ``POST /api/login/token``. They call only the two OTP operations and the
  token operation, apply the app's OAuth client identifiers server-side, and
  return a redacted result. They do not create sessions or store tokens.
* Provides no authentication, no stored sessions, and no automatic requests.

Run
---
    python3 serve.py                 # http://127.0.0.1:8765
    python3 serve.py --port 9000
    python3 serve.py --allow-base http://127.0.0.1:9001   # test/mock only

``--allow-base`` is intentionally opt-in and is meant for a local mock server
when testing the site. Do not add real third-party hosts.
"""
from __future__ import annotations

import argparse
import json
import math
import os
import re
import secrets
import ssl
import sys
import threading
import time
import urllib.error
import urllib.parse
import urllib.request
from collections import deque
from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer

HERE = os.path.dirname(os.path.abspath(__file__))
API_DIR = os.path.dirname(HERE)
CATALOG_PATH = os.path.join(API_DIR, "pw_api_catalog.json")
DEFAULT_PORT = 8765

MAX_REQUEST_BYTES = 1_000_000
MAX_RESPONSE_BYTES = 1_000_000
DEFAULT_TIMEOUT = 30
MAX_TIMEOUT = 60
MAX_TOKEN_CHARS = 8192

ARTIFACTS = {
    "/api/catalog": ("pw_api_catalog.json", "application/json; charset=utf-8"),
    "/api/openapi.yaml": ("openapi.yaml", "application/yaml; charset=utf-8"),
    "/api/endpoints.csv": ("endpoints.csv", "text/csv; charset=utf-8"),
    "/api/README.md": ("README.md", "text/markdown; charset=utf-8"),
}

HEADER_NAME = re.compile(r"^[!#$%&'*+\-.^_`|~0-9A-Za-z]+$")
PATH_PLACEHOLDER = re.compile(r"\{([^{}]+)\}")

# The mobile app's embedded OAuth client identifiers and application headers.
# These are app constants recovered from LoginBody, not user credentials. They
# are kept on the server and are never included in a page or a log.
LOGIN_ORGANIZATION_ID = "5eb393ee95fab7468a79d189"
LOGIN_CLIENT_ID = "system-admin"
LOGIN_CLIENT_SECRET = "KjPXuAVfC5xbmgreETNMaL7z"
LOGIN_APP_HEADERS = {
    "client-id": LOGIN_ORGANIZATION_ID,
    "client-version": "15.60.03",
    "user-agent": "Android",
    "client-type": "MOBILE",
    "referer": "https://android.pw.live",
}
LOGIN_OTP_SEND_OP = "post_v3_users_get_otp_app"
LOGIN_OTP_RESEND_OP = "post_v3_users_resend_otp_app"
LOGIN_TOKEN_OP = "post_v3_oauth_token"
LOGIN_OPERATIONS = frozenset({
    LOGIN_OTP_SEND_OP,
    LOGIN_OTP_RESEND_OP,
    LOGIN_TOKEN_OP,
})
LOGIN_PHONE_RE = re.compile(r"^[0-9]{6,15}$")
LOGIN_COUNTRY_RE = re.compile(r"^\+?[0-9]{1,4}$")
LOGIN_OTP_RE = re.compile(r"^[0-9]{4,8}$")
LOGIN_MESSAGE_LIMIT = 300

# Conservative defaults for the manual OTP flow. They are enforced by the
# server, not just the page, so a direct request cannot bypass the cooldown.
LOGIN_OTP_COOLDOWN_SECONDS = 30
LOGIN_OTP_WINDOW_SECONDS = 600
LOGIN_OTP_MAX_SENDS = 5
LOGIN_TOKEN_COOLDOWN_SECONDS = 1
LOGIN_TOKEN_WINDOW_SECONDS = 600
LOGIN_TOKEN_MAX_ATTEMPTS = 10

# Headers that must not be forwarded through the proxy. Request framing and
# upstream connection controls are the server's job. Compression is excluded so
# responses stay readable without a decompression dependency.
BLOCKED_REQUEST_HEADERS = {
    "accept-encoding",
    "connection",
    "content-length",
    "host",
    "keep-alive",
    "proxy-authenticate",
    "proxy-authorization",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
}
BLOCKED_RESPONSE_HEADERS = {
    "connection",
    "keep-alive",
    "proxy-authenticate",
    "proxy-authorization",
    "set-cookie",
    "set-cookie2",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
}


class ProxyError(Exception):
    """A user-facing proxy error with an HTTP status code."""

    def __init__(self, message: str, status: int = HTTPStatus.BAD_REQUEST,
                 retry_after: int | None = None):
        super().__init__(message)
        self.status = status
        self.retry_after = retry_after


class LoginRateLimiter:
    """Small in-memory limiter for the manual OTP sign-in helpers.

    A restart clears the counters. The goal is to stop accidental loops and
    casual abuse of a loopback research tool, not to replace upstream limits.
    """

    def __init__(self, otp_cooldown=LOGIN_OTP_COOLDOWN_SECONDS,
                 otp_window=LOGIN_OTP_WINDOW_SECONDS,
                 otp_max=LOGIN_OTP_MAX_SENDS,
                 token_cooldown=LOGIN_TOKEN_COOLDOWN_SECONDS,
                 token_window=LOGIN_TOKEN_WINDOW_SECONDS,
                 token_max=LOGIN_TOKEN_MAX_ATTEMPTS,
                 clock=time.monotonic):
        self._limits = {
            "otp": (float(otp_cooldown), float(otp_window), int(otp_max)),
            "token": (float(token_cooldown), float(token_window), int(token_max)),
        }
        self._events = {"otp": deque(), "token": deque()}
        self._clock = clock
        self._lock = threading.Lock()

    def check(self, kind: str) -> None:
        cooldown, window, maximum = self._limits[kind]
        with self._lock:
            now = self._clock()
            events = self._events[kind]
            cutoff = now - window
            while events and events[0] <= cutoff:
                events.popleft()
            wait = 0
            if events:
                wait = max(wait, events[-1] + cooldown - now)
            if len(events) >= maximum:
                wait = max(wait, events[0] + window - now)
            if wait > 0:
                seconds = max(1, math.ceil(wait))
                raise ProxyError(
                    "Too many sign-in attempts. Try again in %d second%s."
                    % (seconds, "" if seconds == 1 else "s"),
                    HTTPStatus.TOO_MANY_REQUESTS,
                    retry_after=seconds,
                )
            events.append(now)


class NoRedirect(urllib.request.HTTPRedirectHandler):
    """Do not follow upstream redirects; they could leave the allowlist."""

    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def _origin(value: str) -> str:
    """Return a normalized scheme://host:port origin or raise ValueError."""
    parsed = urllib.parse.urlsplit(value)
    if parsed.scheme not in ("http", "https"):
        raise ValueError("only http and https base URLs are supported")
    if not parsed.hostname:
        raise ValueError("base URL has no host")
    if parsed.username or parsed.password:
        raise ValueError("base URLs must not contain credentials")
    if parsed.path not in ("", "/") or parsed.query or parsed.fragment:
        raise ValueError("base URLs must be origins without a path, query or fragment")
    return "%s://%s" % (parsed.scheme, parsed.netloc.lower())


def load_catalog(path: str) -> dict:
    with open(path, encoding="utf-8") as fh:
        return json.load(fh)


def _short_type(jvm_type: str) -> str:
    """Make a JVM descriptor a little easier to read in error messages."""
    if not jvm_type:
        return "unknown"
    return jvm_type


class AtlasHandler(SimpleHTTPRequestHandler):
    server_version = "PWApiAtlas/1.0"

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=HERE, **kwargs)

    # -- shared responses ------------------------------------------------
    def end_headers(self):
        self.send_header("X-Content-Type-Options", "nosniff")
        self.send_header("Referrer-Policy", "no-referrer")
        self.send_header("X-Frame-Options", "DENY")
        self.send_header("Cross-Origin-Opener-Policy", "same-origin")
        self.send_header(
            "Content-Security-Policy",
            "default-src 'self'; script-src 'self'; style-src 'self'; "
            "img-src 'self' data:; connect-src 'self'; font-src 'self'; "
            "object-src 'none'; base-uri 'none'; form-action 'none'; "
            "frame-ancestors 'none'",
        )
        super().end_headers()

    def log_message(self, fmt, *args):
        """Log method/status only. Never log headers or request bodies."""
        message = fmt % args
        message = re.sub(r"\?[^\" ]*", "", message)
        sys.stderr.write("%s - %s\n" % (self.address_string(), message))

    def _send_bytes(self, data: bytes, content_type: str, status=HTTPStatus.OK,
                    cache="no-cache"):
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(data)))
        self.send_header("Cache-Control", cache)
        self.end_headers()
        if self.command != "HEAD":
            self.wfile.write(data)

    def _send_json(self, payload, status=HTTPStatus.OK):
        data = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self._send_bytes(data, "application/json; charset=utf-8", status,
                         cache="no-store")

    def _send_error_json(self, message: str, status: int, detail=None,
                         extra=None):
        payload = {"error": message}
        if detail:
            payload["detail"] = detail
        if extra:
            payload.update(extra)
        self._send_json(payload, status)

    def list_directory(self, path):
        """Directory listings are not part of the site."""
        self._send_error_json("not found", HTTPStatus.NOT_FOUND)
        return None

    # -- GET / HEAD ------------------------------------------------------
    def do_GET(self):
        path = urllib.parse.urlsplit(self.path).path
        if path == "/api/health":
            self._send_json({
                "ok": True,
                "catalog_operations": len(self.server.catalog["operations"]),
                "allowed_base_urls": sorted(self.server.allowed_origins),
            })
            return
        if path in ARTIFACTS:
            filename, content_type = ARTIFACTS[path]
            try:
                with open(os.path.join(API_DIR, filename), "rb") as fh:
                    data = fh.read()
            except OSError:
                self._send_error_json("artifact not available", HTTPStatus.NOT_FOUND)
                return
            self._send_bytes(data, content_type)
            return
        super().do_GET()

    def do_HEAD(self):
        path = urllib.parse.urlsplit(self.path).path
        if path in ARTIFACTS:
            filename, content_type = ARTIFACTS[path]
            try:
                size = os.path.getsize(os.path.join(API_DIR, filename))
            except OSError:
                self.send_error(HTTPStatus.NOT_FOUND)
                return
            self.send_response(HTTPStatus.OK)
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(size))
            self.send_header("Cache-Control", "no-cache")
            self.end_headers()
            return
        if path == "/api/health":
            self._send_json({"ok": True})
            return
        super().do_HEAD()

    # -- request proxy ---------------------------------------------------
    def _check_same_origin(self) -> None:
        origin = self.headers.get("Origin")
        host = self.headers.get("Host", "")
        if origin:
            parsed = urllib.parse.urlsplit(origin)
            if parsed.netloc.lower() != host.lower():
                raise ProxyError("cross-origin requests are not allowed",
                                 HTTPStatus.FORBIDDEN)
        fetch_site = self.headers.get("Sec-Fetch-Site")
        if fetch_site and fetch_site not in ("same-origin", "none"):
            raise ProxyError("cross-site requests are not allowed",
                             HTTPStatus.FORBIDDEN)

    def _read_payload(self) -> dict:
        length_header = self.headers.get("Content-Length")
        if not length_header:
            raise ProxyError("request body is required")
        try:
            length = int(length_header)
        except ValueError:
            raise ProxyError("invalid Content-Length")
        if length <= 0:
            raise ProxyError("request body is required")
        if length > MAX_REQUEST_BYTES:
            raise ProxyError("request body is too large", HTTPStatus.REQUEST_ENTITY_TOO_LARGE)
        content_type = self.headers.get("Content-Type", "").split(";", 1)[0].strip().lower()
        if content_type != "application/json":
            raise ProxyError("Content-Type must be application/json",
                             HTTPStatus.UNSUPPORTED_MEDIA_TYPE)
        try:
            payload = json.loads(self.rfile.read(length).decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError):
            raise ProxyError("request body is not valid JSON")
        if not isinstance(payload, dict):
            raise ProxyError("request body must be a JSON object")
        return payload

    def _pick_origin(self, requested) -> str:
        origins = self.server.allowed_origins
        if requested is None or requested == "":
            return self.server.primary_origin
        if not isinstance(requested, str):
            raise ProxyError("base_url must be a string")
        try:
            origin = _origin(requested)
        except ValueError as exc:
            raise ProxyError("invalid base_url: %s" % exc)
        if origin not in origins:
            raise ProxyError(
                "base_url is not in this server's allowlist; use one of the "
                "catalog hosts" + (" or the --allow-base test origin" if
                                   self.server.extra_origins else ""),
                HTTPStatus.FORBIDDEN,
            )
        return origin

    def _build_path(self, op: dict, values) -> str:
        if values is None:
            values = {}
        if not isinstance(values, dict):
            raise ProxyError("path parameters must be an object")
        template = op.get("path") or ""
        if not template.startswith("/") or "://" in template:
            raise ProxyError("operation has no fixed path")
        required = PATH_PLACEHOLDER.findall(template)
        missing = [name for name in required if name not in values]
        if missing:
            raise ProxyError("missing path parameter(s): %s" % ", ".join(missing))
        path = template
        for name in required:
            value = values[name]
            if not isinstance(value, (str, int, float)) or isinstance(value, bool):
                raise ProxyError("path parameter %r must be a string or number" % name)
            text = str(value)
            if len(text) > 4096:
                raise ProxyError("path parameter %r is too long" % name)
            path = path.replace("{%s}" % name,
                                urllib.parse.quote(text, safe=""))
        if PATH_PLACEHOLDER.search(path):
            raise ProxyError("unfilled path parameter(s) remain")
        return path

    def _build_query(self, values):
        if values is None:
            return []
        if not isinstance(values, dict):
            raise ProxyError("query must be an object")
        if len(values) > 200:
            raise ProxyError("too many query parameters")
        pairs = []
        for key, value in values.items():
            if not isinstance(key, str) or not key:
                raise ProxyError("query parameter names must be non-empty strings")
            if value is None:
                continue
            if isinstance(value, (list, tuple)):
                if len(value) > 200:
                    raise ProxyError("too many values for query parameter %r" % key)
                for item in value:
                    self._append_query(pairs, key, item)
            else:
                self._append_query(pairs, key, value)
        return pairs

    @staticmethod
    def _append_query(pairs, key, value):
        if value is None:
            return
        if isinstance(value, bool):
            pairs.append((key, "true" if value else "false"))
        elif isinstance(value, (str, int, float)):
            pairs.append((key, str(value)))
        else:
            raise ProxyError("query parameter %r has an unsupported value" % key)

    def _build_headers(self, payload) -> dict:
        headers = {
            "accept": "application/json",
            "user-agent": "PW-API-Atlas/1.0 (local research proxy)",
        }
        token = payload.get("token")
        if token is not None:
            if not isinstance(token, str):
                raise ProxyError("token must be a string")
            token = token.strip()
            if token.lower().startswith("bearer "):
                token = token[7:].strip()
            if len(token) > MAX_TOKEN_CHARS:
                raise ProxyError("token is too long")
            if "\r" in token or "\n" in token:
                raise ProxyError("token contains invalid characters")
            if token:
                headers["authorization"] = "Bearer " + token

        extra = payload.get("headers")
        if extra is None:
            extra = {}
        if not isinstance(extra, dict):
            raise ProxyError("headers must be an object")
        if len(extra) > 100:
            raise ProxyError("too many headers")
        for name, value in extra.items():
            if not isinstance(name, str) or not HEADER_NAME.match(name):
                raise ProxyError("invalid header name")
            lowered = name.lower()
            if lowered in BLOCKED_REQUEST_HEADERS:
                raise ProxyError("header %r is managed by the proxy" % name)
            if not isinstance(value, (str, int, float, bool)):
                raise ProxyError("header %r must have a scalar value" % name)
            text = str(value)
            if "\r" in text or "\n" in text:
                raise ProxyError("header %r contains invalid characters" % name)
            if len(text) > 16384:
                raise ProxyError("header %r is too long" % name)
            # A user-supplied Authorization header overrides the token field.
            headers[lowered] = text
        return headers

    def _build_body(self, payload, method: str):
        if "body" not in payload or payload["body"] is None:
            return None, None
        body = payload["body"]
        if not isinstance(body, dict):
            raise ProxyError("body must be an object")
        if method in ("GET", "HEAD"):
            raise ProxyError("GET and HEAD operations cannot send a body")
        mode = body.get("mode")
        if mode == "json":
            value = body.get("value")
            try:
                data = json.dumps(value, ensure_ascii=False,
                                  separators=(",", ":")).encode("utf-8")
            except (TypeError, ValueError):
                raise ProxyError("JSON body is not serializable")
            return data, "application/json"
        if mode == "form":
            value = body.get("value", {})
            if not isinstance(value, dict):
                raise ProxyError("form body must be an object")
            pairs = []
            for key, item in value.items():
                if not isinstance(key, str) or not key:
                    raise ProxyError("form field names must be non-empty strings")
                if isinstance(item, (list, tuple)):
                    for entry in item:
                        pairs.append((key, str(entry)))
                else:
                    pairs.append((key, str(item)))
            try:
                data = urllib.parse.urlencode(pairs, doseq=True).encode("utf-8")
            except (TypeError, ValueError):
                raise ProxyError("form body could not be encoded")
            return data, "application/x-www-form-urlencoded"
        if mode == "raw":
            value = body.get("value", "")
            if not isinstance(value, str):
                raise ProxyError("raw body must be a string")
            content_type = body.get("content_type") or "text/plain; charset=utf-8"
            if not isinstance(content_type, str) or "\r" in content_type \
                    or "\n" in content_type or len(content_type) > 200:
                raise ProxyError("invalid raw content type")
            try:
                data = value.encode("utf-8")
            except UnicodeEncodeError:
                raise ProxyError("raw body could not be encoded")
            return data, content_type
        raise ProxyError("body.mode must be json, form or raw")

    def _execute(self, payload: dict, allow_preauth: bool = False) -> dict:
        op_id = payload.get("operation")
        if not isinstance(op_id, str) or not op_id:
            raise ProxyError("operation is required")
        op = self.server.operations.get(op_id)
        if op is None:
            raise ProxyError("unknown operation", HTTPStatus.NOT_FOUND)

        origin = self._pick_origin(payload.get("base_url"))
        path = self._build_path(op, payload.get("path"))
        query_pairs = self._build_query(payload.get("query"))
        query = urllib.parse.urlencode(query_pairs)
        url = origin + path + (("?" + query) if query else "")

        headers = self._build_headers(payload)
        if not op.get("public") and not headers.get("authorization", "").strip():
            if not (allow_preauth and op_id in LOGIN_OPERATIONS):
                raise ProxyError(
                    "a bearer token is required for this operation; it is not "
                    "marked public",
                    HTTPStatus.UNAUTHORIZED,
                )
        data, content_type = self._build_body(payload, op["http"])
        if content_type:
            headers["content-type"] = content_type

        login_kind = None
        if op_id in (LOGIN_OTP_SEND_OP, LOGIN_OTP_RESEND_OP):
            login_kind = "otp"
        elif op_id == LOGIN_TOKEN_OP and self._payload_has_otp(payload):
            login_kind = "token"
        if login_kind:
            self.server.login_limiter.check(login_kind)

        timeout = payload.get("timeout", DEFAULT_TIMEOUT)
        if not isinstance(timeout, (int, float)) or isinstance(timeout, bool):
            raise ProxyError("timeout must be a number")
        timeout = max(1.0, min(float(timeout), float(MAX_TIMEOUT)))

        request = urllib.request.Request(
            url, data=data, headers=headers, method=op["http"])
        started = time.monotonic()
        try:
            with self.server.opener.open(request, timeout=timeout) as response:
                status = response.status
                reason = response.reason
                response_headers = response.headers.items()
                raw = response.read(MAX_RESPONSE_BYTES + 1)
        except urllib.error.HTTPError as exc:
            status = exc.code
            reason = exc.reason
            response_headers = exc.headers.items() if exc.headers else []
            raw = exc.read(MAX_RESPONSE_BYTES + 1)
        except urllib.error.URLError as exc:
            raise ProxyError("upstream request failed: %s" % exc.reason,
                             HTTPStatus.BAD_GATEWAY)
        except TimeoutError:
            raise ProxyError("upstream request timed out", HTTPStatus.GATEWAY_TIMEOUT)
        except OSError as exc:
            raise ProxyError("upstream request failed: %s" % exc,
                             HTTPStatus.BAD_GATEWAY)

        elapsed_ms = round((time.monotonic() - started) * 1000)
        truncated = len(raw) > MAX_RESPONSE_BYTES
        if truncated:
            raw = raw[:MAX_RESPONSE_BYTES]
        text = raw.decode("utf-8", "replace")
        parsed = None
        if text.strip():
            try:
                parsed = json.loads(text)
            except (ValueError, TypeError):
                parsed = None

        clean_headers = {}
        for name, value in response_headers:
            if name.lower() not in BLOCKED_RESPONSE_HEADERS:
                clean_headers[name] = str(value).replace("\r", " ").replace("\n", " ")
        content_type_header = clean_headers.get("Content-Type") or \
            clean_headers.get("content-type") or ""
        binary = bool(content_type_header) and not any(
            marker in content_type_header.lower()
            for marker in ("json", "text/", "xml", "javascript", "x-www-form-urlencoded")
        )

        return {
            "ok": 200 <= status < 300,
            "status": status,
            "reason": reason,
            "url": url,
            "duration_ms": elapsed_ms,
            "bytes": len(raw),
            "truncated": truncated,
            "headers": clean_headers,
            "content_type": content_type_header,
            "binary": binary,
            "json": parsed,
            "text": text,
        }

    # -- manual sign-in helpers -----------------------------------------
    @staticmethod
    def _login_phone(value) -> str:
        if not isinstance(value, str):
            raise ProxyError("phone must be a string")
        phone = re.sub(r"[\s().-]", "", value.strip())
        if phone.startswith("+"):
            raise ProxyError(
                "enter the phone number without the country code; use the "
                "country-code field instead")
        if not LOGIN_PHONE_RE.fullmatch(phone):
            raise ProxyError("phone must contain 6 to 15 digits")
        return phone

    @staticmethod
    def _login_country(value) -> str:
        if not isinstance(value, str):
            raise ProxyError("country_code must be a string")
        country = value.strip()
        if not LOGIN_COUNTRY_RE.fullmatch(country):
            raise ProxyError("country_code must be a + sign and 1 to 4 digits")
        return country if country.startswith("+") else "+" + country

    @staticmethod
    def _login_otp(value) -> str:
        if not isinstance(value, str):
            raise ProxyError("otp must be a string")
        otp = re.sub(r"\s", "", value.strip())
        if not LOGIN_OTP_RE.fullmatch(otp):
            raise ProxyError("otp must contain 4 to 8 digits")
        return otp

    @staticmethod
    def _login_headers() -> dict:
        headers = dict(LOGIN_APP_HEADERS)
        headers["randomId"] = secrets.token_hex(16)
        headers["accept"] = "application/json"
        return headers

    @staticmethod
    def _payload_has_otp(payload: dict) -> bool:
        body = payload.get("body")
        if not isinstance(body, dict):
            return False
        value = body.get("value")
        return isinstance(value, dict) and bool(value.get("otp"))

    @staticmethod
    def _redact_login_text(value) -> str:
        text = str(value)
        text = re.sub(r"(?i)\bBearer\s+[A-Za-z0-9._~+/-]{8,}",
                      "Bearer [redacted]", text)
        text = re.sub(r"[A-Za-z0-9_-]{24,}", "[redacted]", text)
        text = re.sub(r"\+?[0-9][0-9\s().-]{4,}[0-9]",
                      "[number redacted]", text)
        text = re.sub(r"(?i)\b(otp|code|secret|token)\b\s*[:=]?\s*"
                      r"[A-Za-z0-9._~+/-]{4,}", r"\1 [redacted]", text)
        return text[:LOGIN_MESSAGE_LIMIT]

    @classmethod
    def _login_message(cls, result: dict) -> str:
        parsed = result.get("json")
        candidates = []
        if isinstance(parsed, dict):
            for key in ("message", "error", "detail"):
                value = parsed.get(key)
                if isinstance(value, str):
                    candidates.append(value)
                elif isinstance(value, dict):
                    for sub in ("message", "error", "description", "detail"):
                        if isinstance(value.get(sub), str):
                            candidates.append(value[sub])
            data = parsed.get("data")
            if isinstance(data, dict):
                for key in ("message", "error"):
                    if isinstance(data.get(key), str):
                        candidates.append(data[key])
        for candidate in candidates:
            if candidate.strip():
                return cls._redact_login_text(candidate.strip())
        return ""

    @classmethod
    def _login_success(cls, result: dict) -> bool:
        parsed = result.get("json")
        if isinstance(parsed, dict) and parsed.get("success") is False:
            return False
        return bool(result.get("ok"))

    @staticmethod
    def _token_fields(parsed):
        containers = []
        if isinstance(parsed, dict):
            data = parsed.get("data")
            if isinstance(data, dict):
                containers.append(data)
            containers.append(parsed)
        token = ""
        container = None
        for item in containers:
            for key in ("access_token", "accessToken", "_accessToken", "token"):
                value = item.get(key)
                if isinstance(value, str) and value.strip():
                    token = value.strip()
                    container = item
                    break
            if token:
                break
        if not token:
            return "", None, "Bearer", None
        expires_in = None
        for key in ("expires_in", "expiresIn"):
            value = container.get(key)
            if isinstance(value, (int, float)) and not isinstance(value, bool):
                expires_in = int(value)
                break
        token_type = "Bearer"
        for key in ("token_type", "tokenType"):
            value = container.get(key)
            if isinstance(value, str) and value.strip():
                token_type = value.strip()
                break
        return token, container, token_type, expires_in

    def _require_login_ack(self, payload: dict) -> None:
        if payload.get("acknowledged") is not True:
            raise ProxyError(
                "confirm that you own the number or are authorized to use it",
                HTTPStatus.FORBIDDEN,
            )

    def _handle_login_otp(self) -> dict:
        payload = self._read_payload()
        self._require_login_ack(payload)
        action = payload.get("action")
        if action not in ("send", "resend"):
            raise ProxyError("action must be send or resend")
        phone = self._login_phone(payload.get("phone"))
        country = self._login_country(payload.get("country_code", "+91"))
        origin = self._pick_origin(payload.get("base_url"))
        resend = action == "resend"
        op_id = LOGIN_OTP_RESEND_OP if resend else LOGIN_OTP_SEND_OP
        body = (
            {"mobile": phone, "organizationId": LOGIN_ORGANIZATION_ID}
            if resend else
            {"username": phone, "countryCode": country,
             "organizationId": LOGIN_ORGANIZATION_ID}
        )
        result = self._execute({
            "operation": op_id,
            "base_url": origin,
            "path": {},
            "query": {},
            "headers": self._login_headers(),
            "body": {"mode": "json", "value": body},
            "timeout": DEFAULT_TIMEOUT,
        }, allow_preauth=True)
        ok = self._login_success(result)
        message = self._login_message(result)
        if not message:
            message = ("OTP request accepted." if ok
                       else "The OTP request was not accepted.")
        return {
            "ok": ok,
            "status": result.get("status"),
            "message": message,
            "cooldown_seconds": LOGIN_OTP_COOLDOWN_SECONDS if ok else 0,
        }

    def _handle_login_token(self) -> dict:
        payload = self._read_payload()
        self._require_login_ack(payload)
        phone = self._login_phone(payload.get("phone"))
        otp = self._login_otp(payload.get("otp"))
        origin = self._pick_origin(payload.get("base_url"))
        body = {
            "username": phone,
            "otp": otp,
            "referralCode": "",
            "linkToken": "",
            "client_id": LOGIN_CLIENT_ID,
            "client_secret": LOGIN_CLIENT_SECRET,
            "grant_type": "password",
            "organizationId": LOGIN_ORGANIZATION_ID,
        }
        result = self._execute({
            "operation": LOGIN_TOKEN_OP,
            "base_url": origin,
            "path": {},
            "query": {},
            "headers": self._login_headers(),
            "body": {"mode": "json", "value": body},
            "timeout": DEFAULT_TIMEOUT,
        }, allow_preauth=True)
        if not self._login_success(result):
            message = self._login_message(result)
            return {
                "ok": False,
                "status": result.get("status"),
                "error": message or "The token exchange was not accepted.",
            }
        token, _container, token_type, expires_in = self._token_fields(
            result.get("json"))
        if not token:
            message = self._login_message(result)
            return {
                "ok": False,
                "status": result.get("status"),
                "error": message or "The endpoint did not return an access token.",
            }
        if len(token) > MAX_TOKEN_CHARS:
            raise ProxyError("the endpoint returned an unexpectedly large token",
                             HTTPStatus.BAD_GATEWAY)
        return {
            "ok": True,
            "status": result.get("status"),
            "token": token,
            "token_type": token_type,
            "expires_in": expires_in,
            "message": "Access token received.",
        }

    def do_POST(self):
        path = urllib.parse.urlsplit(self.path).path
        if path not in ("/api/execute", "/api/login/otp", "/api/login/token"):
            self._send_error_json("not found", HTTPStatus.NOT_FOUND)
            return
        try:
            self._check_same_origin()
            if path == "/api/login/otp":
                result = self._handle_login_otp()
            elif path == "/api/login/token":
                result = self._handle_login_token()
            else:
                result = self._execute(self._read_payload())
        except ProxyError as exc:
            extra = None
            if exc.retry_after:
                extra = {"retry_after": exc.retry_after}
            self._send_error_json(str(exc), exc.status, extra=extra)
        except Exception as exc:  # noqa: BLE001 - keep unexpected detail local
            if self.server.debug:
                self.log_message("proxy internal error: %r", exc)
            self._send_error_json("internal proxy error",
                                  HTTPStatus.INTERNAL_SERVER_ERROR)
        else:
            self._send_json(result)


class AtlasServer(ThreadingHTTPServer):
    daemon_threads = True
    allow_reuse_address = True

    def __init__(self, address, handler, catalog, allowed_origins,
                 extra_origins, debug=False, login_limiter=None):
        super().__init__(address, handler)
        self.catalog = catalog
        self.operations = {op["id"]: op for op in catalog["operations"]}
        self.allowed_origins = set(allowed_origins)
        self.extra_origins = set(extra_origins)
        self.primary_origin = _origin(catalog["base_urls"][0])
        context = ssl.create_default_context()
        self.opener = urllib.request.build_opener(
            NoRedirect(), urllib.request.HTTPSHandler(context=context))
        self.debug = debug
        self.login_limiter = login_limiter or LoginRateLimiter()


def parse_args(argv=None):
    parser = argparse.ArgumentParser(
        description="Serve the PW API Atlas documentation explorer.")
    parser.add_argument("--host", default="127.0.0.1",
                        help="bind address (default: 127.0.0.1)")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT,
                        help="port (default: %d; use 0 for an ephemeral port)"
                        % DEFAULT_PORT)
    parser.add_argument("--allow-base", action="append", default=[],
                        metavar="URL",
                        help="add a test-only origin to the proxy allowlist "
                             "(for a local mock server)")
    parser.add_argument("--debug", action="store_true",
                        help="log unexpected proxy errors locally")
    return parser.parse_args(argv)


def main(argv=None):
    args = parse_args(argv)
    try:
        catalog = load_catalog(CATALOG_PATH)
    except (OSError, ValueError) as exc:
        sys.stderr.write("error: could not read catalog: %s\n" % exc)
        return 1

    try:
        catalog_origins = [_origin(url) for url in catalog["base_urls"]]
    except (KeyError, ValueError) as exc:
        sys.stderr.write("error: catalog has invalid base_urls: %s\n" % exc)
        return 1

    extra_origins = []
    for value in args.allow_base:
        try:
            extra_origins.append(_origin(value))
        except ValueError as exc:
            sys.stderr.write("error: invalid --allow-base %r: %s\n" % (value, exc))
            return 1

    try:
        server = AtlasServer(
            (args.host, args.port), AtlasHandler, catalog,
            catalog_origins + extra_origins, extra_origins, debug=args.debug)
    except OSError as exc:
        sys.stderr.write("error: could not start server: %s\n" % exc)
        return 1

    host, port = server.server_address[:2]
    shown_host = "127.0.0.1" if host in ("0.0.0.0", "::") else host
    print("PW API Atlas", flush=True)
    print("  site:   http://%s:%d/" % (shown_host, port), flush=True)
    print("  proxy:  POST /api/execute", flush=True)
    print("  login:  POST /api/login/otp, POST /api/login/token "
          "(manual, rate-limited)", flush=True)
    print("  hosts:  %s" % ", ".join(sorted(server.allowed_origins)), flush=True)
    print("  note:   requests are manual; tokens are never logged or stored.",
          flush=True)
    if host not in ("127.0.0.1", "localhost", "::1"):
        print("  WARNING: bound to %s; this proxy is reachable from the network. "
              "Keep it on loopback unless you trust every host that can reach "
              "this port." % host, flush=True)
    if extra_origins:
        print("  test:   extra allowlist origin(s): %s"
              % ", ".join(extra_origins), flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping.", flush=True)
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    sys.exit(main())
