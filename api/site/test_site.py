#!/usr/bin/env python3
"""Integration tests for the PW API Atlas local server and guardrails."""
import contextlib
import importlib.util
import io
import json
import os
import threading
import urllib.error
import urllib.parse
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

SITE_DIR = os.path.dirname(os.path.abspath(__file__))
TOKEN = "test-token-do-not-log-0123456789"
MOCK_ACCESS_TOKEN = "mock-access-token-0123456789abcdef"
MOCK_REFRESH_TOKEN = "mock-refresh-token-should-not-leak"
MOCK_REQUESTS = []


class MockHandler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, fmt, *args):
        pass

    def _handle(self):
        parsed = urllib.parse.urlsplit(self.path)
        length = int(self.headers.get("Content-Length", "0") or 0)
        raw = self.rfile.read(length) if length else b""
        query = urllib.parse.parse_qs(parsed.query, keep_blank_values=True)
        if query.get("big") == ["1"]:
            body = ('{"data":"' + ("x" * 1_100_000) + '"}').encode()
        else:
            payload = {
                "method": self.command,
                "path": parsed.path,
                "query": query,
                "headers": {k.lower(): v for k, v in self.headers.items()},
                "body": raw.decode("utf-8", "replace"),
            }
            MOCK_REQUESTS.append(payload)
            if parsed.path in ("/v3/users/get-otp-app",
                               "/v3/users/resend-otp-app"):
                body = json.dumps({
                    "success": True,
                    "data": {
                        "message": "OTP sent to +919876543210; code 123456",
                    },
                }).encode()
            elif parsed.path == "/v3/oauth/token" and b'"otp"' in raw:
                body = json.dumps({
                    "success": True,
                    "data": {
                        "access_token": MOCK_ACCESS_TOKEN,
                        "refresh_token": MOCK_REFRESH_TOKEN,
                        "expires_in": 86400,
                        "token_type": "Bearer",
                    },
                }).encode()
            else:
                body = json.dumps(payload).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Set-Cookie", "session=must-not-be-forwarded")
        self.send_header("X-Mock", "yes")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    do_GET = _handle
    do_POST = _handle


def load_module():
    spec = importlib.util.spec_from_file_location(
        "atlas_serve", SITE_DIR + "/serve.py")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def http(port, path, method="GET", payload=None, headers=None, raw=None):
    hdrs = dict(headers or {})
    data = raw
    if payload is not None:
        data = json.dumps(payload).encode()
        hdrs.setdefault("Content-Type", "application/json")
    req = urllib.request.Request(
        "http://127.0.0.1:%d%s" % (port, path),
        data=data, headers=hdrs, method=method)
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            return resp.status, dict(resp.headers.items()), resp.read().decode()
    except urllib.error.HTTPError as exc:
        return exc.code, dict(exc.headers.items()), exc.read().decode()


def check(name, condition, detail=""):
    if condition:
        print("ok   %s" % name)
    else:
        print("FAIL %s %s" % (name, detail))
        raise SystemExit(1)


def last_mock_request(path):
    for item in reversed(MOCK_REQUESTS):
        if item["path"] == path:
            return item
    return {}


def main():
    atlas = load_module()
    catalog = atlas.load_catalog(atlas.CATALOG_PATH)
    MOCK_REQUESTS.clear()

    mock = ThreadingHTTPServer(("127.0.0.1", 0), MockHandler)
    mock_origin = "http://127.0.0.1:%d" % mock.server_address[1]
    threading.Thread(target=mock.serve_forever, daemon=True).start()

    site = atlas.AtlasServer(
        ("127.0.0.1", 0), atlas.AtlasHandler, catalog,
        list(catalog["base_urls"]) + [mock_origin], [mock_origin],
        login_limiter=atlas.LoginRateLimiter(
            otp_cooldown=0, otp_window=60, otp_max=100,
            token_cooldown=0, token_window=60, token_max=100))
    site_port = site.server_address[1]
    threading.Thread(target=site.serve_forever, daemon=True).start()

    status, headers, body = http(site_port, "/")
    check("index served", status == 200 and "PW API Atlas" in body)
    check("sign-in panel served", 'id="signinForm"' in body)
    check("CSP header present", "Content-Security-Policy" in headers)
    status, _, body = http(site_port, "/assets/app.js")
    check("app.js served", status == 200 and "function sendRequest" in body)
    check("sign-in helper script served", "sendSigninOtp" in body)
    status, headers, body = http(site_port, "/api/catalog")
    check("catalog served", status == 200 and len(json.loads(body)["operations"]) == 1157)
    check("catalog content type",
          "application/json" in headers.get("Content-Type", ""))
    status, headers, body = http(site_port, "/api/openapi.yaml")
    check("OpenAPI served",
          status == 200 and body.startswith("openapi: 3.1.0"))
    check("OpenAPI content type",
          "yaml" in headers.get("Content-Type", ""))
    status, headers, body = http(site_port, "/api/endpoints.csv")
    check("CSV served",
          status == 200 and body.splitlines()[0].startswith("id,method,path"))
    status, headers, body = http(site_port, "/api/README.md")
    check("README served",
          status == 200 and "recovered mobile API" in body)
    status, _, body = http(site_port, "/api/health")
    check("health endpoint", status == 200 and json.loads(body)["ok"] is True)
    status, _, _ = http(site_port, "/api/not-a-route")
    check("unknown API route returns 404", status == 404)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "does-not-exist",
        "base_url": mock_origin,
    })
    check("unknown operation rejected", status == 404, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": "https://example.com",
    })
    check("non-allowlisted host rejected", status == 403, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": mock_origin,
        "path": {"batchId": "b1"},
    })
    check("private operation without token rejected", status == 401, body)

    captured = io.StringIO()
    with contextlib.redirect_stderr(captured):
        status, headers, body = http(site_port, "/api/execute", "POST", {
            "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
            "base_url": mock_origin,
            "path": {"batchId": "abc/def"},
            "query": {"batchSubjectId": "s1", "date": "2026-11-05", "userId": "u 1"},
            "headers": {"x-test": "yes"},
            "token": TOKEN,
        })
    result = json.loads(body) if body else {}
    check("GET proxy succeeds", status == 200 and result.get("status") == 200, body[:300])
    echoed = result.get("json", {})
    check("method forwarded", echoed.get("method") == "GET", echoed)
    check("path parameter encoded",
          echoed.get("path") == "/batch-service/batches/abc%2Fdef/schedules-for-given-date",
          echoed.get("path"))
    check("query forwarded",
          echoed.get("query", {}).get("userId") == ["u 1"], echoed.get("query"))
    check("token forwarded as bearer",
          echoed.get("headers", {}).get("authorization") == "Bearer " + TOKEN)
    check("custom header forwarded",
          echoed.get("headers", {}).get("x-test") == "yes")
    check("set-cookie stripped from proxy response",
          "set-cookie" not in {k.lower() for k in result.get("headers", {})})
    check("token not written to server log", TOKEN not in captured.getvalue(),
          captured.getvalue())

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_v3_oauth_token",
        "base_url": mock_origin,
    })
    result = json.loads(body)
    echoed = result.get("json", {})
    check("public operation allowed without token",
          status == 200 and result.get("status") == 200, body[:300])
    check("no token means no auth header",
          "authorization" not in echoed.get("headers", {}))
    check("app headers are not added automatically",
          "client-id" not in echoed.get("headers", {}))

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": mock_origin,
        "token": TOKEN,
    })
    check("missing path parameter rejected", status == 400, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": mock_origin,
        "path": {"batchId": "b1"},
        "token": TOKEN,
        "body": {"mode": "json", "value": {"x": 1}},
    })
    check("GET body rejected", status == 400, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": mock_origin,
        "path": {"batchId": "b1"},
        "token": "line one\nline two",
    })
    check("token with newline rejected", status == 400, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_api_v2_0_shortlinks_by_onelinktemplateid",
        "base_url": mock_origin,
        "path": {"oneLinkTemplateId": "tpl-1"},
        "headers": {"x-demo": "1"},
        "token": TOKEN,
        "body": {"mode": "json", "value": {"hello": "world"}},
    })
    result = json.loads(body)
    echoed = result.get("json", {})
    check("POST JSON succeeds", status == 200 and result.get("status") == 200, body[:300])
    check("JSON body forwarded", echoed.get("body") == '{"hello":"world"}', echoed.get("body"))
    check("JSON content type forwarded",
          echoed.get("headers", {}).get("content-type") == "application/json")

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_api_v2_0_shortlinks_by_onelinktemplateid",
        "base_url": mock_origin,
        "path": {"oneLinkTemplateId": "tpl-1"},
        "token": TOKEN,
        "body": {"mode": "form", "value": {"a": "1", "b": "two words"}},
    })
    echoed = json.loads(body).get("json", {})
    check("form body forwarded", echoed.get("body") == "a=1&b=two+words", echoed.get("body"))
    check("form content type forwarded",
          echoed.get("headers", {}).get("content-type") == "application/x-www-form-urlencoded")

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_api_v2_0_shortlinks_by_onelinktemplateid",
        "base_url": mock_origin,
        "path": {"oneLinkTemplateId": "tpl-1"},
        "token": TOKEN,
        "body": {"mode": "raw", "value": "raw-data", "content_type": "text/plain"},
    })
    echoed = json.loads(body).get("json", {})
    check("raw body forwarded", echoed.get("body") == "raw-data", echoed.get("body"))
    check("raw content type forwarded",
          echoed.get("headers", {}).get("content-type") == "text/plain")

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "get_batch_service_batches_by_batchid_schedules_for_given_date",
        "base_url": mock_origin,
        "path": {"batchId": "b1"},
        "query": {"big": "1"},
        "token": TOKEN,
    })
    result = json.loads(body)
    check("large response truncated", result.get("truncated") is True and result.get("bytes") == 1_000_000,
          (result.get("truncated"), result.get("bytes")))
    check("truncated text length", len(result.get("text", "")) == 1_000_000)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_api_v2_0_shortlinks_by_onelinktemplateid",
        "base_url": mock_origin,
        "path": {"oneLinkTemplateId": "tpl-1"},
        "headers": {"Host": "evil.example"},
    })
    check("managed header rejected", status == 400, body)

    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_api_v2_0_shortlinks_by_onelinktemplateid",
        "base_url": mock_origin,
        "path": {"oneLinkTemplateId": "tpl-1"},
    }, headers={"Origin": "http://evil.example"})
    check("cross-origin rejected", status == 403, body)

    status, _, body = http(
        site_port, "/api/execute", "POST",
        raw=b"not json", headers={"Content-Type": "text/plain"})
    check("non-JSON proxy request rejected", status == 415, body)

    # -- manual OTP sign-in helpers -----------------------------------
    status, _, body = http(site_port, "/api/execute", "POST", {
        "operation": "post_v3_users_get_otp_app",
        "base_url": mock_origin,
        "body": {"mode": "json", "value": {"username": "9876543210"}},
    })
    check("OTP operation stays guarded in the generic proxy",
          status == 401, body)

    status, _, body = http(site_port, "/api/login/otp", "POST", {
        "action": "send",
        "phone": "9876543210",
        "country_code": "+91",
        "base_url": mock_origin,
    })
    check("login OTP requires acknowledgement", status == 403, body)

    status, _, body = http(site_port, "/api/login/otp", "POST", {
        "acknowledged": True,
        "action": "send",
        "phone": "+919876543210",
        "country_code": "+91",
        "base_url": mock_origin,
    })
    check("login OTP rejects a number with country code", status == 400, body)

    status, _, body = http(site_port, "/api/login/otp", "POST", {
        "acknowledged": True,
        "action": "delete",
        "phone": "9876543210",
        "country_code": "+91",
        "base_url": mock_origin,
    })
    check("login OTP rejects an unknown action", status == 400, body)

    status, _, body = http(site_port, "/api/login/token", "POST", {
        "phone": "9876543210",
        "otp": "123456",
        "base_url": mock_origin,
    })
    check("token exchange requires acknowledgement", status == 403, body)

    status, _, body = http(site_port, "/api/login/token", "POST", {
        "acknowledged": True,
        "phone": "9876543210",
        "otp": "12a4",
        "base_url": mock_origin,
    })
    check("token exchange validates the OTP", status == 400, body)

    captured = io.StringIO()
    with contextlib.redirect_stderr(captured):
        status, _, body = http(site_port, "/api/login/otp", "POST", {
            "acknowledged": True,
            "action": "send",
            "phone": "9876543210",
            "country_code": "91",
            "base_url": mock_origin,
        })
        result = json.loads(body)
        check("login OTP succeeds", status == 200 and result.get("ok") is True,
              body)
        check("login OTP reports the cooldown",
              result.get("cooldown_seconds") == 30, result)
        check("login OTP redacts phone and code",
              "9876543210" not in body and "123456" not in body
              and "[number redacted]" in body, body)

        otp_request = last_mock_request("/v3/users/get-otp-app")
        otp_payload = json.loads(otp_request.get("body") or "{}")
        check("login OTP request body", otp_payload == {
            "username": "9876543210",
            "countryCode": "+91",
            "organizationId": atlas.LOGIN_ORGANIZATION_ID,
        }, otp_payload)
        otp_headers = otp_request.get("headers", {})
        check("login OTP sends app client id",
              otp_headers.get("client-id") == atlas.LOGIN_ORGANIZATION_ID,
              otp_headers)
        check("login OTP sends app client version",
              otp_headers.get("client-version") == "15.60.03", otp_headers)
        check("login OTP sends the app user agent",
              otp_headers.get("user-agent") == "Android", otp_headers)
        check("login OTP sends the app client type",
              otp_headers.get("client-type") == "MOBILE", otp_headers)
        check("login OTP sends the app referer",
              otp_headers.get("referer") == "https://android.pw.live",
              otp_headers)
        check("login OTP generates a fresh random id",
              len(otp_headers.get("randomid", "")) == 32, otp_headers)
        check("login OTP request carries no bearer token",
              "authorization" not in otp_headers, otp_headers)

        status, _, body = http(site_port, "/api/login/otp", "POST", {
            "acknowledged": True,
            "action": "resend",
            "phone": "9876543210",
            "country_code": "+91",
            "base_url": mock_origin,
        })
        result = json.loads(body)
        check("login OTP resend succeeds",
              status == 200 and result.get("ok") is True, body)
        resend_payload = json.loads(
            last_mock_request("/v3/users/resend-otp-app").get("body") or "{}")
        check("login OTP resend body", resend_payload == {
            "mobile": "9876543210",
            "organizationId": atlas.LOGIN_ORGANIZATION_ID,
        }, resend_payload)

        status, _, body = http(site_port, "/api/login/token", "POST", {
            "acknowledged": True,
            "phone": "9876543210",
            "country_code": "+91",
            "otp": "123456",
            "base_url": mock_origin,
        })
        result = json.loads(body)
        check("token exchange succeeds",
              status == 200 and result.get("ok") is True
              and result.get("token") == MOCK_ACCESS_TOKEN, body)
        check("token exchange returns expiry and type",
              result.get("expires_in") == 86400
              and result.get("token_type") == "Bearer", result)
        check("token exchange does not leak the refresh token",
              MOCK_REFRESH_TOKEN not in body, body)
        check("token exchange response omits phone and OTP fields",
              "9876543210" not in body
              and '"otp"' not in body and '"phone"' not in body, body)

        token_request = last_mock_request("/v3/oauth/token")
        token_payload = json.loads(token_request.get("body") or "{}")
        check("token exchange request body", token_payload == {
            "username": "9876543210",
            "otp": "123456",
            "referralCode": "",
            "linkToken": "",
            "client_id": atlas.LOGIN_CLIENT_ID,
            "client_secret": atlas.LOGIN_CLIENT_SECRET,
            "grant_type": "password",
            "organizationId": atlas.LOGIN_ORGANIZATION_ID,
        }, token_payload)
        token_headers = token_request.get("headers", {})
        check("token exchange sends app headers",
              token_headers.get("client-id") == atlas.LOGIN_ORGANIZATION_ID
              and token_headers.get("client-type") == "MOBILE"
              and token_headers.get("referer") == "https://android.pw.live",
              token_headers)
        check("token exchange request carries no bearer token",
              "authorization" not in token_headers, token_headers)

    logged = captured.getvalue()
    for label, secret in [
        ("phone number", "9876543210"),
        ("OTP", "123456"),
        ("access token", MOCK_ACCESS_TOKEN),
        ("refresh token", MOCK_REFRESH_TOKEN),
        ("OAuth client secret", atlas.LOGIN_CLIENT_SECRET),
    ]:
        check("%s is not written to server logs" % label, secret not in logged,
              logged)

    # Rate limits are enforced by the server, not just the page.
    site.login_limiter = atlas.LoginRateLimiter()
    status, _, body = http(site_port, "/api/login/otp", "POST", {
        "acknowledged": True,
        "action": "send",
        "phone": "9876543210",
        "country_code": "+91",
        "base_url": mock_origin,
    })
    check("first OTP send accepted under default limits", status == 200, body)
    status, _, body = http(site_port, "/api/login/otp", "POST", {
        "acknowledged": True,
        "action": "resend",
        "phone": "9876543210",
        "country_code": "+91",
        "base_url": mock_origin,
    })
    limited = json.loads(body)
    check("OTP cooldown returns 429",
          status == 429 and limited.get("retry_after", 0) >= 29, body)

    status, _, body = http(site_port, "/api/login/token", "POST", {
        "acknowledged": True,
        "phone": "9876543210",
        "otp": "123456",
        "base_url": mock_origin,
    })
    check("token exchange accepted under default limits", status == 200, body)
    status, _, body = http(site_port, "/api/login/token", "POST", {
        "acknowledged": True,
        "phone": "9876543210",
        "otp": "123456",
        "base_url": mock_origin,
    })
    limited = json.loads(body)
    check("token cooldown returns 429",
          status == 429 and limited.get("retry_after", 0) >= 1, body)

    clock = [0.0]
    limiter = atlas.LoginRateLimiter(
        otp_cooldown=0, otp_window=600, otp_max=2,
        token_cooldown=0, token_window=600, token_max=2,
        clock=lambda: clock[0])
    limiter.check("otp")
    clock[0] = 1.0
    limiter.check("otp")
    clock[0] = 2.0
    try:
        limiter.check("otp")
    except atlas.ProxyError as exc:
        check("OTP attempt limit enforced",
              exc.status == 429 and exc.retry_after == 598,
              (exc.status, exc.retry_after))
    else:
        check("OTP attempt limit enforced", False, "no rate-limit error")

    site.shutdown()
    mock.shutdown()
    site.server_close()
    mock.server_close()
    print("\nAll integration checks passed.")


if __name__ == "__main__":
    main()
