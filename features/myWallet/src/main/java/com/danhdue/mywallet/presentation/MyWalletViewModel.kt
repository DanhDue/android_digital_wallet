/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.framework.network.NetworkResult
import com.danhdue.mywallet.domain.usecase.GetMyWalletDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the MyWallet feature.
 */
@HiltViewModel
class MyWalletViewModel @Inject constructor(
    private val getMyWalletDataUseCase: GetMyWalletDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MyWalletState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyWalletEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: MyWalletAction) {
        when (action) {
            is MyWalletAction.ToggleBalanceVisibility -> {
                _state.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
            }
            is MyWalletAction.CopyAddress -> {
                // Handle copy address event if needed
            }
            is MyWalletAction.TabChanged -> {
                _state.update { it.copy(selectedTabIndex = action.index) }
            }
        }
    }

    @Suppress("UnusedPrivateProperty")
    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    totalBalance = "$51,245.89",
                    walletAddress = "0xB38...844d",
                )
            }
            when (val result = getMyWalletDataUseCase()) {
                is NetworkResult.Success -> {
                    // Handle success
                }
                is NetworkResult.Error -> {
                    // Handle error
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
