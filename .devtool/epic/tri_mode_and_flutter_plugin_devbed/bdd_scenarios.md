# BDD Scenarios: Tri-Mode Android Native Template & Flutter Plugin Native Devbed

## Document Metadata
- **Epic**: `tri_mode_and_flutter_plugin_devbed`
- **Specification Source**: [tri_mode_and_flutter_plugin_devbed.en.md](tri_mode_and_flutter_plugin_devbed.en.md)
- **Status**: Living Behavioral Specification

---

## 1. Mode Configuration & Script Automation

### Feature: Mode Switching Automation (`scripts/configure_mode.sh`)

#### Scenario 1.1: Switch from Enterprise to Lean Mode (Happy Path)
```gherkin
Given the Android project is in the default "enterprise" mode with 12 modules included
When the developer runs "./scripts/configure_mode.sh lean"
Then the script exits with code 0
And "settings.gradle.kts" has ":features:scanner", ":features:settings:sample", and ":konsist-test" commented out
And "app/build.gradle.kts" has "dynamicFeatures" removed or commented
And root "build.gradle.kts" has Binary Compatibility Validator disabled
And "./gradlew projects" successfully resolves 9 active modules
```

#### Scenario 1.2: Switch from Enterprise to Plugin Mode (Happy Path)
```gherkin
Given the Android project is in "enterprise" mode
When the developer runs "./scripts/configure_mode.sh plugin"
Then the script exits with code 0
And "settings.gradle.kts" includes only ":plugin" and ":sample"
And all host modules (":app", ":shell", ":packages:*", ":features:*", ":konsist-test") are excluded
And "./gradlew :plugin:assembleRelease" and "./gradlew :sample:assembleDebug" succeed
```

#### Scenario 1.3: Idempotent Mode Transitions (State Transitions & Idempotency)
```gherkin
Given the Android project is in "lean" mode
When the developer runs "./scripts/configure_mode.sh lean" repeatedly
Then the script completes with code 0 on every execution
And git status reports zero unintended changes or duplicate commented lines
When the developer runs "./scripts/configure_mode.sh enterprise"
Then all 12 modules are cleanly uncommented and active
And "./gradlew :konsist-test:test" passes 100%
```

#### Scenario 1.4: Invalid Arguments Handling (Edge Cases & Boundaries)
```gherkin
Given the project is in a clean git working state
When the developer executes "./scripts/configure_mode.sh unknown_mode"
Then the script exits with code 1
And prints a descriptive error message listing valid modes "enterprise | lean | plugin"
And no files on disk are modified
```

#### Scenario 1.5: Project Renaming with Mode Flag (`scripts/rename_project.sh`)
```gherkin
Given a fresh clone of the template
When the developer runs "./scripts/rename_project.sh my_app com.acme.myapp --mode lean --dry-run"
Then the output displays the rename plan with mode set to "lean"
And when executed without "--dry-run"
Then package paths and namespaces are renamed to "com.acme.myapp"
And the project is left configured in "lean" mode
And "./gradlew assembleDebug" compiles without errors
```

---

## 2. Dependency Injection & Architecture (:plugin Module)

### Feature: Pure Dagger 2 Dependency Injection

#### Scenario 2.1: Context-based Injection without Hilt (Happy Path)
```gherkin
Given an Android Application or Service context
When "PluginComponentProvider.get(context)" is invoked
Then a singleton "PluginComponent" instance is created via Pure Dagger 2
And provides "GetDataUseCase" and "PluginRepository" with non-null dependencies
And no Hilt bytecode transformation or "@HiltAndroidApp" is required
```

#### Scenario 2.2: Concurrent Multithreaded Access (Async & Race Conditions)
```gherkin
Given 10 concurrent threads request "PluginComponentProvider.get(context)" simultaneously
When all threads complete their acquisition
Then every thread receives the exact same singleton instance
And Dagger graph initialization is executed exactly once without race conditions
```

#### Scenario 2.3: Process Recreation and State Restoration (State Transitions)
```gherkin
Given "PluginComponentProvider" holds an initialized component
When "PluginComponentProvider.reset()" is called (simulating process restart)
And subsequent calls to "get(context)" are made
Then a new "PluginComponent" instance is cleanly instantiated
And all subsequent use cases function normally
```

---

## 3. Background Execution without Flutter Engine

### Feature: Headless Native Worker Execution (`DataSyncWorker`)

#### Scenario 3.1: WorkManager Execution in Background / Killed State (Happy Path)
```gherkin
Given the host application is in the background or killed by the OS
And FlutterEngine is completely uninitialized (0MB Flutter RAM)
When Android WorkManager triggers "DataSyncWorker.doWork()"
Then the worker retrieves "GetDataUseCase" from "PluginComponentProvider.get(applicationContext)"
And executes native repository sync logic using Kotlin Coroutines
And returns "ListenableWorker.Result.success()"
And at no point is any FlutterEngine or FlutterPlugin class initialized
```

#### Scenario 3.2: Network Failure & Exponential Backoff (Resilience & Failures)
```gherkin
Given "DataSyncWorker" is executed
And the remote network call throws a SocketTimeoutException or 500 HTTP error
When "syncUseCase.execute()" fails
Then the worker catches the failure gracefully
And returns "ListenableWorker.Result.retry()" to WorkManager
And no unhandled exceptions crash the host application
```

---

## 4. Flutter Platform Communication & Presentation (With-UI & No-UI)

### Feature: PlatformView & Pigeon Headless IPC

#### Scenario 4.1: Pigeon Headless IPC Message Exchange (Happy Path)
```gherkin
Given Flutter attaches to "MyPlugin" via "onAttachedToEngine"
When Flutter sends a "getData" request over the Pigeon HostApi interface
Then "MyPluginHostApiImpl" calls "GetDataUseCase"
And returns a typed "PluginData" response asynchronously to Flutter
```

#### Scenario 4.2: Jetpack Compose PlatformView Lifecycle (State Transitions)
```gherkin
Given a Flutter UI requesting a native PlatformView with viewType "my_plugin_view"
When "MyPlatformViewFactory.create(context, id, args)" is invoked
Then "MyPlatformView" wraps a "ComposeView" rendering "MyPluginScreen"
And when the Flutter view is popped or destroyed
Then "MyPlatformView.dispose()" is called
And Compose lifecycle observers and coroutine scopes are cancelled cleanly
```

#### Scenario 4.3: Rapid UI Multi-Tap Interactions (Async & Race Conditions)
```gherkin
Given "MyPluginScreen" is displayed in Compose PlatformView
When the user rapidly taps the "Refresh" action 5 times within 200ms
Then "MyPluginViewModel" processes actions through an MVI Channel
And emits loading and loaded states in strict unidirectional order
And avoids duplicate out-of-order network emissions
```

---

## 5. Standalone Testbed Runner (:sample Module)

### Feature: Standalone Execution & Verification

#### Scenario 5.1: Launching Sample App without Flutter (Happy Path)
```gherkin
Given an engineer opens the project in Android Studio with mode set to "plugin"
When the engineer runs ":sample:assembleDebug" and launches "MainActivity"
Then "MainActivity" renders "MyPluginScreen" natively
And action buttons allow testing the background worker and Pigeon contracts interactively
```

---

## 6. Mason Bricks Scaffolding

### Feature: Native Plugin Code Generation

#### Scenario 6.1: Scaffold Headless Plugin (`native_plugin --has_ui false`)
```gherkin
When running "mason make native_plugin --name biometric_auth --has_ui false"
Then an Android Library module is generated with "platform/", "domain/", "data/", and "di/"
And no "presentation/" folder is generated
And "./gradlew compileReleaseKotlin" compiles the generated module successfully
```

#### Scenario 6.2: Upgrade to Native UI (`add_native_ui`)
```gherkin
Given an existing headless plugin generated in Scenario 6.1
When running "mason make add_native_ui --name biometric_auth"
Then "presentation/" is added containing Compose Screen, MviViewModel, and PlatformView
And "build.gradle.kts" is updated with "compose = true"
And the updated module compiles with Jetpack Compose enabled
```
