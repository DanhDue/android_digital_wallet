/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.mvi

import androidx.annotation.CallSuper
import com.danhdue.framework.base.mvvm.MvvmViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Base MVI ViewModel that provides unidirectional data flow architecture.
 *
 * ## Key Features:
 * - **Type-safe state**: Strongly typed STATE parameter with compile-time checking
 * - **Dual state architecture**: Separate `uiState` (domain) and `viewState` (loading/error wrapper)
 * - **Atomic updates**: Thread-safe state mutations via `reduce()`
 * - **One-time events**: Buffered channel for navigation, toasts, etc.
 * - **Shared events**: SharedFlow for analytics where multiple collectors need the same event
 *
 * ## Usage:
 * ```kotlin
 * class MyViewModel : MviViewModel<MyState, MyAction, MyEvent>(MyState()) {
 *     override fun onAction(action: MyAction) {
 *         when (action) {
 *             is MyAction.Load -> reduce { copy(isLoading = true) }
 *         }
 *     }
 * }
 * ```
 *
 * @param STATE The UI state type, must be an immutable data class
 * @param ACTION Actions/Intents that can be dispatched to this ViewModel
 * @param EVENT One-time events for navigation, toasts, etc.
 * @param initialState The initial state when ViewModel is created
 */
abstract class MviViewModel<STATE : Any, ACTION, EVENT>(
    initialState: STATE,
) : MvvmViewModel() {

    // ==================== UI State Management ====================

    private val _uiState = MutableStateFlow(initialState)

    /**
     * The current UI state as an immutable [StateFlow].
     * Collectors will receive the current state immediately and all subsequent updates.
     * This state is never erased by loading/error - use for pure domain data.
     */
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    /**
     * Provides direct access to the current state value.
     * Useful for state-dependent logic within actions.
     */
    protected val currentState: STATE get() = _uiState.value

    // ==================== View State Wrapper ====================

    private val _viewState = MutableStateFlow<ViewState<STATE>>(ViewState.Content(initialState))

    /**
     * Wrapped UI state for handling Loading/Error/Content states.
     * Use this when you need loading indicators or error screens.
     * The underlying data is preserved in [uiState] during loading.
     */
    val viewState: StateFlow<ViewState<STATE>> = _viewState.asStateFlow()

    // ==================== One-Time Events ====================

    private val _event = Channel<EVENT>(Channel.BUFFERED)

    /**
     * One-time events flow. Each event is delivered only once to a single collector.
     * Suitable for navigation, Snackbar, Toast, etc.
     * Buffered to prevent event loss during configuration changes.
     */
    val event = _event.receiveAsFlow()

    // ==================== Shared Events (Multi-Collector) ====================

    private val _sharedEvent = MutableSharedFlow<EVENT>(extraBufferCapacity = 1)

    /**
     * Shared events that can be collected by multiple collectors.
     * Useful for analytics or logging where multiple observers need the same event.
     */
    val sharedEvent = _sharedEvent.asSharedFlow()

    // ==================== Action Handling ====================

    /**
     * Dispatch an action to be processed by the ViewModel.
     * This is the main entry point for UI interactions.
     *
     * Use this from the UI layer:
     * ```kotlin
     * viewModel.dispatch(MyAction.OnButtonClicked)
     * ```
     */
    fun dispatch(action: ACTION) {
        onAction(action)
    }

    /**
     * Handle an action and update state or trigger events accordingly.
     * Subclasses must implement this to define action-to-state mappings.
     *
     * @param action The action to process
     */
    protected abstract fun onAction(action: ACTION)

    // ==================== State Updates ====================

    /**
     * Update state atomically using a reducer function.
     * Thread-safe and guarantees no state race conditions.
     *
     * Usage:
     * ```kotlin
     * reduce { copy(isLoading = true, error = null) }
     * ```
     *
     * @param reducer A function that transforms current state to new state
     */
    protected fun reduce(reducer: STATE.() -> STATE) {
        _uiState.update { it.reducer() }
        _viewState.value = ViewState.Content(_uiState.value)
    }

    /**
     * Set a completely new state, replacing the current one.
     * Prefer [reduce] for incremental updates.
     *
     * @param state The new state to set
     */
    protected fun setState(state: STATE) {
        _uiState.value = state
        _viewState.value = ViewState.Content(state)
    }

    // ==================== Event Emission ====================

    /**
     * Send a one-time event to the UI.
     * Only one collector will receive this event.
     *
     * @param event The event to send
     */
    protected fun sendEvent(event: EVENT) {
        safeLaunch { _event.send(event) }
    }

    /**
     * Emit a shared event to all collectors.
     * Use for analytics or when multiple observers need the same event.
     *
     * @param event The event to emit
     */
    protected fun emitSharedEvent(event: EVENT) {
        safeLaunch { _sharedEvent.emit(event) }
    }

    // ==================== Loading & Error Handling ====================

    /**
     * Transitions [viewState] to Loading while preserving [uiState] data.
     * Override to add custom loading behavior.
     */
    @CallSuper
    override fun startLoading() {
        super.startLoading()
        _viewState.value = ViewState.Loading
    }

    /**
     * Transitions [viewState] to Error while preserving [uiState] data.
     * Override to add custom error handling.
     *
     * @param exception The error that occurred
     */
    @CallSuper
    override fun handleError(exception: Throwable) {
        super.handleError(exception)
        _viewState.value = ViewState.Error(exception)
    }

    /**
     * Restore [viewState] to Content state after loading or error.
     * Call this when you want to show the current data again.
     */
    protected fun showContent() {
        _viewState.value = ViewState.Content(_uiState.value)
    }
}

/**
 * Represents the overall view state wrapper for handling loading, error, and content states.
 * This wraps the domain state to provide UI-layer concerns.
 *
 * Usage in Compose:
 * ```kotlin
 * val viewState by viewModel.viewState.collectAsStateWithLifecycle()
 * when (viewState) {
 *     is ViewState.Loading -> LoadingScreen()
 *     is ViewState.Error -> ErrorScreen(viewState.throwable)
 *     is ViewState.Content -> ContentScreen(viewState.data)
 * }
 * ```
 */
sealed interface ViewState<out T> {
    /**
     * Loading state - show loading indicator.
     * The underlying data is still available in [MviViewModel.uiState].
     */
    data object Loading : ViewState<Nothing>

    /**
     * Error state - show error message/screen.
     *
     * @property throwable The error that occurred
     */
    data class Error(val throwable: Throwable) : ViewState<Nothing>

    /**
     * Content state - show the actual data.
     *
     * @property data The domain state to display
     */
    data class Content<T>(val data: T) : ViewState<T>
}
