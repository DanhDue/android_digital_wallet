/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the business logic and state for the Home screen (the main tabbed container).
 * Follows MVI pattern with immutable state updates.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() :
    MviViewModel<HomeState, HomeAction, HomeEvent>(
        initialState = HomeState(),
    ) {
        override fun onAction(action: HomeAction) {
            when (action) {
                is HomeAction.TabSelected -> {
                    reduce { copy(selectedTab = action.tab) }
                }
                is HomeAction.NavigateInTab -> {
                    reduce {
                        when (action.tab) {
                            HomeTab.Wallet -> copy(walletBackStack = walletBackStack + action.destination)
                            HomeTab.Transactions -> copy(transactionsBackStack = transactionsBackStack + action.destination)
                            HomeTab.Scanner -> copy(scannerBackStack = scannerBackStack + action.destination)
                            HomeTab.Trends -> copy(trendsBackStack = trendsBackStack + action.destination)
                            HomeTab.Settings -> copy(settingsBackStack = settingsBackStack + action.destination)
                        }
                    }
                }
                is HomeAction.PopInTab -> {
                    reduce {
                        when (action.tab) {
                            HomeTab.Wallet -> {
                                if (walletBackStack.size > 1) {
                                    copy(walletBackStack = walletBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            HomeTab.Transactions -> {
                                if (transactionsBackStack.size > 1) {
                                    copy(transactionsBackStack = transactionsBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            HomeTab.Scanner -> {
                                if (scannerBackStack.size > 1) {
                                    copy(scannerBackStack = scannerBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            HomeTab.Trends -> {
                                if (trendsBackStack.size > 1) {
                                    copy(trendsBackStack = trendsBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            HomeTab.Settings -> {
                                if (settingsBackStack.size > 1) {
                                    copy(settingsBackStack = settingsBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                        }
                    }
                }
            }
        }
    }
