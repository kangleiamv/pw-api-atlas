/* PW API Atlas — catalog explorer and manual request console.
   No frameworks, no external requests, no persistence beyond a session
   acknowledgement. Tokens live only in this page's memory. */
(() => {
  "use strict";

  const CONSENT_KEY = "pw-api-atlas-consent-v1";
  const SIGNIN_MAX_SENDS = 5;
  const SIGNIN_MAX_ATTEMPTS = 10;
  const METHOD_CLASS = {
    GET: "method-get",
    POST: "method-post",
    PUT: "method-put",
    PATCH: "method-patch",
    DELETE: "method-delete",
  };
  const PRIMITIVES = {
    V: "void", Z: "boolean", B: "byte", C: "char", S: "short",
    I: "int", J: "long", F: "float", D: "double",
  };
  const numberFormat = new Intl.NumberFormat("en-US");

  const state = {
    catalog: null,
    ops: [],
    filtered: [],
    page: 1,
    pageSize: 50,
    query: "",
    method: "",
    tag: "",
    framework: "",
    publicOnly: false,
    payloadOnly: false,
    parameterizedOnly: false,
    sort: "catalog",
    selected: null,
    detailTab: "request",
    baseUrl: "",
    token: "",
    consent: false,
    sending: false,
    signin: {
      stage: "phone",
      status: "idle",
      busy: false,
      sends: 0,
      attempts: 0,
      cooldownUntil: 0,
      timer: 0,
      token: "",
      tokenType: "Bearer",
      expiresIn: null,
      reveal: false,
    },
    lastResponse: null,
    responseTab: "json",
  };

  const els = {};
  let searchTimer = 0;
  let toastTimer = 0;
  let drawerTimer = 0;
  let curlFrame = 0;

  /* ------------------------------------------------------------------ */
  /* Boot                                                                */
  /* ------------------------------------------------------------------ */

  function init() {
    cacheElements();
    bindGlobalEvents();
    initConsent();
    loadCatalog();
  }

  function cacheElements() {
    const ids = [
      "searchInput", "methodFilter", "tagSelect", "frameworkSelect",
      "publicOnly", "payloadOnly", "parameterizedOnly", "resetFilters",
      "sortSelect", "pageSizeSelect", "resultSummary", "activeFilters",
      "operationList", "pagination", "catalogError", "catalogErrorDetail",
      "appVersion", "generatedNote", "tokenEndpoints", "appHeadersTable",
      "detailPanel", "detailBackdrop", "detailKicker", "detailTitle",
      "detailBody", "detailClose", "consentDialog", "consentCheck",
      "consentContinue", "consentRead", "toast",
      "signinForm", "signinHost", "signinCountry", "signinPhone",
      "signinAck", "signinSend", "signinResend", "signinReset",
      "signinConsent", "signinOtpStep", "signinOtp", "signinExchange",
      "signinMessage", "signinStatus", "signinResult", "signinTokenPreview",
      "signinTokenMeta", "signinReveal", "signinUse", "signinForget",
    ];
    ids.forEach((id) => { els[id] = document.getElementById(id); });
  }

  function bindGlobalEvents() {
    els.searchInput.addEventListener("input", () => {
      state.query = els.searchInput.value;
      window.clearTimeout(searchTimer);
      searchTimer = window.setTimeout(() => {
        state.page = 1;
        applyFilters();
      }, 120);
    });
    els.searchInput.addEventListener("keydown", (event) => {
      if (event.key === "Escape" && els.searchInput.value) {
        els.searchInput.value = "";
        state.query = "";
        state.page = 1;
        applyFilters();
      }
    });

    els.methodFilter.addEventListener("click", (event) => {
      const button = event.target.closest("button[data-method]");
      if (!button) return;
      state.method = button.dataset.method || "";
      state.page = 1;
      els.methodFilter.querySelectorAll("button[data-method]").forEach((item) => {
        item.classList.toggle("is-active", item === button);
        item.setAttribute("aria-pressed", item === button ? "true" : "false");
      });
      applyFilters();
    });

    els.tagSelect.addEventListener("change", () => {
      state.tag = els.tagSelect.value;
      state.page = 1;
      applyFilters();
    });
    els.frameworkSelect.addEventListener("change", () => {
      state.framework = els.frameworkSelect.value;
      state.page = 1;
      applyFilters();
    });

    [
      [els.publicOnly, "publicOnly"],
      [els.payloadOnly, "payloadOnly"],
      [els.parameterizedOnly, "parameterizedOnly"],
    ].forEach(([input, key]) => {
      input.addEventListener("change", () => {
        state[key] = input.checked;
        state.page = 1;
        applyFilters();
      });
    });

    els.sortSelect.addEventListener("change", () => {
      state.sort = els.sortSelect.value;
      state.page = 1;
      applyFilters();
    });
    els.pageSizeSelect.addEventListener("change", () => {
      state.pageSize = Number(els.pageSizeSelect.value) || 50;
      state.page = 1;
      applyFilters();
    });

    els.resetFilters.addEventListener("click", resetFilters);

    els.operationList.addEventListener("click", (event) => {
      const row = event.target.closest("button[data-op-id]");
      if (!row) return;
      const op = state.ops.find((item) => item.id === row.dataset.opId);
      if (op) openOperation(op);
    });

    els.pagination.addEventListener("click", (event) => {
      const button = event.target.closest("button[data-page]");
      if (!button) return;
      state.page = Number(button.dataset.page);
      renderResults();
      const pane = document.querySelector(".results-pane");
      if (pane) pane.scrollIntoView({ behavior: "smooth", block: "start" });
    });

    els.detailClose.addEventListener("click", () => closeDrawer());
    els.detailBackdrop.addEventListener("click", () => closeDrawer());
    window.addEventListener("hashchange", handleHashChange);

    els.detailBody.addEventListener("click", handleDetailClick);
    els.detailBody.addEventListener("input", handleDetailInput);
    els.detailBody.addEventListener("change", handleDetailInput);
    els.detailBody.addEventListener("keydown", (event) => {
      if ((event.metaKey || event.ctrlKey) && event.key === "Enter"
          && event.target.matches("textarea")) {
        event.preventDefault();
        sendRequest();
      }
    });

    document.addEventListener("keydown", handleGlobalKeydown);

    els.consentDialog.addEventListener("cancel", (event) => event.preventDefault());
    els.consentDialog.addEventListener("close", syncBodyLock);
    els.consentCheck.addEventListener("change", () => {
      els.consentContinue.disabled = !els.consentCheck.checked;
    });
    els.consentContinue.addEventListener("click", acceptConsent);
    els.consentRead.addEventListener("click", browseWithoutSending);

    bindSigninEvents();
  }

  function initConsent() {
    let acknowledged = false;
    try {
      acknowledged = window.sessionStorage.getItem(CONSENT_KEY) === "yes";
    } catch (error) {
      acknowledged = false;
    }
    state.consent = acknowledged;
    if (!acknowledged) openConsent();
  }

  function openConsent() {
    if (!els.consentDialog || els.consentDialog.open) return;
    els.consentCheck.checked = false;
    els.consentContinue.disabled = true;
    els.consentDialog.showModal();
    syncBodyLock();
  }

  function acceptConsent() {
    if (!els.consentCheck.checked) return;
    state.consent = true;
    try {
      window.sessionStorage.setItem(CONSENT_KEY, "yes");
    } catch (error) {
      /* Session storage can be unavailable; consent still applies to this page. */
    }
    els.consentDialog.close();
    syncConsentUI();
    if (state.selected && els.detailPanel.classList.contains("is-open")) {
      els.detailClose.focus({ preventScroll: true });
    }
    toast("Requests enabled for this tab.");
  }

  function browseWithoutSending(event) {
    event.preventDefault();
    state.consent = false;
    els.consentDialog.close();
    syncConsentUI();
    const explorer = document.getElementById("explorer");
    if (explorer) explorer.scrollIntoView({ behavior: "smooth", block: "start" });
    toast("Browsing only. Open a request console to review the notice again.");
  }

  function syncBodyLock() {
    const dialogOpen = Boolean(els.consentDialog && els.consentDialog.open);
    const drawerOpen = Boolean(
      state.selected && els.detailPanel.classList.contains("is-open"));
    document.body.classList.toggle("is-locked", dialogOpen || drawerOpen);
    [
      document.querySelector("main"),
      document.querySelector(".topbar"),
      document.querySelector(".notice-strip"),
      document.querySelector(".site-footer"),
    ].forEach((node) => {
      if (!node) return;
      if (drawerOpen) node.setAttribute("inert", "");
      else node.removeAttribute("inert");
    });
  }

  /* ------------------------------------------------------------------ */
  /* Manual OTP sign-in helper                                           */
  /* ------------------------------------------------------------------ */

  function bindSigninEvents() {
    if (!els.signinForm) return;
    els.signinForm.addEventListener("submit", (event) => {
      event.preventDefault();
      if (state.signin.stage === "otp") exchangeSigninToken();
      else if (state.signin.stage === "phone") sendSigninOtp("send");
    });
    els.signinResend.addEventListener("click", () => {
      if (state.signin.stage === "otp") sendSigninOtp("resend");
    });
    els.signinAck.addEventListener("change", syncSigninUI);
    els.signinReset.addEventListener("click", resetSignin);
    els.signinConsent.addEventListener("click", openConsent);
    els.signinReveal.addEventListener("click", () => {
      state.signin.reveal = !state.signin.reveal;
      renderSigninResult();
    });
    els.signinUse.addEventListener("click", useSigninToken);
    els.signinForget.addEventListener("click", forgetSigninToken);
    document.addEventListener("visibilitychange", () => {
      if (document.hidden && state.signin.reveal) {
        state.signin.reveal = false;
        renderSigninResult();
      }
    });
    syncSigninUI();
  }

  function setupSignin() {
    if (!els.signinHost) return;
    const urls = (state.catalog && state.catalog.base_urls) || [];
    if (urls.length && !els.signinHost.options.length) {
      urls.forEach((url) => {
        const option = document.createElement("option");
        option.value = url;
        option.textContent = url;
        els.signinHost.appendChild(option);
      });
      els.signinHost.value = state.baseUrl || urls[0];
    }
    syncSigninUI();
  }

  function setSigninStatus(text, tone) {
    if (!els.signinStatus) return;
    els.signinStatus.textContent = text;
    els.signinStatus.classList.remove("status-ok", "status-warn", "status-bad");
    els.signinStatus.classList.add(
      tone === "ok" ? "status-ok"
        : tone === "bad" ? "status-bad" : "status-warn");
  }

  function setSigninMessage(text, tone) {
    if (!els.signinMessage) return;
    els.signinMessage.textContent = text || "";
    els.signinMessage.classList.toggle("is-error", tone === "error");
    els.signinMessage.classList.toggle("is-ok", tone === "ok");
  }

  function syncSigninUI() {
    if (!els.signinForm) return;
    const s = state.signin;
    const remaining = Math.max(
      0, Math.ceil((s.cooldownUntil - Date.now()) / 1000));
    const stage = s.stage;
    const ack = Boolean(els.signinAck && els.signinAck.checked);
    const locked = stage !== "phone" || s.busy;

    [els.signinHost, els.signinCountry, els.signinPhone].forEach((field) => {
      if (field) field.disabled = locked;
    });
    if (els.signinAck) els.signinAck.disabled = s.busy;

    if (els.signinSend) {
      const retrySend = remaining > 0 && stage === "phone";
      els.signinSend.hidden = stage !== "phone";
      els.signinSend.disabled = s.busy || !state.consent || !ack
        || retrySend;
      els.signinSend.textContent = s.busy && stage === "phone"
        ? "Sending…"
        : retrySend ? "Retry in " + remaining + "s" : "Send OTP";
    }
    if (els.signinResend) {
      els.signinResend.hidden = stage !== "otp";
      els.signinResend.disabled = s.busy || !state.consent || !ack
        || remaining > 0 || s.sends >= SIGNIN_MAX_SENDS;
      els.signinResend.textContent = s.busy && stage === "otp"
        ? "Requesting…"
        : remaining > 0 ? "Resend in " + remaining + "s"
          : s.sends >= SIGNIN_MAX_SENDS ? "Resend limit reached"
            : "Resend OTP";
    }
    if (els.signinOtpStep) els.signinOtpStep.hidden = stage !== "otp";
    if (els.signinOtp) els.signinOtp.disabled = s.busy;
    if (els.signinExchange) {
      const exhausted = s.attempts >= SIGNIN_MAX_ATTEMPTS;
      els.signinExchange.disabled = s.busy || !state.consent || !ack
        || exhausted;
      els.signinExchange.textContent = s.busy && stage === "otp"
        ? "Exchanging…"
        : exhausted ? "Attempt limit reached" : "Exchange for token";
    }
    if (els.signinReset) {
      els.signinReset.hidden = stage === "phone";
      els.signinReset.disabled = s.busy;
    }
    if (els.signinConsent) els.signinConsent.hidden = state.consent;

    if (s.busy) setSigninStatus("Working…", "warn");
    else if (!state.consent) setSigninStatus("Consent required", "warn");
    else if (s.status === "error") setSigninStatus("Needs attention", "bad");
    else if (stage === "token") setSigninStatus("Token ready", "ok");
    else if (stage === "otp") setSigninStatus("OTP sent", "warn");
    else setSigninStatus("Idle", "warn");
  }

  function startSigninCooldown(seconds) {
    const s = state.signin;
    const wait = Math.max(0, Number(seconds) || 0);
    s.cooldownUntil = Date.now() + wait * 1000;
    if (s.timer) {
      window.clearInterval(s.timer);
      s.timer = 0;
    }
    if (wait > 0) {
      s.timer = window.setInterval(() => {
        if (Date.now() >= s.cooldownUntil) {
          window.clearInterval(s.timer);
          s.timer = 0;
        }
        syncSigninUI();
      }, 500);
    }
    syncSigninUI();
  }

  function requireSigninReady() {
    if (!state.consent) {
      openConsent();
      return false;
    }
    if (!els.signinAck || !els.signinAck.checked) {
      setSigninMessage(
        "Confirm that you own the number or have permission to use it.",
        "error");
      if (els.signinAck) els.signinAck.focus();
      return false;
    }
    return true;
  }

  async function sendSigninOtp(action) {
    const s = state.signin;
    if (s.busy || s.stage !== "phone") return;
    if (!requireSigninReady()) return;

    const phone = normalizePhone(els.signinPhone.value);
    const country = normalizeCountry(els.signinCountry.value);
    const host = els.signinHost.value;
    const phoneError = validatePhone(phone);
    if (phoneError) {
      setSigninMessage(phoneError, "error");
      els.signinPhone.focus();
      return;
    }
    const countryError = validateCountry(country);
    if (countryError) {
      setSigninMessage(countryError, "error");
      els.signinCountry.focus();
      return;
    }
    if (!host) {
      setSigninMessage("Choose an API host first.", "error");
      return;
    }

    s.busy = true;
    s.status = "pending";
    setSigninMessage(action === "resend"
      ? "Requesting another OTP…" : "Requesting an OTP…");
    syncSigninUI();
    try {
      const data = await postLogin("/api/login/otp", {
        acknowledged: true,
        action: action,
        phone: phone,
        country_code: country,
        base_url: host,
      });
      if (!data.ok) {
        s.status = "error";
        setSigninMessage(
          data.message || "The OTP request was not accepted.", "error");
        return;
      }
      s.sends += 1;
      s.stage = "otp";
      s.status = "otp";
      setSigninMessage(
        (data.message || "OTP request accepted.")
        + " Enter the code from the SMS for this number.", "ok");
      startSigninCooldown(Number(data.cooldown_seconds) || 0);
      window.setTimeout(() => {
        if (state.signin.stage === "otp" && !state.signin.busy) {
          els.signinOtp.focus();
        }
      }, 0);
    } catch (error) {
      s.status = "error";
      if (error.retryAfter) startSigninCooldown(error.retryAfter);
      setSigninMessage(
        error.message || "The OTP request failed.", "error");
    } finally {
      s.busy = false;
      syncSigninUI();
    }
  }

  async function exchangeSigninToken() {
    const s = state.signin;
    if (s.busy || s.stage !== "otp") return;
    if (!requireSigninReady()) return;

    const otp = normalizeOtp(els.signinOtp.value);
    const otpError = validateOtp(otp);
    if (otpError) {
      setSigninMessage(otpError, "error");
      els.signinOtp.focus();
      return;
    }
    if (s.attempts >= SIGNIN_MAX_ATTEMPTS) {
      setSigninMessage(
        "Exchange attempt limit reached. Use Start over to try again later.",
        "error");
      return;
    }
    const phone = normalizePhone(els.signinPhone.value);
    const country = normalizeCountry(els.signinCountry.value);
    const host = els.signinHost.value;

    s.attempts += 1;
    s.busy = true;
    s.status = "pending";
    setSigninMessage("Exchanging the code for an access token…");
    syncSigninUI();
    try {
      const data = await postLogin("/api/login/token", {
        acknowledged: true,
        phone: phone,
        country_code: country,
        otp: otp,
        base_url: host,
      });
      if (!data.ok || !data.token) {
        s.status = "error";
        setSigninMessage(
          data.error || data.message || "The token exchange was not accepted.",
          "error");
        return;
      }
      const expires = Number(data.expires_in);
      s.token = String(data.token);
      s.tokenType = String(data.token_type || "Bearer").slice(0, 40);
      s.expiresIn = Number.isFinite(expires) && expires > 0 ? expires : null;
      s.reveal = false;
      s.stage = "token";
      s.status = "token";
      els.signinOtp.value = "";
      setSigninMessage(
        "Access token received. It is held in this tab’s memory only.", "ok");
      renderSigninResult();
    } catch (error) {
      s.status = "error";
      setSigninMessage(
        error.message || "The token exchange failed.", "error");
    } finally {
      s.busy = false;
      syncSigninUI();
    }
  }

  function renderSigninResult() {
    if (!els.signinResult) return;
    const s = state.signin;
    const hasToken = Boolean(s.token);
    els.signinResult.hidden = !hasToken;
    els.signinReveal.textContent = s.reveal ? "Hide" : "Show";
    els.signinReveal.setAttribute("aria-pressed", s.reveal ? "true" : "false");
    if (!hasToken) {
      els.signinTokenPreview.textContent = "";
      els.signinTokenMeta.textContent = "";
      return;
    }
    els.signinTokenPreview.textContent = s.reveal
      ? s.token : maskToken(s.token);
    els.signinTokenMeta.textContent = (s.tokenType || "Bearer")
      + " token · expires " + formatExpiry(s.expiresIn);
  }

  function useSigninToken() {
    const token = state.signin.token;
    if (!token) return;
    state.token = token;
    const input = document.getElementById("tokenInput");
    if (input) input.value = token;
    scheduleCurl();
    syncSendUI();
    setSigninMessage(
      "Loaded as the request console’s bearer token. It was not stored.", "ok");
    toast("Bearer token loaded into the request console.");
  }

  function forgetSigninToken() {
    const s = state.signin;
    const token = s.token;
    const input = document.getElementById("tokenInput");
    if (token && (state.token === token || (input && input.value === token))) {
      state.token = "";
      if (input && input.value === token) input.value = "";
      scheduleCurl();
      syncSendUI();
    }
    s.token = "";
    s.tokenType = "Bearer";
    s.expiresIn = null;
    s.reveal = false;
    s.stage = "phone";
    s.status = "idle";
    s.cooldownUntil = 0;
    s.attempts = 0;
    if (s.timer) {
      window.clearInterval(s.timer);
      s.timer = 0;
    }
    els.signinOtp.value = "";
    renderSigninResult();
    setSigninMessage("Sign-in token removed from this page.", "");
    syncSigninUI();
  }

  function resetSignin() {
    const s = state.signin;
    s.stage = "phone";
    s.status = "idle";
    s.busy = false;
    s.sends = 0;
    s.attempts = 0;
    s.cooldownUntil = 0;
    s.token = "";
    s.tokenType = "Bearer";
    s.expiresIn = null;
    s.reveal = false;
    if (s.timer) {
      window.clearInterval(s.timer);
      s.timer = 0;
    }
    els.signinPhone.value = "";
    els.signinOtp.value = "";
    renderSigninResult();
    setSigninMessage("", "");
    syncSigninUI();
    els.signinPhone.focus();
  }

  async function postLogin(path, payload) {
    let response;
    try {
      response = await fetch(path, {
        method: "POST",
        cache: "no-store",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        body: JSON.stringify(payload),
      });
    } catch (error) {
      throw new Error("Could not reach the local server. Is serve.py running?");
    }
    let data = null;
    try {
      data = await response.json();
    } catch (error) {
      data = null;
    }
    if (!response.ok) {
      const message = data && typeof data.error === "string"
        ? data.error : "Request failed (HTTP " + response.status + ").";
      const failure = new Error(message);
      failure.status = response.status;
      failure.retryAfter = Number(data && data.retry_after) || 0;
      throw failure;
    }
    if (!data || typeof data !== "object") {
      throw new Error("The local server returned an unexpected response.");
    }
    return data;
  }

  /* ------------------------------------------------------------------ */
  /* Catalog and filtering                                               */
  /* ------------------------------------------------------------------ */

  async function loadCatalog() {
    try {
      const response = await fetch("/api/catalog", {
        cache: "no-store",
        headers: { Accept: "application/json" },
      });
      if (!response.ok) throw new Error("HTTP " + response.status);
      const catalog = await response.json();
      if (!catalog || !Array.isArray(catalog.operations)) {
        throw new Error("The catalog response has an unexpected shape.");
      }
      state.catalog = catalog;
      state.ops = catalog.operations.map((op) => ({
        ...op,
        _haystack: buildHaystack(op),
      }));
      state.baseUrl = (catalog.base_urls && catalog.base_urls[0]) || "";
      setupCatalog();
      applyFilters();
      const initialId = idFromHash();
      if (initialId) {
        const op = state.ops.find((item) => item.id === initialId);
        if (op) openOperation(op);
        else toast("That operation is not in this catalog.");
      }
    } catch (error) {
      showCatalogError(error);
    }
  }

  function setupCatalog() {
    const catalog = state.catalog;
    const stats = catalog.stats || {};
    document.title = "PW API Atlas — recovered interface inventory";
    if (els.appVersion) els.appVersion.textContent = catalog.app.version_name;
    setStat("operations", stats.operations);
    setStat("declarations", stats.declarations);
    setStat(
      "paths",
      new Set(catalog.operations.map((op) => op.path).filter(Boolean)).size);
    setStat("hosts", (catalog.base_urls || []).length);
    if (els.generatedNote) {
      els.generatedNote.textContent =
        "Catalog generated " + (catalog.generated_at || "—")
        + " · APK " + (catalog.app.version_name || "—");
    }

    document.querySelectorAll("[data-framework-count]").forEach((node) => {
      const key = node.dataset.frameworkCount;
      node.textContent = numberFormat.format(
        (stats.frameworks && stats.frameworks[key]) || 0);
    });
    const publicCount = state.ops.filter((op) => op.public).length;
    const publicNode = document.querySelector("[data-public-count]");
    if (publicNode) publicNode.textContent = numberFormat.format(publicCount);

    populateTags(stats.tags || {});
    renderTokenEndpoints();
    renderAppHeaders(catalog.default_headers || {});
    renderSurfaces(catalog);
    setupSignin();
  }

  function renderSurfaces(catalog) {
    const grid = document.getElementById("surfaceGrid");
    if (!grid) return;
    const external = catalog.external_endpoints || [];
    const urlOps = catalog.url_parameterized_operations || [];
    const services = catalog.service_endpoints || [];

    const card = (title, count, items, empty) =>
      '<article class="panel surface-card"><h3>' + escapeHtml(title)
      + '<span class="surface-count">' + numberFormat.format(count) + "</span></h3>"
      + (items
        ? '<ul class="plain-list">' + items.join("") + "</ul>"
        : '<p class="surface-loading">' + escapeHtml(empty) + "</p>")
      + "</article>";

    const externalItems = external.map((entry) =>
      "<li><code>" + escapeHtml(entry.http || "—") + '</code> <span class="surface-url">'
      + escapeHtml(entry.url || "—") + '</span><span class="surface-meta">'
      + escapeHtml(entry.framework || "—") + " · " + escapeHtml(entry.class || "—")
      + "#" + escapeHtml(entry.method || "—") + "</span></li>");

    const urlItems = urlOps.map((entry) =>
      "<li><code>" + escapeHtml(entry.http || "—") + '</code> <span class="surface-url">'
      + escapeHtml(entry.framework || "—") + " · " + escapeHtml(entry.class || "—")
      + "#" + escapeHtml(entry.method || "—") + '</span><span class="surface-meta">'
      + escapeHtml(entry.note || "URL supplied at runtime.") + "</span></li>");

    const serviceItems = services.map((entry) => {
      const paths = (entry.paths || []).map((path) =>
        '<li><span class="surface-summary">' + escapeHtml(path) + "</span></li>").join("");
      return "<li><strong>" + escapeHtml(entry.name || "service") + "</strong>"
        + '<span class="surface-meta">' + escapeHtml(entry.protocol || "HTTP")
        + " · " + escapeHtml(entry.source || "DEX string") + "</span>"
        + '<div class="surface-url">' + escapeHtml(entry.url || "—") + "</div>"
        + (paths ? '<ul class="surface-subpaths">' + paths + "</ul>" : "")
        + "</li>";
    });

    grid.innerHTML =
      card("External URLs", external.length, externalItems, "None recorded.")
      + card("Runtime URLs", urlOps.length, urlItems, "None recorded.")
      + card("Other network surfaces", services.length, serviceItems, "None recorded.");
  }

  function setStat(name, value) {
    document.querySelectorAll('[data-stat="' + name + '"]').forEach((node) => {
      node.textContent = numberFormat.format(Number(value) || 0);
    });
  }

  function populateTags(tags) {
    const entries = Object.entries(tags).sort((a, b) =>
      b[1] - a[1] || a[0].localeCompare(b[0]));
    const fragment = document.createDocumentFragment();
    entries.forEach(([name, count]) => {
      const option = document.createElement("option");
      option.value = name;
      option.textContent = name + " (" + numberFormat.format(count) + ")";
      fragment.appendChild(option);
    });
    els.tagSelect.appendChild(fragment);
  }

  function renderTokenEndpoints() {
    const endpoints = (state.catalog.auth && state.catalog.auth.token_endpoints) || [];
    if (!endpoints.length) return;
    els.tokenEndpoints.innerHTML = endpoints.map((endpoint) => {
      const split = String(endpoint).indexOf(" ");
      const method = split > 0 ? endpoint.slice(0, split) : "";
      const path = split > 0 ? endpoint.slice(split + 1) : endpoint;
      return "<li>" + (method ? "<code>" + escapeHtml(method) + "</code> " : "")
        + "<code>" + escapeHtml(path) + "</code></li>";
    }).join("");
  }

  function renderAppHeaders(headers) {
    const rows = Object.entries(headers).map(([name, value]) =>
      "<tr><td>" + escapeHtml(name) + "</td><td><code>"
      + escapeHtml(value) + "</code></td></tr>");
    els.appHeadersTable.innerHTML = rows.length
      ? rows.join("")
      : '<tr><td colspan="2">No default headers recorded.</td></tr>';
  }

  function buildHaystack(op) {
    const params = (op.params || []).map((param) =>
      [param.name, param.in, param.type].filter(Boolean).join(" ")).join(" ");
    return [
      op.id, op.path, op.label, op.class, op.method, op.tag, op.framework,
      params, (op.body_types || []).join(" "),
    ].filter(Boolean).join(" ").toLowerCase();
  }

  function applyFilters() {
    const terms = state.query.trim().toLowerCase().split(/\s+/).filter(Boolean);
    let rows = state.ops.filter((op) => {
      if (state.method && op.http !== state.method) return false;
      if (state.tag && op.tag !== state.tag) return false;
      if (state.framework && op.framework !== state.framework) return false;
      if (state.publicOnly && !op.public) return false;
      if (state.parameterizedOnly && !(op.params && op.params.length)) return false;
      if (state.payloadOnly && !hasPayload(op)) return false;
      if (terms.length && !terms.every((term) => op._haystack.includes(term))) {
        return false;
      }
      return true;
    });

    rows = sortOperations(rows);
    state.filtered = rows;
    const totalPages = Math.max(1, Math.ceil(rows.length / state.pageSize));
    if (state.page > totalPages) state.page = totalPages;
    if (state.page < 1) state.page = 1;
    renderResults();
  }

  function hasPayload(op) {
    if (op.body_types && op.body_types.length) return true;
    return (op.params || []).some((param) =>
      ["Body", "Field", "Part", "PartMap", "BodyMap"].includes(param.in));
  }

  function sortOperations(rows) {
    if (state.sort === "catalog") return rows;
    const copy = rows.slice();
    if (state.sort === "path") {
      copy.sort((a, b) => a.path.localeCompare(b.path));
    } else if (state.sort === "method") {
      copy.sort((a, b) => a.http.localeCompare(b.http)
        || a.path.localeCompare(b.path));
    } else if (state.sort === "tag") {
      copy.sort((a, b) => a.tag.localeCompare(b.tag)
        || a.path.localeCompare(b.path));
    } else if (state.sort === "label") {
      copy.sort((a, b) => (a.label || "").localeCompare(b.label || "")
        || a.path.localeCompare(b.path));
    }
    return copy;
  }

  function resetFilters() {
    state.query = "";
    state.method = "";
    state.tag = "";
    state.framework = "";
    state.publicOnly = false;
    state.payloadOnly = false;
    state.parameterizedOnly = false;
    state.sort = "catalog";
    state.page = 1;
    els.searchInput.value = "";
    els.tagSelect.value = "";
    els.frameworkSelect.value = "";
    els.publicOnly.checked = false;
    els.payloadOnly.checked = false;
    els.parameterizedOnly.checked = false;
    els.sortSelect.value = "catalog";
    els.methodFilter.querySelectorAll("button[data-method]").forEach((button) => {
      const active = button.dataset.method === "";
      button.classList.toggle("is-active", active);
      button.setAttribute("aria-pressed", active ? "true" : "false");
    });
    applyFilters();
  }

  /* ------------------------------------------------------------------ */
  /* Results                                                             */
  /* ------------------------------------------------------------------ */

  function renderResults() {
    renderSummary();
    renderActiveFilters();
    renderOperationList();
    renderPagination();
    markSelected();
  }

  function renderSummary() {
    const total = state.filtered.length;
    const catalogTotal = state.ops.length;
    const start = total ? (state.page - 1) * state.pageSize + 1 : 0;
    const end = Math.min(state.page * state.pageSize, total);
    let text = "Showing <strong>" + start + "–" + end + "</strong> of <strong>"
      + numberFormat.format(total) + "</strong>";
    text += total === catalogTotal ? " operations" : " matching operations";
    if (total !== catalogTotal) {
      text += " · " + numberFormat.format(catalogTotal) + " total";
    }
    els.resultSummary.innerHTML = text;
  }

  function renderActiveFilters() {
    const active = [];
    if (state.query.trim()) active.push("search “" + state.query.trim() + "”");
    if (state.method) active.push(state.method);
    if (state.tag) active.push("tag " + state.tag);
    if (state.framework) active.push(state.framework);
    if (state.publicOnly) active.push("public inferred");
    if (state.payloadOnly) active.push("typed payload");
    if (state.parameterizedOnly) active.push("has parameters");
    els.activeFilters.hidden = active.length === 0;
    els.activeFilters.textContent = active.length
      ? "Active filters: " + active.join(" · ")
      : "";
  }

  function renderOperationList() {
    const total = state.filtered.length;
    if (!total) {
      els.operationList.innerHTML =
        '<div class="empty-state"><h3>No matching operations</h3>'
        + "<p>Try a shorter search, clear a filter, or switch the method. "
        + "The catalog is static; nothing was requested.</p></div>";
      return;
    }
    const start = (state.page - 1) * state.pageSize;
    const rows = state.filtered.slice(start, start + state.pageSize);
    const html = rows.map((op) => {
      const paramCount = (op.params || []).length;
      const paramLabel = paramCount
        ? paramCount + (paramCount === 1 ? " param" : " params")
        : "no params";
      const selected = state.selected && state.selected.id === op.id
        ? " is-selected" : "";
      return '<button type="button" class="operation-row' + selected
        + '" data-op-id="' + escapeHtml(op.id) + '" aria-label="'
        + escapeHtml(op.http + " " + op.path) + '">'
        + '<span class="method ' + methodClass(op.http) + '">'
        + escapeHtml(op.http) + "</span>"
        + '<span class="op-main">'
        + '<span class="op-path">' + highlightText(op.path, state.query) + "</span>"
        + '<span class="op-meta">' + escapeHtml(op.label || op.id)
        + '<span class="meta-sep">·</span>' + escapeHtml(op.tag || "untagged")
        + '<span class="meta-sep">·</span>' + escapeHtml(paramLabel)
        + "</span></span>"
        + '<span class="op-side">'
        + '<span class="tag-pill">' + escapeHtml(op.tag || "—") + "</span>"
        + '<span class="framework-pill">' + escapeHtml(op.framework) + "</span>"
        + '<span class="param-count">' + escapeHtml(paramLabel) + "</span>"
        + "</span></button>";
    }).join("");
    els.operationList.innerHTML = html;
  }

  function showCatalogError(error) {
    els.operationList.innerHTML = "";
    els.resultSummary.textContent = "Catalog unavailable";
    els.catalogError.hidden = false;
    els.catalogErrorDetail.textContent = error && error.message
      ? error.message
      : String(error);
  }

  /* ------------------------------------------------------------------ */
  /* Pagination                                                          */
  /* ------------------------------------------------------------------ */

  function renderPagination() {
    const totalPages = Math.max(1, Math.ceil(state.filtered.length / state.pageSize));
    if (totalPages <= 1) {
      els.pagination.innerHTML = "";
      return;
    }
    const pages = new Set([1, totalPages]);
    for (let page = state.page - 1; page <= state.page + 1; page += 1) {
      if (page >= 1 && page <= totalPages) pages.add(page);
    }
    const ordered = Array.from(pages).sort((a, b) => a - b);
    const parts = [];
    parts.push('<button type="button" class="page-button" data-page="'
      + (state.page - 1) + '"' + (state.page === 1 ? " disabled" : "")
      + ' aria-label="Previous page">‹</button>');
    let previous = 0;
    ordered.forEach((page) => {
      if (previous && page - previous > 1) {
        parts.push('<span class="page-gap" aria-hidden="true">…</span>');
      }
      parts.push('<button type="button" class="page-button'
        + (page === state.page ? " is-current" : "") + '" data-page="' + page + '"'
        + (page === state.page ? ' aria-current="page"' : "")
        + ' aria-label="Page ' + page + '">' + page + "</button>");
      previous = page;
    });
    parts.push('<button type="button" class="page-button" data-page="'
      + (state.page + 1) + '"' + (state.page === totalPages ? " disabled" : "")
      + ' aria-label="Next page">›</button>');
    els.pagination.innerHTML = parts.join("");
  }

  /* ------------------------------------------------------------------ */
  /* Detail drawer                                                       */
  /* ------------------------------------------------------------------ */

  function openOperation(op) {
    const wasOpen = Boolean(state.selected && state.selected.id === op.id);
    state.selected = op;
    state.detailTab = "request";
    state.responseTab = "json";
    state.lastResponse = null;
    state.sending = false;
    state.sendSeq = (state.sendSeq || 0) + 1;
    renderDetail();
    window.clearTimeout(drawerTimer);
    els.detailBackdrop.hidden = false;
    els.detailPanel.removeAttribute("inert");
    els.detailPanel.setAttribute("aria-hidden", "false");
    window.requestAnimationFrame(() => {
      if (!state.selected || state.selected.id !== op.id) return;
      els.detailBackdrop.classList.add("is-open");
      els.detailPanel.classList.add("is-open");
      syncBodyLock();
      if (!wasOpen && !els.consentDialog.open) {
        els.detailClose.focus({ preventScroll: true });
      }
    });
    markSelected();
    document.title = op.http + " " + op.path + " — PW API Atlas";
    history.replaceState(
      null, "",
      window.location.pathname + window.location.search
        + "#op=" + encodeURIComponent(op.id));
  }

  function closeDrawer(fromHashChange) {
    if (!state.selected) return;
    const closing = state.selected;
    state.sendSeq = (state.sendSeq || 0) + 1;
    state.selected = null;
    state.sending = false;
    els.detailPanel.classList.remove("is-open");
    els.detailBackdrop.classList.remove("is-open");
    els.detailPanel.setAttribute("aria-hidden", "true");
    els.detailPanel.setAttribute("inert", "");
    syncBodyLock();
    document.title = "PW API Atlas — recovered interface inventory";
    window.clearTimeout(drawerTimer);
    drawerTimer = window.setTimeout(() => {
      if (state.selected) return;
      els.detailBackdrop.hidden = true;
      els.detailBody.innerHTML = "";
    }, 220);
    if (!fromHashChange && window.location.hash.indexOf("#op=") === 0) {
      history.replaceState(
        null, "", window.location.pathname + window.location.search);
    }
    markSelected();
    const row = findOperationRow(closing.id);
    if (row) row.focus({ preventScroll: true });
  }

  function handleHashChange() {
    const id = idFromHash();
    if (id && state.ops.length) {
      const op = state.ops.find((item) => item.id === id);
      if (op) {
        openOperation(op);
      } else {
        toast("That operation is not in this catalog.");
        closeDrawer(true);
      }
      return;
    }
    if (state.selected) closeDrawer(true);
  }

  function handleGlobalKeydown(event) {
    if (event.key === "Escape" && state.selected
        && !els.consentDialog.open) {
      event.preventDefault();
      closeDrawer();
      return;
    }
    if (event.key !== "Tab" || !state.selected
        || !els.detailPanel.classList.contains("is-open")
        || els.consentDialog.open) {
      return;
    }
    const focusable = getFocusable(els.detailPanel);
    if (!focusable.length) return;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (!els.detailPanel.contains(document.activeElement)) {
      event.preventDefault();
      first.focus();
      return;
    }
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  function getFocusable(container) {
    return Array.from(container.querySelectorAll(
      'a[href], button:not([disabled]), input:not([disabled]), '
      + 'select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'))
      .filter((node) => node.getClientRects().length > 0);
  }

  function findOperationRow(id) {
    return Array.from(els.operationList.querySelectorAll("button[data-op-id]"))
      .find((row) => row.dataset.opId === id) || null;
  }

  function markSelected() {
    els.operationList.querySelectorAll("button[data-op-id]").forEach((row) => {
      row.classList.toggle(
        "is-selected", Boolean(state.selected && row.dataset.opId === state.selected.id));
    });
  }

  function idFromHash() {
    const match = window.location.hash.match(/^#op=(.+)$/);
    if (!match) return null;
    try {
      return decodeURIComponent(match[1]);
    } catch (error) {
      return null;
    }
  }

  /* ------------------------------------------------------------------ */
  /* Detail rendering                                                    */
  /* ------------------------------------------------------------------ */

  function renderDetail() {
    const op = state.selected;
    if (!op) return;
    const kicker = [op.http, op.tag, op.framework];
    if (op.public) kicker.push("public inferred");
    els.detailKicker.textContent = kicker.filter(Boolean).join(" · ");
    els.detailTitle.innerHTML = '<span class="method ' + methodClass(op.http)
      + '">' + escapeHtml(op.http) + "</span>" + escapeHtml(op.path);

    const params = (op.params || []).length;
    const declarations = (op.declarations || []).length;
    const tags = [
      '<span class="tag-pill">' + escapeHtml(op.tag || "untagged") + "</span>",
      '<span class="tag-pill">' + escapeHtml(op.framework) + "</span>",
      '<span class="tag-pill">' + numberFormat.format(params)
        + (params === 1 ? " parameter" : " parameters") + "</span>",
      '<span class="tag-pill">' + numberFormat.format(declarations)
        + (declarations === 1 ? " declaration" : " declarations") + "</span>",
    ];
    if (op.public) tags.push('<span class="tag-pill is-public">public / no bearer inferred</span>');

    els.detailBody.innerHTML =
      '<p class="detail-summary"><strong>' + escapeHtml(op.label || op.id)
      + "</strong><br>" + escapeHtml(op.class || "unknown class")
      + '<span class="meta-sep">·</span>' + escapeHtml(op.method || "unknown method")
      + "</p>"
      + '<div class="detail-tags">' + tags.join("") + "</div>"
      + '<div class="detail-tabs" role="tablist" aria-label="Operation details">'
      + tabButton("request", "Request", true)
      + tabButton("parameters", "Parameters", false)
      + tabButton("source", "Source", false)
      + tabButton("json", "JSON", false)
      + "</div>"
      + '<div class="tab-panel" data-panel="request">' + renderRequestPanel(op) + "</div>"
      + '<div class="tab-panel" data-panel="parameters" hidden>'
      + renderParametersPanel(op) + "</div>"
      + '<div class="tab-panel" data-panel="source" hidden>'
      + renderSourcePanel(op) + "</div>"
      + '<div class="tab-panel" data-panel="json" hidden>'
      + renderJsonPanel(op) + "</div>";

    selectDetailTab("request");
    updateCurl();
    syncConsentUI();
  }

  function tabButton(name, label, active) {
    return '<button type="button" role="tab" class="tab-button'
      + (active ? " is-active" : "") + '" data-dtab="' + name
      + '" aria-selected="' + (active ? "true" : "false") + '">'
      + escapeHtml(label) + "</button>";
  }

  function selectDetailTab(name) {
    state.detailTab = name;
    els.detailBody.querySelectorAll("[data-dtab]").forEach((button) => {
      const active = button.dataset.dtab === name;
      button.classList.toggle("is-active", active);
      button.setAttribute("aria-selected", active ? "true" : "false");
    });
    els.detailBody.querySelectorAll("[data-panel]").forEach((panel) => {
      panel.hidden = panel.dataset.panel !== name;
    });
  }

  function renderRequestPanel(op) {
    const pathNames = pathParameters(op);
    const queryNames = namesFor(op, "query_params", "Query");
    const headerNames = namesFor(op, "header_params", "Header")
      .filter((name) => name.toLowerCase() !== "authorization");
    const hasAuthHeaderParam = (op.params || []).some((param) =>
      param.in === "Header" && String(param.name || "").toLowerCase() === "authorization");
    const bodyAllowed = supportsBody(op);
    const consentWarning = state.consent ? "" :
      '<div class="console-warning" data-consent-warning>'
      + "Requests are disabled until you accept the authorized-use notice. "
      + '<button type="button" class="link-button" data-action="open-consent">'
      + "Review the notice</button></div>";
    const publicNote = op.public
      ? '<div class="note-box"><p><strong>Public inferred.</strong> The catalog '
        + "marks this operation as token/OTP/public by path. That is static "
        + "inference, not a live check.</p></div>"
      : "";
    const mapNote = (op.query_map || op.header_map)
      ? '<p class="body-hint">This operation also declares a free-form '
        + (op.query_map ? "query map" : "") + (op.query_map && op.header_map ? " and " : "")
        + (op.header_map ? "header map" : "")
        + "; add extra pairs manually.</p>"
      : "";

    const baseOptions = (state.catalog.base_urls || []).map((url) =>
      '<option value="' + escapeHtml(url) + '"'
      + (url === state.baseUrl ? " selected" : "") + ">" + escapeHtml(url)
      + "</option>").join("");

    const pathBlock = pathNames.length
      ? '<fieldset class="console-block"><legend class="field-label">'
        + 'Path parameters <span class="hint">required · URL-encoded</span></legend>'
        + '<div class="pair-list">' + pathNames.map((name) =>
          pathRowHtml(name)).join("") + "</div></fieldset>"
      : "";

    const queryRows = queryNames.map((name) =>
      pairRowHtml(name, defaultFor(op, name, "Query"), true, "query")).join("");
    const queryBlock =
      '<fieldset class="console-block"><legend class="field-label">'
      + 'Query parameters <span class="hint">blank values are omitted</span></legend>'
      + '<div class="pair-list" data-pair-group="query" data-pair-list="query">'
      + queryRows + "</div>"
      + '<button type="button" class="add-pair" data-add-pair="query">+ Add query pair</button>'
      + mapNote + "</fieldset>";

    const headerRows = headerNames.map((name) =>
      pairRowHtml(name, defaultFor(op, name, "Header"), true, "header")).join("");
    const headerBlock =
      '<fieldset class="console-block"><legend class="field-label">'
      + 'Headers <span class="hint">blank values are omitted</span></legend>'
      + '<div class="pair-list" data-pair-group="headers" data-pair-list="headers">'
      + headerRows + "</div>"
      + '<button type="button" class="add-pair" data-add-pair="headers">+ Add header</button>'
      + (op.header_map ? '<p class="body-hint">Free-form header map; extra names are allowed.</p>' : "")
      + (hasAuthHeaderParam
        ? '<p class="body-hint">This operation declares an Authorization header. '
          + "The token field sends a Bearer value; add an Authorization pair to override it.</p>"
        : "")
      + "</fieldset>";

    return publicNote
      + '<section class="console">'
      + '<div class="console-head"><h3>Request console</h3><span>manual only</span></div>'
      + '<div class="console-body">'
      + consentWarning
      + '<div class="console-block">'
      + '<label class="field-label" for="baseUrlSelect">Target host</label>'
      + '<select class="field" id="baseUrlSelect" data-base-url>' + baseOptions + "</select>"
      + '<p class="body-hint">Only hosts in the catalog are accepted by the local proxy.</p>'
      + "</div>"
      + '<div class="console-block">'
      + '<label class="field-label" for="tokenInput">Bearer token '
      + '<span class="hint">memory only</span></label>'
      + '<div class="token-row"><input class="field" id="tokenInput" data-token '
      + 'type="password" autocomplete="off" autocapitalize="none" spellcheck="false" '
      + 'placeholder="Paste a token for an account you are authorized to test" value="'
      + escapeHtml(state.token) + '">'
      + '<button type="button" class="mini-button" data-action="toggle-token">Show</button>'
      + '<button type="button" class="mini-button" data-action="clear-token">Clear</button></div>'
      + '<p class="token-note">Kept in this tab’s memory. Added as '
      + "<code>Authorization: Bearer &lt;token&gt;</code> only when you press Send. "
      + (op.public
        ? "This operation is marked public; a token is optional. "
        : "Required here: this operation is not marked public. ")
      + "Never written to storage, a URL or a log.</p>"
      + "</div>"
      + pathBlock
      + queryBlock
      + headerBlock
      + (bodyAllowed ? renderBodyEditor(op) : "")
      + '<div class="curl-block"><div class="curl-head"><span>Generated curl</span>'
      + '<button type="button" class="copy-mini" data-action="copy-curl">Copy</button></div>'
      + '<pre class="curl-code" id="curlCode"></pre></div>'
      + '<p class="inline-error" id="consoleError" role="alert" hidden></p>'
      + '<div class="send-row"><button type="button" class="send-button" '
      + 'data-action="send">Send request</button><span class="send-note"></span></div>'
      + "</div></section>"
      + '<div id="responseArea"><div class="response-placeholder">'
      + "No request sent. The console is manual: choose a host, add a token if "
      + "you have one, then press Send.</div></div>";
  }

  function pathRowHtml(name) {
    return '<div class="pair-row"><input class="field pair-name" value="'
      + escapeHtml(name) + '" readonly tabindex="-1" aria-label="Path parameter name">'
      + '<input class="field pair-value" data-path-param="' + escapeHtml(name)
      + '" placeholder="value" autocomplete="off" spellcheck="false" aria-label="'
      + escapeHtml("Value for " + name) + '">'
      + '<span class="pair-spacer" aria-hidden="true"></span></div>';
  }

  function pairRowHtml(name, value, readOnly, group) {
    const readOnlyAttr = readOnly ? ' readonly tabindex="-1"' : "";
    const label = name || "extra " + group + " parameter";
    return '<div class="pair-row"><input class="field pair-name" value="'
      + escapeHtml(name) + '" placeholder="name" aria-label="' + escapeHtml(label + " name")
      + '" autocomplete="off" spellcheck="false"' + readOnlyAttr + '>'
      + '<input class="field pair-value" value="' + escapeHtml(value || "")
      + '" placeholder="value" aria-label="' + escapeHtml(label + " value")
      + '" autocomplete="off" spellcheck="false">'
      + '<button type="button" class="pair-remove" data-remove-pair aria-label="'
      + escapeHtml("Remove " + label) + '">×</button></div>';
  }

  function renderBodyEditor(op) {
    const traits = op.traits || [];
    let mode = "json";
    if (traits.includes("FormUrlEncoded")) mode = "form";
    if (traits.includes("Multipart")) mode = "raw";
    const bodyTypes = op.body_types || [];
    let hint = "Optional. Send JSON, form fields or a raw body; blank means no body.";
    if (bodyTypes.length) {
      hint += " Declared type: " + bodyTypes.map(prettyType).join(", ") + ".";
    }
    if (traits.includes("Multipart")) {
      hint += " Multipart is declared here, but this console does not build multipart parts.";
    }
    const modes = ["json", "form", "raw"].map((name) =>
      '<button type="button" data-body-mode="' + name + '"'
      + (name === mode ? ' class="is-active"' : "") + ">"
      + name.toUpperCase() + "</button>").join("");
    return '<fieldset class="console-block"><legend class="field-label">'
      + 'Request body <span class="hint">optional</span></legend>'
      + '<div class="body-mode" role="group" aria-label="Body encoding">' + modes + "</div>"
      + '<div data-body-editor="json"' + (mode === "json" ? "" : " hidden") + ">"
      + '<textarea class="field" data-body-input="json" spellcheck="false" '
      + 'placeholder="{&#10;  &quot;key&quot;: &quot;value&quot;&#10;}"></textarea></div>'
      + '<div data-body-editor="form"' + (mode === "form" ? "" : " hidden") + ">"
      + '<textarea class="field" data-body-input="form" spellcheck="false" '
      + 'placeholder="key=value&#10;another=value"></textarea></div>'
      + '<div data-body-editor="raw"' + (mode === "raw" ? "" : " hidden") + ">"
      + '<textarea class="field" data-body-input="raw" spellcheck="false" '
      + 'placeholder="Raw request body"></textarea>'
      + '<label class="field-label field-label-spaced" for="rawContentType">'
      + "Content-Type</label>"
      + '<input class="field" id="rawContentType" data-raw-content-type '
      + 'value="text/plain; charset=utf-8" autocomplete="off" spellcheck="false">'
      + "</div>"
      + '<p class="body-hint">' + escapeHtml(hint) + "</p>"
      + "</fieldset>";
  }

  function renderParametersPanel(op) {
    const params = op.params || [];
    const rows = params.map((param) => {
      const name = param.name
        ? escapeHtml(param.name)
        : '<span class="muted">(unnamed)</span>';
      const fallback = "-";
      return "<tr><td><span class=\"binding\">" + escapeHtml(param.in || "—")
        + "</span></td><td>" + name + "</td><td>"
        + '<code class="type-code" title="' + escapeHtml(param.type || "")
        + '">' + escapeHtml(prettyType(param.type)) + "</code></td><td>"
        + (param.default !== undefined && param.default !== null
          ? "<code>" + escapeHtml(param.default) + "</code>" : fallback)
        + "</td></tr>";
    }).join("");
    const flags = [];
    if (op.query_map) flags.push("free-form query map");
    if (op.header_map) flags.push("free-form header map");
    if (op.url_param) flags.push("runtime URL parameter");
    if (op.public) flags.push("public inferred");
    return '<section class="detail-section"><h3>Declared parameters</h3>'
      + (params.length
        ? '<div class="table-wrap"><table class="param-table"><thead><tr>'
          + "<th>Binding</th><th>Name</th><th>Type</th><th>Default</th>"
          + "</tr></thead><tbody>" + rows + "</tbody></table></div>"
        : '<p class="note-box">No parameters were declared for this operation.</p>')
      + (flags.length ? '<p class="body-hint">Flags: ' + escapeHtml(flags.join(" · ")) + "</p>" : "")
      + "</section>"
      + '<section class="detail-section"><h3>Response and payload types</h3>'
      + '<dl class="kv-list"><div><dt>Response</dt><dd><code class="type-code">'
      + escapeHtml(prettyType(op.response)) + "</code></dd></div>"
      + "<div><dt>Body types</dt><dd>"
      + (op.body_types && op.body_types.length
        ? op.body_types.map((type) => '<code class="type-code">'
          + escapeHtml(prettyType(type)) + "</code>").join("<br>")
        : "—")
      + "</dd></div><div><dt>Traits</dt><dd>"
      + escapeHtml((op.traits || []).join(", ") || "—")
      + "</dd></div></dl></section>";
  }

  function renderSourcePanel(op) {
    const declarations = op.declarations || [];
    const rows = declarations.map((decl) => "<tr><td>"
      + escapeHtml(decl.framework || "—") + "</td><td><code>"
      + escapeHtml(decl.class || "—") + "#" + escapeHtml(decl.method || "—")
      + "</code></td><td><code class=\"type-code\">"
      + escapeHtml(prettyType(decl.response)) + "</code></td></tr>").join("");
    return '<section class="detail-section"><h3>Source</h3>'
      + '<dl class="kv-list">'
      + "<div><dt>Operation id</dt><dd><code>" + escapeHtml(op.id) + "</code></dd></div>"
      + "<div><dt>Class</dt><dd><code>" + escapeHtml(op.class || "—") + "</code></dd></div>"
      + "<div><dt>Method</dt><dd><code>" + escapeHtml(op.method || "—") + "</code></dd></div>"
      + "<div><dt>Framework</dt><dd>" + escapeHtml(op.framework) + "</dd></div>"
      + "<div><dt>Path</dt><dd><code>" + escapeHtml(op.path) + "</code></dd></div>"
      + "</dl></section>"
      + '<section class="detail-section"><h3>Declarations</h3>'
      + (rows
        ? '<div class="table-wrap"><table class="decl-table"><thead><tr>'
          + "<th>Framework</th><th>Declaration</th><th>Response</th></tr></thead><tbody>"
          + rows + "</tbody></table></div>"
        : '<p class="note-box">No declaration metadata.</p>')
      + '<p class="body-hint">Duplicate method/path declarations were merged; '
      + "the rows above are the originals. Response types are JVM signatures, "
      + "not expanded DTO schemas.</p></section>";
  }

  function renderJsonPanel(op) {
    const clone = Object.assign({}, op);
    delete clone._haystack;
    return '<section class="detail-section"><h3>Catalog entry</h3>'
      + '<div class="curl-head"><span>' + escapeHtml(op.id) + "</span>"
      + '<button type="button" class="copy-mini" data-action="copy-op-json">Copy JSON</button>'
      + "</div>"
      + '<pre class="raw-json">' + escapeHtml(JSON.stringify(clone, null, 2))
      + "</pre></section>";
  }

  /* ------------------------------------------------------------------ */
  /* Console interactions                                                */
  /* ------------------------------------------------------------------ */

  function handleDetailClick(event) {
    const tab = event.target.closest("[data-dtab]");
    if (tab) {
      selectDetailTab(tab.dataset.dtab);
      return;
    }
    const bodyMode = event.target.closest("[data-body-mode]");
    if (bodyMode) {
      selectBodyMode(bodyMode.dataset.bodyMode);
      return;
    }
    const addPair = event.target.closest("[data-add-pair]");
    if (addPair) {
      addPairRow(addPair.dataset.addPair);
      return;
    }
    const removePair = event.target.closest("[data-remove-pair]");
    if (removePair) {
      const row = removePair.closest(".pair-row");
      if (row) row.remove();
      scheduleCurl();
      syncSendUI();
      return;
    }
    const responseTab = event.target.closest("[data-response-tab]");
    if (responseTab) {
      state.responseTab = responseTab.dataset.responseTab;
      renderResponseView();
      return;
    }
    const action = event.target.closest("[data-action]");
    if (!action) return;
    const name = action.dataset.action;
    if (name === "send") {
      sendRequest();
    } else if (name === "copy-curl") {
      const code = document.getElementById("curlCode");
      if (code) copyText(code.textContent, "curl copied.");
    } else if (name === "copy-response") {
      const view = document.getElementById("responseView");
      if (view) copyText(view.textContent, "Response copied.");
    } else if (name === "copy-op-json") {
      copyOperationJson();
    } else if (name === "open-consent") {
      openConsent();
    } else if (name === "toggle-token") {
      toggleTokenVisibility(action);
    } else if (name === "clear-token") {
      clearToken();
    }
  }

  function handleDetailInput(event) {
    const target = event.target;
    if (target.matches("[data-token]")) {
      state.token = target.value;
    } else if (target.matches("[data-base-url]")) {
      state.baseUrl = target.value;
    }
    if (target.closest(".console")) {
      scheduleCurl();
      syncSendUI();
    }
  }

  function toggleTokenVisibility(button) {
    const input = document.getElementById("tokenInput");
    if (!input) return;
    const showing = input.type === "text";
    input.type = showing ? "password" : "text";
    button.textContent = showing ? "Show" : "Hide";
    input.focus();
  }

  function clearToken() {
    state.token = "";
    const input = document.getElementById("tokenInput");
    if (input) {
      input.value = "";
      input.focus();
    }
    scheduleCurl();
    syncSendUI();
    toast("Token cleared from this tab.");
  }

  function selectBodyMode(mode) {
    els.detailBody.querySelectorAll("[data-body-mode]").forEach((button) => {
      button.classList.toggle("is-active", button.dataset.bodyMode === mode);
    });
    els.detailBody.querySelectorAll("[data-body-editor]").forEach((editor) => {
      editor.hidden = editor.dataset.bodyEditor !== mode;
    });
    scheduleCurl();
  }

  function addPairRow(group) {
    const list = els.detailBody.querySelector(
      '[data-pair-list="' + group + '"]');
    if (!list) return;
    const label = group === "headers" ? "header" : "query";
    list.insertAdjacentHTML("beforeend", pairRowHtml("", "", false, label));
    const row = list.lastElementChild;
    const nameInput = row && row.querySelector(".pair-name");
    if (nameInput) nameInput.focus();
    scheduleCurl();
  }

  /* ------------------------------------------------------------------ */
  /* curl preview                                                        */
  /* ------------------------------------------------------------------ */

  function scheduleCurl() {
    window.cancelAnimationFrame(curlFrame);
    curlFrame = window.requestAnimationFrame(updateCurl);
  }

  function updateCurl() {
    const code = document.getElementById("curlCode");
    if (!code || !state.selected) return;
    code.textContent = buildCurl(state.selected);
  }

  function buildCurl(op) {
    const pathValues = currentPathValues();
    const query = readPairs("query");
    const url = previewUrl(op, pathValues, query);
    const headers = readPairs("headers", false);
    const hasAuth = Object.keys(headers)
      .some((name) => name.toLowerCase() === "authorization");
    if (currentToken() && !hasAuth) {
      headers.Authorization = "Bearer $PW_TOKEN";
    }
    const body = previewBody(op);
    const hasContentType = Object.keys(headers)
      .some((name) => name.toLowerCase() === "content-type");
    if (body && !hasContentType) headers["Content-Type"] = body.contentType;

    let command = "curl -X " + shellQuote(op.http) + " \\\n  " + shellQuote(url);
    Object.entries(headers).forEach(([name, value]) => {
      const values = Array.isArray(value) ? value : [value];
      values.forEach((item) => {
        command += " \\\n  -H " + shellQuote(name + ": " + item);
      });
    });
    if (body) command += " \\\n  --data-raw " + shellQuote(body.data);
    return command;
  }

  function previewUrl(op, pathValues, query) {
    let path = op.path.replace(/\{([^{}]+)\}/g, (whole, name) => {
      const value = pathValues[name];
      return value ? encodeURIComponent(value) : "{" + name + "}";
    });
    const base = state.baseUrl || (state.catalog.base_urls || [])[0] || "";
    let url = base + path;
    const parts = [];
    Object.entries(query).forEach(([name, value]) => {
      const values = Array.isArray(value) ? value : [value];
      values.forEach((item) => {
        parts.push(encodeURIComponent(name) + "=" + encodeURIComponent(item));
      });
    });
    if (parts.length) url += "?" + parts.join("&");
    return url;
  }

  function previewBody(op) {
    if (!supportsBody(op)) return null;
    const mode = activeBodyMode();
    if (mode === "json") {
      const text = bodyText("json").trim();
      if (!text) return null;
      return { data: text, contentType: "application/json" };
    }
    if (mode === "form") {
      const text = bodyText("form").trim();
      if (!text) return null;
      return {
        data: previewForm(text),
        contentType: "application/x-www-form-urlencoded",
      };
    }
    if (mode === "raw") {
      const text = bodyText("raw");
      if (!text.trim()) return null;
      const typeInput = els.detailBody.querySelector("[data-raw-content-type]");
      return {
        data: text,
        contentType: (typeInput && typeInput.value.trim())
          || "text/plain; charset=utf-8",
      };
    }
    return null;
  }

  function previewForm(text) {
    return text.split(/\r?\n/).map((line) => line.trim()).filter(Boolean)
      .map((line) => {
        const split = line.indexOf("=");
        if (split < 0) return encodeURIComponent(line);
        return encodeURIComponent(line.slice(0, split).trim()) + "="
          + encodeURIComponent(line.slice(split + 1));
      }).join("&");
  }

  function activeBodyMode() {
    const active = els.detailBody.querySelector("[data-body-mode].is-active");
    return active ? active.dataset.bodyMode : "json";
  }

  function bodyText(mode) {
    const editor = els.detailBody.querySelector(
      '[data-body-editor="' + mode + '"] [data-body-input="' + mode + '"]');
    return editor ? editor.value : "";
  }

  function currentPathValues() {
    const values = {};
    els.detailBody.querySelectorAll("[data-path-param]").forEach((input) => {
      values[input.dataset.pathParam] = input.value;
    });
    return values;
  }

  function readPairs(group, allowArrays) {
    const result = {};
    els.detailBody.querySelectorAll(
      '[data-pair-group="' + group + '"] .pair-row').forEach((row) => {
      const nameInput = row.querySelector(".pair-name");
      const valueInput = row.querySelector(".pair-value");
      if (!nameInput || !valueInput) return;
      const name = nameInput.value.trim();
      const value = valueInput.value.trim();
      if (!name || !value) return;
      if (allowArrays === false) result[name] = value;
      else addValue(result, name, value);
    });
    return result;
  }

  function addValue(target, name, value) {
    if (Object.prototype.hasOwnProperty.call(target, name)) {
      if (Array.isArray(target[name])) target[name].push(value);
      else target[name] = [target[name], value];
    } else {
      target[name] = value;
    }
  }

  function shellQuote(value) {
    return "'" + String(value).replace(/'/g, "'\\''") + "'";
  }

  function currentToken() {
    const input = document.getElementById("tokenInput");
    return input ? input.value : state.token;
  }

  function currentAuthorizationValue() {
    let found = "";
    els.detailBody.querySelectorAll(
      '[data-pair-group="headers"] .pair-row').forEach((row) => {
      const nameInput = row.querySelector(".pair-name");
      const valueInput = row.querySelector(".pair-value");
      if (!nameInput || !valueInput) return;
      if (nameInput.value.trim().toLowerCase() === "authorization"
          && valueInput.value.trim()) {
        found = valueInput.value.trim();
      }
    });
    return found;
  }

  function hasUserCredential() {
    return Boolean(currentToken().trim() || currentAuthorizationValue());
  }

  /* ------------------------------------------------------------------ */
  /* Sending                                                             */
  /* ------------------------------------------------------------------ */

  async function sendRequest() {
    if (!state.consent) {
      openConsent();
      return;
    }
    if (state.sending) return;
    const op = state.selected;
    if (!op) return;

    hideConsoleError();
    if (!op.public && !hasUserCredential()) {
      showConsoleError(
        "This operation is not marked public. Add a bearer token for an account "
        + "you are authorized to test, or add an Authorization header.");
      return;
    }
    let payload;
    try {
      payload = buildExecutePayload(op);
    } catch (error) {
      showConsoleError(error.message);
      return;
    }

    const seq = (state.sendSeq || 0) + 1;
    state.sendSeq = seq;
    state.sending = true;
    syncSendUI();
    const controller = new AbortController();
    const timeout = window.setTimeout(() => controller.abort(), 65000);

    try {
      const response = await fetch("/api/execute", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        signal: controller.signal,
      });
      let data;
      try {
        data = await response.json();
      } catch (error) {
        throw new Error("The local proxy returned a non-JSON response.");
      }
      if (!response.ok) {
        throw new Error(data.error || ("Proxy returned HTTP " + response.status));
      }
      if (seq !== state.sendSeq || !state.selected
          || state.selected.id !== op.id) return;
      state.lastResponse = data;
      state.responseTab = data.json !== null && data.json !== undefined
        ? "json" : "raw";
      renderResponse();
    } catch (error) {
      if (seq !== state.sendSeq) return;
      const message = error.name === "AbortError"
        ? "The request timed out after 65 seconds."
        : error.message;
      renderResponseError(message);
    } finally {
      window.clearTimeout(timeout);
      if (seq === state.sendSeq) {
        state.sending = false;
        syncSendUI();
      }
    }
  }

  function buildExecutePayload(op) {
    const path = currentPathValues();
    const missing = pathParameters(op).filter((name) =>
      String(path[name] || "").trim() === "");
    if (missing.length) {
      throw new Error("Fill in path parameter: " + missing.join(", "));
    }

    const payload = {
      operation: op.id,
      base_url: state.baseUrl,
      path: path,
      query: readPairs("query"),
      headers: readPairs("headers", false),
    };
    const token = currentToken();
    if (token) payload.token = token;

    if (supportsBody(op)) {
      const mode = activeBodyMode();
      if (mode === "json") {
        const text = bodyText("json").trim();
        if (text) {
          try {
            payload.body = { mode: "json", value: JSON.parse(text) };
          } catch (error) {
            throw new Error("JSON body is not valid: " + error.message);
          }
        }
      } else if (mode === "form") {
        const text = bodyText("form").trim();
        if (text) {
          payload.body = { mode: "form", value: parseFormLines(text) };
        }
      } else if (mode === "raw") {
        const text = bodyText("raw");
        if (text.trim()) {
          const typeInput = els.detailBody.querySelector("[data-raw-content-type]");
          payload.body = {
            mode: "raw",
            value: text,
            content_type: (typeInput && typeInput.value.trim())
              || "text/plain; charset=utf-8",
          };
        }
      }
    }
    return payload;
  }

  function parseFormLines(text) {
    const result = {};
    text.split(/\r?\n/).forEach((line, index) => {
      const trimmed = line.trim();
      if (!trimmed) return;
      const split = trimmed.indexOf("=");
      if (split < 1) {
        throw new Error("Form line " + (index + 1) + " must be key=value.");
      }
      const key = trimmed.slice(0, split).trim();
      const value = trimmed.slice(split + 1);
      if (!key) throw new Error("Form line " + (index + 1) + " has no key.");
      addValue(result, key, value);
    });
    return result;
  }

  function showConsoleError(message) {
    const node = document.getElementById("consoleError");
    if (!node) return;
    node.textContent = message;
    node.hidden = false;
    const send = els.detailBody.querySelector('[data-action="send"]');
    if (send) send.focus();
  }

  function hideConsoleError() {
    const node = document.getElementById("consoleError");
    if (!node) return;
    node.hidden = true;
    node.textContent = "";
  }

  function syncConsentUI() {
    els.detailBody.querySelectorAll("[data-consent-warning]").forEach((node) => {
      node.hidden = state.consent;
    });
    syncSendUI();
    syncSigninUI();
  }

  function syncSendUI() {
    const op = state.selected;
    const tokenMissing = Boolean(op && !op.public && !hasUserCredential());
    els.detailBody.querySelectorAll('[data-action="send"]').forEach((button) => {
      button.disabled = state.sending || !state.consent || tokenMissing;
      button.textContent = state.sending ? "Sending…" : "Send request";
    });
    els.detailBody.querySelectorAll(".send-note").forEach((note) => {
      if (!state.consent) note.textContent = "Consent required to send.";
      else if (state.sending) note.textContent = "Waiting for the host…";
      else if (tokenMissing) {
        note.textContent = "A bearer token is required for this operation.";
      } else if (op && ["POST", "PUT", "PATCH", "DELETE"].includes(op.http)) {
        note.textContent = "This " + op.http
          + " request may change data. Send only if you are authorized.";
      } else note.textContent = "A real request is sent only on this click.";
    });
  }

  /* ------------------------------------------------------------------ */
  /* Response rendering                                                  */
  /* ------------------------------------------------------------------ */

  function renderResponse() {
    const area = document.getElementById("responseArea");
    const result = state.lastResponse;
    if (!area || !result) return;
    const statusClass = result.status >= 200 && result.status < 300
      ? "status-ok" : result.status >= 300 && result.status < 400
        ? "status-warn" : "status-bad";
    const statusText = result.status
      + (result.reason ? " " + result.reason : "");
    const meta = [
      result.duration_ms + " ms",
      formatBytes(result.bytes),
      result.truncated ? "truncated" : "",
      result.content_type ? result.content_type.split(";")[0] : "",
    ].filter(Boolean).join(" · ");
    const notes = [];
    if (result.truncated) {
      notes.push("The response exceeded the proxy's display limit and was truncated.");
    }
    if (result.binary) {
      notes.push("This looks like a binary response; the body is shown as lossy UTF-8.");
    }
    area.innerHTML =
      '<section class="response-panel">'
      + '<div class="response-head"><span class="status-pill ' + statusClass + '">'
      + escapeHtml(statusText) + '</span>'
      + '<span class="response-meta">' + escapeHtml(meta) + "</span>"
      + '<div class="response-actions">'
      + '<button type="button" class="copy-mini" data-action="copy-response">Copy view</button>'
      + "</div></div>"
      + '<div class="response-url">' + escapeHtml(result.url || "") + "</div>"
      + '<div class="response-tabs" role="tablist" aria-label="Response view">'
      + responseTabButton("json", "JSON", state.responseTab === "json")
      + responseTabButton("raw", "Raw", state.responseTab === "raw")
      + responseTabButton("headers", "Headers", state.responseTab === "headers")
      + "</div>"
      + (notes.length ? '<p class="response-note">' + escapeHtml(notes.join(" ")) + "</p>" : "")
      + '<pre class="response-view" id="responseView"></pre>'
      + "</section>";
    renderResponseView();
  }

  function responseTabButton(name, label, active) {
    return '<button type="button" role="tab" data-response-tab="' + name + '"'
      + (active ? ' class="is-active" aria-selected="true"' : ' aria-selected="false"')
      + ">" + label + "</button>";
  }

  function renderResponseView() {
    const view = document.getElementById("responseView");
    const result = state.lastResponse;
    if (!view || !result) return;
    let text = "";
    if (state.responseTab === "headers") {
      const entries = Object.entries(result.headers || {}).sort((a, b) =>
        a[0].localeCompare(b[0]));
      text = entries.length
        ? entries.map(([name, value]) => name + ": " + value).join("\n")
        : "(no response headers)";
    } else if (state.responseTab === "raw") {
      text = result.text || "(empty response body)";
    } else {
      text = result.json !== null && result.json !== undefined
        ? JSON.stringify(result.json, null, 2)
        : (result.text || "(empty response body)");
    }
    view.textContent = text;
    view.classList.toggle("raw", state.responseTab !== "json");
    els.detailBody.querySelectorAll("[data-response-tab]").forEach((button) => {
      const active = button.dataset.responseTab === state.responseTab;
      button.classList.toggle("is-active", active);
      button.setAttribute("aria-selected", active ? "true" : "false");
    });
  }

  function renderResponseError(message) {
    const area = document.getElementById("responseArea");
    if (!area) return;
    state.lastResponse = null;
    area.innerHTML =
      '<section class="response-panel"><div class="response-head">'
      + '<span class="status-pill status-bad">request failed</span>'
      + '<span class="response-meta">no response</span></div>'
      + '<pre class="response-view">' + escapeHtml(message) + "</pre></section>";
  }

  function copyOperationJson() {
    const op = state.selected;
    if (!op) return;
    const clone = Object.assign({}, op);
    delete clone._haystack;
    copyText(JSON.stringify(clone, null, 2), "Operation JSON copied.");
  }

  /* ------------------------------------------------------------------ */
  /* Small helpers                                                       */
  /* ------------------------------------------------------------------ */

  function supportsBody(op) {
    return op.http !== "GET" && op.http !== "HEAD";
  }

  function pathParameters(op) {
    const names = [];
    const template = op.path || "";
    template.replace(/\{([^{}]+)\}/g, (whole, name) => {
      if (!names.includes(name)) names.push(name);
      return whole;
    });
    return names;
  }

  function namesFor(op, key, binding) {
    const names = [];
    (op[key] || []).forEach((name) => {
      if (name && !names.includes(name)) names.push(name);
    });
    (op.params || []).forEach((param) => {
      if (param.in === binding && param.name && !names.includes(param.name)) {
        names.push(param.name);
      }
    });
    const seen = new Set();
    return names.filter((name) => {
      const canonical = binding === "Header" ? name.toLowerCase() : name;
      if (seen.has(canonical)) return false;
      seen.add(canonical);
      return true;
    });
  }

  function defaultFor(op, name, binding) {
    const param = (op.params || []).find((item) =>
      item.in === binding && item.name === name);
    if (!param || param.default === undefined || param.default === null) return "";
    return String(param.default);
  }

  function methodClass(method) {
    return METHOD_CLASS[method] || "method-post";
  }

  function prettyType(type) {
    if (!type) return "—";
    let text = String(type);
    if (text.startsWith("[")) return prettyType(text.slice(1)) + "[]";
    text = text.replace(/\//g, ".");
    text = text.replace(/L([A-Za-z0-9_$.]+);/g, "$1");
    text = text.replace(/L([A-Za-z0-9_$.]+)</g, "$1<");
    text = text.replace(/^L([A-Za-z0-9_$.]+);/, "$1");
    text = text.replace(/;/g, "");
    if (PRIMITIVES[text]) return PRIMITIVES[text];
    return text;
  }

  function normalizePhone(value) {
    return String(value === null || value === undefined ? "" : value)
      .trim().replace(/[\s().-]/g, "");
  }

  function normalizeCountry(value) {
    const text = String(value === null || value === undefined ? "" : value)
      .trim().replace(/[\s().-]/g, "");
    if (!text) return "";
    return text.startsWith("+") ? text : "+" + text;
  }

  function normalizeOtp(value) {
    return String(value === null || value === undefined ? "" : value)
      .trim().replace(/[\s-]/g, "");
  }

  function validatePhone(phone) {
    if (!phone) return "Enter the phone number.";
    if (phone.startsWith("+")) {
      return "Enter the number without the country code; use the country-code field instead.";
    }
    if (!/^[0-9]{6,15}$/.test(phone)) {
      return "Phone number must contain 6 to 15 digits.";
    }
    return "";
  }

  function validateCountry(country) {
    if (!/^\+[0-9]{1,4}$/.test(country)) {
      return "Country code must be a + sign and 1 to 4 digits.";
    }
    return "";
  }

  function validateOtp(otp) {
    if (!otp) return "Enter the one-time code.";
    if (!/^[0-9]{4,8}$/.test(otp)) {
      return "The one-time code must contain 4 to 8 digits.";
    }
    return "";
  }

  function maskToken(token) {
    const text = String(token || "");
    if (text.length < 12) return "••••••••";
    return text.slice(0, 4) + "…" + text.slice(-4);
  }

  function formatExpiry(seconds) {
    const value = Number(seconds);
    if (!Number.isFinite(value) || value <= 0) return "not reported";
    if (value < 60) return Math.round(value) + " s";
    if (value < 3600) return Math.round(value / 60) + " min";
    if (value < 86400) {
      return (value / 3600).toFixed(1).replace(/\.0$/, "") + " h";
    }
    return (value / 86400).toFixed(1).replace(/\.0$/, "") + " d";
  }

  function escapeHtml(value) {
    if (value === null || value === undefined) return "";
    return String(value).replace(/[&<>"']/g, (character) => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;",
    }[character]));
  }

  function escapeRegExp(value) {
    return String(value).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  }

  function highlightText(text, query) {
    const terms = String(query || "").trim().split(/\s+/)
      .filter(Boolean).slice(0, 8).map((term) => term.slice(0, 64));
    if (!terms.length) return escapeHtml(text);
    const pattern = terms.map(escapeRegExp)
      .sort((a, b) => b.length - a.length).join("|");
    if (!pattern) return escapeHtml(text);
    const matcher = new RegExp("(" + pattern + ")", "gi");
    return String(text).split(matcher).map((part, index) =>
      index % 2 ? "<mark>" + escapeHtml(part) + "</mark>" : escapeHtml(part)
    ).join("");
  }

  function formatBytes(bytes) {
    const count = Number(bytes) || 0;
    if (count < 1024) return count + " B";
    if (count < 1024 * 1024) return (count / 1024).toFixed(1) + " KB";
    return (count / (1024 * 1024)).toFixed(2) + " MB";
  }

  function toast(message) {
    if (!els.toast) return;
    els.toast.textContent = message;
    els.toast.classList.add("is-visible");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => {
      els.toast.classList.remove("is-visible");
    }, 3600);
  }

  async function copyText(text, message) {
    if (!text) return;
    try {
      if (navigator.clipboard && window.isSecureContext) {
        await navigator.clipboard.writeText(text);
      } else {
        throw new Error("Clipboard API unavailable");
      }
      toast(message || "Copied.");
    } catch (error) {
      const textarea = document.createElement("textarea");
      textarea.className = "copy-helper";
      textarea.value = text;
      textarea.setAttribute("readonly", "");
      document.body.appendChild(textarea);
      textarea.select();
      try {
        document.execCommand("copy");
        toast(message || "Copied.");
      } catch (copyError) {
        toast("Copy failed. Select the text manually.");
      }
      textarea.remove();
    }
  }

  init();
})();
