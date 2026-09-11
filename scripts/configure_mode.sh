#!/usr/bin/env bash
set -Eeuo pipefail

# ============================================================================
# scripts/configure_mode.sh — Switch project between 3 modes:
#   1. enterprise  (Default: Full Super App, DFM, Konsist, BCV, 12 modules)
#   2. lean        (Standalone MVP App, fast build, 9 modules, no DFM/Konsist/BCV)
#   3. plugin      (Flutter Plugin Native Devbed: :plugin + :sample only, pure Dagger 2)
#
# Usage:
#   scripts/configure_mode.sh <enterprise|lean|plugin> [--prune] [--force] [--root-dir=<path>]
# ============================================================================

usage() {
  cat >&2 <<'EOF'
Usage: scripts/configure_mode.sh <enterprise|lean|plugin> [--prune] [--force] [--root-dir=<path>]

Modes:
  enterprise  Enable all 12 modules (DFM :features:scanner, :konsist-test, BCV active)
  lean        Disable DFM, Konsist, and BCV for fast standalone builds (9 modules active)
  plugin      Enable only :plugin and :sample for Flutter Plugin native development

Options:
  --prune            Physically remove unneeded module directories for the selected mode
  --force            Proceed with --prune even if git working tree is dirty
  --root-dir=<path>  Specify alternate repository root directory (e.g. for testing)
  -h, --help         Show this help message
EOF
  exit 1
}

# --- Parse arguments --------------------------------------------------------
MODE=""
PRUNE=0
FORCE=0
CUSTOM_ROOT=""

while [ $# -gt 0 ]; do
  case "$1" in
    enterprise|lean|plugin)
      if [ -n "${MODE}" ]; then
        echo "error: multiple modes specified ('${MODE}' and '$1')" >&2
        usage
      fi
      MODE="$1"
      ;;
    --prune)
      PRUNE=1
      ;;
    --force)
      FORCE=1
      ;;
    --root-dir=*)
      CUSTOM_ROOT="${1#*=}"
      ;;
    --root-dir)
      shift
      [ $# -gt 0 ] || usage
      CUSTOM_ROOT="$1"
      ;;
    -h|--help)
      usage
      ;;
    *)
      echo "error: invalid argument '$1'. Valid modes are: enterprise, lean, plugin" >&2
      usage
      ;;
  esac
  shift
done

[ -n "${MODE}" ] || usage

# --- Locate repo root -------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -n "${CUSTOM_ROOT}" ]; then
  ROOT_DIR="${CUSTOM_ROOT}"
else
  ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
fi

SETTINGS_FILE="${ROOT_DIR}/settings.gradle.kts"
APP_BUILD_FILE="${ROOT_DIR}/app/build.gradle.kts"
ROOT_BUILD_FILE="${ROOT_DIR}/build.gradle.kts"

if [ ! -f "${SETTINGS_FILE}" ]; then
  echo "error: settings.gradle.kts not found in ${ROOT_DIR}" >&2
  exit 1
fi

echo "==> Configuring Android Template Mode: [${MODE}]"

# --- Helper functions for safe regex patching --------------------------------
comment_line() {
  local mod="$1" file="$2"
  [ -f "${file}" ] || return 0
  perl -i -pe "s{^\s*include\(\s*\"\Q$mod\E\"\s*\)}{// include(\"$mod\")}" "${file}"
}

uncomment_line() {
  local mod="$1" file="$2"
  [ -f "${file}" ] || return 0
  perl -i -pe "s{^\s*//\s*include\(\s*\"\Q$mod\E\"\s*\)}{include(\"$mod\")}" "${file}"
}

comment_dfm() {
  local file="$1"
  [ -f "${file}" ] || return 0
  perl -i -pe 's{^\s*dynamicFeatures\s*\+=\s*setOf\(":features:scanner"\)}{    // dynamicFeatures += setOf(":features:scanner")}' "${file}"
}

uncomment_dfm() {
  local file="$1"
  [ -f "${file}" ] || return 0
  perl -i -pe 's{^\s*//\s*dynamicFeatures\s*\+=\s*setOf\(":features:scanner"\)}{    dynamicFeatures += setOf(":features:scanner")}' "${file}"
}

comment_bcv() {
  local file="$1"
  [ -f "${file}" ] || return 0
  perl -i -pe 's{^\s*id\(Deps\.BCV_PLUGIN_ID\)\s*apply\s*true}{// id(Deps.BCV_PLUGIN_ID) apply true}' "${file}"
  perl -0777 -i -pe 's{^\s*(apiValidation\s*\{[\s\S]*?\n\})}{/* [BCV_DISABLED]\n$1\n*/}m' "${file}"
}

uncomment_bcv() {
  local file="$1"
  [ -f "${file}" ] || return 0
  perl -i -pe 's{^\s*//\s*id\(Deps\.BCV_PLUGIN_ID\)\s*apply\s*true}{    id(Deps.BCV_PLUGIN_ID) apply true}' "${file}"
  perl -0777 -i -pe 's{/\*\s*\[BCV_DISABLED\]\s*\n(apiValidation\s*\{[\s\S]*?\n\})\s*\n\*/}{$1}m' "${file}"
}

ensure_plugin_modules_declared() {
  local file="$1"
  if ! grep -qF 'include(":plugin")' "${file}"; then
    cat >> "${file}" <<'EOF'

// ── Flutter Plugin Native Devbed modules ────────────────────────────────────
// include(":plugin")
// include(":sample")
EOF
  fi
}

ensure_plugin_modules_declared "${SETTINGS_FILE}"

# --- Apply Mode Transitions -------------------------------------------------
case "${MODE}" in
  enterprise)
    # 1. settings.gradle.kts: activate all 12 modules
    uncomment_line ":app" "${SETTINGS_FILE}"
    uncomment_line ":packages:core" "${SETTINGS_FILE}"
    uncomment_line ":packages:network" "${SETTINGS_FILE}"
    uncomment_line ":packages:platform" "${SETTINGS_FILE}"
    uncomment_line ":packages:framework" "${SETTINGS_FILE}"
    uncomment_line ":packages:ui_kit" "${SETTINGS_FILE}"
    uncomment_line ":shell" "${SETTINGS_FILE}"
    uncomment_line ":libraries:testutils" "${SETTINGS_FILE}"
    uncomment_line ":features:settings" "${SETTINGS_FILE}"
    uncomment_line ":features:settings:sample" "${SETTINGS_FILE}"
    uncomment_line ":features:scanner" "${SETTINGS_FILE}"
    uncomment_line ":konsist-test" "${SETTINGS_FILE}"

    # Deactivate plugin modules in enterprise app
    comment_line ":plugin" "${SETTINGS_FILE}"
    comment_line ":sample" "${SETTINGS_FILE}"

    # 2. app/build.gradle.kts: activate DFM scanner split
    uncomment_dfm "${APP_BUILD_FILE}"

    # 3. root build.gradle.kts: activate BCV
    uncomment_bcv "${ROOT_BUILD_FILE}"
    ;;

  lean)
    # 1. settings.gradle.kts: keep 9 core modules active, comment heavy enterprise modules
    uncomment_line ":app" "${SETTINGS_FILE}"
    uncomment_line ":packages:core" "${SETTINGS_FILE}"
    uncomment_line ":packages:network" "${SETTINGS_FILE}"
    uncomment_line ":packages:platform" "${SETTINGS_FILE}"
    uncomment_line ":packages:framework" "${SETTINGS_FILE}"
    uncomment_line ":packages:ui_kit" "${SETTINGS_FILE}"
    uncomment_line ":shell" "${SETTINGS_FILE}"
    uncomment_line ":libraries:testutils" "${SETTINGS_FILE}"
    uncomment_line ":features:settings" "${SETTINGS_FILE}"

    # Comment out DFM, sample runner, and Konsist
    comment_line ":features:scanner" "${SETTINGS_FILE}"
    comment_line ":features:settings:sample" "${SETTINGS_FILE}"
    comment_line ":konsist-test" "${SETTINGS_FILE}"

    # Deactivate plugin modules in lean app
    comment_line ":plugin" "${SETTINGS_FILE}"
    comment_line ":sample" "${SETTINGS_FILE}"

    # 2. app/build.gradle.kts: comment out DFM scanner split
    comment_dfm "${APP_BUILD_FILE}"

    # 3. root build.gradle.kts: comment out BCV
    comment_bcv "${ROOT_BUILD_FILE}"
    ;;

  plugin)
    # 1. settings.gradle.kts: comment out all host modules
    comment_line ":app" "${SETTINGS_FILE}"
    comment_line ":packages:core" "${SETTINGS_FILE}"
    comment_line ":packages:network" "${SETTINGS_FILE}"
    comment_line ":packages:platform" "${SETTINGS_FILE}"
    comment_line ":packages:framework" "${SETTINGS_FILE}"
    comment_line ":packages:ui_kit" "${SETTINGS_FILE}"
    comment_line ":shell" "${SETTINGS_FILE}"
    comment_line ":libraries:testutils" "${SETTINGS_FILE}"
    comment_line ":features:settings" "${SETTINGS_FILE}"
    comment_line ":features:settings:sample" "${SETTINGS_FILE}"
    comment_line ":features:scanner" "${SETTINGS_FILE}"
    comment_line ":konsist-test" "${SETTINGS_FILE}"

    # Activate plugin modules
    uncomment_line ":plugin" "${SETTINGS_FILE}"
    uncomment_line ":sample" "${SETTINGS_FILE}"

    # 2. app/build.gradle.kts: disable DFM
    comment_dfm "${APP_BUILD_FILE}"

    # 3. root build.gradle.kts: disable BCV
    comment_bcv "${ROOT_BUILD_FILE}"
    ;;
esac

# --- Optional Prune handling ------------------------------------------------
if [ "${PRUNE}" -eq 1 ]; then
  echo "==> Pruning unneeded module directories on disk..."
  
  if [ -d "${ROOT_DIR}/.git" ] && [ "${FORCE}" -ne 1 ]; then
    DIRTY="$(git -C "${ROOT_DIR}" status --porcelain 2>/dev/null || true)"
    if [ -n "${DIRTY}" ]; then
      echo "error: git working tree is dirty. Refusing to --prune without --force." >&2
      exit 1
    fi
  fi

  case "${MODE}" in
    lean)
      echo "  Removing features/scanner, features/settings/sample, konsist-test..."
      rm -rf "${ROOT_DIR}/features/scanner" \
             "${ROOT_DIR}/features/settings/sample" \
             "${ROOT_DIR}/konsist-test"
      ;;
    plugin)
      echo "  Removing host app modules..."
      rm -rf "${ROOT_DIR}/app" \
             "${ROOT_DIR}/shell" \
             "${ROOT_DIR}/features" \
             "${ROOT_DIR}/konsist-test"
      ;;
    enterprise)
      echo "  Enterprise mode preserves all modules (nothing pruned)."
      ;;
  esac
fi

echo "✅ Mode '${MODE}' configured successfully."
