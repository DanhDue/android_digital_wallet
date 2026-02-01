# Resolved Issues Analysis

Here is a detailed explanation of the issues identified during the Full Project Audit and how they were fixed to align with Modern Android Development (MAD) and Clean Architecture standards.

## 1. Architecture: Cold Flows vs. Suspending Functions
**The Issue:** `suspend fun execute(): Flow<T>` in `ReturnUseCase`.
*   **Why it's bad:** A `Flow` is a "cold" stream—it shouldn't do any work until it is collected. Marking the function itself as `suspend` implies that simply *creating* the flow requires an asynchronous operation, which is contradictory. It forces the caller to launch a coroutine just to get a reference to the stream.
*   **The Solution:** Removed the `suspend` keyword.
    ```diff
    - protected abstract suspend fun execute(params: Params): Flow<ReturnType>
    + protected abstract fun execute(params: Params): Flow<ReturnType>
    ```
    *Benefit:* The Flow is now returned immediately and synchronously, complying with reactive programming standards.

## 2. Concurrency: Hardcoded Dispatchers
**The Issue:** `Dispatchers.IO` and `Dispatchers.Default` were hardcoded in `AppCoroutineScope` and UseCases.
*   **Why it's bad:** This makes unit testing extremely difficult. You cannot easily swap these dispatchers for `TestDispatcher` (which allows you to control time and execution order in tests). Tests become flaky because they rely on real system threads.
*   **The Solution:** Created a `DispatcherProvider` interface and injected it.
    ```kotlin
    // Now we can inject TestDispatchers during unit tests!
    class AppCoroutineScope @Inject constructor(
        private val dispatcherProvider: DispatcherProvider
    ) {
        val io = dispatcherProvider.io
    }
    ```

## 3. MVI Pattern: `TransactionListViewModel`
**The Issue:** The ViewModel was manually implementing state management using strict `ViewModel` pattern instead of standardized `MviViewModel`.
*   **Why it's bad:** It led to inconsistencies. It exposed `MutableStateFlow` (leaking mutability to UI), didn't have a structured way to handle one-time events, and lacked the thread-safe `reduce {}` mechanism provided by the base class.
*   **The Solution:** Refactored to inherit `MviViewModel`.
    *   **State**: Used `uiState` (immutable) instead of exposing mutable flows.
    *   **Updates**: Used `reduce { copy(...) }` for atomic, thread-safe updates.
    *   **UI**: Updated `TransactionListScreen` to use `collectAsStateWithLifecycle()` on the proper flow.

## 4. Security: Context Injection in Data Layer
**The Issue:** `TokenAuthenticator` was injecting `ApplicationContext` to create `SharedPreferences` internally.
*   **Why it's bad:** The Data Layer should not know about Android Contexts if possible. It violates Dependency Injection principles by "hiding" the file I/O dependency inside the class.
*   **The Solution:** Injected `SecureCacheStore` directly.
    ```kotlin
    // Before: Needs Context, hides the fact it uses SharedPreferences
    class TokenAuthenticator(context: Context)
    
    // After: Clear dependency, easy to mock SecureCacheStore for testing
    class TokenAuthenticator(store: SecureCacheStore)
    ```

## 5. Code Style: Detekt Violations
**The Issue:** Import ordering and spacing consistency (e.g., in `AppModule`).
*   **Why it's bad:** While minor, these cause "noise" in Pull Requests and merge conflicts. Consistency is key for team velocity.
*   **The Solution:** Applied standard lexicographic import ordering (Android -> App -> Java/Kotlin) and fixed trailing whitespace to pass limits enforced by the `./gradlew check` tool.

---

## Summary
The project now passes all checks:
*   **Architecture**: ✅ Clean & Reactive
*   **Testing**: ✅ Ready for Unit/UI tests (via Dispatcher injection)
*   **Security**: ✅ Dependencies are explicit and secure
*   **Build**: ✅ `BUILD SUCCESSFUL`

## 6. Safety: Force Unwraps (`!!`) (Audit 2.0)
**The Issue:** The operator `!!` was used in `MobileService.kt` and `AppCoroutineScope.kt`.
*   **Why it's bad:** `!!` throws a `NullPointerException` if the value is null. It bypasses Kotlin's null-safety system and causes crashes in production. It defeats the purpose of writing safer Kotlin code.
*   **The Solution:** Replaced with safer alternatives.
    *   `MobileService.kt`: Used Safe Cast `as Int` (guarded by a null check).
    *   `AppCoroutineScope.kt`: Used `requireNotNull { "message" }` to provide a meaningful error message if initialization failed, rather than a blind crash.

## 7. Expressiveness: String Templates (Audit 2.0)
**The Issue:** Legacy C-style formatting `Timber.i("... %s", val)` in `MobileService.kt`.
*   **Why it's bad:** It's less readable and more error-prone (type mismatch) compared to Kotlin's native String Templates.
*   **The Solution:** Updated to use String Templates.
    ```kotlin
    // Before
    Timber.i("value: %s", value)

    // After
    Timber.i("value: $value")
    ```
