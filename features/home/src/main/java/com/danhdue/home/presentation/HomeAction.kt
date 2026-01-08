/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

/**
 * Defines the actions that can be sent from the UI to the ViewModel for the Home screen.
 */
sealed interface HomeAction {
    data class TabSelected(val tab: HomeTab) : HomeAction
    data class NavigateInTab(val tab: HomeTab, val destination: Any) : HomeAction
    data class PopInTab(val tab: HomeTab) : HomeAction
}
