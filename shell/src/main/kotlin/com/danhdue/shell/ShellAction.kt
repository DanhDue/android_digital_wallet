/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

/**
 * Defines the actions that can be sent from the UI to the ViewModel for the Shell.
 */
sealed interface ShellAction {
    data class TabSelected(
        val tab: ShellTab,
    ) : ShellAction

    data class NavigateInTab(
        val tab: ShellTab,
        val destination: Any,
    ) : ShellAction

    data class PopInTab(
        val tab: ShellTab,
    ) : ShellAction
}
