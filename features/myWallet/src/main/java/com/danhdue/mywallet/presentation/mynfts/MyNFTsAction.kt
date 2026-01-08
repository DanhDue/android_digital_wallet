/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mynfts

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the MyNFTs feature.
 */
sealed interface MyNFTsAction {
    data object Refresh : MyNFTsAction
}
