#!/usr/bin/env bash
set -euo pipefail

# ============================================================================
# scripts/test_mason_bricks.sh — BDD & TDD Test Suite for Mason Bricks:
#   - native_plugin
#   - add_native_ui
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

PASSED=0
FAILED=0

pass() {
  echo "  ✅ PASS: $1"
  PASSED=$((PASSED + 1))
}

fail() {
  echo "  ❌ FAIL: $1"
  [ -n "${2:-}" ] && echo "     $2"
  FAILED=$((FAILED + 1))
}

echo "==> Running BDD Tests for Mason Bricks (native_plugin & add_native_ui)..."

# Prerequisite check: mason CLI
if ! command -v mason &>/dev/null; then
  echo "error: mason CLI is not installed or not in PATH." >&2
  exit 1
fi

TEMP_DIR="$(mktemp -d /tmp/mason_test_XXXXXX)"
trap 'rm -rf "${TEMP_DIR}"' EXIT

# Test 1: mason get succeeds with mason.yaml
set +e
MASON_GET_OUT="$(cd "${ROOT_DIR}" && mason get 2>&1)"
MASON_GET_EC=$?
set -e

if [ "${MASON_GET_EC}" -eq 0 ]; then
  pass "mason get succeeds with registered bricks in mason.yaml"
else
  fail "mason get succeeds with registered bricks in mason.yaml" "${MASON_GET_OUT}"
fi

# Test 2: Missing required 'name' parameter fails
set +e
MISSING_ARG_OUT="$(cd "${ROOT_DIR}" && mason make native_plugin --package com.danhdue.test --no-hooks --on-conflict overwrite -o "${TEMP_DIR}/missing_name" 2>&1)"
MISSING_ARG_EC=$?
set -e

if [ "${MISSING_ARG_EC}" -ne 0 ] || echo "${MISSING_ARG_OUT}" | grep -qE "error|missing"; then
  pass "Missing required 'name' parameter fails gracefully"
else
  fail "Missing required 'name' parameter fails gracefully" "Output: ${MISSING_ARG_OUT}"
fi

# Test 3: Scaffold Headless Plugin (has_ui = false)
HEADLESS_OUT_DIR="${TEMP_DIR}/headless_plugin"
mkdir -p "${HEADLESS_OUT_DIR}"

set +e
(cd "${ROOT_DIR}" && mason make native_plugin \
  --name biometric_auth \
  --package com.danhdue.biometric \
  --has_ui false \
  --on-conflict overwrite \
  -o "${HEADLESS_OUT_DIR}")
HEADLESS_EC=$?
set -e

if [ "${HEADLESS_EC}" -eq 0 ]; then
  pass "mason make native_plugin --has_ui false exits with code 0"
else
  fail "mason make native_plugin --has_ui false exits with code 0"
fi

PLUGIN_DIR="${HEADLESS_OUT_DIR}/biometric_auth"
if [ ! -d "${PLUGIN_DIR}" ]; then
  PLUGIN_DIR="${HEADLESS_OUT_DIR}"
fi

# Check folder structure for headless
if [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/platform" ] && \
   [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/domain" ] && \
   [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/data" ] && \
   [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/di" ]; then
  pass "Headless plugin generates platform/, domain/, data/, and di/ layers"
else
  fail "Headless plugin generates platform/, domain/, data/, and di/ layers"
fi

if [ ! -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/presentation" ]; then
  pass "Headless plugin omits presentation/ layer"
else
  fail "Headless plugin omits presentation/ layer" "Found presentation directory"
fi

if [ -f "${PLUGIN_DIR}/build.gradle.kts" ] && ! grep -q "compose = true" "${PLUGIN_DIR}/build.gradle.kts"; then
  pass "Headless plugin build.gradle.kts does not enable compose"
else
  fail "Headless plugin build.gradle.kts does not enable compose"
fi

# Test 4: Scaffold UI-Enabled Plugin (has_ui = true)
UI_OUT_DIR="${TEMP_DIR}/ui_plugin"
mkdir -p "${UI_OUT_DIR}"

set +e
(cd "${ROOT_DIR}" && mason make native_plugin \
  --name custom_camera \
  --package com.danhdue.camera \
  --has_ui true \
  --on-conflict overwrite \
  -o "${UI_OUT_DIR}")
UI_EC=$?
set -e

if [ "${UI_EC}" -eq 0 ]; then
  pass "mason make native_plugin --has_ui true exits with code 0"
else
  fail "mason make native_plugin --has_ui true exits with code 0"
fi

CAMERA_DIR="${UI_OUT_DIR}/custom_camera"
if [ ! -d "${CAMERA_DIR}" ]; then
  CAMERA_DIR="${UI_OUT_DIR}"
fi

if [ -d "${CAMERA_DIR}/src/main/kotlin/com/danhdue/camera/presentation" ]; then
  pass "UI plugin generates presentation/ layer"
else
  fail "UI plugin generates presentation/ layer"
fi

if [ -f "${CAMERA_DIR}/build.gradle.kts" ] && grep -q "compose = true" "${CAMERA_DIR}/build.gradle.kts"; then
  pass "UI plugin build.gradle.kts enables compose = true"
else
  fail "UI plugin build.gradle.kts enables compose = true"
fi

# Test 5: Upgrade Headless Plugin with add_native_ui
set +e
(cd "${ROOT_DIR}" && mason make add_native_ui \
  --name biometric_auth \
  --package com.danhdue.biometric \
  --on-conflict overwrite \
  -o "${HEADLESS_OUT_DIR}")
UPGRADE_EC=$?
set -e

if [ "${UPGRADE_EC}" -eq 0 ]; then
  pass "mason make add_native_ui exits with code 0"
else
  fail "mason make add_native_ui exits with code 0"
fi

if [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/presentation" ]; then
  pass "add_native_ui adds presentation/ layer to headless plugin"
else
  fail "add_native_ui adds presentation/ layer to headless plugin"
fi

if [ -f "${PLUGIN_DIR}/build.gradle.kts" ] && grep -q "compose = true" "${PLUGIN_DIR}/build.gradle.kts"; then
  pass "add_native_ui patches build.gradle.kts to enable compose"
else
  fail "add_native_ui patches build.gradle.kts to enable compose"
fi

# Test 6: Verify intact domain and data layers after upgrade
if [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/domain" ] && \
   [ -d "${PLUGIN_DIR}/src/main/kotlin/com/danhdue/biometric/data" ]; then
  pass "add_native_ui preserves domain and data layers"
else
  fail "add_native_ui preserves domain and data layers"
fi

# Test 7: Compile verification in Gradle
SETTINGS_BACKUP="$(mktemp /tmp/settings_backup_XXXXXX)"
cp "${ROOT_DIR}/settings.gradle.kts" "${SETTINGS_BACKUP}"

restore_settings() {
  cp "${SETTINGS_BACKUP}" "${ROOT_DIR}/settings.gradle.kts"
  rm -f "${SETTINGS_BACKUP}"
}
trap 'restore_settings; rm -rf "${TEMP_DIR}"' EXIT

cat >> "${ROOT_DIR}/settings.gradle.kts" <<EOF

include(":test_scaffolded_plugin")
project(":test_scaffolded_plugin").projectDir = file("${CAMERA_DIR}")
EOF

echo "==> Verifying Gradle compilation of scaffolded plugin..."
set +e
GRADLE_COMPILE_OUT="$(cd "${ROOT_DIR}" && ./gradlew :test_scaffolded_plugin:compileDebugKotlin --console=plain 2>&1)"
GRADLE_COMPILE_EC=$?
set -e

if [ "${GRADLE_COMPILE_EC}" -eq 0 ]; then
  pass "Scaffolded plugin compiles cleanly with ./gradlew compileDebugKotlin"
else
  fail "Scaffolded plugin compiles cleanly with ./gradlew compileDebugKotlin" "${GRADLE_COMPILE_OUT}"
fi

echo ""
echo "=== Mason Bricks Test Summary ==="
echo "Passed: ${PASSED}"
echo "Failed: ${FAILED}"

if [ "${FAILED}" -gt 0 ]; then
  exit 1
fi
exit 0
