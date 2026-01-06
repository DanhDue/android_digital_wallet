/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.trends.domain.usecase.GetTrendsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Trends feature.
 */
@HiltViewModel
class TrendsViewModel
    @Inject
    constructor(
        private val getTrendsDataUseCase: GetTrendsDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(TrendsState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<TrendsEvent>()
        val event = _event.asSharedFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: TrendsAction) {
            when (action) {
                else -> {
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                getTrendsDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
