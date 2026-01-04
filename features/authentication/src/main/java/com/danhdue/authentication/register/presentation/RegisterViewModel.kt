/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.authentication.register.domain.usecase.GetRegisterDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Register feature.
 */
@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val getRegisterDataUseCase: GetRegisterDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(RegisterState())
        val state = _state.asStateFlow()

        private val _event = Channel<RegisterEvent>()
        val event = _event.receiveAsFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: RegisterAction) {
            when (action) {
                else -> {
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                getRegisterDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
