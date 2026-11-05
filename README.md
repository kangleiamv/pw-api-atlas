# PW API Atlas

Independent, private research repository for the API inventory recovered from
the PW Android package (`xyz.penpencil.physicswala`, version `15.60.03`, version
code `156003`).

This is not a PW client, a look-alike consumer app, or a repackaged APK. It does
not include PW UI, brand assets or copied consumer-app content. The explorer and
all written material are original research tooling and documentation.

## What is in this repository

| Path | Contents |
| --- | --- |
| `api/pw_api_catalog.json` | Full inventory of 1,157 fixed-path operations, declarations, parameters, response types and tags |
| `api/openapi.yaml` | OpenAPI 3.1 document for the fixed-path operations |
| `api/endpoints.csv` | Flat endpoint list for spreadsheets and imports |
| `api/pw_api.py` | Dependency-free Python client and CLI driven by the catalog |
| `api/site/` | Local documentation explorer, guarded request console, manual OTP sign-in helper and tests |
| `api/README.md` | Extraction notes, safety model, startup instructions and test commands |
| `classes7/`, `classes18/` | Small decompiled DTO evidence used to confirm login request and response field names |
| `PW_15.60.03.apks` | Original package set used for extraction (97.7 MiB) |

The large local extraction workspace (`work/`: extracted DEX files, smali trees,
decompiler binaries and temporary logs) is intentionally not tracked. It is
reproducible from the APKS and would make the repository unnecessarily large.

APKS SHA-256:

```text
0a81b091ca302c817fc407cf6f454368b92921e23eee21c6925dd73123776d36
```

## Running the explorer

```bash
cd api/site
python3 serve.py
# open http://127.0.0.1:8765/
```

The server uses only the Python standard library. It binds to `127.0.0.1` by
default and makes external requests only when the user explicitly sends one.

## Tests

```bash
cd api/site
node --check assets/app.js
node test_client.js
python3 -m py_compile serve.py test_site.py
python3 test_site.py
```

The integration tests use a local mock upstream; they do not call production
hosts.

## Scope and responsible use

- This is private research and documentation, not a service or product.
- Use the request console and OTP helper only with accounts and systems you own
  or are explicitly authorized to test.
- The optional OTP helper completes the documented sign-in flow only for a
  phone number you control. It does not bypass authentication, device limits or
  other upstream security checks.
- The app package and decompiled material remain the property of their
  respective owners. Do not redistribute them.
- This repository is not affiliated with, endorsed by, or supported by Physics
  Wallah.

## Security note

This private repository contains application constants recovered from the APK,
including OAuth client identifiers used by the local sign-in helper. Keep the
repository private. If it is ever made public, those constants should first be
removed from `api/site/serve.py` and supplied through a local, untracked file or
environment variable.

## License

No open-source license is granted. Third-party material is included only as
private research evidence.
