# Architectural Improvements & Modernization

This document outlines the recent architectural improvements made to the `android_digital_wallet` project, explaining the "what" and "why" for each change.

## 1. Version Catalogs (`libs.versions.toml`)

### What changed?
We migrated dependency declarations from `buildSrc/src/main/kotlin/Deps.kt` (or hardcoded strings) to a standard Gradle Version Catalog located at `gradle/libs.versions.toml`.

### Why?
- **Standardization**: Version Catalogs are the recommended standard for dependency management in Gradle.
- **Performance**: Unlike `buildSrc`, changes in `libs.versions.toml` do not require a full rebuild of the build logic. This speeds up syncing and build times.
- **Type Safety**: Gradle generates type-safe accessors (e.g., `libs.androidx.core.ktx`) automatically, preventing typos.
- **Centralization**: All versions are defined in one place, making it easy to manage upgrades and ensure consistent versions across modules.
- **Dependabot Support**: Tools like Dependabot and Renovate support version catalogs out-of-the-box for automated dependency updates.

## 2. CI/CD with GitHub Actions

### What changed?
We introduced a Continuous Integration (CI) and Continuous Delivery (CD) pipeline using GitHub Actions, defined in `.github/workflows/ci.yml`.

### Why?
- **Quality Assurance**: Every pull request and push to main branches is automatically verified.
- **Automation**:
    - **Quality Checks**: Runs `./gradlew check` which includes:
        - **Unit Tests**: `testDebugUnitTest`
        - **Linting**: `lintDebug`
        - **Detekt**: Static code analysis for Kotlin.
        - **Spotless**: Code formatting verification.
    - **Building**: Runs `assembleDebug` to verify the app compiles successfully.
- **Consistency**: Ensures the project builds in a clean environment (using JDK 21), eliminating "it works on my machine" issues.

## 3. Composite Builds (`build-logic`)

### What changed?
We refactored the build logic from `buildSrc` into a standard **Composite Build** named `build-logic`. This module contains our convention plugins (e.g., `android-library.gradle.kts` converted to a plugin).

### Why?
- **Build Performance**: `buildSrc` is treated as a single change unit. Any change in `buildSrc` invalidates the entire build cache for the whole project. Composite builds are separate builds; changes in `build-logic` only invalidate tasks that strictly depend on the changed plugin.
- **Encapsulation**: Promotes better separation of concerns. `build-logic` can be developed, tested, and versioned independently if needed.
- **Scalability**: As the project grows, composite builds handle complexity much better than `buildSrc`.

### Build Logic (Composite Build) vs buildSrc

| Feature | `buildSrc` | `build-logic` (Composite Build) |
| :--- | :--- | :--- |
| **Compilation** | Recompiled on *any* change to `buildSrc`. | Recompiled only when the specific plugin changes. |
| **Cache Invalidation** | Invalidates the **entire project's** build cache. | Invalidates only the tasks that depend on the changed logic. |
| **Classpath** | Automatically added to the classpath of **all** modules. | Must be explicitly included and applied. |
| ** separation of Concerns** | Tends to become a "dumping ground" for all build scripts. | Encourages modularization of build logic (e.g., separate plugins for Android, Kotlin, etc.). |
| **Performance** | Poor for large projects due to frequent invalidation. | Excellent, as it behaves like a standalone project. |

**Why we switched:**
`buildSrc` is convenient for small projects but becomes a bottleneck as the project grows. By moving to `build-logic` (a composite build), we ensure that modifying a build script doesn't force a full rebuild of the entire application, significantly saving development time.

### How to use?
1.  **Include the Build**: The `build-logic` module is included in the root `settings.gradle.kts`:
    ```kotlin
    includeBuild("build-logic")
    ```
2.  **Apply Plugins**: In your feature or library modules (e.g., `libraries/framework/build.gradle.kts`), apply the convention plugins using the Version Catalog aliases:
    ```kotlin
    plugins {
        alias(libs.plugins.danhdue.android.library)
        // or
        alias(libs.plugins.danhdue.android.feature)
    }
    ```
    *Note: These plugins automatically configure Android settings, Kotlin options, and common dependencies.*

## 4. Explicit API Mode

### What changed?
We enabled Kotlin's **Explicit API mode** in the `libraries/framework` module.
*Currently set to `warning` mode to avoid breaking the build due to existing violations.*

### Why?
- **Public API Clarity**: Forces developers to explicitly mark classes and functions as `public`, `internal`, or `private`.
- **Library Design**: Crucial for library modules (like `framework`). It ensures we don't accidentally expose internal implementation details to feature modules.
- **Binary Compatibility**: Helps maintain a stable API surface area, which is important effectively for a modularized architecture.
- **Code Quality**: Reduces technical debt by making visibility decisions a conscious choice rather than a default.
