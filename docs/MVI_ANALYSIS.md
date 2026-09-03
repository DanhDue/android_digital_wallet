# MVI ViewModel Architecture Analysis

> Analysis and optimization recommendations for `MviViewModel.kt`

## Table of Contents

1. [Current Implementation Overview](#current-implementation-overview)
2. [Identified Issues](#identified-issues)
3. [Optimization Areas](#optimization-areas)
4. [Recommendations](#recommendations)
5. [State Management Approaches](#state-management-approaches)

---

## Current Implementation Overview

The existing `MviViewModel` provides a base class for Model-View-Intent architecture:

```kotlin
abstract class MviViewModel<STATE : BaseViewState<*>, ACTION, EVENT> : MvvmViewModel() {
    private val _uiState = MutableStateFlow<BaseViewState<*>>(BaseViewState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<EVENT>()
    val event = _event.receiveAsFlow()

    abstract fun onAction(action: ACTION)
    protected fun setState(state: STATE)
    protected fun sendEvent(event: EVENT)
}
```

---

## Identified Issues

### 1. Type Safety with Generic State

| Aspect | Current | Issue |
|--------|---------|-------|
| State Type | `BaseViewState<*>` | Star projection erases type information |
| Access Pattern | Manual casting required | Runtime crash risk |

**Example of problematic code in ViewModels:**
```kotlin
val currentState = (uiState.value as? BaseViewState.Data<*>)?.value as? LoginState ?: LoginState()
```

### 2. Mixed Concerns in BaseViewState

`BaseViewState` conflates:
- **Infrastructure states**: Loading, Error, Empty
- **Domain states**: Actual business data

> [!WARNING]
> When transitioning to `Loading`, the previous domain data is lost, breaking "loading overlay" patterns.

### 3. Event Channel Configuration

| Current | Risk |
|---------|------|
| Default `RENDEZVOUS` capacity | Events may be lost during config changes |

### 4. Missing State Reducer Pattern

No built-in atomic state update mechanism, leading to:
- Boilerplate `updateState` functions in each ViewModel
- Potential race conditions with `emit()` in coroutines

---

## Optimization Areas

### Area 1: Strongly Typed State

**Proposed Change:**
```kotlin
abstract class MviViewModel<STATE : Any, ACTION, EVENT>(
    initialState: STATE,
) : MvvmViewModel()
```

| Pros | Cons |
|------|------|
| ✅ Compile-time type safety | ⚠️ Breaking change - requires initial state |
| ✅ IDE autocomplete works | ⚠️ Cannot use `Empty` singleton |
| ✅ No casting in ViewModels | |
| ✅ Easier testing | |

---

### Area 2: Dual State Architecture

**Proposed Change:**
```kotlin
// Pure domain state - never loses data
val uiState: StateFlow<STATE>

// View-layer wrapper for loading/error
val viewState: StateFlow<ViewState<STATE>>
```

| Pros | Cons |
|------|------|
| ✅ Loading doesn't erase data | ⚠️ Two states to observe |
| ✅ Supports "loading overlay" pattern | ⚠️ Slightly more complex |
| ✅ Clean separation of concerns | |
| ✅ Pull-to-refresh works properly | |

---

### Area 3: Atomic State Updates

**Proposed Change:**
```kotlin
protected inline fun reduce(reducer: STATE.() -> STATE) {
    _uiState.update { it.reducer() }
}
```

| Pros | Cons |
|------|------|
| ✅ Thread-safe, atomic | ⚠️ Requires understanding `update()` |
| ✅ No coroutine overhead | |
| ✅ Cleaner syntax with `copy()` | |
| ✅ No race conditions | |

---

### Area 4: Buffered Event Channel

**Proposed Change:**
```kotlin
private val _event = Channel<EVENT>(Channel.BUFFERED)
```

| Capacity | Pros | Cons |
|----------|------|------|
| `RENDEZVOUS` (current) | Backpressure | Events lost if UI not ready |
| `BUFFERED` (proposed) | Events preserved during config change | Memory usage (bounded) |
| `UNLIMITED` | Never blocks | Memory leak risk |

---

### Area 5: Public API Design

**Proposed Change:**
```kotlin
fun dispatch(action: ACTION) {
    onAction(action)
}

protected abstract fun onAction(action: ACTION)
```

| Pros | Cons |
|------|------|
| ✅ Single entry point for actions | ⚠️ API change |
| ✅ Middleware support (logging, analytics) | |
| ✅ Consistent usage pattern | |

---

## Recommendations

### Summary Matrix

| Feature | Risk Level | Benefit | Recommendation |
|---------|------------|---------|----------------|
| Strongly Typed State | Medium | High | ✅ Implement |
| Dual State Architecture | Low | Medium | ✅ Implement |
| Atomic `reduce()` | Low | High | ✅ Implement |
| Buffered Channel | Low | Medium | ✅ Implement |
| `dispatch()` API | Low | Medium | ✅ Implement |
| Shared Events | Low | Low | ⚪ Optional |

### Migration Strategy

1. **Phase 1**: Update `MviViewModel` with new implementation
2. **Phase 2**: Update existing ViewModels to use new APIs
3. **Phase 3**: Remove deprecated `BaseViewState` usage

---

## State Management Approaches

This section analyzes three approaches to manage `uiState` and `viewState` in MviViewModel.

### Memory Layout (Current Dual State)

```
┌──────────────────────────────────────────────────────────┐
│ MviViewModel                                              │
├──────────────────────────────────────────────────────────┤
│ _uiState: MutableStateFlow<STATE>  ──────┐               │
│                                          ▼               │
│                                    ┌──────────┐          │
│                                    │  STATE   │ (actual) │
│                                    │  object  │          │
│                                    └──────────┘          │
│                                          ▲               │
│ _viewState: MutableStateFlow<ViewState>  │               │
│        └─► ViewState.Content(data: T) ───┘               │
│            (lightweight wrapper ~24 bytes)                │
└──────────────────────────────────────────────────────────┘
```

---

### Approach A: Dual State (Current)

```kotlin
private val _uiState = MutableStateFlow(initialState)
private val _viewState = MutableStateFlow<ViewState<STATE>>(ViewState.Content(initialState))
```

| Pros ✅ | Cons ❌ |
|---------|---------|
| Data preserved during loading | ~24 bytes extra memory per ViewModel |
| Pull-to-refresh works naturally | Two sources of truth to sync |
| Loading overlay patterns supported | More complex mental model |
| Partial loading possible | Potential sync bugs |

**Best for:** Complex UIs with pull-to-refresh, cached data

---

### Approach B: Single Wrapped State

```kotlin
private val _viewState = MutableStateFlow<ViewState<STATE>>(ViewState.Content(initialState))
val currentState: STATE? get() = (_viewState.value as? ViewState.Content)?.data
```

| Pros ✅ | Cons ❌ |
|---------|---------|
| Single source of truth | Data lost during loading |
| Less memory | Nullable `currentState` access |
| Simpler mental model | Loading wipes UI content |
| Easier debugging | Pull-to-refresh breaks |

**Best for:** Simple screens with full-screen loading

---

### Approach C: Inline Loading Flag

```kotlin
data class LoginState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
private val _uiState = MutableStateFlow(initialState)
```

| Pros ✅ | Cons ❌ |
|---------|---------|
| Minimal memory | Mixed concerns |
| Simplest API | Boilerplate in every state |
| Type-safe access | Inconsistent patterns |
| Atomic updates | No reusable loading handling |

**Best for:** Small apps, prototypes, MVPs

---

### Comparison Matrix

| Criterion | A: Dual State | B: Single Wrapped | C: Inline Flag |
|-----------|---------------|-------------------|----------------|
| **Memory** | +24 bytes | Minimal | Minimal |
| **Separation of Concerns** | ✅ Excellent | ⚠️ OK | ❌ Poor |
| **Pull-to-refresh** | ✅ Works | ❌ Breaks | ✅ Works |
| **Loading Overlay** | ✅ Native | ❌ Manual | ⚠️ Manual |
| **Type Safety** | ✅ Full | ⚠️ Nullable | ✅ Full |
| **Reusability** | ✅ Generic | ✅ Generic | ❌ Per-state |

---

### Recommendation for this template

> [!TIP]
> **Keep Approach A (Dual State)** — Users expect to see balance/tokens while refreshing. The ~24 bytes overhead is negligible (~2.4 KB for 100 ViewModels).

---

## References

- [Guide to MVI Architecture](https://developer.android.com/topic/architecture)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Kotlin Channel Documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/)

