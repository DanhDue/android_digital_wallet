/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.FeatureInstaller
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages the tab state for the Shell (the Host's main tabbed container).
 * Follows MVI pattern with immutable state updates.
 *
 * Task 11 pilot — cross-feature signalling via [AppEventBus], no feature import:
 * subscribes to [AppEvent.ProfileNameChanged] (published by `:features:settings`)
 * and mirrors the name into [ShellState.profileName], which `ShellScreen` renders
 * as the Settings bottom-bar tab label.
 *
 * Task 14 — on-demand Dynamic Feature Module install branch (design §4.4):
 * selecting the Scanner tab for the first time triggers
 * [FeatureInstaller.ensureInstalled] for the `scanner` split (a no-op in unit
 * tests, where [com.danhdue.platform.NoOpFeatureInstaller] reports every module
 * ready immediately). While it runs [ShellState.scannerInstalling] is true; once
 * the split is installed [ShellState.scannerReady] flips and `ShellScreen`
 * folds the split's `ServiceLoader`-loaded `FeatureEntry` into the entry
 * provider.
 */
@HiltViewModel
class ShellViewModel
    @Inject
    constructor(
        private val appEventBus: AppEventBus,
        private val featureInstaller: FeatureInstaller,
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
                    if (action.tab == ShellTab.Scanner) {
                        ensureScannerInstalled()
                    }
                }
                is ShellAction.NavigateInTab -> {
                    reduce {
                        when (action.tab) {
                            ShellTab.Home -> copy(homeBackStack = homeBackStack + action.destination)
                            ShellTab.Scanner -> copy(scannerBackStack = scannerBackStack + action.destination)
                            ShellTab.Settings -> copy(settingsBackStack = settingsBackStack + action.destination)
                        }
                    }
                }
                is ShellAction.PopInTab -> {
                    reduce {
                        when (action.tab) {
                            ShellTab.Home -> {
                                if (homeBackStack.size > 1) {
                                    copy(homeBackStack = homeBackStack.dropLast(1))
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

        private fun ensureScannerInstalled() {
            if (currentState.scannerReady || currentState.scannerInstalling) return
            reduce { copy(scannerInstalling = true) }
            safeLaunch {
                runCatching {
                    featureInstaller.ensureInstalled(SCANNER_MODULE) {
                        reduce { copy(scannerReady = true, scannerInstalling = false) }
                    }
                }.onFailure { error ->
                    Timber.w(error, "scanner split install failed")
                    reduce { copy(scannerInstalling = false) }
                }
            }
        }

        private companion object {
            const val SCANNER_MODULE = "scanner"
        }
    }
