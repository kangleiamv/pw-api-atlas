#!/usr/bin/env node
/* Pure-helper tests for the PW API Atlas client.
 *
 * The page module is wrapped in an IIFE that calls init() on load. To test the
 * parsing/formatting helpers without a DOM, this script swaps that final init()
 * call for an export object, evaluates the module in the current context, and
 * checks the helpers directly. Run with:
 *
 *     node test_client.js
 */
"use strict";

const fs = require("fs");
const path = require("path");
const vm = require("vm");

const sourcePath = path.join(__dirname, "assets", "app.js");
const marker = "  init();\n})();";
let source = fs.readFileSync(sourcePath, "utf8");
if (!source.includes(marker)) {
  console.error("Could not find the init marker in assets/app.js");
  process.exit(1);
}
source = source.replace(
  marker,
  "  globalThis.__atlas = { prettyType, escapeHtml, highlightText, shellQuote,"
  + " formatBytes, parseFormLines, previewForm, pathParameters, namesFor,"
  + " defaultFor, methodClass, renderSurfaces, normalizePhone,"
  + " normalizeCountry, normalizeOtp, validatePhone, validateCountry,"
  + " validateOtp, maskToken, formatExpiry };\n})();"
);
vm.runInThisContext(source, { filename: sourcePath });
const helpers = globalThis.__atlas;

let failures = 0;
function eq(name, actual, expected) {
  const got = JSON.stringify(actual);
  const want = JSON.stringify(expected);
  if (got !== want) {
    failures += 1;
    console.error("FAIL " + name + "\n  got:  " + got + "\n  want: " + want);
    return;
  }
  console.log("ok   " + name);
}

eq("pretty simple", helpers.prettyType("Lcom/foo/Bar;"), "com.foo.Bar");
eq(
  "pretty nested",
  helpers.prettyType("Lcom/ApiResponse<Ljava/util/List<Lcom/Topic;>;>;"),
  "com.ApiResponse<java.util.List<com.Topic>>"
);
eq("pretty primitive", helpers.prettyType("Z"), "boolean");
eq("pretty array", helpers.prettyType("[Ljava/lang/String;"), "java.lang.String[]");
eq("escape", helpers.escapeHtml("<&\"' >"), "&lt;&amp;&quot;&#39; &gt;");
eq(
  "highlight",
  helpers.highlightText("GET /v3/x<y>", "v3"),
  "GET /<mark>v3</mark>/x&lt;y&gt;"
);
eq("shell quote", helpers.shellQuote("a'b"), "'a'\\''b'");
eq("format bytes", helpers.formatBytes(1536), "1.5 KB");
eq("method class", helpers.methodClass("DELETE"), "method-delete");
eq(
  "path params",
  helpers.pathParameters({ path: "/a/{id}/b/{name}" }),
  ["id", "name"]
);
eq(
  "names union",
  helpers.namesFor(
    {
      query_params: ["a"],
      params: [{ in: "Query", name: "b" }, { in: "Query", name: "a" }],
    },
    "query_params",
    "Query"
  ),
  ["a", "b"]
);
eq(
  "default",
  helpers.defaultFor(
    { params: [{ in: "Query", name: "x", default: "7" }] },
    "x",
    "Query"
  ),
  "7"
);
eq("form parse", helpers.parseFormLines("a=1\nb=two words"), {
  a: "1",
  b: "two words",
});
eq("form preview", helpers.previewForm("a=1\nb=two words"), "a=1&b=two%20words");
eq("phone normalize", helpers.normalizePhone(" 98765-43210. "), "9876543210");
eq("phone keep plus", helpers.normalizePhone("+91 98765 43210"), "+919876543210");
eq("country normalize", helpers.normalizeCountry(" 91 "), "+91");
eq("country keep plus", helpers.normalizeCountry("+44"), "+44");
eq("otp normalize", helpers.normalizeOtp(" 12 34-56 "), "123456");
eq("phone valid", helpers.validatePhone("9876543210"), "");
eq(
  "phone plus rejected",
  helpers.validatePhone("+919876543210"),
  "Enter the number without the country code; use the country-code field instead."
);
eq(
  "phone too short",
  helpers.validatePhone("12345"),
  "Phone number must contain 6 to 15 digits."
);
eq("country valid", helpers.validateCountry("+91"), "");
eq(
  "country invalid",
  helpers.validateCountry("91"),
  "Country code must be a + sign and 1 to 4 digits."
);
eq("otp valid", helpers.validateOtp("123456"), "");
eq(
  "otp invalid",
  helpers.validateOtp("12a4"),
  "The one-time code must contain 4 to 8 digits."
);
eq("token masked", helpers.maskToken("abcdefghijklmnop"), "abcd…mnop");
eq("short token hidden", helpers.maskToken("short"), "••••••••");
eq("expiry seconds", helpers.formatExpiry(30), "30 s");
eq("expiry minutes", helpers.formatExpiry(600), "10 min");
eq("expiry hours", helpers.formatExpiry(3600), "1 h");
eq("expiry days", helpers.formatExpiry(90000), "1 d");
eq("expiry missing", helpers.formatExpiry(null), "not reported");
try {
  helpers.parseFormLines("bad");
  failures += 1;
  console.error("FAIL invalid form line was accepted");
} catch (error) {
  console.log("ok   invalid form line rejected");
}

// Render the excluded-surface cards against the real catalog with a one-node
// document stub. This exercises the generator without needing a DOM library.
const catalog = JSON.parse(
  fs.readFileSync(path.join(__dirname, "..", "pw_api_catalog.json"), "utf8")
);
const grid = { innerHTML: "" };
globalThis.document = {
  getElementById(id) {
    return id === "surfaceGrid" ? grid : null;
  },
};
helpers.renderSurfaces(catalog);
delete globalThis.document;

const surfaceChecks = [
  ["external card", "External URLs"],
  ["runtime card", "Runtime URLs"],
  ["service card", "Other network surfaces"],
  ["external sample", "https://demo8795119.mockable.io/chapters"],
  ["runtime note", "URL supplied at runtime"],
  ["service sample", "https://live-class.penpencil.co/pw-live-class/ws"],
];
surfaceChecks.forEach(([name, needle]) => {
  if (grid.innerHTML.includes(needle)) {
    console.log("ok   " + name);
  } else {
    failures += 1;
    console.error("FAIL " + name + " missing from rendered surfaces");
  }
});
if (/<script/i.test(grid.innerHTML)) {
  failures += 1;
  console.error("FAIL surface renderer emitted a script tag");
}

if (failures) {
  console.error("\n" + failures + " client helper check(s) failed.");
  process.exit(1);
}
console.log("\nPure client helpers passed.");
