---
id: "task_5_scaffold_sample_runner_app"
status: "done"
priority: "medium"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T17:25:00Z"
completedAt: "2026-09-11T17:25:00Z"
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
- **Applicable Skills:** `test-driven-development`.

### BDD SCENARIOS

#### Scenario 1: Launch Sample App and Display Plugin UI (Happy Path)
```gherkin
Given the engineer installs ":sample" APK on an emulator or device
When "MainActivity" is launched
Then "MyPluginScreen" is rendered within the sample layout
And default data loaded from Dagger 2 use case is displayed
```

#### Scenario 2: On-Demand Trigger of Background Worker (Happy Path)
```gherkin
Given "MainActivity" is running on screen
When the engineer taps the "Trigger DataSyncWorker" button
Then WorkManager enqueues a "OneTimeWorkRequest" for "DataSyncWorker"
And the UI displays the job state changing from "ENQUEUED" to "SUCCEEDED"
```

#### Scenario 3: Screen Orientation & Configuration Changes (State Transitions)
```gherkin
Given "MainActivity" is displaying plugin data
When the device rotates from portrait to landscape
Then Activity recreates cleanly
And "MyPluginScreen" preserves its loaded state without restarting network calls
```

#### Scenario 4: Rapid Button Tapping on Worker Trigger (Async / Race Conditions)
```gherkin
Given "MainActivity" is displayed
When the user rapidly taps "Trigger DataSyncWorker" 5 times in 1 second
Then WorkManager applies "ExistingWorkPolicy.KEEP"
And only one active worker execution runs at a time
```

#### Scenario 5: Missing System Permissions Handling (Failures & Resilience)
```gherkin
Given a native plugin feature requiring runtime permissions (e.g. notifications)
When permission is denied by the user
Then the sample app catches the denial gracefully
And shows an explanatory snackbar instead of crashing
```

#### Scenario 6: Edge Case Payload Verification (Edge Cases & Boundaries)
```gherkin
Given the interactive test panel in sample app
When the tester inputs empty strings or special Unicode characters
Then the API call executes safely without crashing the UI
```

### TDD Checklist (The Dev Persona)
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
