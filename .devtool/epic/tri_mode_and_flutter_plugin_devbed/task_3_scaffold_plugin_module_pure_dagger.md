---
id: "task_3_scaffold_plugin_module_pure_dagger"
status: "done"
priority: "high"
assignee: null
epic: "tri_mode_and_flutter_plugin_devbed"
dueDate: null
created: "2026-09-11T15:20:00Z"
modified: "2026-09-11T16:11:00Z"
completedAt: "2026-09-11T16:11:00Z"
labels: ["architecture", "di", "plugin", "dagger", "workmanager"]
order: "a3"
---

# Task 3: Scaffold Clean Architecture :plugin Module with Pure Dagger 2 & Background Worker

Epic: [tri_mode_and_flutter_plugin_devbed](../epic/tri_mode_and_flutter_plugin_devbed/tri_mode_and_flutter_plugin_devbed.en.md)

## Requirement Analysis
Scaffold the core `:plugin` Android Library module (`com.android.library`) representing the native Android portion of a Flutter Plugin:
1. **Pure Dagger 2 (No Hilt):**
   - Apply KSP (`com.google.devtools.ksp`) and pure Dagger 2 dependencies (`com.google.dagger:dagger` + `dagger-compiler`).
   - Create `@Component` interface `PluginComponent` and `@Module` `PluginModule`.
   - Provide `PluginComponentProvider` thread-safe singleton holder allowing Context-based DI resolution.
2. **Clean Architecture Core Layers:**
   - `domain/`: Pure Kotlin data classes (e.g. `PluginData`), repository interface `PluginRepository`, and use cases (e.g. `GetDataUseCase`, `SyncDataUseCase`) with `@Inject constructor`.
   - `data/`: `PluginRepositoryImpl`, local storage / hardware data source.
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
- `plugin/src/test/kotlin/com/danhdue/plugin/di/PluginComponentTest.kt` [NEW]
- `plugin/src/test/kotlin/com/danhdue/plugin/data/worker/DataSyncWorkerTest.kt` [NEW]

## Design Rationale
- **Zero Hilt Coupling:** A Flutter Plugin must not impose Hilt requirements on the consumer host application. Pure Dagger 2 delivers compile-time safety and `@Inject constructor` cleanly scoped within the library boundary.
- **Resource Protection on Background Wakeup:** Native execution of `DataSyncWorker` via `PluginComponentProvider` avoids launching the 150MB+ FlutterEngine when triggered by Android OS in low-memory situations.
- **Applicable Skills:** `test-driven-development`.

### BDD SCENARIOS

#### Scenario 1: Context-Based Dagger 2 Dependency Resolution (Happy Path)
```gherkin
Given an Android Application or Service context
When "PluginComponentProvider.get(context)" is invoked
Then a non-null "PluginComponent" instance is returned
And its "getDataUseCase()" is fully wired with "PluginRepositoryImpl"
And no Hilt bytecode transformation or "@HiltAndroidApp" is required
```

#### Scenario 2: Headless DataSyncWorker Execution (Happy Path)
```gherkin
Given the host app is killed and FlutterEngine is not running
When Android OS triggers "DataSyncWorker.doWork()" via WorkManager
Then the worker retrieves "SyncDataUseCase" from "PluginComponentProvider.get(context)"
And executes native repository sync logic using Kotlin Coroutines
And returns "ListenableWorker.Result.success()"
And zero Flutter classes or engines are initialized
```

#### Scenario 3: Null Application Context Handling (Edge Cases & Boundaries)
```gherkin
Given a mock Context whose "applicationContext" returns null
When "PluginComponentProvider.get(context)" is called
Then it falls back gracefully to using the direct "context"
And does not throw a NullPointerException
```

#### Scenario 4: Concurrent Singleton Access (Async / Race Conditions)
```gherkin
Given 10 concurrent threads invoking "PluginComponentProvider.get(context)" simultaneously
When all threads complete
Then all threads receive the exact same singleton instance
And Dagger graph initialization is executed exactly once
```

#### Scenario 5: Network Timeout & Retry Handling in Worker (Failures & Resilience)
```gherkin
Given "DataSyncWorker" is running
And the remote network call throws a SocketTimeoutException
When "syncUseCase.execute()" fails
Then the worker catches the failure gracefully
And returns "ListenableWorker.Result.retry()" to WorkManager
```

#### Scenario 6: Process Death and Provider Reset (State Transitions)
```gherkin
Given "PluginComponentProvider" holds an initialized component
When "PluginComponentProvider.reset()" is called (simulating process recreation)
And "PluginComponentProvider.get(context)" is invoked again
Then a fresh "PluginComponent" is instantiated
And subsequent use case invocations function normally
```

### TDD Checklist (The Dev Persona)
- [ ] **RED**: Write unit tests in `PluginComponentTest.kt` asserting DI graph resolution and singleton identity under concurrency. Write `DataSyncWorkerTest.kt` asserting success and retry result codes without Flutter dependencies.
- [ ] **GREEN**: Implement `PluginComponent`, `PluginModule`, `PluginComponentProvider`, domain models, use cases, repository implementation, and `DataSyncWorker`. Run tests to verify all pass.
- [ ] **REFACTOR**: Ensure `@Singleton` scopes are properly applied, optimize imports, and verify zero lint warnings.

## Definition of Done (DoD)
- `./gradlew :plugin:testDebugUnitTest` passes 100%.
- KSP compiles Dagger 2 components without warnings.
- `DataSyncWorker` executes successfully in tests with zero references to Flutter classes.

## Dependencies & Blockers
- Blocked by: None.

## References & Rollback
- Reference: Spec Section 4.1 & 4.2.
- Rollback: `rm -rf plugin`.
