/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mytokens

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the MyTokens feature.
 */
sealed interface MyTokensAction {
    data object Refresh : MyTokensAction
}
