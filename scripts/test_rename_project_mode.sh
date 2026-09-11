#!/usr/bin/env bash
set -uo pipefail

# ============================================================================
# scripts/test_rename_project_mode.sh — BDD tests for --mode integration in rename_project.sh
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
RENAME_SCRIPT="${SCRIPT_DIR}/rename_project.sh"

PASSED=0
FAILED=0

pass() {
  echo "  ✅ PASS: $1"
  PASSED=$((PASSED + 1))
}

fail() {
  echo "  ❌ FAIL: $1"
  echo "     $2"
  FAILED=$((FAILED + 1))
}

echo "Running BDD Tests for scripts/rename_project.sh --mode..."

# Test 1: Help documents --mode
OUT="$("${RENAME_SCRIPT}" --help 2>&1 || true)"
if echo "${OUT}" | grep -q -- '--mode <enterprise|lean|plugin>'; then
  pass "Help message documents --mode <enterprise|lean|plugin>"
else
  fail "Help message documents --mode <enterprise|lean|plugin>" "Output was: ${OUT}"
fi

# Test 2: Invalid mode is rejected
set +e
OUT="$("${RENAME_SCRIPT}" test_app com.test.app --mode invalid_mode 2>&1)"
EC=$?
set -e
if [ "${EC}" -eq 1 ] && echo "${OUT}" | grep -q "error: unknown mode 'invalid_mode'. Valid modes are: enterprise, lean, plugin"; then
  pass "Invalid mode 'invalid_mode' rejected with code 1 and expected error message"
else
  fail "Invalid mode 'invalid_mode' rejected" "Exit code: ${EC}, output: ${OUT}"
fi

# Test 3: Dry run with default mode is enterprise
STATUS_BEFORE="$(cd "${ROOT_DIR}" && git status --porcelain || true)"
OUT="$("${RENAME_SCRIPT}" test_app com.test.app --dry-run 2>&1 || true)"
if echo "${OUT}" | grep -q "MODE ...............  enterprise"; then
  pass "Dry-run with default mode displays enterprise"
else
  fail "Dry-run with default mode displays enterprise" "Output was: ${OUT}"
fi

# Test 4: Dry run with --mode lean
OUT="$("${RENAME_SCRIPT}" test_app com.test.app --mode lean --dry-run 2>&1 || true)"
if echo "${OUT}" | grep -q "MODE ...............  lean"; then
  pass "Dry-run with --mode lean displays lean"
else
  fail "Dry-run with --mode lean displays lean" "Output was: ${OUT}"
fi

# Test 5: Dry run with --mode=plugin (equals syntax)
OUT="$("${RENAME_SCRIPT}" test_app com.test.app --mode=plugin --dry-run 2>&1 || true)"
if echo "${OUT}" | grep -q "MODE ...............  plugin"; then
  pass "Dry-run with --mode=plugin displays plugin"
else
  fail "Dry-run with --mode=plugin displays plugin" "Output was: ${OUT}"
fi

# Test 6: Working tree is unaffected by dry-run
STATUS_AFTER="$(cd "${ROOT_DIR}" && git status --porcelain || true)"
if [ "${STATUS_BEFORE}" = "${STATUS_AFTER}" ]; then
  pass "Git status remains unaffected after dry-runs"
else
  fail "Git status remains unaffected after dry-runs" "Status diff: before vs after"
fi

echo ""
echo "=== Test Summary ==="
echo "Passed: ${PASSED}"
echo "Failed: ${FAILED}"

if [ "${FAILED}" -gt 0 ]; then
  exit 1
fi
exit 0
