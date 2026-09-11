---
id: "task_4_flutter_platform_and_compose_view"
status: "todo"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
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

## TDD Checklist
- [ ] **RED**: Write unit test in `MyPluginViewModelTest.kt` asserting MVI unidirectional data flow (`Action` -> `State` emission via `Turbine`).
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
