---
id: "task_3_scaffold_plugin_module_pure_dagger"
status: "todo"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T15:20:00Z"
completedAt: null
labels: ["architecture", "di", "plugin", "dagger", "workmanager"]
order: "a3"
---

# Task 3: Scaffold Clean Architecture :plugin Module with Pure Dagger 2 & Background Worker

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Create the core `:plugin` Android Library module (`com.android.library`) representing the native Android side of a Flutter Plugin. It must implement:
1. **Pure Dagger 2 (No Hilt):**
   - Apply KSP (`com.google.devtools.ksp`) and pure Dagger 2 dependencies (`com.google.dagger:dagger` + `dagger-compiler`).
   - Create `@Component` interface `PluginComponent` and `@Module` `PluginModule`.
   - Provide `PluginComponentProvider` singleton holder allowing Context-based DI resolution.
2. **Clean Architecture Core Layers:**
   - `domain/`: Pure Kotlin data classes (e.g. `PluginData`), repository interface `PluginRepository`, and use cases (e.g. `GetDataUseCase`, `SyncDataUseCase`) with `@Inject constructor`.
   - `data/`: `PluginRepositoryImpl`, local storage / mock hardware data source.
3. **Headless Background Worker Execution (Zero Flutter Engine):**
   - Implement `DataSyncWorker` extending `androidx.work.CoroutineWorker`.
   - In `doWork()`, resolve `PluginComponentProvider.get(applicationContext).getSyncDataUseCase()` and execute synchronization purely natively without touching or initializing Flutter Engine.

## Relevant Files & Context Pointers
- `plugin/build.gradle.kts` [NEW]
- `plugin/src/main/AndroidManifest.xml` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/di/PluginComponent.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/di/PluginModule.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/di/PluginComponentProvider.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/domain/model/PluginData.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/domain/repository/PluginRepository.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/domain/usecase/GetDataUseCase.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/domain/usecase/SyncDataUseCase.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/data/repository/PluginRepositoryImpl.kt` [NEW]
- `plugin/src/main/kotlin/com/danhdue/plugin/data/worker/DataSyncWorker.kt` [NEW]
- `plugin/src/test/kotlin/com/danhdue/plugin/PluginComponentTest.kt` [NEW]
- `plugin/src/test/kotlin/com/danhdue/plugin/data/worker/DataSyncWorkerTest.kt` [NEW]

## Design Rationale
- **Zero Hilt Coupling:** A Flutter Plugin must not impose Hilt requirements on the consumer host application. Pure Dagger 2 delivers compile-time safety and `@Inject constructor` cleanly scoped within the library boundary.
- **Resource Protection on Background Wakeup:** Native execution of `DataSyncWorker` via `PluginComponentProvider` avoids launching the 150MB+ FlutterEngine when triggered by Android OS in low-memory situations.

## TDD Checklist
- [ ] **RED**: Write unit tests in `PluginComponentTest.kt` and `DataSyncWorkerTest.kt` verifying DI resolution via `PluginComponentProvider` and worker execution success without FlutterEngine.
- [ ] **GREEN**: Implement `build.gradle.kts`, Dagger 2 components, `domain` use cases, `data` repository, and `DataSyncWorker`. Ensure tests pass.
- [ ] **REFACTOR**: Ensure proper scoping (`@Singleton`), clean error handling in `DataSyncWorker`, and clear logging.

## Definition of Done (DoD)
- `./gradlew :plugin:testDebugUnitTest` passes 100%.
- KSP compiles Dagger 2 components without warnings.
- `DataSyncWorker` executes successfully in tests with zero references to Flutter classes.

## Dependencies & Blockers
- Blocked by: None.

## References & Rollback
- Reference: Spec Section 4.1 & 4.2.
- Rollback: `rm -rf plugin`.
