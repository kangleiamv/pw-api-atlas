# Physics Wallah (PW) — recovered mobile API

API surface extracted from `PW_15.60.03.apks` (`xyz.penpencil.physicswala`, version `15.60.03`, code `156003`).

APKS SHA-256: `0a81b091ca302c817fc407cf6f454368b92921e23eee21c6925dd73123776d36`  
base.apk SHA-256: `c3b19308349c60aa040cb11e66189cfb92403c95cdeee1ebc9ee1b8307612a8f`

## What is here

| File | Contents |
| --- | --- |
| `pw_api_catalog.json` | Full inventory: every operation, declaration, parameter, response type and tag |
| `openapi.yaml` | OpenAPI 3.1 document for the fixed-path operations |
| `endpoints.csv` | Flat list for spreadsheet/import use |
| `pw_api.py` | Dependency-free Python client and CLI driven by the catalog |
| `site/` | Independent documentation explorer and guarded request console |

## Documentation explorer

`site/` is a local, dependency-free research explorer over the catalog. It is
not a PW client, a look-alike UI, or a repackaged app: the design, copy and
interface are original. It exists to make the recovered API surface readable
and to support manual, authorized testing.

```bash
cd site
python3 serve.py
# open http://127.0.0.1:8765/
```

What it does:

- Searches and filters all 1,157 operations by method, tag, framework,
  inferred-public status, payload and parameters.
- Shows the full entry for each operation: parameter bindings, declarations,
  response types, source class, generated curl and raw catalog JSON.
- Provides a request console that builds a request from the selected catalog
  entry. Requests are manual; nothing is sent on page load.
- Offers an optional manual OTP helper that can request an OTP for your own
  number and exchange it for a bearer token, without exposing the app's OAuth
  client identifiers to the page.
- Serves the OpenAPI document, catalog, CSV and this README for download.

### Request proxy and safety model

The site is served by `site/serve.py` (Python standard library only). Its
request endpoint is `POST /api/execute`; `/api/health` reports catalog and
allowlist status.

- The target host must exactly match one of `catalog.base_urls` (or an origin
  explicitly added with `--allow-base` for a local mock). Arbitrary URLs and
  redirect following are rejected.
- The operation must be an id from the catalog; the path comes from that entry,
  not from the browser. Path values are URL-encoded. Query and header values are
  validated and length-limited.
- A token is accepted only in the POST body, forwarded once as
  `Authorization: Bearer ...`, and never logged. The server does not store
  sessions, tokens or cookies, and it strips `Set-Cookie` from upstream
  responses. Access logs contain only the request line without its query.
- Operations not marked `public` require a non-empty user-supplied
  `Authorization` value and are rejected with `401` otherwise. The inferred
  public token/OTP endpoints can be called without one.
- `Host`, `Content-Length`, `Connection`, `Transfer-Encoding`,
  `Accept-Encoding` and similar framing headers are managed by the server and
  cannot be overridden.
- The browser keeps a token only in page memory. It is never written to
  localStorage, sessionStorage, a URL or an analytics service. Consent is kept
  in sessionStorage for the tab; the token is not.
- The explorer loads no third-party scripts, fonts, images or trackers. Its
  Content-Security-Policy permits only same-origin assets and connections.

`--allow-base` is a test-only escape hatch for a local mock server:

```bash
python3 serve.py --allow-base http://127.0.0.1:9001
```

Do not add real third-party hosts to the allowlist.

The server binds to `127.0.0.1` by default. Keep it there: `--host 0.0.0.0`
makes the request console reachable from your network, and the proxy has no
user accounts or access control beyond same-origin checks and the host
allowlist.

### Manual OTP sign-in

The **Send an OTP and exchange it for a bearer token** panel is opt-in and
manual. It is only for a phone number and account you own or are explicitly
authorized to use.

It uses two fixed operations from the catalog:

- `POST /v3/users/get-otp-app` with `username`, `countryCode` and
  `organizationId` (and `POST /v3/users/resend-otp-app` with `mobile` and
  `organizationId` for a resend).
- `POST /v3/oauth/token` with `username`, `otp`, `referralCode`, `linkToken`,
  `client_id`, `client_secret`, `grant_type` and `organizationId`.

The app's embedded OAuth client identifiers and application headers
(`client-id`, `client-version`, `user-agent`, `client-type`, a fresh `randomId`
and `referer`) are applied by `serve.py`, not by the browser. They are app
constants recovered from the APK, not user credentials, and the client secret
is never sent to the page.

Safeguards in the server routes (`POST /api/login/otp` and
`POST /api/login/token`):

- The request must include `acknowledged: true`; the page also requires an
  explicit ownership checkbox and the authorized-use notice.
- OTP sends are limited to one every 30 seconds and five per 10 minutes per
  running server. Token exchanges are limited to one per second and ten per
  10 minutes. Limits return `429` with `retry_after`, and are enforced by the
  server, not just by button state.
- Upstream responses are reduced to a short redacted status message. The
  endpoint's raw body, the refresh token, and the phone number or OTP are not
  returned. The access token is returned only to the page that requested it.
- Neither the phone number, OTP, access token nor client secret is written to
  the access log. Responses are `Cache-Control: no-store`.
- The page keeps the phone number, OTP and token in memory only. It writes no
  token to localStorage or sessionStorage, and no token is placed in a URL.
  The token is masked until revealed and is hidden again when the tab is
  backgrounded.
- The account-deletion OTP routes (`/v3/otp-app` and `/v3/otp/verify`) are
  deliberately not used by this helper.

The helper is a convenience over the documented flow, not an authentication
bypass. It does not create a session, persist a token, or make private
operations public; it only returns a token for the account that completed the
OTP challenge. If the account has a device limit or another upstream security
check, the gateway may refuse the exchange; this helper does not bypass it.

### Authorized use

The console sends real HTTP requests to penpencil.co hosts when a user presses
**Send**. Use it only with accounts and systems you own or have written
permission to test. It does not grant access or bypass authentication. The
optional sign-in helper completes the documented OTP flow only for a number you
control; it does not obtain tokens for anyone else. Extracted paths and types
are static evidence, not a guarantee that an operation works.

### Tests

The integration test starts a local mock upstream and the site server on
ephemeral ports, then checks static and artifact serving, allowlist rejection,
token-required enforcement, token and header forwarding, JSON/form/raw bodies,
body truncation, header guardrails, cross-origin rejection, log hygiene, the
manual OTP/token routes, acknowledgement and validation failures, redacted
responses, OTP request shape, and server-side rate limits:

```bash
cd site
python3 test_site.py
```

The pure client helpers (type formatting, escaping, search highlighting, form
parsing, phone/OTP normalization, token masking and expiry formatting) have a
separate Node test that does not need a browser:

```bash
cd site
node test_client.js
```

## Numbers

- **1157** operations with a fixed path (merged from **1750** interface declarations)
- **3** external absolute URLs (mock server / third-party geocoder) kept separately
- **7** operations whose URL is supplied at runtime through `@Url`
- Framework of the first declaration: retrofit 715, ktorfit 442
- Raw declarations: ktorfit 526, retrofit 1224
- Methods: DELETE 22, GET 791, PATCH 8, POST 282, PUT 54

### Largest tags

- `v3` — 388
- `v1` — 282
- `batch-service` — 99
- `v2` — 75
- `vpservice` — 63
- `ott` — 61
- `engagement` — 37
- `sahayak` — 36
- `pi-lens-service` — 29
- `student-engagement-core` — 13
- `thirdPartyService` — 11
- `v4` — 7
- `pw-live-class` — 6
- `user-experience` — 6
- `uxncc-be-go` — 6
- `crm` — 5
- `program-service` — 5
- `order` — 4
- `bundle` — 3
- `api` — 2

## Servers

- `https://pw-api-gate.penpencil.co` — Primary API gateway
- `https://api.penpencil.co` — Legacy API host
- `https://api.penpencil.com` — Legacy API host (upload provider)
- `https://stage-api.penpencil.co` — Staging host

Most paths are gateway-relative (no leading service host). Use the primary gateway; some modules may target a legacy host. `openapi.yaml` lists every host as a server.

### Other network surfaces

- `https://live-class.penpencil.co/pw-live-class/ws` — Live class WebSocket (WebSocket)
- `https://socket-prod.penpencil.co/v1` — Realtime / socket service (Socket.IO / WebSocket)
- `https://analytics-api.penpencil.co` — Analytics / core analytics (HTTP)
  - `/core-analytics/utm/pre-signup-record`
  - `/core-analytics/universal/utm/post-signup-record`
  - `/core-analytics/universal/events/record`
  - `/core-analytics/universal/experiments/get-variant`
  - `/core-analytics/universal/experiments/record/`
  - `/core-analytics/universal/panic-mode/report-trigger/`
- `https://unleash-edge-prod.penpencil.co/api/frontend` — Unleash feature flags (HTTP)
- `https://panic.penpencil.co` — Panic mode (HTTP)

These were found as DEX string constants and are listed for completeness. WebSocket traffic is not modeled by OpenAPI; analytics calls use a runtime-supplied `@Url`.

## Authentication

The app's OkHttp interceptor (`smali11/c57.smali`) sends:

```http
Authorization: Bearer <authToken>
Content-Type: application/json
client-id: 5eb393ee95fab7468a79d189
client-version: 15.60.03
user-agent: Android
randomId: <random id>
client-type: MOBILE
device-meta: <device metadata>
referer: https://android.pw.live
```

The token comes from the app's auth-token provider. Tokens are obtained from the OAuth endpoints (see `POST /v3/oauth/token` and `POST /v3/oauth/truecaller-register-login`). Catalog operations flagged `public` are expected to work without a bearer token; this flag is inferred from the path, not from live probing. The OTP send/resend operations are not flagged public and are called only through the dedicated server helper.

The manual helper models the app's ordinary username+OTP sign-in: request a
code with `get-otp-app`/`resend-otp-app`, then exchange it at `oauth/token`.
The app's embedded `client_id`, `client_secret`, `grant_type=password` and
organization id are applied server-side. `serve.py` omits the app's fabricated
`device-meta` header and generates a fresh `randomId` per request.

## Using the client

```bash
python3 pw_api.py stats
python3 pw_api.py list --grep coachmark
python3 pw_api.py show get_engagement_coachmarks_unseen_user_coachmarks_by_cohortid
PW_TOKEN='<token>' python3 pw_api.py call get_engagement_coachmarks_unseen_user_coachmarks_by_cohortid \
  --path cohortId=<id> --query userSegment=NEW --query pageIdentifier=HOME
```

The client reads `pw_api_catalog.json` from the same directory. `--base-url`, `--token` / `PW_TOKEN`, `--header`, `--query`, `--path`, `--json-body`, `--form` and `--file` are supported. Responses are printed as JSON when possible.

## How it was recovered

1. Extracted `base.apk` from the APKS and unpacked all 18 DEX files.
2. Parsed DEX annotations with androguard rather than decompiling every class.
3. **Ktorfit** interfaces use unobfuscated `de/jensklingenberg/ktorfit/http/*` annotations, so verbs, paths, headers, traits and parameter bindings were read directly.
4. **Retrofit** annotation classes are R8-renamed. The mapping was recovered from `retrofit2.RequestFactory$Builder` bytecode (for example `Lmt10;` = `@GET`, `Lxop0;` = `@Path`, `Laab;` = `@Body`).
5. Duplicate declarations of the same method+path were merged; all original declarations remain in each catalog entry.
6. Response types come from DEX method signatures / generic `Signature` annotations. Kotlin coroutine suspend functions often expose only `Object`, so those stay generic.

## Caveats

- **Static extraction, not live verification.** No operation was called against production; server-side validation, feature flags and gateway routing may differ.
- Paths were reconstructed from annotations. Inline query strings in annotations were split into query parameters with defaults.
- Some path placeholders had no `@Path` name in the annotation (Ktorfit allows this); they were recovered from the URL template. Entries marked `synthesized` in the catalog were added because the template placeholder had no matching parameter at all.
- Free-form `@QueryMap`, `@HeaderMap`, `@FieldMap` and `@PartMap` values cannot be described statically; operations carry `query_map` / `header_map` flags and OpenAPI extensions.
- When several interfaces declare the same method+path with different payload classes, the catalog keeps every body type (`body_types`); OpenAPI uses the first and lists the rest in `x-request-body-variants`.
- DTO field schemas were not reconstructed. Typed bodies/responses are `object` with an `x-jvm-type` extension naming the original class.
- Response envelope fields (`success`, `data`, `message`, `error`, `paginate`) were taken from `com.penpencil.core.network.result.ApiResponse`.
- URLs whose host is chosen at runtime, and absolute third-party URLs, are excluded from `openapi.yaml` but retained in the catalog.

