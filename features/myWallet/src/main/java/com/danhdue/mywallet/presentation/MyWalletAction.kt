/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the MyWallet feature.
 */
sealed interface MyWalletAction {
    data object ToggleBalanceVisibility : MyWalletAction

    data object CopyAddress : MyWalletAction

    data class TabChanged(
        val index: Int,
    ) : MyWalletAction
}
