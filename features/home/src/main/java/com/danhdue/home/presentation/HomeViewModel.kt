/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Manages the business logic and state for the Home screen (the main tabbed container).
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.TabSelected -> {
                _state.update { it.copy(selectedTab = action.tab) }
            }
            is HomeAction.NavigateInTab -> {
                when (action.tab) {
                    HomeTab.Wallet -> _state.value.walletBackStack.add(action.destination)
                    HomeTab.Transactions -> _state.value.transactionsBackStack.add(action.destination)
                    HomeTab.Scanner -> _state.value.scannerBackStack.add(action.destination)
                    HomeTab.Trends -> _state.value.trendsBackStack.add(action.destination)
                    HomeTab.Settings -> _state.value.settingsBackStack.add(action.destination)
                }
            }
            is HomeAction.PopInTab -> {
                val backstack =
                    when (action.tab) {
                        HomeTab.Wallet -> _state.value.walletBackStack
                        HomeTab.Transactions -> _state.value.transactionsBackStack
                        HomeTab.Scanner -> _state.value.scannerBackStack
                        HomeTab.Trends -> _state.value.trendsBackStack
                        HomeTab.Settings -> _state.value.settingsBackStack
                    }
                if (backstack.size > 1) {
                    backstack.removeAt(backstack.size - 1)
                }
            }
        }
    }
}
