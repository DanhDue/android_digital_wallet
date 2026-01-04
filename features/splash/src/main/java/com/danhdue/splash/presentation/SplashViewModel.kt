/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.splash.domain.usecase.GetSplashDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Splash feature.
 */
@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getSplashDataUseCase: GetSplashDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(SplashState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<SplashEvent>()
        val event = _event.asSharedFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: SplashAction) {
            when (action) {
                else -> {
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                getSplashDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
