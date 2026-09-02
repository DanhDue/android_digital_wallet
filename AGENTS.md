# AGENTS.md - AndroidDigitalWallet Project Context

## Project Overview
**AndroidDigitalWallet** is a modular Android application built with modern development practices. It uses a multi-module architecture with a strong focus on clean architecture, dependency injection, and centralized build configuration.

## 🏗 Project Structure
The project is organized into the following top-level directories:

- **`:app`**: Main Android application module.
- **`:libraries`**: Shared library modules:
  - **`:framework`**: Core infrastructure, utility classes, base classes, and Room database configurations.
  - **`:jetframework`**: Jetpack Compose specific utilities and base UI components.
  - **`:components`**: Reusable UI components (Compose).
  - **`:testutils`**: Shared testing utilities, mocks, and test rules.
- **`:core`**: The dependency floor — framework-agnostic primitives (coroutines, extensions, prefs, Room, session, use-cases, `DataState` / `NetworkResponse` call-adapter).
- **`:network`**: The HTTP stack — Retrofit / OkHttp / Moshi wiring, interceptors, `apiCall` / `Failure`, Flipper network tooling. Depends only on `:core`.
- **`:data`**: Data layer modules (e.g., `:data:model`, `:data:local`, `:data:remote`, `:data:repository`).
- **`:features`**: Feature modules (e.g., `:features:home`, `:features:dashboard`, `:features:splash`, `:features:settings`).
- **`buildSrc`**: Contains custom Gradle convention plugins and centralized dependency management.

## 🛠 Tech Stack
- **Language**: Kotlin 2.x.
  - **Language Version**: "2.1" (Managed in `AppConfig.kt`).
  - **Plugin Version**: Managed in `Versions.kt` (e.g., `Versions.kotlinVersion`).
- **UI Toolkit**: Jetpack Compose (Material 3).
- **Dependency Injection**: Dagger Hilt.
- **Async & Concurrency**: Kotlin Coroutines & Flow.
- **Database**: Room (using KSP).
- **Networking**: Retrofit, OkHttp, Moshi (JSON parsing).
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

## 📝 Coding Guidelines for Agents
1.  **Dependency Management**: Do not hardcode versions in `build.gradle.kts`. Always use `Versions.kt` and `Deps.kt` in `buildSrc`.
2.  **Kotlin Configuration**:
    - `AppConfig.kotlinVersion` controls the **language/API version** (e.g., "2.1").
    - `Versions.kotlinVersion` controls the **plugin version**.
3.  **Compose**: When enabling Compose, ensure the Compose Compiler plugin (`Deps.ANDROID_COMPOSE_PLUGIN_ID`) is applied.
4.  **KSP**: Use KSP (`com.google.devtools.ksp`) instead of KAPT for annotation processors (Room, Hilt, Moshi).
5.  **Extensions**: Use extension functions in `DependencyHandlerExtensions.kt` (e.g., `addHiltDependencies()`) to keep build files clean.

## ⚠️ Known Issues / Notes
- **Kotlin Plugin Versioning**: The root `build.gradle.kts` applies Kotlin and Compose plugins **without versions** (`apply false`). The version resolution relies on `settings.gradle.kts` or the `buildSrc` classpath to avoid "plugin already on classpath" errors.
- **Hilt in Libraries vs. App**:
    - The `addHiltDependencies()` extension function is shared.
    - **`kspAndroidTest`** configuration is available **ONLY** in Android Library modules. It causes errors in the `:app` module if unconditionally applied.
    - **Fix**: Logic requiring `kspAndroidTest` is currently disabled/commented out in `DependencyHandlerExtensions.kt` or must be handled conditionally.
- **Kotlin 2.0 Migration**: Ensure `AppConfig.kotlinVersion` is set to a simple version string (e.g., "2.0" or "2.1"), NOT a full plugin version (e.g., "2.0.21"), to be compatible with `KotlinVersion.fromVersion()`.
