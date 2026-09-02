/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the tab state for the Shell (the Host's main tabbed container).
 * Follows MVI pattern with immutable state updates.
 *
 * Task 11 pilot — cross-feature signalling via [AppEventBus], no feature import:
 * subscribes to [AppEvent.ProfileNameChanged] (published by `:features:settings`)
 * and mirrors the name into [ShellState.profileName], which `ShellScreen` renders
 * as the Settings bottom-bar tab label. This closes the Android analogue of the
 * Flutter shell's "notify tab" gap.
 */
@HiltViewModel
class ShellViewModel
    @Inject
    constructor(
        private val appEventBus: AppEventBus,
    ) : MviViewModel<ShellState, ShellAction, ShellEvent>(
            initialState = ShellState(),
        ) {
        init {
            safeLaunch {
                appEventBus.on<AppEvent.ProfileNameChanged>().collect { event ->
                    reduce { copy(profileName = event.displayName) }
                }
            }
        }

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
