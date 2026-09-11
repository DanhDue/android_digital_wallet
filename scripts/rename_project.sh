#!/usr/bin/env bash
set -Eeuo pipefail   # -E so the ERR trap fires from inside helper functions too

# ============================================================================
# scripts/rename_project.sh — the single post-clone entrypoint.
#
# Usage:
#   scripts/rename_project.sh <app_name> <bundle_id> [<display_name>] [--mode <enterprise|lean|plugin>] [--force] [--dry-run]
#
#   <app_name>      snake_case, ^[a-z][a-z0-9_]*$
#                   Basis for the Gradle rootProject name and the PascalCase
#                   theme/composable identifiers.
#   <bundle_id>     reverse-DNS, ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$
#                   Split into:
#                     vendor prefix = every segment but the last  (e.g. com.acme)
#                     app leaf      = the last segment            (e.g. wallet)
#   <display_name>  optional human-facing name. Title-Cased from <app_name>
#                   when omitted (acme_wallet -> "Acme Wallet").
#   --mode          target project mode: enterprise (default), lean, or plugin.
#   --force         run even when the git working tree is dirty.
#   --dry-run       print every file + substitution that WOULD change, make NO
#                   edits, skip the Gradle self-verify, exit 0.
#
# What it does, in one pass:
#   * git mv the physical  src/**/{kotlin,java}/com/danhdue/**  package trees
#     (history follows) and the META-INF/services/com.danhdue.* registrar file.
#   * Rewrite, uniformly, across *.kt *.kts *.java *.xml *.pro *.md, the
#     konsist-test/ + scripts/ governance *.txt (baseline / boundary whitelist),
#     and the agent-config dotfiles:
#         com.danhdue            -> <vendor prefix>      (com.danhdue.core -> com.acme.core)
#         com/danhdue            -> <vendor prefix as path>
#         androiddigitalwallet   -> <app leaf>           (=> com.danhdue.androiddigitalwallet -> <bundle_id>)
#         android_digital_wallet -> <app_name>           (docs naming the source repo directory)
#         AndroidDigitalWallet   -> <app_name PascalCase>  (theme style, *Theme composable, rootProject.name)
#         DigitalWallet          -> <app_name PascalCase>  (the Application class, ui_kit divider)
#         "Digital Wallet" prose -> <display_name>        (docs only)
#     plus the app_name string resource / AndroidManifest label -> <display_name>.
#   * Self-verify: ./gradlew :konsist-test:test assembleDebug --console=plain .
#     A non-zero exit aborts the script loud and prints a recovery hint.
#
# ---------------------------------------------------------------------------
# NEVER rewritten by this script, by design:
#
#   * .devtool/**            — the SDD epic/feature specs; historical record.
#   * docs/superpowers/**    — vendored agent tooling, not project docs.
#   * bricks/**/__brick__/** — Mason templates. Their `{{package}}` / `com.danhdue`
#                              tokens are placeholders resolved at `mason make`
#                              time, NOT this project's package.
#   * .git/**  and  scripts/rename_project.sh (this file).
#
#   This repo commits NO google-services.json / signing config — the cloning
#   project supplies its own. If you add them later, they are yours to manage;
#   this script would never touch them.
# ---------------------------------------------------------------------------

RECOVERY_HINT='recover the pristine checkout with:  git reset --hard HEAD && git clean -fd'

_abort_hint() {
  local ec=$?
  trap - ERR
  {
    echo ""
    echo "rename_project.sh: ABORTED (exit ${ec}) part-way through the rewrite."
    echo "  The working tree is half-renamed and will not build."
    echo "  ${RECOVERY_HINT}"
    echo "  then fix the cause before re-running."
  } >&2
  exit "${ec}"
}

usage() {
  cat >&2 <<'EOF'
usage: scripts/rename_project.sh <app_name> <bundle_id> [<display_name>] [--mode <enterprise|lean|plugin>] [--force] [--dry-run]

  <app_name>      snake_case, ^[a-z][a-z0-9_]*$
  <bundle_id>     reverse-DNS, ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$
  <display_name>  optional; Title-Cased from <app_name> when omitted
  --mode          target project mode: enterprise (default), lean, or plugin
  --force         run even if the git working tree is dirty
  --dry-run       list the changes, edit nothing, exit 0
EOF
  exit 2
}

# --- locate repo root -------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${ROOT_DIR}"

if [ ! -f settings.gradle.kts ] || [ ! -d buildSrc ]; then
  echo "error: run from a clone of this template — settings.gradle.kts + buildSrc/ not found in ${ROOT_DIR}" >&2
  exit 1
fi

# --- parse args -----------------------------------------------------------
FORCE=0
DRY_RUN=0
MODE="enterprise"
APP_NAME=""
BUNDLE_ID=""
DISPLAY_NAME=""
_pos=0
while [ $# -gt 0 ]; do
  case "$1" in
    --force)   FORCE=1 ;;
    --dry-run) DRY_RUN=1 ;;
    --mode)
      shift
      [ $# -gt 0 ] || { echo "error: --mode requires an argument (enterprise, lean, plugin)" >&2; exit 1; }
      MODE="$1"
      ;;
    --mode=*)
      MODE="${1#--mode=}"
      ;;
    --)        shift; break ;;
    -*)        echo "error: unknown flag: $1" >&2; usage ;;
    *)
      _pos=$((_pos + 1))
      case ${_pos} in
        1) APP_NAME="$1" ;;
        2) BUNDLE_ID="$1" ;;
        3) DISPLAY_NAME="$1" ;;
        *) echo "error: too many positional arguments (got '$1')" >&2; usage ;;
      esac
      ;;
  esac
  shift
done
# any trailing operands after `--`
while [ $# -gt 0 ]; do
  _pos=$((_pos + 1))
  case ${_pos} in
    1) APP_NAME="$1" ;;
    2) BUNDLE_ID="$1" ;;
    3) DISPLAY_NAME="$1" ;;
    *) echo "error: too many positional arguments (got '$1')" >&2; usage ;;
  esac
  shift
done

[ -n "${APP_NAME}" ] && [ -n "${BUNDLE_ID}" ] || usage

# --- validate -----------------------------------------------------------
case "${MODE}" in
  enterprise|lean|plugin) ;;
  *)
    echo "error: unknown mode '${MODE}'. Valid modes are: enterprise, lean, plugin" >&2
    exit 1
    ;;
esac

if ! printf '%s' "${APP_NAME}" | grep -qE '^[a-z][a-z0-9_]*$'; then
  echo "error: <app_name> must be snake_case matching ^[a-z][a-z0-9_]*\$ (got '${APP_NAME}')" >&2
  exit 1
fi
if ! printf '%s' "${BUNDLE_ID}" | grep -qE '^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$'; then
  echo "error: <bundle_id> must be reverse-DNS, e.g. com.acme.wallet (got '${BUNDLE_ID}')" >&2
  exit 1
fi

VENDOR="${BUNDLE_ID%.*}"     # com.acme.wallet -> com.acme
LEAF="${BUNDLE_ID##*.}"      # com.acme.wallet -> wallet
VENDOR_PATH="${VENDOR//.//}" # com.acme -> com/acme

# Deep link scheme and App Links host
NEW_SCHEME="${APP_NAME}"
VENDOR_DOMAIN="$(printf '%s' "${VENDOR}" | awk -F'.' '{for(i=NF;i>1;i--) printf "%s.", $i; print $1}')"
NEW_HOST="app.${VENDOR_DOMAIN}"

# PascalCase (no separators) and Title Case (spaced) forms of <app_name>
APP_PASCAL="$(printf '%s' "${APP_NAME}" | awk -F'_' '{s="";for(i=1;i<=NF;i++)s=s toupper(substr($i,1,1)) substr($i,2);print s}')"
if [ -z "${DISPLAY_NAME}" ]; then
  DISPLAY_NAME="$(printf '%s' "${APP_NAME}" | awk -F'_' '{s="";for(i=1;i<=NF;i++)s=s (i>1?" ":"") toupper(substr($i,1,1)) substr($i,2);print s}')"
fi
if ! printf '%s' "${DISPLAY_NAME}" | grep -qE '^[A-Za-z0-9][A-Za-z0-9 ._()-]*$'; then
  echo "error: <display_name> may only contain letters, digits, spaces and . _ ( ) - (got '${DISPLAY_NAME}')" >&2
  exit 1
fi

# --- template sanity: not already renamed --------------------------------
if ! git grep -qI 'com\.danhdue\.androiddigitalwallet' -- '*.kt' '*.kts' 2>/dev/null; then
  echo "error: 'com.danhdue.androiddigitalwallet' not found in the source tree — already renamed, or not this template?" >&2
  exit 1
fi

# --- clean tree guard (a dry-run never mutates, so it is exempt) --------
if [ "${DRY_RUN}" -ne 1 ] && [ "${FORCE}" -ne 1 ] && [ -n "$(git status --porcelain)" ]; then
  echo "error: git working tree is not clean." >&2
  echo "       Commit or stash first so a botched rename is one 'git reset --hard' away," >&2
  echo "       or re-run with --force (or --dry-run to preview)." >&2
  exit 1
fi

echo "==> rename plan"
echo "    vendor prefix ......  com.danhdue            -> ${VENDOR}"
echo "    app leaf ...........  androiddigitalwallet   -> ${LEAF}"
echo "    bundle id / appId ..  com.danhdue.androiddigitalwallet -> ${BUNDLE_ID}"
echo "    PascalCase name ....  AndroidDigitalWallet   -> ${APP_PASCAL}"
echo "    rootProject.name ...  AndroidDigitalWallet   -> ${APP_PASCAL}"
echo "    display name ........ (app_name / label)     -> ${DISPLAY_NAME}"
echo "    deep link scheme ...  myapp                  -> ${NEW_SCHEME}"
echo "    app link host ......  app.example.com        -> ${NEW_HOST}"
if [ "${DRY_RUN}" -eq 1 ]; then
  echo "    MODE ...............  ${MODE} (--dry-run: no edits, no gradle)"
else
  echo "    MODE ...............  ${MODE}"
fi
echo ""

# --- file set (tracked; excludes are the 'NEVER rewritten' list) --------
_collect_files() {
  git ls-files -- \
      '*.kt' '*.kts' '*.java' '*.xml' '*.pro' '*.md' '*.api' \
      '.aiproject' '.cursorrules' \
      'konsist-test/*.txt' 'scripts/*.txt' \
      '*/META-INF/services/*' \
    | grep -Ev '^(\.devtool/|docs/superpowers/|bricks/)' \
    | grep -vx 'scripts/rename_project.sh' \
    | LC_ALL=C sort -u
}

# literal (non-regex) in-place replace across the file set on stdin
_subst() { # <search> <replace>   (file list on stdin)
  local s="$1" r="$2" f n
  while IFS= read -r f; do
    [ -f "${f}" ] || continue
    if [ "${DRY_RUN}" -eq 1 ]; then
      if grep -Fq -- "${s}" "${f}"; then
        n="$(grep -Fo -- "${s}" "${f}" | wc -l | tr -d ' ')"
        printf '  WOULD EDIT  %-64s  %s  ->  %s   (x%s)\n' "${f}" "${s}" "${r}" "${n}"
      fi
    else
      S="${s}" R="${r}" perl -i -pe 's/\Q$ENV{S}\E/$ENV{R}/g' "${f}"
    fi
  done
}

# regex replace of the prose "Digital Wallet" phrase — docs + code comments
_subst_prose() { # <replacement>
  local r="$1" f
  while IFS= read -r f; do
    case "${f}" in *.md|*.kt|*.kts|.aiproject|.cursorrules) ;; *) continue ;; esac
    [ -f "${f}" ] || continue
    if [ "${DRY_RUN}" -eq 1 ]; then
      if grep -Eiq '(android )?digital wallet' "${f}"; then
        printf '  WOULD EDIT  %-64s  /(Android )?Digital Wallet/i  ->  %s\n' "${f}" "${r}"
      fi
    else
      R="${r}" perl -i -pe 's/\bandroid digital wallet\b/$ENV{R}/gi; s/\bdigital wallet\b/$ENV{R}/gi' "${f}"
    fi
  done
}

# ======================================================================
trap _abort_hint ERR

# --- Phase A: physical renames (git mv) --------------------------------
TMP_TAG="__rename_project_tmp_$$"

while IFS= read -r d; do
  [ -d "${d}" ] || continue
  srcroot="${d%/com/danhdue}"
  dest="${srcroot}/${VENDOR_PATH}"
  if [ "${DRY_RUN}" -eq 1 ]; then
    printf '  WOULD RENAME  %s  ->  %s\n' "${d}" "${dest}"
    [ -d "${d}/androiddigitalwallet" ] && \
      printf '  WOULD RENAME  %s  ->  %s\n' "${dest}/androiddigitalwallet" "${dest}/${LEAF}"
    continue
  fi
  git mv "${d}" "${srcroot}/${TMP_TAG}"
  find "${srcroot}" -maxdepth 1 -type d -name com -empty -exec rmdir {} +
  mkdir -p "$(dirname "${srcroot}/${VENDOR_PATH}")"
  git mv "${srcroot}/${TMP_TAG}" "${dest}"
  # the app-leaf sub-tree, if this src root has one
  if [ -d "${dest}/androiddigitalwallet" ]; then
    git mv "${dest}/androiddigitalwallet" "${dest}/${LEAF}"
  fi
done < <(find . -type d -path '*/src/*/com/danhdue' \
           -not -path './.git/*' -not -path './build/*' -not -path '*/build/*' \
           -not -path './bricks/*' | sed 's|^\./||' | LC_ALL=C sort)

while IFS= read -r f; do
  [ -f "${f}" ] || continue
  b="${f##*/}"; dir="${f%/*}"
  nb="${VENDOR}.${b#com.danhdue.}"
  if [ "${DRY_RUN}" -eq 1 ]; then
    printf '  WOULD RENAME  %s  ->  %s/%s\n' "${f}" "${dir}" "${nb}"
  else
    git mv "${f}" "${dir}/${nb}"
  fi
done < <(find . -type f -path '*/META-INF/services/com.danhdue.*' \
           -not -path './.git/*' -not -path '*/build/*' -not -path './bricks/*' | sed 's|^\./||')

# source files whose *name* carries the old domain (DigitalWalletApp.kt)
while IFS= read -r f; do
  [ -f "${f}" ] || continue
  b="${f##*/}"; dir="${f%/*}"
  nb="${b/DigitalWallet/${APP_PASCAL}}"
  if [ "${DRY_RUN}" -eq 1 ]; then
    printf '  WOULD RENAME  %s  ->  %s/%s\n' "${f}" "${dir}" "${nb}"
  else
    git mv "${f}" "${dir}/${nb}"
  fi
done < <(git ls-files -- '*DigitalWallet*.kt' '*DigitalWallet*.kts' | grep -Ev '^bricks/')

# --- Phase B: content rewrite ----------------------------------------
FILES=()
while IFS= read -r _f; do FILES+=("${_f}"); done < <(_collect_files)
_feed() { printf '%s\n' ${FILES[@]+"${FILES[@]}"}; }

# app-facing display name (must precede the PascalCase pass)
_feed | _subst \
  '<string name="app_name">AndroidDigitalWallet</string>' \
  "<string name=\"app_name\">${DISPLAY_NAME}</string>"

# PascalCase identifiers.  AndroidDigitalWallet first (superset of DigitalWallet).
_feed | _subst 'AndroidDigitalWallet' "${APP_PASCAL}"
_feed | _subst 'DigitalWallet'        "${APP_PASCAL}"

# vendor token — dotted form.  A whole-file (perl -0777) regex so a
# formatter-wrapped fully-qualified name (`com\n  .danhdue\n  .konsist...`)
# is caught too, not just same-line `com.danhdue`.
if [ "${DRY_RUN}" -eq 1 ]; then
  _feed | _subst 'com.danhdue' "${VENDOR}"
else
  _feed | while IFS= read -r f; do
    [ -f "${f}" ] || continue
    S="${VENDOR}" perl -0777 -i -pe 's/\bcom\s*\.\s*danhdue\b/$ENV{S}/g' "${f}"
  done
fi

# vendor token — slashed form (path references in comments / docs) — and app leaf
_feed | _subst 'com/danhdue'          "${VENDOR_PATH}"
_feed | _subst 'androiddigitalwallet' "${LEAF}"

# underscored repo-dir form (docs that name the source repo directory)
_feed | _subst 'android_digital_wallet' "${APP_NAME}"

# deep link scheme & App Links host
_feed | _subst 'const val deepLinkScheme = "myapp"' "const val deepLinkScheme = \"${NEW_SCHEME}\""
_feed | _subst 'const val appLinkHost = "app.example.com"' "const val appLinkHost = \"${NEW_HOST}\""
_feed | _subst 'myapp://' "${NEW_SCHEME}://"
_feed | _subst 'app.example.com' "${NEW_HOST}"

# prose
_feed | _subst_prose "${DISPLAY_NAME}"

# --- Phase B.2: configure mode --------------------------------------
if [ "${DRY_RUN}" -eq 1 ]; then
  echo ""
  echo "  WOULD CONFIGURE MODE  scripts/configure_mode.sh ${MODE}"
else
  echo ""
  echo "==> configure project mode: scripts/configure_mode.sh ${MODE}"
  "${SCRIPT_DIR}/configure_mode.sh" "${MODE}"
fi

# --- Phase C: self-verify ------------------------------------------
if [ "${DRY_RUN}" -eq 1 ]; then
  echo ""
  echo "dry-run complete — no files changed."
  trap - ERR
  exit 0
fi

LEFT="$(git grep -lI 'com\.danhdue' -- '*.kt' '*.kts' '*.java' '*.xml' 2>/dev/null | grep -Ev '^bricks/' || true)"
if [ -n "${LEFT}" ]; then
  echo "warning: 'com.danhdue' still present in source after rewrite:" >&2
  printf '  %s\n' ${LEFT} >&2
fi

LEFT_SCHEME="$(git grep -lI 'myapp://' -- '*.kt' '*.kts' '*.java' '*.xml' 2>/dev/null | grep -Ev '^(\.devtool/|bricks/)' || true)"
if [ -n "${LEFT_SCHEME}" ]; then
  echo "warning: 'myapp://' still present in source after rewrite:" >&2
  printf '  %s\n' ${LEFT_SCHEME} >&2
fi

# Re-point the vendor prefix breaks ktlint / detekt import ordering (imports were
# sorted for `com.danhdue`; `com.<new>` sorts elsewhere). Normalise formatting
# before the gate so the rename leaves a tree that passes `detekt` + `spotlessCheck`
# in one shot, not one that needs a manual `spotlessApply` first.
echo ""
echo "==> normalise formatting: ./gradlew spotlessApply --console=plain"
./gradlew spotlessApply --console=plain

echo ""
case "${MODE}" in
  enterprise)
    echo "==> self-verify: ./gradlew :konsist-test:test detekt spotlessCheck assembleDebug --console=plain"
    ./gradlew :konsist-test:test detekt spotlessCheck assembleDebug --console=plain
    ;;
  lean)
    echo "==> self-verify: ./gradlew detekt spotlessCheck assembleDebug --console=plain"
    ./gradlew detekt spotlessCheck assembleDebug --console=plain
    ;;
  plugin)
    echo "==> self-verify: ./gradlew :plugin:testDebugUnitTest :plugin:assembleRelease --console=plain"
    ./gradlew :plugin:testDebugUnitTest :plugin:assembleRelease --console=plain
    ;;
esac

trap - ERR

# --- done ----------------------------------------------------------
cat <<EOF

Rename complete.

  vendor prefix .... ${VENDOR}
  app leaf ......... ${LEAF}
  applicationId .... ${BUNDLE_ID}
  rootProject.name . ${APP_PASCAL}
  display name ..... ${DISPLAY_NAME}
  deep link scheme . ${NEW_SCHEME}://
  app link host .... ${NEW_HOST}

Not touched (by design): .devtool/, docs/superpowers/, bricks/**/__brick__/**.
This template commits no Firebase / signing config — add your own.

Review the diff, then:  git add -A && git commit -m "chore: rename project from template"
EOF

