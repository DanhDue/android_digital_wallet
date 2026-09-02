/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the tab state for the Shell (the Host's main tabbed container).
 * Follows MVI pattern with immutable state updates.
 */
@HiltViewModel
class ShellViewModel @Inject constructor() :
    MviViewModel<ShellState, ShellAction, ShellEvent>(
        initialState = ShellState(),
    ) {
        override fun onAction(action: ShellAction) {
            when (action) {
                is ShellAction.TabSelected -> {
                    reduce { copy(selectedTab = action.tab) }
                }
                is ShellAction.NavigateInTab -> {
                    reduce {
                        when (action.tab) {
                            ShellTab.Wallet -> copy(walletBackStack = walletBackStack + action.destination)
                            ShellTab.Transactions -> copy(transactionsBackStack = transactionsBackStack + action.destination)
                            ShellTab.Scanner -> copy(scannerBackStack = scannerBackStack + action.destination)
                            ShellTab.Trends -> copy(trendsBackStack = trendsBackStack + action.destination)
                            ShellTab.Settings -> copy(settingsBackStack = settingsBackStack + action.destination)
                        }
                    }
                }
                is ShellAction.PopInTab -> {
                    reduce {
                        when (action.tab) {
                            ShellTab.Wallet -> {
                                if (walletBackStack.size > 1) {
                                    copy(walletBackStack = walletBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            ShellTab.Transactions -> {
                                if (transactionsBackStack.size > 1) {
                                    copy(transactionsBackStack = transactionsBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            ShellTab.Scanner -> {
                                if (scannerBackStack.size > 1) {
                                    copy(scannerBackStack = scannerBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            ShellTab.Trends -> {
                                if (trendsBackStack.size > 1) {
                                    copy(trendsBackStack = trendsBackStack.dropLast(1))
                                } else {
                                    this
                                }
                            }
                            ShellTab.Settings -> {
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
