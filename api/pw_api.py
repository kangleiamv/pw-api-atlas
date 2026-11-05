#!/usr/bin/env python3
"""Dependency-free client for the Physics Wallah (PW) mobile API.

The operation inventory lives in ``pw_api_catalog.json`` next to this file.
Only the Python standard library is required.

Examples
--------
    python3 pw_api.py stats
    python3 pw_api.py list --grep coachmark
    python3 pw_api.py show get_engagement_coachmarks_unseen_user_coachmarks_by_cohortid
    PW_TOKEN=... python3 pw_api.py call <operation-id> \
        --path cohortId=abc --query userSegment=NEW

Set ``PW_TOKEN`` (or pass ``--token``) to send ``Authorization: Bearer ...``.
Use ``--base-url`` to target a gateway other than the default.
"""
import argparse
import json
import mimetypes
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid

HERE = os.path.dirname(os.path.abspath(__file__))
CATALOG_FILE = os.path.join(HERE, "pw_api_catalog.json")
DEFAULT_BASE_URL = "https://pw-api-gate.penpencil.co"
DEFAULT_CLIENT_ID = "5eb393ee95fab7468a79d189"


def load_catalog(path=CATALOG_FILE):
    with open(path) as fh:
        return json.load(fh)


CATALOG = load_catalog()


class PWApiError(Exception):
    pass


class PWClient:
    def __init__(self, token=None, base_url=None, timeout=30, headers=None,
                 user_agent="Android", app_version=None, client_id=None,
                 random_id=None, device_meta=None, catalog=None):
        self.catalog = catalog or CATALOG
        self.token = token if token is not None else os.environ.get("PW_TOKEN")
        self.base_url = (base_url or DEFAULT_BASE_URL).rstrip("/")
        self.timeout = timeout
        self.extra_headers = dict(headers or {})
        self.user_agent = user_agent
        self.app_version = app_version or self.catalog["app"]["version_name"]
        self.client_id = client_id or os.environ.get("PW_CLIENT_ID", DEFAULT_CLIENT_ID)
        self.random_id = random_id or uuid.uuid4().hex
        self.device_meta = device_meta or json.dumps({
            "platform": "android",
            "appVersion": self.app_version,
        }, separators=(",", ":"))
        self._by_id = {op["id"]: op for op in self.catalog["operations"]}

    # -- lookup ---------------------------------------------------------
    def operation(self, ref):
        if ref in self._by_id:
            return self._by_id[ref]
        matches = [op for op in self.catalog["operations"] if op["path"] == ref]
        if len(matches) == 1:
            return matches[0]
        matches = [op for op in self.catalog["operations"]
                   if ref.lower() in op["id"].lower()
                   or ref.lower() in op["path"].lower()]
        if len(matches) == 1:
            return matches[0]
        if not matches:
            raise PWApiError("no operation matches %r" % ref)
        raise PWApiError("%r matches %d operations: %s"
                         % (ref, len(matches),
                            ", ".join(op["id"] for op in matches[:10])))

    # -- URL / encoding -------------------------------------------------
    def build_url(self, op, path_params=None, query=None):
        path_params = path_params or {}
        path = op["path"]
        for name in op.get("path_params") or []:
            if name not in path_params:
                raise PWApiError("missing path parameter %r for %s"
                                 % (name, op["id"]))
            path = path.replace("{%s}" % name,
                                urllib.parse.quote(str(path_params[name]), safe=""))
        leftover = set(re.findall(r"\{([^{}]+)\}", path))
        if leftover:
            raise PWApiError("unfilled path parameters: %s" % ", ".join(sorted(leftover)))
        url = self.base_url + path
        pairs = []
        for key, value in (query or {}).items():
            if value is None:
                continue
            if isinstance(value, (list, tuple)):
                for item in value:
                    pairs.append((key, _stringify(item)))
            elif isinstance(value, bool):
                pairs.append((key, "true" if value else "false"))
            else:
                pairs.append((key, _stringify(value)))
        if pairs:
            url += ("&" if "?" in url else "?") + urllib.parse.urlencode(pairs)
        return url

    def default_headers(self):
        headers = {
            "accept": "application/json",
            "user-agent": self.user_agent,
            "client-id": self.client_id,
            "client-version": self.app_version,
            "randomId": self.random_id,
            "client-type": "MOBILE",
            "device-meta": self.device_meta,
            "referer": "https://android.pw.live",
        }
        if self.token:
            headers["authorization"] = "Bearer " + self.token
        headers.update(self.extra_headers)
        return headers

    # -- request --------------------------------------------------------
    def call(self, ref, path_params=None, query=None, headers=None,
             json_body=None, body=None, form=None, files=None):
        """Call an operation and return {status, headers, json, text, url}.

        ``json_body`` is JSON-encoded, ``body`` is sent as-is, ``form`` is
        urlencoded, and ``files`` is a mapping name -> path or (filename, bytes).
        """
        op = self.operation(ref)
        url = self.build_url(op, path_params, query)
        req_headers = self.default_headers()
        data = None
        content_type = None

        if json_body is not None:
            data = json.dumps(json_body).encode()
            content_type = "application/json"
        elif files:
            data, content_type = _encode_multipart(form or {}, files)
        elif form:
            data = urllib.parse.urlencode(form, doseq=True).encode()
            content_type = "application/x-www-form-urlencoded"
        elif body is not None:
            data = body.encode() if isinstance(body, str) else body
            content_type = "application/octet-stream"

        if content_type:
            req_headers["content-type"] = content_type
        req_headers.update(dict(headers or {}))

        request = urllib.request.Request(url, data=data, headers=req_headers,
                                         method=op["http"])
        try:
            with urllib.request.urlopen(request, timeout=self.timeout) as resp:
                raw = resp.read()
                status = resp.status
                resp_headers = dict(resp.headers.items())
        except urllib.error.HTTPError as exc:
            raw = exc.read()
            status = exc.code
            resp_headers = dict(exc.headers.items()) if exc.headers else {}
        except urllib.error.URLError as exc:
            raise PWApiError("request failed: %s" % exc.reason)

        text = raw.decode("utf-8", "replace")
        parsed = None
        if text:
            try:
                parsed = json.loads(text)
            except ValueError:
                parsed = None
        result = {
            "operation": op["id"],
            "url": url,
            "status": status,
            "headers": resp_headers,
            "json": parsed,
            "text": text,
            "ok": 200 <= status < 300,
        }
        if not result["ok"]:
            message = text[:500] if text else "HTTP %d" % status
            raise PWApiError("HTTP %d: %s" % (status, message), )
        return result


def _stringify(value):
    return str(value)


def _encode_multipart(fields, files):
    boundary = "----PWBoundary" + uuid.uuid4().hex
    lines = []

    def add(value):
        lines.append(value.encode() if isinstance(value, str) else value)

    for name, value in fields.items():
        add("--%s\r\n" % boundary)
        add('Content-Disposition: form-data; name="%s"\r\n\r\n' % name)
        add(value)
        add("\r\n")
    for name, item in files.items():
        if isinstance(item, tuple):
            filename, content = item
        else:
            filename = os.path.basename(item)
            with open(item, "rb") as fh:
                content = fh.read()
        ctype = mimetypes.guess_type(filename)[0] or "application/octet-stream"
        add("--%s\r\n" % boundary)
        add('Content-Disposition: form-data; name="%s"; filename="%s"\r\n'
            % (name, filename))
        add("Content-Type: %s\r\n\r\n" % ctype)
        add(content)
        add("\r\n")
    add("--%s--\r\n" % boundary)
    return b"".join(lines), "multipart/form-data; boundary=%s" % boundary


# -- CLI -----------------------------------------------------------------
def parse_pairs(items):
    out = {}
    for item in items or []:
        if "=" not in item:
            raise SystemExit("expected key=value, got %r" % item)
        key, value = item.split("=", 1)
        out[key] = value
    return out


def cmd_stats(args):
    s = CATALOG["stats"]
    print(json.dumps({
        "app": CATALOG["app"],
        "base_urls": CATALOG["base_urls"],
        "stats": s,
    }, indent=1))


def cmd_list(args):
    rows = []
    needle = (args.grep or "").lower()
    for op in CATALOG["operations"]:
        if args.method and op["http"].upper() != args.method.upper():
            continue
        if args.tag and op["tag"] != args.tag:
            continue
        if needle and needle not in op["id"].lower() \
                and needle not in op["path"].lower() \
                and needle not in op["label"].lower():
            continue
        rows.append(op)
    if args.limit:
        rows = rows[:args.limit]
    if args.json:
        print(json.dumps(rows, indent=1))
        return
    for op in rows:
        print("%-6s %-90s %s" % (op["http"], op["path"], op["id"]))


def cmd_show(args):
    op = PWClient().operation(args.operation)
    print(json.dumps(op, indent=1))


def cmd_call(args):
    client = PWClient(token=args.token, base_url=args.base_url,
                      timeout=args.timeout, headers=parse_pairs(args.header))
    json_body = None
    if args.json_body:
        raw = args.json_body
        if raw.startswith("@"):
            with open(raw[1:]) as fh:
                raw = fh.read()
        json_body = json.loads(raw)
    body = None
    if args.body is not None:
        body = args.body
    elif args.body_file:
        with open(args.body_file, "rb") as fh:
            body = fh.read()
    files = parse_pairs(args.file)

    result = client.call(
        args.operation,
        path_params=parse_pairs(args.path),
        query=parse_pairs(args.query),
        headers=parse_pairs(args.header),
        json_body=json_body,
        body=body,
        form=parse_pairs(args.form),
        files=files or None,
    )
    if args.raw:
        sys.stdout.write(result["text"])
    else:
        print(json.dumps({
            "status": result["status"],
            "url": result["url"],
            "json": result["json"],
        }, indent=1))


def build_parser():
    parser = argparse.ArgumentParser(
        description="Client for the API surface recovered from the PW Android APK.")
    sub = parser.add_subparsers(dest="command", required=True)

    p = sub.add_parser("stats", help="show catalog statistics")
    p.set_defaults(func=cmd_stats)

    p = sub.add_parser("list", help="list operations")
    p.add_argument("--grep", help="substring filter over id/path/label")
    p.add_argument("--method", help="HTTP method filter")
    p.add_argument("--tag", help="tag filter (v1, v3, batch-service, ...)")
    p.add_argument("--limit", type=int, default=0)
    p.add_argument("--json", action="store_true")
    p.set_defaults(func=cmd_list)

    p = sub.add_parser("show", help="show one operation as JSON")
    p.add_argument("operation")
    p.set_defaults(func=cmd_show)

    p = sub.add_parser("call", help="invoke an operation")
    p.add_argument("operation")
    p.add_argument("--path", action="append", metavar="KEY=VALUE")
    p.add_argument("--query", action="append", metavar="KEY=VALUE")
    p.add_argument("--header", action="append", metavar="KEY=VALUE")
    p.add_argument("--json-body", help="inline JSON or @file")
    p.add_argument("--body", help="raw request body")
    p.add_argument("--body-file", help="raw request body file")
    p.add_argument("--form", action="append", metavar="KEY=VALUE")
    p.add_argument("--file", action="append", metavar="NAME=PATH")
    p.add_argument("--token", default=None)
    p.add_argument("--base-url", default=None)
    p.add_argument("--timeout", type=float, default=30)
    p.add_argument("--raw", action="store_true", help="print the response body only")
    p.set_defaults(func=cmd_call)
    return parser


def main(argv=None):
    parser = build_parser()
    args = parser.parse_args(argv)
    try:
        args.func(args)
    except PWApiError as exc:
        sys.stderr.write("error: %s\n" % exc)
        return 1
    except FileNotFoundError as exc:
        sys.stderr.write("error: %s\n" % exc)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
