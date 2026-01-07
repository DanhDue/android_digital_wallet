/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mytokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.mywallet.domain.usecase.GetMyTokensDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the MyTokens feature.
 */
@HiltViewModel
class MyTokensViewModel @Inject constructor(
    private val getMyTokensDataUseCase: GetMyTokensDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MyTokensState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyTokensEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: MyTokensAction) {
        when (action) {
            else -> {
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getMyTokensDataUseCase()
                .onSuccess {
                }.onFailure {
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
