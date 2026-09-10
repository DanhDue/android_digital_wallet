---
id: "task_2_settings_sample_app"
status: "todo"
priority: "high"
assignee: null
epic: "sandbox_and_contract_governance"
dueDate: null
created: "2026-09-10T23:53:00+07:00"
modified: "2026-09-10T23:53:00+07:00"
completedAt: null
labels: ["sandbox", "settings", "exemplar", "compose"]
order: "a2"
---

# Task 2: Exemplar Sandbox Implementation :features:settings:sample

Epic: [sandbox_and_contract_governance](../epic/sandbox_and_contract_governance/sandbox_and_contract_governance.en.md)

## Requirement Analysis
Provide the reference implementation of a Mini App Sandbox runner for the `:features:settings` module:
- Create subproject `:features:settings:sample` using `commons.android-sample`.
- Register `:features:settings:sample` in `settings.gradle.kts`.
- Implement `SettingsSampleApp` (`@HiltAndroidApp` subclassing `CoreApplication`).
- Implement `SettingsSampleActivity` (`@AndroidEntryPoint` hosting `NavDisplay` with `SettingsRoute`).
- Aggregate `Set<EntryProviderInstaller>` provided by `SettingsNavigationModule`.
- Provide graceful fallback when a user triggers an out-of-module action (e.g. Deeplink or external navigation) by logging or showing a visual Toast notification instead of crashing.

## Relevant Files & Context Pointers
- `features/settings/sample/build.gradle.kts` (Sample build script)
- `features/settings/sample/src/main/AndroidManifest.xml` (Sample manifest with Launcher intent)
- `features/settings/sample/src/main/kotlin/com/danhdue/features/settings/sample/SettingsSampleApp.kt`
- `features/settings/sample/src/main/kotlin/com/danhdue/features/settings/sample/SettingsSampleActivity.kt`
- `features/settings/sample/src/main/res/values/strings.xml`
- `settings.gradle.kts` (Include `:features:settings:sample`)
- `features/settings/src/main/kotlin/com/danhdue/settings/di/SettingsNavigationModule.kt` (Target entry provider)

## Design Rationale
The sample runner serves as a living exemplar for all future Mini Apps. It proves that a feature module's Compose UI and MVI ViewModel can run in an isolated process with mocked host context. Developers can build and launch it via Android Studio in seconds without touching `:app`.

## TDD Checklist
- [ ] **RED**:
  - Add `include(":features:settings:sample")` to `settings.gradle.kts` and create empty `features/settings/sample/build.gradle.kts`.
  - Verify Gradle configuration fails until source files and Manifest are provided.
- [ ] **GREEN**:
  - Create `AndroidManifest.xml` declaring `SettingsSampleApp` and `SettingsSampleActivity` with `MAIN`/`LAUNCHER` intent filter.
  - Implement `SettingsSampleApp` initializing Timber.
  - Implement `SettingsSampleActivity` setting up `remember { Navigator(SettingsRoute) }` and `NavDisplay` bound to `LocalEntryProviderInstallers`.
  - Run `./gradlew :features:settings:sample:assembleDebug` and confirm `app-debug.apk` is generated under `features/settings/sample/build/outputs/apk/debug/`.
- [ ] **REFACTOR**:
  - Verify `aapt dump badging` on the sample APK confirms `package: name='com.danhdue.androiddigitalwallet.sample.settings'`.
  - Confirm Spotless and Detekt pass cleanly on the new module.
