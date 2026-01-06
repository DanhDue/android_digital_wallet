/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.transactions.domain.usecase.GetTransactionDetailDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the TransactionDetail feature.
 */
@HiltViewModel
class TransactionDetailViewModel
    @Inject
    constructor(
        private val getTransactionDetailDataUseCase: GetTransactionDetailDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(TransactionDetailState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<TransactionDetailEvent>()
        val event = _event.asSharedFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: TransactionDetailAction) {
            when (action) {
                else -> {
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                getTransactionDetailDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }
