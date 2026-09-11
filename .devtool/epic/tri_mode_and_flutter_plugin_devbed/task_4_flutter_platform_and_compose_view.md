---
id: "task_4_flutter_platform_and_compose_view"
status: "todo"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:28:00Z"
completedAt: null
labels: ["presentation", "compose", "flutter", "pigeon", "platformview"]
order: "a4"
---

# Task 4: Implement Flutter Platform Layer & Jetpack Compose PlatformView (With-UI & No-UI)

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Implement the presentation and platform communication layers for `:plugin` supporting both Headless IPC and Native Jetpack Compose UI:
1. **Platform Layer (`platform/`):**
   - `MyPlugin.kt`: Implement `FlutterPlugin` (`onAttachedToEngine`, `onDetachedFromEngine`).
   - `Messages.g.kt` & `MyPluginHostApiImpl.kt`: Pigeon type-safe contracts for headless method calls, injected via Dagger 2.
   - `MyPlatformViewFactory.kt`: Implement `PlatformViewFactory` registering the Compose PlatformView with Flutter's `PlatformViewRegistry`.
2. **Presentation Layer (`presentation/`):**
   - `base/MviViewModel.kt`: Self-contained base MVI ViewModel using Coroutines `StateFlow` and `Channel` without Hilt dependencies.
   - `MyPluginViewModel.kt`: Concrete ViewModel with `@Inject constructor` taking `GetDataUseCase`.
   - `MyPluginScreen.kt`: Modern Jetpack Compose UI (`@Composable`).
   - `MyPlatformView.kt`: Wraps `ComposeView` into Flutter's `io.flutter.plugin.platform.PlatformView`.

## Relevant Files & Context Pointers
- `plugin/build.gradle.kts`
- `plugin/src/main/kotlin/com/danhdue/plugin/platform/MyPlugin.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/platform/Messages.g.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/platform/MyPluginHostApiImpl.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/platform/MyPlatformViewFactory.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/presentation/base/MviViewModel.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/presentation/base/ViewContract.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/presentation/MyPluginViewModel.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/presentation/MyPluginScreen.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/presentation/MyPlatformView.kt` [NEW]
- `plugin/src/test/kotlin/com/danhdue/plugin/presentation/MyPluginViewModelTest.kt` [NEW]

## Design Rationale
- **1:1 Alignment with Flutter Mason Bricks:** The structure matches `pac_native_plugin` from the Flutter template (`bloc_digital_wallet`), enabling seamless copy/export of Kotlin code into a Flutter plugin's `android/` directory.
- **Compose PlatformView Integration:** Binds modern Jetpack Compose into Flutter's hybrid composition pipeline via standard Android `ComposeView`.
- **Applicable Skills:** `test-driven-development`.

### BDD SCENARIOS

#### Scenario 1: Pigeon Headless IPC Message Exchange (Happy Path)
```gherkin
Given Flutter attaches to "MyPlugin" via "onAttachedToEngine"
When Flutter sends a "getData" request over Pigeon HostApi
Then "MyPluginHostApiImpl" calls "GetDataUseCase"
And returns a typed "PluginData" response asynchronously to Flutter
```

#### Scenario 2: Jetpack Compose PlatformView Mounting (Happy Path)
```gherkin
Given Flutter requests a native PlatformView with viewType "my_plugin_view"
When "MyPlatformViewFactory.create(context, id, args)" is invoked
Then "MyPlatformView" wraps a "ComposeView" rendering "MyPluginScreen"
And the Compose content renders with default StateFlow values
```

#### Scenario 3: PlatformView Disposal & Resource Teardown (State Transitions)
```gherkin
Given a running "MyPlatformView" instance
When Flutter navigates back and triggers "MyPlatformView.dispose()"
Then the ComposeView is detached from window
And internal ViewModel coroutine scopes and event channels are cancelled cleanly
And no memory leaks or dangling callbacks remain
```

#### Scenario 4: Rapid Multi-Tap Interactions & Debouncing (Async / Race Conditions)
```gherkin
Given "MyPluginScreen" is displayed in Compose PlatformView
When the user rapidly taps the "Refresh" action 5 times within 200ms
Then "MyPluginViewModel" processes actions through an MVI Channel
And emits loading and loaded states in strict unidirectional order
And avoids duplicate out-of-order network emissions
```

#### Scenario 5: Null / Empty Response Payload via Pigeon (Edge Cases & Boundaries)
```gherkin
Given "GetDataUseCase" returns an empty list or null property
When "MyPluginHostApiImpl.getData()" serializes the Pigeon message
Then it returns an empty "PluginData" object with default values
And does not crash or throw ClassCastException across the IPC boundary
```

#### Scenario 6: UI Error Handling on Repository Exception (Failures & Resilience)
```gherkin
Given "GetDataUseCase" throws an IOException or NetworkException
When "MyPluginViewModel.onAction(LoadData)" is executed
Then ViewModel catches the error
And emits "ErrorState(message = ...)"
And "MyPluginScreen" renders an error banner with a "Retry" button
```

### TDD Checklist (The Dev Persona)
- [ ] **RED**: Write unit test in `MyPluginViewModelTest.kt` asserting MVI unidirectional data flow (`Action` -> `State` emission via `Turbine`), and test Pigeon host API implementation.
- [ ] **GREEN**: Implement `MviViewModel`, `MyPluginViewModel`, `MyPluginScreen`, `MyPlatformView`, and `MyPlatformViewFactory`. Verify tests pass.
- [ ] **REFACTOR**: Ensure clean Compose lifecycle detachment in `MyPlatformView.dispose()`, and verify Pigeon error propagation.

## Definition of Done (DoD)
- `./gradlew :plugin:testDebugUnitTest` passes for all ViewModel and platform tests.
- `:plugin` builds an AAR artifact via `./gradlew :plugin:assembleRelease`.
- `MyPlatformView` disposes cleanly without memory leaks.

## Dependencies & Blockers
- Blocked by [Task 3](task_3_scaffold_plugin_module_pure_dagger.md).

## References & Rollback
- Reference: Spec Section 4.3.
- Rollback: `git checkout HEAD -- plugin/build.gradle.kts && rm -rf plugin/src/main/kotlin/com/danhdue/plugin/presentation plugin/src/main/kotlin/com/danhdue/plugin/platform`.
