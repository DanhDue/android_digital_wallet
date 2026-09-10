---
id: "task_1_commons_android_sample_plugin"
status: "done"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-11T00:19:30+07:00"
completedAt: "2026-09-11T00:19:30+07:00"
labels: ["architecture", "buildSrc", "gradle", "sample"]
order: "a1"
---

# Task 1: Convention Plugin commons.android-sample

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
In a governed Super App architecture, each Mini App needs a lightweight standalone application runner (`:features:<name>:sample`) to execute in isolation without booting the full `:app` composition root.
To eliminate boilerplate across multiple sample runners, `buildSrc` requires a dedicated convention plugin `commons.android-sample` (`commons.AndroidSampleConventionPlugin`):
- Applies `com.android.application`, `org.jetbrains.kotlin.android`, `org.jetbrains.kotlin.plugin.compose`, `com.google.devtools.ksp`, and `dagger.hilt.android.plugin`.
- Computes standalone `applicationId` as `"${AppConfig.applicationId}.sample.${project.parent?.name ?: project.name}"`.
- Inherits `compileSdk`, `minSdk`, `targetSdk`, and Java 17 / Kotlin JVM options from `AppConfig`.
- Auto-wires shared baseline dependencies required by any standalone Compose + Navigation 3 runner:
  - `:packages:core`
  - `:packages:platform`
  - `:packages:framework`
  - `:packages:ui_kit`
  - AndroidX Activity Compose, Lifecycle ViewModel Compose, Navigation 3 runtime & UI
  - Dagger Hilt Android runtime
  - Timber logging

## Relevant Files & Context Pointers
- `buildSrc/src/main/kotlin/commons/AndroidSampleConventionPlugin.kt` (New convention plugin)
- `buildSrc/build.gradle.kts` (Register plugin ID `commons.android-sample`)
- `buildSrc/src/main/kotlin/Deps.kt` (Add `COMMONS_ANDROID_SAMPLE` ID constant)
- `buildSrc/src/main/kotlin/AppConfig.kt` (Global application SDK configurations)

## Design Rationale
Using a convention plugin aligns directly with the project's existing Gradle architecture (`commons.android-library`, `commons.android-feature`, `commons.android-compose`). A sample runner's `build.gradle.kts` is reduced to 5 lines:
```kotlin
plugins {
    id(Deps.COMMONS_ANDROID_SAMPLE)
}
dependencies {
    implementation(project(":features:<feature_name>"))
}
```

## TDD Checklist
- [x] **RED**:
  - Add plugin registration `commons.android-sample` in `buildSrc/build.gradle.kts` and verify Gradle fails when the implementation class is missing.
- [x] **GREEN**:
  - Implement `commons.AndroidSampleConventionPlugin` configuring Android Application extension, Compose compiler, Hilt, and core dependency bundles.
  - Define `COMMONS_ANDROID_SAMPLE = "commons.android-sample"` in `Deps.kt`.
  - Verify Gradle sync succeeds and convention plugin can be resolved.
- [x] **REFACTOR**:
  - Ensure all packaging options (e.g. 16 KB uncompressed native libs) and namespace derivations follow `AppConfig` conventions.
  - Run `./gradlew :buildSrc:compileKotlin` to guarantee 100% clean compilation.
