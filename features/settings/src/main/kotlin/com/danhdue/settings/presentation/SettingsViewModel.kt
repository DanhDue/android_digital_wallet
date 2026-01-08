/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Settings feature.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val getSettingsDataUseCase: GetSettingsDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(SettingsState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<SettingsEvent>()
        val event = _event.asSharedFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: SettingsAction) {
            when (action) {
                is SettingsAction.OpenProfile -> {
                    viewModelScope.launch {
                        _event.emit(SettingsEvent.NavigateToProfile)
                    }
                }
                is SettingsAction.Logout -> {
                    viewModelScope.launch {
                        _event.emit(SettingsEvent.NavigateToLogin)
                    }
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                getSettingsDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
