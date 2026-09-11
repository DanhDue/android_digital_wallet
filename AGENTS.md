# AGENTS.md - Android Super App Template — Project Context

## Project Overview
This is a **governed multi-module Android template**: Clean Architecture + MVI, Jetpack Compose,
a Host/Shell composition root, an on-demand Dynamic Feature Module example, and a Konsist
architecture gate. Strong focus on module boundaries, dependency injection, and centralized
build configuration. The authoritative architecture reference is
**[`docs/architecture/ARCHITECTURE.md`](docs/architecture/ARCHITECTURE.md)**, and the complete development process is defined in **[`docs/architecture/EPIC_LIFECYCLE.md`](docs/architecture/EPIC_LIFECYCLE.md)**.

## 🏗 Project Structure

Full detail: **[`docs/architecture/ARCHITECTURE.md`](docs/architecture/ARCHITECTURE.md)** §III.3.
Packages are split into small single-responsibility modules (under `packages/`)
(epic `android_super_app_template`, design §4.1):

- **`:app`**: Thin composition root — Hilt aggregation, `NavDisplay` (navigation3), `Application`, entry `Activity`.
- **`:shell`**: Host-only tab shell — `ShellViewModel`, bottom nav, per-tab nested nav. `home` is a stub page here, not a module.
- **`:packages:core`** (`com.danhdue.core`): The dependency floor — framework-agnostic primitives (coroutines, `DispatcherProvider`, extensions, DataStore/Tink prefs, Room base, `SessionManager`, use-cases, `Logger`, `AppInitializer`, `DataState` / `NetworkResponse` call-adapter). Compose-free; **no project dependencies**.
- **`:packages:framework`** (`com.danhdue.framework`): `MviViewModel` / `MvvmViewModel` / `BaseViewState` + the navigation3 host mechanism (`Navigator`, `NestedNavigator` + `LocalNestedNavigator`). Depends solely on `:packages:core` (`api`).
- **`:packages:network`** (`com.danhdue.network`): The HTTP stack — Retrofit / OkHttp / Moshi wiring, interceptors, `apiCall` / `Failure`, `HttpStatusCode`, token authenticator. Depends only on `:packages:core`.
- **`:packages:ui_kit`** (`com.danhdue.uikit`): Shared Compose design system (`ui/theme`, `ui/widgets`), Compose helpers, runtime-permission handlers. Depends only on `:packages:core`.
- **`:packages:platform`** (`com.danhdue.platform`): Cross-feature seam — `AppRoutes` (shared `NavKey` registry), `AppEventBus` (`SharedFlow<AppEvent>`), `EntryProviderInstaller` + `LocalEntryProviderInstallers`, `FeatureEntry` / `FeatureInstaller` (DFM only), `DeepLinkRouter` / `DeepLinkResolver` / `AppDeepLinks`.
- **`:features:*`** (`com.danhdue.{feature}`): Feature modules, each with `data` / `domain` / `presentation` layers. The template ships `settings` (a real reference feature) and `scanner` (an on-demand Dynamic Feature Module example). Depend only on the package modules — **never on another feature**. `:packages:core` + `:packages:platform` are wired by the `commons.android-feature` convention plugin.
- **`:features:*:sample`** (`com.danhdue.features.{feature}.sample`): Isolated Sandbox APK runners configuring standalone execution testbeds via `commons.android-sample`. Enables local hot-reload and testing of single Mini Apps without assembling `:app` or sibling features.
- **`:libraries:testutils`** (`com.danhdue.libraries.testutils`): Shared testing utilities, mocks, and test rules. The only remaining `libraries/*` module.
- **`:konsist-test`** (`com.danhdue.konsist`): JVM/JUnit architecture-enforcement gate (rules K1–K10). Never shipped in the APK. Run with `./gradlew :konsist-test:test`.
- **`buildSrc`**: Custom Gradle convention plugins (`commons.android-library` / `-compose` / `-feature` / `-sample` / `dagger-hilt`) and centralized dependency management (`Versions.kt`, `Deps.kt`, `Modules` object).

## 🛠 Tech Stack
- **Language**: Kotlin 2.x.
  - **Language Version**: "2.1" (Managed in `AppConfig.kt`).
  - **Plugin Version**: Managed in `Versions.kt` (e.g., `Versions.kotlinVersion`).
- **UI Toolkit**: Jetpack Compose (Material 3).
- **Dependency Injection**: Dagger Hilt.
- **Async & Concurrency**: Kotlin Coroutines & Flow.
- **Database**: Room (using KSP).
- **Networking**: Retrofit, OkHttp, Moshi (JSON parsing).
- **Contract Governance**: JetBrains Binary Compatibility Validator (BCV 0.17.0) for shared package ABI validation (`./gradlew apiCheck` / `apiDump`).
- **Observability**: OpenTelemetry (OTel).
- **Testing**: JUnit 4, Mockk, Robolectric, Turbine.

## ⚙️ Build System & Configuration
The project uses **Gradle Kotlin DSL (`.kts`)** with a centralized configuration strategy located in `buildSrc`:

### Key Configuration Files (`buildSrc/src/main/kotlin/`)
- **`Versions.kt`**: Single source of truth for all library versions.
- **`Deps.kt`**: Definition of library dependency strings and groups.
- **`AppConfig.kt`**: App-wide configuration (`compileSdk`, `minSdk`, `kotlinVersion` for language level).

### Convention Plugins (`buildSrc/src/main/kotlin/commons/`)
The project uses custom convention plugins to reduce boilerplate:
- `commons.android-library`: Applies Android Library, Kotlin, KSP, and common configurations.
- `commons.dagger-hilt`: Sets up Hilt and KSP for Hilt.
- `commons.android-compose`: Sets up Jetpack Compose configurations.
- `commons.android-feature`: The feature-module convention — android-library + Hilt + Compose + quality + navigation, and auto-wires `:core` (the mandatory floor) + `:platform` (the cross-feature seam) + `:libraries:testutils`. Also carries the Gradle guard that fails the sync on a cross-feature dependency.
- `commons.android-sample`: The sandbox application convention — android-application + Hilt + Compose + quality, computes isolated `applicationId` (`*.sample.<feature>`), and wires the target feature as the sole implementation dependency.

## 📝 Coding Guidelines for Agents
1.  **Dependency Management**: Do not hardcode versions in `build.gradle.kts`. Always use `Versions.kt` and `Deps.kt` in `buildSrc`.
2.  **Kotlin Configuration**:
    - `AppConfig.kotlinVersion` controls the **language/API version** (e.g., "2.1").
    - `Versions.kotlinVersion` controls the **plugin version**.
3.  **Compose**: When enabling Compose, ensure the Compose Compiler plugin (`Deps.ANDROID_COMPOSE_PLUGIN_ID`) is applied.
4.  **KSP**: Use KSP (`com.google.devtools.ksp`) instead of KAPT for annotation processors (Room, Hilt, Moshi).
5.  **Extensions**: Use extension functions in `DependencyHandlerExtensions.kt` (e.g., `addHiltDependencies()`) to keep build files clean.
6.  **Public ABI Contracts**: Foundation packages (`core`, `platform`, `network`, `framework`, `ui_kit`) are tracked by Binary Compatibility Validator. If modifying public declarations, mark non-public API with `@InternalApi` or run `./gradlew apiDump` when intentionally expanding contracts. Verify with `./gradlew apiCheck`.

## 🧪 3-Tier Testing Standard
All test suites and implementation workflows across the project follow a standardized 3-tier model driven by a **Tri-Persona System**:
- **Tier A (Unit / Package Tests)**: Driven by the **TDD Master (Dev Persona)**. Isolated logic tests for individual packages and features (`:packages:*`, `:features:*`). Tests MVI State, ViewModel, UseCase, Repository, Parsers, and Guards with mocks.
  - Command: `./gradlew testDebugUnitTest`
- **Tier B (Tooling & Governance Tests)**: Architecture rules and contract validation. AST linting via Konsist K1–K10 (`:konsist-test:test`), Binary Compatibility Validator public ABI checks (`apiCheck`), Detekt code smells, and Spotless formatting.
  - Command: `./gradlew :konsist-test:test apiCheck detekt spotlessCheck`
- **Tier C (Acceptance & App Tests)**: Driven by the **System Integration & E2E Engineer (Integration Persona)**. Integration Flow Tests (`*FlowTest.kt` in `:app` + `:shell`) and automated acceptance harness. Tests whole-app DI graph, navigation flow, Intent cold-start / warm-start, on-demand DFM splits, and project lifecycle.
  - Command: `./scripts/acceptance_check.sh` (along with Host Flow Tests in `testDebugUnitTest`)

### 👥 Tri-Persona Execution Workflow
> [!IMPORTANT]
> **Adversarial Independence Rule**: Phase 1 (QA Red Team) MUST operate completely decoupled from coding. Deriving BDD scenarios must rely strictly on HLD specifications, Use Cases, and Sequence Diagrams — NEVER on implementation code or coding convenience. This prevents confirmation bias and guarantees 100% coverage of edge cases, race conditions, and failure modes.

1. **Expert QA (Red Team Persona)**: Authors exhaustive BDD Scenarios (Gherkin syntax) completely decoupled from coding, categorizing them into `[Tier A - Unit]` vs `[Tier C - Integration]`.
2. **TDD Master (Dev Persona — Tier A)**: Translates unit BDD scenarios into failing tests (RED), writes minimal code to pass (GREEN), refactors cleanly.
3. **System Integration Engineer (Integration Persona — Tier C)**: Translates flow/sequence BDD scenarios into Host App integration tests (`:app` / `:shell`), verifies DI, Navigation, Auth Replay, DFM split loading, and runs `acceptance_check.sh`.

## ⚠️ Known Issues / Notes
- **Kotlin Plugin Versioning**: The root `build.gradle.kts` applies Kotlin and Compose plugins **without versions** (`apply false`). The version resolution relies on `settings.gradle.kts` or the `buildSrc` classpath to avoid "plugin already on classpath" errors.
- **Hilt in Libraries vs. App**:
    - The `addHiltDependencies()` extension function is shared.
    - **`kspAndroidTest`** configuration is available **ONLY** in Android Library modules. It causes errors in the `:app` module if unconditionally applied.
    - **Fix**: Logic requiring `kspAndroidTest` is currently disabled/commented out in `DependencyHandlerExtensions.kt` or must be handled conditionally.
- **Kotlin 2.0 Migration**: Ensure `AppConfig.kotlinVersion` is set to a simple version string (e.g., "2.0" or "2.1"), NOT a full plugin version (e.g., "2.0.21"), to be compatible with `KotlinVersion.fromVersion()`.
