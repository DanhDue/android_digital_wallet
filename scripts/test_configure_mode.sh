#!/usr/bin/env bash
set -Eeuo pipefail

# ============================================================================
# scripts/test_configure_mode.sh — Test suite for scripts/configure_mode.sh
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
TEST_TMP_DIR="$(mktemp -d /tmp/test_configure_mode_XXXXXX)"
trap 'rm -rf "${TEST_TMP_DIR}"' EXIT

PASSED=0
FAILED=0

assert_eq() {
  local expected="$1" actual="$2" msg="$3"
  if [ "${expected}" = "${actual}" ]; then
    echo "  ✅ PASS: ${msg}"
    PASSED=$((PASSED + 1))
  else
    echo "  ❌ FAIL: ${msg}"
    echo "     Expected: ${expected}"
    echo "     Actual:   ${actual}"
    FAILED=$((FAILED + 1))
  fi
}

assert_contains() {
  local needle="$1" file="$2" msg="$3"
  if grep -qF -- "${needle}" "${file}"; then
    echo "  ✅ PASS: ${msg}"
    PASSED=$((PASSED + 1))
  else
    echo "  ❌ FAIL: ${msg}"
    echo "     File ${file} does not contain '${needle}'"
    FAILED=$((FAILED + 1))
  fi
}

assert_not_contains() {
  local needle="$1" file="$2" msg="$3"
  if ! grep -qF -- "${needle}" "${file}"; then
    echo "  ✅ PASS: ${msg}"
    PASSED=$((PASSED + 1))
  else
    echo "  ❌ FAIL: ${msg}"
    echo "     File ${file} unexpectedly contains '${needle}'"
    FAILED=$((FAILED + 1))
  fi
}

echo "==> Running Test Suite for configure_mode.sh..."

# Check existence of configure_mode.sh
if [ ! -f "${ROOT_DIR}/scripts/configure_mode.sh" ]; then
  echo "❌ configure_mode.sh does not exist yet (RED phase expected failure)"
  exit 1
fi

# Set up test sandbox
mkdir -p "${TEST_TMP_DIR}/app"
cp "${ROOT_DIR}/settings.gradle.kts" "${TEST_TMP_DIR}/settings.gradle.kts"
cp "${ROOT_DIR}/app/build.gradle.kts" "${TEST_TMP_DIR}/app/build.gradle.kts"
cp "${ROOT_DIR}/build.gradle.kts" "${TEST_TMP_DIR}/build.gradle.kts"

# Test 1: Invalid mode argument should exit non-zero
echo "--> Test 1: Invalid mode argument"
set +e
"${ROOT_DIR}/scripts/configure_mode.sh" invalid_mode --root-dir="${TEST_TMP_DIR}" >/dev/null 2>&1
EC=$?
set -e
assert_eq 1 "${EC}" "Invalid mode should exit with code 1"

# Test 2: Switch to Lean mode
echo "--> Test 2: Switch to Lean mode"
"${ROOT_DIR}/scripts/configure_mode.sh" lean --root-dir="${TEST_TMP_DIR}"
assert_contains '// include(":features:scanner")' "${TEST_TMP_DIR}/settings.gradle.kts" "DFM scanner should be commented out in settings"
assert_contains '// include(":features:settings:sample")' "${TEST_TMP_DIR}/settings.gradle.kts" "settings:sample should be commented out in settings"
assert_contains '// include(":konsist-test")' "${TEST_TMP_DIR}/settings.gradle.kts" "konsist-test should be commented out in settings"
assert_contains 'include(":features:settings")' "${TEST_TMP_DIR}/settings.gradle.kts" "features:settings should remain active"
assert_contains '// dynamicFeatures += setOf(":features:scanner")' "${TEST_TMP_DIR}/app/build.gradle.kts" "dynamicFeatures should be commented in app"
assert_contains '// id(Deps.BCV_PLUGIN_ID)' "${TEST_TMP_DIR}/build.gradle.kts" "BCV plugin should be commented in root build.gradle.kts"

# Test 3: Idempotency (switching to Lean again shouldn't duplicate comment markers)
echo "--> Test 3: Lean mode idempotency"
"${ROOT_DIR}/scripts/configure_mode.sh" lean --root-dir="${TEST_TMP_DIR}"
assert_not_contains '//// include(":features:scanner")' "${TEST_TMP_DIR}/settings.gradle.kts" "No duplicate comment slashes"

# Test 4: Switch to Plugin mode
echo "--> Test 4: Switch to Plugin mode"
"${ROOT_DIR}/scripts/configure_mode.sh" plugin --root-dir="${TEST_TMP_DIR}"
assert_contains 'include(":plugin")' "${TEST_TMP_DIR}/settings.gradle.kts" "include :plugin should be active"
assert_contains 'include(":sample")' "${TEST_TMP_DIR}/settings.gradle.kts" "include :sample should be active"
assert_contains '// include(":app")' "${TEST_TMP_DIR}/settings.gradle.kts" "host app should be commented out"
assert_contains '// include(":shell")' "${TEST_TMP_DIR}/settings.gradle.kts" "host shell should be commented out"
assert_contains '// include(":packages:core")' "${TEST_TMP_DIR}/settings.gradle.kts" "packages:core should be commented out"

# Test 5: Restore to Enterprise mode
echo "--> Test 5: Restore to Enterprise mode"
"${ROOT_DIR}/scripts/configure_mode.sh" enterprise --root-dir="${TEST_TMP_DIR}"
assert_contains 'include(":app")' "${TEST_TMP_DIR}/settings.gradle.kts" ":app should be active"
assert_contains 'include(":features:scanner")' "${TEST_TMP_DIR}/settings.gradle.kts" ":features:scanner should be active"
assert_contains 'include(":konsist-test")' "${TEST_TMP_DIR}/settings.gradle.kts" ":konsist-test should be active"
assert_contains 'dynamicFeatures += setOf(":features:scanner")' "${TEST_TMP_DIR}/app/build.gradle.kts" "dynamicFeatures active"
assert_contains 'id(Deps.BCV_PLUGIN_ID)' "${TEST_TMP_DIR}/build.gradle.kts" "BCV plugin active"
assert_contains '// include(":plugin")' "${TEST_TMP_DIR}/settings.gradle.kts" ":plugin commented out in enterprise"

# Test 6: Rapid successive switching (Async / Idempotency loop)
echo "--> Test 6: Rapid mode switching loop"
for m in lean plugin enterprise lean enterprise; do
  "${ROOT_DIR}/scripts/configure_mode.sh" "${m}" --root-dir="${TEST_TMP_DIR}" >/dev/null
done
assert_contains 'include(":app")' "${TEST_TMP_DIR}/settings.gradle.kts" "Final state is enterprise (:app included)"
assert_contains 'include(":features:scanner")' "${TEST_TMP_DIR}/settings.gradle.kts" "Final state is enterprise (scanner included)"

# Test 7: Prune flag safeguard when git tree is dirty
echo "--> Test 7: Prune safeguard on dirty tree"
git -C "${TEST_TMP_DIR}" init >/dev/null 2>&1 || true
touch "${TEST_TMP_DIR}/untracked_file.txt"
set +e
"${ROOT_DIR}/scripts/configure_mode.sh" lean --prune --root-dir="${TEST_TMP_DIR}" >/dev/null 2>&1
EC_PRUNE=$?
set -e
assert_eq 1 "${EC_PRUNE}" "Prune on dirty tree without --force must fail"

echo ""
echo "==> Results: ${PASSED} passed, ${FAILED} failed."
[ "${FAILED}" -eq 0 ] || exit 1
