# 🧱 Mason Code Generation Guide

This guide explains how to use the custom [Mason](https://github.com/felangel/mason) bricks to automate code generation for the **MVI Clean Architecture** project.

These bricks handle boiler-plate code, module registration, dependency injection, and clean layer structure automatically.

## Table of Contents

- [🛠 Prerequisites](#-prerequisites)
- [🚀 Available Commands](#-available-commands)
- [1. Create a New Feature Module (`mvi_feature`)](#1-create-a-new-feature-module-mvi_feature)
- [2. Add a Screen to a Module (`mvi_subfeature`)](#2-add-a-screen-to-a-module-mvi_subfeature)
- [3. Remove a Feature Module (`remove_feature`)](#3-remove-a-feature-module-remove_feature)
- [4. Remove a Subfeature (`remove_subfeature`)](#4-remove-a-subfeature-remove_subfeature)
- [⚡️ Workflow Example](#️-workflow-example)

---

## 🛠 Prerequisites

1.  **Install Flutter/Dart SDK** (Required for Mason):
    ```bash
    # On macOS (Homebrew)
    brew tap dart-lang/dart
    brew install dart
    ```
    *Alternatively, you can install [Flutter](https://flutter.dev/docs/get-started/install) which includes the Dart SDK.*

2.  **Install Mason CLI** (if not already installed):
    ```bash
    dart pub global activate mason_cli
    ```
3.  **Add Bricks Locally** (run from project root):
    ```bash
    mason get
    ```
    *This registers the bricks defined in `mason.yaml`.*

---

## 🚀 Available Commands

| Command | Description |
| :--- | :--- |
| **`mvi_feature`** | Creates a new **Android Feature Module**. |
| **`mvi_subfeature`** | Adds a new **Screen/Subfeature** to an existing module. |
| **`remove_feature`** | Removes a **Feature Module** and its configs. |
| **`remove_subfeature`** | Removes a **Screen/Subfeature** and its files. |

---

## 1. Create a New Feature Module (`mvi_feature`)

Generates a fully configured Android module with `data`, `domain`, and `presentation` layers and auto-wires it into the host **the governance way** — the generated module never edits another feature's files.

### Command
```bash
# install-time (default)
mason make mvi_feature \
  --name Payment \
  --package com.danhdue.payment \
  --screen Home

# on-demand Dynamic Feature Module
mason make mvi_feature \
  --name Kyc \
  --package com.danhdue.kyc \
  --screen Main \
  --delivery on-demand
```

### Variables

| Variable | Description | Default | Example |
|---|---|---|---|
| `name` | Feature name (PascalCase) | – | `Payment` |
| `package` | Base package path | – | `com.danhdue.payment` |
| `screen` | Initial screen name (PascalCase) | `Main` | `Home` |
| `delivery` | `install-time` \| `on-demand` | `install-time` | `on-demand` |

An unset or unrecognised `delivery` value falls back to `install-time` with a warning.

> **After `rename_project.sh`:** the `__brick__` templates hardcode the template's original
> infra import prefix (`core` / `platform` / `framework` / `network`) and the rename script
> never rewrites `bricks/`. Scaffold features *before* renaming, or pass
> `--package <your.vendor>.<feature>` and repoint the generated infra `import` lines to your
> vendor prefix.

### `--delivery install-time` (default)

The module is a plain `com.android.library` feature. The hook:

1. **Generates files** — `features/payment/` with the full Clean Architecture tree.
2. **Wires the module** into:
   * `settings.gradle.kts` — `include(":features:payment")`
   * `buildSrc/.../Deps.kt` — `Modules.featurePayment`
   * `buildSrc/.../DependencyHandlerExtensions.kt` — `FEATURE_PAYMENT` accessor
   * `app/build.gradle.kts` — `import` + `FEATURE_PAYMENT`
   * `shell/build.gradle.kts` — same `FEATURE_PAYMENT` (**guarded**: skipped until `:shell` exists)
3. **Leaves `:platform` `AppRoutes` untouched** — the feature's `*Route : NavKey` stays private.
4. Navigation entries reach the host through the generated
   `PaymentNavigationModule` (`@Module` providing `@Provides @IntoSet EntryProviderInstaller`) — Hilt multibinding does the rest.
5. Runs a Gradle sync.

### `--delivery on-demand` (Dynamic Feature Module)

The module becomes a `com.android.dynamic-feature` split. On top of `settings.gradle.kts`, the hook:

* **Rewrites `features/kyc/build.gradle.kts`** to apply `com.android.dynamic-feature`; the dependency direction is inverted — the module gets `implementation(project(":app"))` and `:app` never sees it at compile time.
* **Registers it in `:app`** — `android { dynamicFeatures += setOf(":features:kyc") }` (Konsist rule K8 reads this list to exempt the module from the "no host imports" rule).
* **Rewrites the manifest** with `<dist:module dist:onDemand="true">` + a split-title string resource.
* **Generates `KycFeatureEntry : com.danhdue.platform.FeatureEntry`** plus `src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry` so the host can load the entry at runtime through `ServiceLoader`.
* **Forces the route constant into `:platform`** — appends `@Serializable data object KycRoute : NavKey` to `AppRoutes` (a DFM host cannot import the feature, so its route must live in `:platform`).
* Drops a **guarded `// TODO(task_14)` install-branch marker** into `:shell` (skipped until `:shell` exists).

> **Not done by the brick:** the `SplitInstallManager` / `SplitCompat` runtime and the `:shell` install branch that actually downloads the split are owned by **Task 14**. Until then the split is still bundled by `assembleDebug`, so an `on-demand` module compiles and runs like an install-time one. The Hilt Gradle plugin does not support `com.android.dynamic-feature`, so the generated DFM keeps `hilt-android` on the compile classpath only (no Hilt processing) — DI is Task 14's job.

### Generated Structure
```text
features/payment/src/main/kotlin/
├── data/           # RepositoryImpl, DTOs, Mappers, DI
├── domain/         # Entities, UseCases, Repo Interface, DI
└── presentation/   # ViewModels, UI, State, Events, Navigation DI
# on-demand only, additionally:
├── <Name>FeatureEntry.kt
└── ../resources/META-INF/services/com.danhdue.platform.FeatureEntry
```

---

## 2. Add a Screen to a Module (`mvi_subfeature`)

Adds a new sub-feature (screen) with all Clean Architecture layers to an existing module.

### Command
```bash
mason make mvi_subfeature \
  --module payment \
  --name TransactionDetail
```

### Parameters:
*   `--module`: The folder name of the feature (snake_case), e.g., `payment`, `my_wallet`.
*   `--name`: Name of the new screen (PascalCase), e.g., `TransactionDetail`.

### What it does:
Generates **14 files** across all layers:

*   **Data**: `Dto`, `Mapper`, `RepositoryImpl`, `DataModule` (DI)
*   **Domain**: `Entity`, `UseCase`, `Repository` (Interface), `DomainModule` (DI)
*   **Presentation**: `Screen`, `ViewModel`, `State`, `Event`, `Action`, `UiModel`

*Note: The package name is automatically derived as `com.danhdue.{module}`.*

---

## 3. Remove a Feature Module (`remove_feature`)

Completely removes a feature module and cleans up all project configurations.

### Command
```bash
mason make remove_feature --name Payment
```

### What it does:
1.  **Deletes**: `features/payment/` folder.
2.  **Cleans Configs**: Removes references from `settings.gradle.kts`, `Deps.kt`, `DependencyHandlerExtensions.kt`, and `app/build.gradle.kts`.
3.  **Syncs Project**: Runs Gradle sync to ensure a clean state.

---

## 4. Remove a Subfeature (`remove_subfeature`)

Removes all files associated with a specific subfeature/screen.

### Command
```bash
mason make remove_subfeature \
  --module payment \
  --name TransactionDetail
```

### What it does:
Deletes the specific files and folders generated by `mvi_subfeature` in Data, Domain, and Presentation layers.

---

## ⚡️ Workflow Example

**Scenario**: You want to build a "Trends" feature with a list screen and a detail screen.

1.  **Create the Module**:
    ```bash
    mason make mvi_feature --name Trends --package com.danhdue.trends --screen TrendingList
    ```
    *(Creates `features/trends` with `TrendingList` screen)*

2.  **Add Configuration**:
    *   Wait for Gradle Sync to finish.

3.  **Add Detail Screen**:
    ```bash
    mason make mvi_subfeature --module trends --name TrendDetail
    ```
    *(Adds `TrendDetail` files to data, domain, and presentation layers)*

4.  **Mistake? Remove Detail Screen**:
    ```bash
    mason make remove_subfeature --module trends --name TrendDetail
    ```
