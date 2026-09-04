# Template Acceptance

The epic's definition of "done" is not "modules are split" — it is **"a stranger can
clone this template, rename it, add features in both delivery modes, and ship."**
This page is the runbook that proves it, without a device where possible.

## 1. The automated harness — `scripts/acceptance_check.sh`

One script runs the entire non-device acceptance against a **throwaway copy** of the
tracked working tree (your checkout is never touched). It prints a `PASS` / `FAIL`
line per phase and exits non-zero if any phase fails.

```bash
./scripts/acceptance_check.sh              # run it; temp copy is deleted on exit
./scripts/acceptance_check.sh --keep       # keep the temp copy for inspection
./scripts/acceptance_check.sh --tmp /path  # use /path as the temp root
```

Prerequisites: JDK 21, the Android SDK (`local.properties` or `$ANDROID_HOME`),
`mason` (`dart pub global activate mason_cli`) and the Dart SDK on `PATH`.

### What each phase proves

| Phase | Proves |
|------:|--------|
| **0** | A tracked-only copy of the repo builds a clean throwaway git repo (wrapper + `local.properties` overlaid, stale machine-local `mason` lock files dropped). |
| **1** | On the **pristine `com.danhdue` base**: `mason get`, then `mvi_feature --name Payments --delivery install-time` and `mvi_feature --name Kyc --delivery on-demand`. Asserts each module is scaffolded and wired — `settings.gradle.kts`, `buildSrc` registry, `:app` install-time dep for Payments, `:app android.dynamicFeatures` + `:platform AppRoutes` + `dist:on-demand` manifest + a line appended to the **`:app`-owned** `META-INF/services/…FeatureEntry` file for Kyc (no per-module copy) — and that the install-time one did **not** leak into `dynamicFeatures`. |
| **2** | `scripts/rename_project.sh acme_wallet com.acme.wallet "Acme Wallet"`. The script self-verifies (`spotlessApply` → `:konsist-test:test detekt spotlessCheck assembleDebug`) **and** its `.kt` pass repoints the two fresh features' `com.danhdue.*` package imports to `com.acme.*`. Asserts no `com.danhdue` survives and the package dirs physically moved. |
| **3** | The full gate on the renamed tree: `./gradlew :konsist-test:test detekt spotlessCheck testDebugUnitTest assembleDebug bundleDebug` → **BUILD SUCCESSFUL**. |
| **4** | `app-debug.aab` contains the `scanner/dex/**` **and** `kyc/dex/**` splits; the base `app-debug.apk` carries **0** `com/acme/{scanner,kyc}` classes (the splits are genuinely absent from the base). |
| **5** | `remove_feature` is a clean inverse: `remove_feature Payments` + `remove_feature Kyc`, then `:konsist-test:test assembleDebug` → BUILD SUCCESSFUL, every wire point unwound (`AppRoutes`, `OnDemandFeatures`, `strings.xml`, `dynamicFeatures`, `settings.gradle.kts`, `buildSrc`), and nothing outside that wire-point set touched. |

### Brick ↔ rename ordering (why Phase 1 comes before Phase 2)

`rename_project.sh` never edits `bricks/`, and the `__brick__` templates emit the
template's **original** package import prefix (`com.danhdue.core.*`, …). A feature
scaffolded on the pristine base and *then* renamed is fine — the rename's `.kt`
pass fixes the imports. A feature scaffolded *after* a rename keeps `com.danhdue.*`
imports you must fix by hand. The harness (and you) should scaffold first, rename
second. See `TEMPLATE_USAGE.md` §2 and `docs/MASON_GUIDE.md`.

## 2. Captured run

Latest `./scripts/acceptance_check.sh` on this branch (per-phase assertions elided
for brevity; the phase list and the summary are verbatim):

```
  Phase 0: throwaway clone
  ok  : template feature modules present
  ok  : still the un-renamed com.danhdue template
  ok  : mason.yaml + bricks copied
>>> PASS  Phase 0

  Phase 1: scaffold Payments (install-time) + Kyc (on-demand) on the pristine base
  ok  : payments in settings.gradle.kts
  ok  : payments in buildSrc Deps.kt registry
  ok  : payments wired into :app as install-time dep
  ok  : payments is NOT in :app android.dynamicFeatures
  ok  : kyc in :app android.dynamicFeatures
  ok  : kyc is NOT ALSO an install-time :app dep
  ok  : KycRoute forced into :platform AppRoutes
  ok  : kyc FeatureEntry appended to the :app-owned ServiceLoader file
  ok  : scanner FeatureEntry still in the :app-owned ServiceLoader file (aggregation, no clobber)
  ok  : kyc ships NO per-module META-INF/services file
  ok  : kyc dist:on-demand manifest
>>> PASS  Phase 1

  Phase 2: rename_project.sh acme_wallet com.acme.wallet
  ... rename self-verify: ./gradlew spotlessApply
      then :konsist-test:test detekt spotlessCheck assembleDebug  -> BUILD SUCCESSFUL
  ok  : no com.danhdue.* left in the two fresh features
  ok  : payments package physically moved to com/acme
  ok  : kyc package physically moved to com/acme
  ok  : KycRoute still wired after rename
  ok  : app package / applicationId renamed to com.acme.wallet
  ok  : no com.danhdue left in buildable sources
>>> PASS  Phase 2

  Phase 3: full quality gate + bundleDebug on the renamed tree
  + ./gradlew :konsist-test:test detekt spotlessCheck testDebugUnitTest assembleDebug bundleDebug
  BUILD SUCCESSFUL (full gate + bundleDebug)
>>> PASS  Phase 3

  Phase 4: AAB split assertion (scanner + kyc) / base-APK class isolation
  ok  : AAB built at app/build/outputs/bundle/debug/app-debug.aab
  ok  : AAB contains the scanner DFM split (scanner/dex/**)
  ok  : AAB contains the generated kyc on-demand split (kyc/dex/**)
  ok  : base APK built at app/build/outputs/apk/debug/app-debug.apk
  ok  : base APK carries 0 com/acme/{scanner,kyc} classes (n=0)
>>> PASS  Phase 4

  Phase 5: remove_feature teardown is a clean inverse
  ok  : both feature module trees removed
  ok  : both unwired from settings.gradle.kts
  ok  : install-time accessors + Deps/DependencyHandler entries removed
  ok  : kyc removed from :app android.dynamicFeatures
  ok  : PaymentsRoute/KycRoute removed from :platform AppRoutes
  ok  : payments/kyc entry removed from :shell OnDemandFeatures
  ok  : kyc_feature_title removed from :app strings.xml
  ok  : KycFeatureEntry line removed from the :app ServiceLoader file
  ok  : scanner FeatureEntry line kept in the :app ServiceLoader file
  ok  : only the expected wire points changed
  + ./gradlew :konsist-test:test assembleDebug   -> BUILD SUCCESSFUL
>>> PASS  Phase 5

======================================================================
  ACCEPTANCE SUMMARY
======================================================================
PASS  Phase 0 — throwaway clone
PASS  Phase 1 — scaffold Payments (install-time) + Kyc (on-demand) on the pristine base
PASS  Phase 2 — rename_project.sh acme_wallet com.acme.wallet
PASS  Phase 3 — full quality gate + bundleDebug on the renamed tree
PASS  Phase 4 — AAB split assertion (scanner + kyc) / base-APK class isolation
PASS  Phase 5 — remove_feature teardown is a clean inverse
----------------------------------------------------------------------
RESULT: PASS — a stranger can clone, add features (both modes), rename, and ship.
```

Clone → scaffold (both delivery modes) → rename → full gate + `bundleDebug` →
split assertion (both `scanner/dex/**` and `kyc/dex/**` present, 0 split classes
in the base APK) → `remove_feature` teardown — all green, end to end.

## 3. Defects this task surfaced and fixed

The acceptance harness is what exercised, for the first time, **two on-demand
DFMs in one bundle**, a rename over freshly-scaffolded features, and a
`remove_feature` on a renamed project. It found six real defects in Tasks 1-15 /
the bricks; all are fixed:

### E — two on-demand DFMs collided in the App Bundle (architectural)

```
:app:packageDebugBundle → com.android.tools.build.bundletool.model.exceptions.InvalidBundleException:
  Modules 'kyc' and 'scanner' contain entry
  'root/META-INF/services/com.<vendor>.platform.FeatureEntry' with different content.
```

Each on-demand DFM used to ship its own
`src/main/resources/META-INF/services/com.<vendor>.platform.FeatureEntry` naming
its `<Name>FeatureEntry`. bundletool forbids two feature splits carrying the same
root resource with differing bytes, so the template only ever built with **one**
on-demand DFM (`scanner`). Fix (reopens the Task 14 DFM discovery contract):

- the registration file is now **owned by `:app`** —
  `app/src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry` —
  and lists **one FQCN per line** for every on-demand `FeatureEntry`;
- `mvi_feature --delivery on-demand` **appends** its line (idempotent);
  `remove_feature` deletes it (matched by the `.presentation.di.<Name>FeatureEntry`
  suffix, file resolved by search so it works post-rename);
- `features/scanner` no longer ships a per-module copy (its
  `ScannerFeatureEntry.kt` class is unchanged);
- `:shell` drives the `ServiceLoader` iterator **element by element**
  (`installersFrom` in `ShellScreen.kt`, reused by the seeded
  `OnDemandFeatures.loadInstalledFeatureInstallers`) and **skips** any entry
  whose split is not installed (`ServiceConfigurationError` / `LinkageError`),
  instead of a `for`/`asSequence` that would propagate the first one.
  Covered by `shell/src/test/.../DynamicFeatureInstallersTest`.

### A-D, F — small `scripts/` / `bricks/` / `.gitignore` fixes

- `rename_project.sh` now runs `spotlessApply` and gates on `detekt` +
  `spotlessCheck` — re-pointing the vendor prefix breaks ktlint/detekt import
  ordering, which the old self-verify (`assembleDebug` only) never caught.
- `mvi_feature` on-demand: `_rewriteWithoutInject` also drops the now-empty
  primary `constructor()` (`detekt EmptyDefaultConstructor`); the seeded
  `OnDemandFeatures.routes` KDoc is a line comment (`detekt
  CommentOverPrivateProperty`).
- `remove_feature` locates `AppRoutes.kt` / `OnDemandFeatures.kt` by search
  instead of a hard-coded `com/danhdue/**` path (silently missed on a renamed
  project) and now also removes the `<name>_feature_title` split-title string.
- `.mason/bricks.json` and `mason-lock.json` were committed with absolute paths
  into one machine's checkout — they are now git-ignored; `mason get` (already the
  first step in `TEMPLATE_USAGE.md`) regenerates them per clone.

## 4. The one manual step (needs a device / emulator)

Everything above is headless. The runtime install path needs a device once:

```bash
# from the renamed project, after `./gradlew bundleDebug`
bundletool build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=/tmp/app.apks --local-testing --overwrite
bundletool install-apks --apks=/tmp/app.apks     # device or emulator attached
adb shell monkey -p com.acme.wallet 1            # or launch from the launcher
```

Verify by hand:

1. App opens on the **Settings** tab (the default), with **Home / Scanner /
   Settings** in the bottom bar.
2. Tap **Scanner** → a spinner shows while the `scanner` split installs
   (`--local-testing` serves it from local storage) → the Scanner screen appears.
3. Navigate to any **install-time** feature you added (e.g. `Payments`) → it is
   present with no install step.
4. A generated **on-demand** feature (e.g. `Kyc`) installs its split on first
   navigation the same way `scanner` does.

## 5. What CI does automatically

`.github/workflows/ci.yml` runs the Phase 3 gate on every PR
(`:konsist-test:test detekt spotlessCheck testDebugUnitTest assembleDebug
bundleDebug`) and then a **split-existence assertion**: it downloads a pinned,
SHA-256-verified `bundletool` (1.17.2), runs
`build-apks --bundle=…/app-debug.aab --local-testing`, and fails the job unless a
`scanner` dynamic-feature split APK (`splits/scanner-*.apk`) is in the generated
`.apks`. CI does **not** run the rename or the brick flow — that is what
`scripts/acceptance_check.sh` is for.
