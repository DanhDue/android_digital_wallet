# Template Usage

## What this is

A **governed multi-module Android template** for building a super-app: Clean Architecture + MVI,
Jetpack Compose, a Host/Shell composition root, an on-demand Dynamic Feature Module example
(`:features:scanner`), a two-tier DeepLink Router engine, and a Konsist architecture gate
(rules K1–K10) that hard-blocks cross-feature imports and layer violations.

Module surface: `:app` · `:shell` · `:core` · `:framework` · `:network` · `:ui_kit` ·
`:platform` (with `DeepLinkRouter` / `AppDeepLinks`) · `:features:settings` · `:features:scanner` ·
`:libraries:testutils` · `:konsist-test`.

## 1. Rename after clone (the one command)

Run the single post-clone entrypoint. It rewrites the Kotlin package, `applicationId`,
every module `namespace`, the Gradle `rootProject.name`, the `AndroidManifest` label /
`app_name` string, and the doc titles — in one pass — then self-verifies with
`./gradlew :konsist-test:test assembleDebug`.

```bash
./scripts/rename_project.sh acme_wallet com.acme.wallet "Acme Wallet"
git add -A && git commit -m "chore: rename project from template"
```

- `<app_name>` — snake_case (`^[a-z][a-z0-9_]*$`). Basis for `rootProject.name` and the
  PascalCase theme/composable identifiers.
- `<bundle_id>` — reverse-DNS. Split into a **vendor prefix** (all but the last segment,
  e.g. `com.acme`) that replaces `com.danhdue`, and an **app leaf** (the last segment,
  e.g. `wallet`) that replaces `androiddigitalwallet`.
- `<display_name>` — optional; Title-Cased from `<app_name>` when omitted.
- `--force` — run on a dirty tree. `--dry-run` — list every change, edit nothing, exit 0.

**Never rewritten by the script:** `.devtool/`, `docs/superpowers/`,
`bricks/**/__brick__/**` (Mason `{{package}}` placeholders). This repo commits no Firebase /
signing config — add your own.

## 2. Add a feature

```bash
mason get   # once, after clone

# install-time feature module
mason make mvi_feature --name Profile --package com.acme.profile --screen Main

# on-demand Dynamic Feature Module
mason make mvi_feature --name Rewards --package com.acme.rewards --screen Main --delivery on-demand
```

> **Brick ↔ rename ordering:** the `__brick__` templates hardcode the template's original
> package import prefix (its `core` / `platform` / `framework` / `network` packages) and
> `rename_project.sh` never touches `bricks/`. A feature scaffolded *after* the rename
> therefore imports package modules that no longer exist and won't compile. Either run
> `mason make` *before* the rename, or pass `--package <your.vendor>.<feature>` and then
> repoint the generated package `import` lines from the old prefix to your new vendor prefix.

`--name` is PascalCase and `--package` has no default (a bare `--name` drops into an
interactive prompt); `--screen` defaults to `Main`, `--delivery` to `install-time`. The brick
scaffolds `features/<name>/` with `data` / `domain` / `presentation` layers and an
MVI `ViewModel` / `Action` / `State` / `Event` set, and wires it into `settings.gradle.kts`,
the `buildSrc` module registry, and (for `on-demand`) the `:app` `android.dynamicFeatures`
list + a `dist:onDemand` manifest. `mvi_subfeature` adds a screen to an existing feature.
See [docs/MASON_GUIDE.md](../MASON_GUIDE.md).

## 3. Sandbox Development (Standalone Mini App Runners)

Each install-time Mini App feature includes a dedicated `:sample` application module (e.g. `:features:settings:sample`) configured via the `commons.android-sample` plugin:
- **Fast Build & Hot Reload**: Compiles only the target feature and shared packages without assembling `:app` or sibling features.
- **Isolated Run**: Installs with a distinct application ID (`com.<vendor>.<app>.sample.<feature>`).
- **Run Sandbox**:
  ```bash
  ./gradlew :features:settings:sample:assembleDebug
  # or install directly to a connected device/emulator:
  ./gradlew :features:settings:sample:installDebug
  ```

## 4. Contract Governance (BCV)

Shared packages (`:packages:core`, `:packages:platform`, `:packages:network`, `:packages:framework`, `:packages:ui_kit`) have their public ABI strictly tracked:
- **Verify compatibility**: `./gradlew apiCheck` (runs as part of CI Phase 6 and root `:check`).
- **Update signatures**: `./gradlew apiDump` (run when intentionally adding new public API).

## 5. Where the rules live

- **Architecture (authoritative):** [docs/architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md).
  The repo-root `ARCHITECTURE.md` is a redirect.
- **Governance / coding standards:** `PROJECT_RULES.md`, `AGENTS.md`,
  `.agent/rules/CRITICAL_RULES.md`.
- **The gate:** `./gradlew :konsist-test:test` — the K1–K10 rule bodies are in
  `konsist-test/src/test/kotlin/.../konsist/`.
- **Contract gate:** `./gradlew apiCheck` — verifies public declarations against `.api` files.
- **Full quality run:** `./gradlew :konsist-test:test detekt spotlessCheck apiCheck testDebugUnitTest assembleDebug`.

## 6. Acceptance

[docs/getting-started/TEMPLATE_ACCEPTANCE.md](./TEMPLATE_ACCEPTANCE.md) is the end-to-end
acceptance runbook — one script, `scripts/acceptance_check.sh`, drives the whole non-device
flow against a throwaway copy: scaffold a feature in **each** delivery mode on the pristine
base → `rename_project.sh` → full gate + `bundleDebug` → assert the on-demand splits are in
the AAB → `remove_feature` teardown → binary compatibility validation (`apiCheck`). Run it
after any change to the rename script, build plugins, or the Mason bricks. It also documents
the single manual device step (`bundletool --local-testing` install → launch → tap Scanner →
split downloads → screen opens).
