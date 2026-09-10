#!/usr/bin/env bash
set -Eeuo pipefail

# ============================================================================
# scripts/acceptance_check.sh — the epic's automated "definition of done".
#
# Proves, without a device, that a stranger can clone this template, add
# features in BOTH delivery modes, rename it, run the full quality gate, ship
# an AAB whose on-demand splits are real, and cleanly remove the features
# again. Design §9 Phase 3 / Task 16.
#
# It never touches your checkout: everything happens in a throwaway copy of the
# tracked working tree under a temp dir (removed on exit unless --keep).
#
# Usage:
#   scripts/acceptance_check.sh [--keep] [--tmp <dir>]
#
#   --keep        do NOT delete the temp working copy on exit (for debugging).
#   --tmp <dir>   use <dir> as the temp root instead of `mktemp -d` (created if
#                 absent; combine with --keep to inspect the result).
#
# Exit status: 0 iff every phase reports PASS. Non-zero if any phase fails.
# Every phase runs regardless of earlier failures so the summary is complete;
# a failing phase is a real defect in Tasks 1-15 or a brick — investigate the
# phase that broke, do not paper over it.
#
# ---------------------------------------------------------------------------
# Phases
#
#   Phase 0  Materialise a throwaway clone: copy the *tracked* working tree
#            into $TMP/repo (git ls-files, existing files only), overlay
#            local.properties + the executable Gradle wrapper, drop any stale
#            machine-local mason lock files, `git init && add -A && commit` so
#            rename_project.sh's clean-tree guard is satisfied.
#
#   Phase 1  Scaffold on the PRISTINE (com.danhdue) base — the brick↔rename
#            ordering that works (TEMPLATE_USAGE.md / MASON_GUIDE.md):
#              * mason get
#              * mvi_feature Payments  --delivery install-time
#              * mvi_feature Kyc       --delivery on-demand
#            Assert both modules exist and are wired (settings.gradle.kts,
#            :app android.dynamicFeatures, :platform AppRoutes, dist:on-demand
#            manifest, ServiceLoader FeatureEntry) and that the install-time
#            one did NOT land in android.dynamicFeatures.
#
#   Phase 2  Rename: `rename_project.sh acme_wallet com.acme.wallet ...`.
#            The script self-verifies (spotlessApply -> :konsist-test:test
#            detekt spotlessCheck assembleDebug) AND its .kt pass repoints the
#            two fresh features' com.danhdue.* package imports to com.acme.*.
#            Assert no `com.danhdue` survives in the feature sources and the
#            packages physically moved.
#
#   Phase 3  Full gate on the renamed tree:
#              ./gradlew :konsist-test:test detekt spotlessCheck \
#                        testDebugUnitTest assembleDebug bundleDebug
#            -> BUILD SUCCESSFUL.
#
#   Phase 4  Split assertion on the AAB + base APK:
#              * app-debug.aab contains `scanner/dex/**`  (DFM split, Task 14)
#              * app-debug.aab contains `kyc/dex/**`      (generated on-demand)
#              * 0 `com/acme/{scanner,kyc}` classes in the base app-debug.apk
#
#   Phase 5  Brick teardown is a clean inverse:
#              * remove_feature Payments ; remove_feature Kyc
#              * ./gradlew :konsist-test:test assembleDebug -> BUILD SUCCESSFUL
#              * `git status --porcelain` empty vs the post-rename baseline
#                (no stray files, every wire point unwound).
#
#   Phase 6  Binary compatibility & contract validation:
#              * ./gradlew apiCheck -> BUILD SUCCESSFUL
#              * verifies public ABI contracts across all 5 shared packages.
# ---------------------------------------------------------------------------

RENAME_APP_NAME="acme_wallet"
RENAME_BUNDLE_ID="com.acme.wallet"
RENAME_DISPLAY_NAME="Acme Wallet"

# --- args ------------------------------------------------------------------
KEEP=0
TMP_ROOT=""
while [ $# -gt 0 ]; do
  case "$1" in
    --keep) KEEP=1 ;;
    --tmp)  shift; [ $# -gt 0 ] || { echo "error: --tmp needs a directory" >&2; exit 2; }; TMP_ROOT="$1" ;;
    --tmp=*) TMP_ROOT="${1#--tmp=}" ;;
    -h|--help) sed -n '3,72p' "$0"; exit 0 ;;
    *) echo "error: unknown argument: $1" >&2; exit 2 ;;
  esac
  shift
done

# --- locate the source repo ---------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
[ -f "${SRC_ROOT}/settings.gradle.kts" ] && [ -d "${SRC_ROOT}/bricks" ] \
  || { echo "error: ${SRC_ROOT} is not this template" >&2; exit 1; }

# --- tools on PATH -----------------------------------------------------
export PATH="${HOME}/.pub-cache/bin:${PATH}"
for _dartdir in "${HOME}/fvm/default/bin" /usr/local/bin /opt/homebrew/bin; do
  if [ -x "${_dartdir}/dart" ]; then export PATH="${_dartdir}:${PATH}"; break; fi
done
command -v mason >/dev/null || { echo "error: mason not found on PATH (need mason_cli)" >&2; exit 1; }
command -v dart  >/dev/null || { echo "error: dart not found on PATH (mason brick hooks need it)" >&2; exit 1; }

# --- temp working copy ------------------------------------------------
if [ -n "${TMP_ROOT}" ]; then
  mkdir -p "${TMP_ROOT}"
  WORK="$(cd "${TMP_ROOT}" && pwd)"
else
  WORK="$(mktemp -d "${TMPDIR:-/tmp}/acceptance_check.XXXXXX")"
fi
REPO="${WORK}/repo"

cleanup() {
  local ec=$?
  if [ "${KEEP}" -eq 1 ]; then
    echo ""
    echo "--keep: left the working copy at ${WORK}"
  else
    rm -rf "${WORK}"
  fi
  exit "${ec}"
}
trap cleanup EXIT

# --- phase runner ---------------------------------------------------
SUMMARY=()
OVERALL=0

run_phase() {
  local num="$1" desc="$2"
  echo ""
  echo "======================================================================"
  echo "  Phase ${num}: ${desc}"
  echo "======================================================================"
  local rc=0
  # Run the phase body in a subshell so an internal failure cannot abort the
  # harness; capture its status and record a PASS/FAIL line.
  ( set -Eeuo pipefail; "phase_${num}" ) || rc=$?
  if [ "${rc}" -eq 0 ]; then
    SUMMARY+=("PASS  Phase ${num} — ${desc}")
    echo ">>> PASS  Phase ${num}"
  else
    SUMMARY+=("FAIL  Phase ${num} — ${desc}  (rc=${rc})")
    echo ">>> FAIL  Phase ${num}  (rc=${rc})"
    OVERALL=1
  fi
}

# assertion helpers — bump PHASE_FAILS instead of relying on errexit inside
# a helper (unreliable). Each phase ends with `return "${PHASE_FAILS}"`.
_ok()          { echo "  ok  : $1"; }
_bad()         { echo "  FAIL: $1"; PHASE_FAILS=$((PHASE_FAILS + 1)); }
_assert()      { if eval "$1"; then _ok "$2"; else _bad "$2"; fi; }
_assert_not()  { if eval "$1"; then _bad "$2"; else _ok "$2"; fi; }
_run()         { echo "  + $*"; "$@"; }
_gradle()      { echo "  + ./gradlew $*"; ./gradlew --console=plain --stacktrace "$@"; }

# ======================================================================
# Phase 0 — throwaway clone
# ======================================================================
phase_0() {
  PHASE_FAILS=0
  mkdir -p "${REPO}"
  echo "  copying working tree (tracked + untracked-not-ignored) -> ${REPO}"
  # Test what `git add -A && commit` would capture: tracked files PLUS untracked
  # files git does not ignore (e.g. a not-yet-committed `:app` resource). Drop
  # index entries whose working-tree file was deleted so rsync never aborts.
  ( cd "${SRC_ROOT}" && { git ls-files -z; git ls-files -z --others --exclude-standard; } \
      | while IFS= read -r -d '' f; do [ -e "${f}" ] && printf '%s\0' "${f}"; done ) \
    | rsync -0 --files-from=- -a "${SRC_ROOT}/" "${REPO}/"

  chmod +x "${REPO}/gradlew"
  # machine-local mason lock files must not leak in from the source tree; a
  # fresh `mason get` (Phase 1) regenerates them for THIS path.
  rm -f "${REPO}/.mason/bricks.json" "${REPO}/mason-lock.json"

  if [ -f "${SRC_ROOT}/local.properties" ]; then
    cp "${SRC_ROOT}/local.properties" "${REPO}/local.properties"
  elif [ -n "${ANDROID_HOME:-}" ]; then
    printf 'sdk.dir=%s\n' "${ANDROID_HOME}" > "${REPO}/local.properties"
  elif [ -n "${ANDROID_SDK_ROOT:-}" ]; then
    printf 'sdk.dir=%s\n' "${ANDROID_SDK_ROOT}" > "${REPO}/local.properties"
  else
    _bad "no local.properties in source and no ANDROID_HOME/ANDROID_SDK_ROOT"
    return "${PHASE_FAILS}"
  fi

  cd "${REPO}"
  rm -rf .git
  git init -q
  git config user.email "acceptance@example.com"
  git config user.name  "Acceptance Harness"
  git add -A
  git commit -qm "base: pristine template copy"

  _assert '[ -d features/scanner ] && [ -d features/settings ]' "template feature modules present"
  _assert 'git grep -qI "com\.danhdue\.androiddigitalwallet" -- "*.kt" "*.kts"' "still the un-renamed com.danhdue template"
  _assert '[ -f mason.yaml ] && [ -d bricks/mvi_feature ]' "mason.yaml + bricks copied"
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 1 — scaffold on the pristine base
# ======================================================================
phase_1() {
  PHASE_FAILS=0
  cd "${REPO}"
  _run mason get

  _run mason make mvi_feature \
    --name Payments --package com.danhdue.payments --screen Main --delivery install-time \
    -o . --on-conflict overwrite < /dev/null

  _run mason make mvi_feature \
    --name Kyc --package com.danhdue.kyc --screen Main --delivery on-demand \
    -o . --on-conflict overwrite < /dev/null

  # install-time feature
  _assert '[ -d features/payments/src/main ]'                             "features/payments/ scaffolded"
  _assert 'grep -q ":features:payments" settings.gradle.kts'              "payments in settings.gradle.kts"
  _assert 'grep -q "featurePayments" buildSrc/src/main/kotlin/Deps.kt'    "payments in buildSrc Deps.kt registry"
  _assert 'grep -q "FEATURE_PAYMENTS" app/build.gradle.kts'              "payments wired into :app as install-time dep"
  _assert_not 'grep -q "\":features:payments\"" app/build.gradle.kts'    "payments is NOT in :app android.dynamicFeatures"

  # on-demand feature
  _assert '[ -d features/kyc/src/main ]'                                  "features/kyc/ scaffolded"
  _assert 'grep -q ":features:kyc" settings.gradle.kts'                   "kyc in settings.gradle.kts"
  _assert 'grep -q "\":features:kyc\"" app/build.gradle.kts'             "kyc in :app android.dynamicFeatures"
  _assert_not 'grep -q "FEATURE_KYC" app/build.gradle.kts'              "kyc is NOT ALSO an install-time :app dep"
  _assert 'grep -q "KycRoute" packages/platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt' \
                                                                         "KycRoute forced into :platform AppRoutes"
  _assert 'grep -q "com.danhdue.kyc.presentation.di.KycFeatureEntry" app/src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry' \
                                                                         "kyc FeatureEntry appended to the :app-owned ServiceLoader file"
  _assert 'grep -q "com.danhdue.scanner.presentation.di.ScannerFeatureEntry" app/src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry' \
                                                                         "scanner FeatureEntry still in the :app-owned ServiceLoader file (aggregation, no clobber)"
  _assert_not '[ -n "$(find features/kyc -path "*META-INF/services*" -print -quit)" ]'  "kyc ships NO per-module META-INF/services file"
  _assert 'grep -Eq "on-demand|onDemand" features/kyc/src/main/AndroidManifest.xml' \
                                                                         "kyc dist:on-demand manifest"
  _assert 'grep -q "\"payments\"" packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt' \
                                                                         "payments registered in :platform AppDeepLinks"
  _assert 'grep -q "\"kyc\"" packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt' \
                                                                         "kyc registered in :platform AppDeepLinks"
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 2 — rename
# ======================================================================
phase_2() {
  PHASE_FAILS=0
  cd "${REPO}"
  # rename_project.sh's clean-tree guard demands a committed tree
  git add -A
  git commit -qm "scaffold: add Payments (install-time) + Kyc (on-demand)"

  _run bash scripts/rename_project.sh "${RENAME_APP_NAME}" "${RENAME_BUNDLE_ID}" "${RENAME_DISPLAY_NAME}"

  _assert_not 'grep -rqI "com\.danhdue" features/payments/src features/kyc/src' \
                                                                         "no com.danhdue.* left in the two fresh features"
  _assert '[ -d features/payments/src/main/kotlin/com/acme/payments ]'   "payments package physically moved to com/acme"
  _assert '[ -d features/kyc/src/main/kotlin/com/acme/kyc ]'             "kyc package physically moved to com/acme"
  _assert 'grep -q "KycRoute" packages/platform/src/main/kotlin/com/acme/platform/AppRoutes.kt' \
                                                                         "KycRoute still wired after rename"
  _assert '[ -d app/src/main/kotlin/com/acme/wallet ] && git grep -qI "com\.acme\.wallet"' \
                                                                         "app package / applicationId renamed to com.acme.wallet"
  # rename_project.sh deliberately never rewrites bricks/ (Mason templates keep
  # the template's original prefix) or the SDD/vendored-tooling trees.
  _assert_not '[ -n "$(git grep -lI "com\.danhdue" -- "*.kt" "*.kts" "*.java" "*.xml" | grep -Ev "^(bricks/|\.devtool/|docs/superpowers/)")" ]' \
                                                                         "no com.danhdue left in buildable sources"

  # freeze this as the post-rename baseline for Phase 5's clean-inverse check
  git add -A
  git commit -qm "rename-applied: acme_wallet / com.acme.wallet"
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 3 — full gate on the renamed tree
# ======================================================================
phase_3() {
  PHASE_FAILS=0
  cd "${REPO}"
  _gradle :konsist-test:test detekt spotlessCheck testDebugUnitTest assembleDebug bundleDebug \
    || _bad "full gate + bundleDebug did not reach BUILD SUCCESSFUL"
  [ "${PHASE_FAILS}" -eq 0 ] && echo "  BUILD SUCCESSFUL (full gate + bundleDebug)"
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 4 — split assertion
# ======================================================================
phase_4() {
  PHASE_FAILS=0
  cd "${REPO}"
  local aab="app/build/outputs/bundle/debug/app-debug.aab"
  local apk="app/build/outputs/apk/debug/app-debug.apk"

  _assert "[ -f '${aab}' ]" "AAB built at ${aab}"
  if [ -f "${aab}" ]; then
    # NB: `unzip -l … | grep -q` under `set -o pipefail` reports the SIGPIPE from
    # unzip, not grep's result — list to a file first, then grep the file.
    unzip -l "${aab}" > "${WORK}/aab.list" 2>/dev/null || true
    _assert "grep -q 'scanner/dex' '${WORK}/aab.list'" "AAB contains the scanner DFM split (scanner/dex/**)"
    _assert "grep -q 'kyc/dex' '${WORK}/aab.list'"     "AAB contains the generated kyc on-demand split (kyc/dex/**)"
  fi

  _assert "[ -f '${apk}' ]" "base APK built at ${apk}"
  if [ -f "${apk}" ]; then
    # a `unzip -l` on the apk won't reveal class names — extract the dex + strings it.
    unzip -p "${apk}" 'classes*.dex' > "${WORK}/base.dex" 2>/dev/null || true
    strings "${WORK}/base.dex" > "${WORK}/base.strings" 2>/dev/null || true
    local n
    n="$(grep -Ec 'Lcom/acme/(scanner|kyc)/' "${WORK}/base.strings" 2>/dev/null || true)"
    _assert "[ '${n:-0}' -eq 0 ]" "base APK carries 0 com/acme/{scanner,kyc} classes (n=${n:-0})"
  fi
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 5 — brick teardown is a clean inverse
# ======================================================================
phase_5() {
  PHASE_FAILS=0
  cd "${REPO}"
  _run mason make remove_feature --name Payments -o . --on-conflict overwrite < /dev/null
  _run mason make remove_feature --name Kyc      -o . --on-conflict overwrite < /dev/null

  # module trees + primary wire points
  _assert_not '[ -d features/payments ] || [ -d features/kyc ]'                   "both feature module trees removed"
  _assert_not 'grep -Eq "features:(payments|kyc)" settings.gradle.kts'           "both unwired from settings.gradle.kts"
  _assert_not 'grep -Eq "FEATURE_(PAYMENTS|KYC)|feature(Payments|Kyc)" app/build.gradle.kts buildSrc/src/main/kotlin/Deps.kt buildSrc/src/main/kotlin/extensions/DependencyHandlerExtensions.kt' \
                                                                                 "install-time accessors + Deps/DependencyHandler entries removed"
  _assert_not 'grep -q "\":features:kyc\"" app/build.gradle.kts'                 "kyc removed from :app android.dynamicFeatures"

  # on-demand wire points that must also be unwound (glob for the real package dir
  # since the project has been renamed away from com.danhdue)
  _assert_not 'grep -Rql --include=AppRoutes.kt -E "PaymentsRoute|KycRoute" packages/platform/src/main/kotlin' \
                                                                                 "PaymentsRoute/KycRoute removed from :platform AppRoutes"
  _assert_not 'grep -Rql --include=AppDeepLinks.kt -E "\"payments\"|\"kyc\"" packages/platform/src/main/kotlin' \
                                                                                 "payments/kyc removed from :platform AppDeepLinks"
  _assert_not 'grep -Rql --include=OnDemandFeatures.kt -E "\"payments\"|\"kyc\"" shell/src/main/kotlin' \
                                                                                 "payments/kyc entry removed from :shell OnDemandFeatures"
  _assert_not 'grep -REql "(payments|kyc)_feature_title" app/src/main/res' \
                                                                                 "kyc_feature_title removed from :app strings.xml"
  _assert_not 'grep -Rq "kyc.presentation.di.KycFeatureEntry" app/src/main/resources/META-INF/services' \
                                                                                 "KycFeatureEntry line removed from the :app ServiceLoader file"
  _assert 'grep -Rq "scanner.presentation.di.ScannerFeatureEntry" app/src/main/resources/META-INF/services' \
                                                                                 "scanner FeatureEntry line kept in the :app ServiceLoader file"

  # nothing unrelated was touched — every changed path must be a removed feature
  # tree or one of remove_feature's known wire points.
  git add -A
  local stray
  stray="$(git status --porcelain \
    | sed 's/^...//' \
    | grep -Ev '^(features/(payments|kyc)/|app/build\.gradle\.kts$|settings\.gradle\.kts$|buildSrc/src/main/kotlin/(Deps\.kt$|extensions/DependencyHandlerExtensions\.kt$)|packages/platform/src/main/kotlin/.*/(AppRoutes|deeplink/AppDeepLinks)\.kt$|shell/src/main/kotlin/.*/OnDemandFeatures\.kt$|app/src/main/res/values/strings\.xml$|app/src/main/resources/META-INF/services/[^/]*\.platform\.FeatureEntry$)' \
    || true)"
  if [ -n "${stray}" ]; then
    echo "  FAIL: remove_feature touched files outside its wire-point set:"
    printf '    %s\n' "${stray}"
    PHASE_FAILS=$((PHASE_FAILS + 1))
  else
    _ok "only the expected wire points changed"
  fi

  _gradle :konsist-test:test assembleDebug || _bad "gate after teardown did not reach BUILD SUCCESSFUL"
  [ "${PHASE_FAILS}" -eq 0 ] && echo "  BUILD SUCCESSFUL (gate after teardown)"
  return "${PHASE_FAILS}"
}

# ======================================================================
# Phase 6 — binary compatibility & contract validation (apiCheck)
# ======================================================================
phase_6() {
  PHASE_FAILS=0
  cd "${REPO}"
  _gradle apiCheck || _bad "apiCheck failed to validate public ABI contracts"
  [ "${PHASE_FAILS}" -eq 0 ] && echo "  BUILD SUCCESSFUL (binary compatibility validation)"
  return "${PHASE_FAILS}"
}

# ======================================================================
main() {
  echo "acceptance_check.sh — throwaway copy at ${WORK}"
  run_phase 0 "throwaway clone"
  run_phase 1 "scaffold Payments (install-time) + Kyc (on-demand) on the pristine base"
  run_phase 2 "rename_project.sh acme_wallet com.acme.wallet"
  run_phase 3 "full quality gate + bundleDebug on the renamed tree"
  run_phase 4 "AAB split assertion (scanner + kyc) / base-APK class isolation"
  run_phase 5 "remove_feature teardown is a clean inverse"
  run_phase 6 "binary compatibility & contract validation (apiCheck)"
}
main

echo ""
echo "======================================================================"
echo "  ACCEPTANCE SUMMARY"
echo "======================================================================"
printf '%s\n' "${SUMMARY[@]}"
echo "----------------------------------------------------------------------"
if [ "${OVERALL}" -eq 0 ]; then
  echo "RESULT: PASS — a stranger can clone, add features (both modes), rename, and ship."
else
  echo "RESULT: FAIL — see the phase that broke above (real defect; do not paper over)."
fi
exit "${OVERALL}"
