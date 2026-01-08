/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Scanner feature.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val getScannerDataUseCase: GetScannerDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ScannerState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ScannerEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: ScannerAction) {
        when (action) {
            else -> {
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getScannerDataUseCase()
                .onSuccess {
                }.onFailure {
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
