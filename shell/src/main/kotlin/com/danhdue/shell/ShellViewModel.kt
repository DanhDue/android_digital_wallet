/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.framework.navigation.Navigator
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.FeatureInstaller
import com.danhdue.platform.deeplink.DeepLinkRouter
import com.danhdue.platform.deeplink.FailureReason
import com.danhdue.platform.deeplink.NavigationCommand
import com.danhdue.uikit.R
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
 * ready immediately). While it runs [ShellState.installingModules] contains the module; once
 * the split is installed [ShellState.readyModules] contains the module and `ShellScreen`
 * folds the split's `ServiceLoader`-loaded `FeatureEntry` into the entry
 * provider.
 *
 * Task 6 — deep link placement command execution:
 * consumes [DeepLinkRouter.commands] and updates nested tab back stacks or the root [Navigator].
 */
@HiltViewModel
class ShellViewModel
    @Inject
    constructor(
        private val appEventBus: AppEventBus,
        private val featureInstaller: FeatureInstaller,
        private val navigator: Navigator,
        private val deepLinkRouter: DeepLinkRouter,
    ) : MviViewModel<ShellState, ShellAction, ShellEvent>(
            initialState = ShellState(),
        ) {
        init {
            safeLaunch {
                appEventBus
                    .on<AppEvent.ProfileNameChanged>()
                    .collect { event ->
                        reduce { copy(profileName = event.displayName) }
                    }
            }
            safeLaunch {
                deepLinkRouter.commands.collect { command ->
                    executeCommand(command)
                }
            }
        }

        override fun onAction(action: ShellAction) {
            when (action) {
                is ShellAction.TabSelected -> {
                    reduce { copy(selectedTab = action.tab) }
                    if (action.tab == ShellTab.Scanner) {
                        ensureModuleInstalled("scanner")
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

        private fun executeCommand(command: NavigationCommand) {
            when (command) {
                is NavigationCommand.OpenInTab -> openInTab(command)
                is NavigationCommand.OpenFullScreen -> openFullScreen(command)
                is NavigationCommand.OpenInCurrentTab -> openInCurrentTab(command)
                is NavigationCommand.EnsureModule ->
                    ensureModuleInstalled(command.module) {
                        deepLinkRouter.dispatch(command.replay)
                    }
                is NavigationCommand.Failed -> handleFailure(command.reason, command.raw)
            }
        }

        private fun openInTab(command: NavigationCommand.OpenInTab) {
            val targetTab =
                ShellTab.fromIndex(command.tab) ?: run {
                    Timber.w("OpenInTab ignored: unknown tab index %d", command.tab)
                    return
                }
            val destination = command.stack.lastOrNull()
            if (currentState.selectedTab == targetTab && topOf(targetTab) == destination) {
                return
            }
            reduce {
                when (targetTab) {
                    ShellTab.Home -> copy(selectedTab = targetTab, homeBackStack = command.stack)
                    ShellTab.Scanner -> copy(selectedTab = targetTab, scannerBackStack = command.stack)
                    ShellTab.Settings -> copy(selectedTab = targetTab, settingsBackStack = command.stack)
                }
            }
        }

        private fun openFullScreen(command: NavigationCommand.OpenFullScreen) {
            if (navigator.backStack.lastOrNull() == command.destination) {
                return
            }
            navigator.navigateTo(command.destination)
        }

        private fun openInCurrentTab(command: NavigationCommand.OpenInCurrentTab) {
            val activeTab = currentState.selectedTab
            if (topOf(activeTab) == command.destination) {
                return
            }
            reduce {
                when (activeTab) {
                    ShellTab.Home -> copy(homeBackStack = homeBackStack + command.destination)
                    ShellTab.Scanner -> copy(scannerBackStack = scannerBackStack + command.destination)
                    ShellTab.Settings -> copy(settingsBackStack = settingsBackStack + command.destination)
                }
            }
        }

        private fun topOf(tab: ShellTab): Any? =
            when (tab) {
                ShellTab.Home -> currentState.homeBackStack.lastOrNull()
                ShellTab.Scanner -> currentState.scannerBackStack.lastOrNull()
                ShellTab.Settings -> currentState.settingsBackStack.lastOrNull()
            }

        private fun handleFailure(
            reason: FailureReason,
            raw: String? = null,
        ) {
            Timber.w("Deep link dispatch failed: reason=%s, raw=%s", reason, raw)
            val messageEvent =
                when (reason) {
                    FailureReason.Malformed -> null
                    FailureReason.UnknownFeature, FailureReason.NoResolver ->
                        ShellEvent.ShowMessage(
                            messageRes = R.string.deeplink_error_unknown_feature,
                            localizationKey = "deeplink.error.unknownFeature",
                        )
                    FailureReason.Blocked ->
                        ShellEvent.ShowMessage(
                            messageRes = R.string.deeplink_error_blocked,
                            localizationKey = "deeplink.error.blocked",
                        )
                    FailureReason.RedirectLoop -> null
                    FailureReason.InstallFailed ->
                        ShellEvent.ShowMessage(
                            messageRes = R.string.deeplink_error_install_failed,
                            localizationKey = "deeplink.error.installFailed",
                        )
                }
            if (messageEvent != null) {
                sendEvent(messageEvent)
            }
        }

        private fun ensureModuleInstalled(
            module: String,
            onInstalled: (() -> Unit)? = null,
        ) {
            if (module in currentState.readyModules) {
                onInstalled?.invoke()
                return
            }
            if (module in currentState.installingModules) return
            reduce { copy(installingModules = installingModules + module) }
            safeLaunch {
                runCatching {
                    featureInstaller.ensureInstalled(module) {
                        reduce {
                            copy(
                                readyModules = readyModules + module,
                                installingModules = installingModules - module,
                            )
                        }
                        onInstalled?.invoke()
                    }
                }.onFailure { error ->
                    Timber.w(error, "$module split install failed")
                    reduce { copy(installingModules = installingModules - module) }
                    handleFailure(FailureReason.InstallFailed)
                }
            }
        }
    }
