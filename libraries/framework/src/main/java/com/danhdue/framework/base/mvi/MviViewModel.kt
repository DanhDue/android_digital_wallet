/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.mvi

import com.danhdue.framework.base.mvvm.MvvmViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class MviViewModel<STATE : BaseViewState<*>, ACTION, EVENT> : MvvmViewModel() {
    private val _uiState = MutableStateFlow<BaseViewState<*>>(BaseViewState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<EVENT>()
    val event = _event.receiveAsFlow()

    abstract fun onAction(action: ACTION)

    protected fun setState(state: STATE) =
        safeLaunch {
            _uiState.emit(state)
        }

    protected fun sendEvent(event: EVENT) =
        safeLaunch {
            _event.send(event)
        }

    override fun startLoading() {
        super.startLoading()
        _uiState.value = BaseViewState.Loading
    }

    override fun handleError(exception: Throwable) {
        super.handleError(exception)
        _uiState.value = BaseViewState.Error(exception)
    }
}
