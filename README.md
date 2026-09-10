# Android Super App Template

A governed, multi-module Android template: Clean Architecture + MVI, Jetpack Compose,
a Host/Shell composition root, an on-demand Dynamic Feature Module example, and a
Konsist architecture gate. Clone it, run one script, start building.

## Table of Contents

- [I. Getting Started](#i-getting-started)
- [II. Architecture](#ii-architecture)
  1. [Clean Architecture](#1-clean-architecture)
  2. [MVI Pattern](#2-mvi-pattern)
  3. [Project Structure](#3-project-structure)
- [III. Tech Stack & Libraries](#iii-tech-stack--libraries)
- [IV. References](#iv-references)
- [V. License](#v-license)

---

## I. Getting Started

### 1. Prerequisites

- **Java JDK**: 17 or higher.
- **Android Studio**: Latest stable or higher.
- **Dart SDK**: only for [Mason][1], the feature-scaffolding tool.

### 2. Rename the project (mandatory first step)

After cloning, run the single post-clone entrypoint to replace the template's
package / applicationId / namespace / rootProject name / app label in one pass:

```bash
./scripts/rename_project.sh acme_wallet com.acme.wallet "Acme Wallet"
git add -A && git commit -m "chore: rename project from template"
```

See **[docs/getting-started/TEMPLATE_USAGE.md](docs/getting-started/TEMPLATE_USAGE.md)**
for the full workflow (adding features, where the rules live).

### 3. Feature scaffolding (Mason)

> **See the [Mason Guide](docs/MASON_GUIDE.md) for full instructions on creating features and screens.**

```bash
dart pub global activate mason_cli
mason get

# install-time feature module
mason make mvi_feature --name Profile --package com.acme.profile --screen Main

# on-demand Dynamic Feature Module
mason make mvi_feature --name Rewards --package com.acme.rewards --screen Main --delivery on-demand
```

> The `__brick__` templates hardcode the template's original package import prefix and
> `rename_project.sh` never touches `bricks/` — run `mason make` before the rename, or pass
> `--package` (as above) and repoint the generated package `import` lines to your vendor prefix.

---

## II. Architecture

This project is built on **Clean Architecture** combined with **MVI Pattern**, organized by **Feature First** structure and fully implemented with Jetpack Compose.

### 1. Clean Architecture

The project is divided into three layers:

- **Presentation**: UI (Compose), ViewModels, States, Events. Depend only on Domain.
- **Domain**: Entities, Use Cases, Repository Interfaces. Pure Kotlin, no Android dependencies.
- **Data**: Repository Implementations, DTOs, API inputs, Database.

### 2. MVI Pattern

We use a Unidirectional Data Flow (UDF):

1.  **UI** fires an **Event** (e.g., `UserClickedButton`).
2.  **ViewModel** processes the Event, often triggering a **Use Case**.
3.  **UseCase** returns data/result.
4.  **ViewModel** reduces the result into a new **State**.
5.  **UI** observes and renders the **State**.

### 3. Feature First Principle

The project is organized by **Feature**, not by Layer.

-   **High Cohesion**: All code for a feature (Data, Domain, Presentation) lives in one `:features:*` module.
-   **Decoupled**: Features never depend on each other — cross-feature traffic goes through `:packages:platform` (`AppRoutes` / `AppEventBus` / `EntryProviderInstaller` / `DeepLinkRouter`).
-   **Scalable**: New features are added as new modules; a Konsist gate (K1–K10) enforces the boundaries.

### 4. Project Structure

```text
<project-root>/
├── app/                  # Thin composition root: Hilt aggregation, NavDisplay, Application, entry Activity
├── shell/                # Host-only tab shell (ShellViewModel + bottom nav + per-tab nested nav); home is a stub page here
├── buildSrc/             # Convention plugins + centralized dependency management (Kotlin DSL)
├── packages/             # Core packages
│   ├── core/             # Dependency floor: DataState/NetworkResponse, DispatcherProvider, extensions, prefs, Room base, SessionManager, Logger
│   ├── framework/        # MviViewModel / MvvmViewModel / BaseViewState + the navigation3 host mechanism
│   ├── network/          # Retrofit / OkHttp / Moshi wiring, interceptors, apiCall / Failure
│   ├── platform/         # Cross-feature seam: AppRoutes, AppEventBus, EntryProviderInstaller, FeatureEntry / FeatureInstaller, DeepLinkRouter / AppDeepLinks
│   └── ui_kit/           # Shared Compose design system + runtime-permission handlers
├── features/
│   ├── settings/         # Real reference feature (theme / locale / profile)
│   └── scanner/          # On-demand Dynamic Feature Module example (com.android.dynamic-feature)
├── libraries/
│   └── testutils/        # Shared test rules and base test classes
├── konsist-test/         # JVM/JUnit architecture gate (rules K1–K10); never shipped in the APK
└── bricks/               # Mason code-generation templates (mvi_feature, mvi_subfeature, ...)
```

Full detail and the module dependency graph: **[docs/architecture/ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md)**.

---

## III. Tech Stack & Libraries

### Core & Architecture
*   [Kotlin][2] - First-class language for Android.
*   [Jetpack Compose][3] - Modern UI toolkit.
*   [Coroutines][4] & [Flow][5] - Asynchronous programming.
*   [Hilt][6] - Dependency Injection.
*   [Navigation Compose][7] - Modern navigation.
*   [Room][8] - Local database.

### Networking & Data
*   [Retrofit][9] - Type-safe HTTP client.
*   [OkHttp][10] - HTTP client & Interceptors.
*   [Moshi][11] - JSON parsing.
*   [Coil][12] - Image loading.

### UI & Utilities
*   [Material 3][13] - Design system.
*   [Timber][14] - Logging.

### Testing
*   [MockK][15] - Mocking library.
*   [Turbine][16] - Flow testing.
*   [Robolectric][17] - Unit testing framework.
*   [JaCoCo][18] - Code coverage.

### Code Quality
*   [Ktlint][19] - Kotlin linter.
*   [Detekt][20] - Static code analysis.
*   [Spotless][21] - Code formatter.

---

## IV. References

*   [Guide to App Architecture][22]
*   [Jetpack Compose Documentation][3]
*   [The Clean Architecture][23]
*   [Setup Jacoco for an Android Multiple Module Project][24]
*   [Change Android Retrofit's Base Url at runtime][25]
*   [Change Android Brightness][26]
*   [Play Youtube videos on Androids][27]
*   [Setup Jacoco for Android Project][28]
*   [Callbacks in Android Application][29]

[0]: https://www.azul.com/downloads/?package=jdk#zulu
[1]: https://github.com/felangel/mason
[2]: https://kotlinlang.org/
[3]: https://developer.android.com/jetpack/compose
[4]: https://kotlinlang.org/docs/coroutines-overview.html
[5]: https://kotlinlang.org/docs/flow.html
[6]: https://dagger.dev/hilt/
[7]: https://developer.android.com/jetpack/compose/navigation
[8]: https://developer.android.com/training/data-storage/room
[9]: https://square.github.io/retrofit/
[10]: https://square.github.io/okhttp/
[11]: https://github.com/square/moshi
[12]: https://coil-kt.github.io/coil/compose/
[13]: https://m3.material.io/
[14]: https://github.com/JakeWharton/timber
[15]: https://mockk.io/
[16]: https://github.com/cashapp/turbine
[17]: http://robolectric.org/
[18]: https://www.eclemma.org/jacoco/
[19]: https://pinterest.github.io/ktlint/
[20]: https://detekt.dev/
[21]: https://github.com/diffplug/spotless
[22]: https://developer.android.com/topic/architecture
[23]: http://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
[24]: https://viblo.asia/p/setup-jacoco-for-an-android-multiple-module-projectclean-architect-project-4dbZNNoqZYM
[25]: https://viblo.asia/p/change-retrofits-base-url-at-runtime-ORNZqDLMK0n
[26]: https://viblo.asia/p/change-android-application-brightness-like-a-boss-djeZ1ok85Wz
[27]: https://viblo.asia/p/how-to-play-youtube-videos-in-an-android-webview-with-just-a-few-lines-of-code-RQqKL9mbZ7z
[28]: https://viblo.asia/p/setup-jacoco-for-android-project-gGJ59zB9KX2
[29]: https://viblo.asia/p/calbacks-trong-ung-dung-android-RnB5pk87lPG

---

## V. License

```
Copyright 2026 DanhDue ExOICTIF

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
