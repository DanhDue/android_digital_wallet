---
id: "task_5_scaffold_sample_runner_app"
status: "todo"
priority: "medium"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["presentation", "testbed", "runner", "sample"]
order: "a5"
---

# Task 5: Scaffold Standalone Testbed Runner :sample Application

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Create the standalone testbed `:sample` Android Application module (`com.android.application`):
1. Depends solely on `:plugin` (`implementation(project(":plugin"))`).
2. Provides an interactive `MainActivity` (using Jetpack Compose) that:
   - Embeds and renders `MyPluginScreen` directly for real-time visual inspection.
   - Provides action buttons to trigger `DataSyncWorker` on demand and inspect WorkManager job states.
   - Provides a test panel to invoke `MyPluginHostApiImpl` methods directly in native Android.
3. Allows Android engineers to launch and debug the plugin native codebase via Android Studio's standard Run/Debug button without having Flutter SDK installed.

## Relevant Files & Context Pointers
- `sample/build.gradle.kts` [NEW]
- `sample/src/main/AndroidManifest.xml` [NEW]
- `sample/src/main/kotlin/com/danhdue/sample/SampleApp.kt` [NEW]
- `sample/src/main/kotlin/com/danhdue/sample/MainActivity.kt` [NEW]
- `sample/src/main/res/values/strings.xml` [NEW]
- `sample/src/test/kotlin/com/danhdue/sample/MainActivityTest.kt` [NEW]

## Design Rationale
- **Zero-Overhead Local Iteration:** Launching a native sample app takes seconds, compared to running a full Flutter runner app.
- **Isolated Debugging:** Network, sensor, and background worker issues can be isolated and verified purely within Android tooling.

## TDD Checklist
- [ ] **RED**: Write UI/Robolectric test in `MainActivityTest.kt` asserting that `MainActivity` launches successfully and renders the plugin preview container.
- [ ] **GREEN**: Implement `sample/build.gradle.kts`, `SampleApp`, and `MainActivity.kt` with Compose UI embedding `MyPluginScreen`. Verify tests pass.
- [ ] **REFACTOR**: Add test controls for triggering the background worker and viewing log outputs.

## Definition of Done (DoD)
- `./gradlew :sample:assembleDebug` builds an executable APK successfully.
- `MainActivity` launches without crash and displays the plugin UI.
- Triggering background worker from the sample UI enqueues a work request successfully.

## Dependencies & Blockers
- Blocked by [Task 3](task_3_scaffold_plugin_module_pure_dagger.md) and [Task 4](task_4_flutter_platform_and_compose_view.md).

## References & Rollback
- Reference: Spec Section 4.4.
- Rollback: `rm -rf sample`.
