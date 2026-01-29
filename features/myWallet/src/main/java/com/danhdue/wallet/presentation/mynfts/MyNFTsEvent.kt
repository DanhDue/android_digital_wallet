/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mynfts

/**
 * Defines the one-off events that the ViewModel can send to the UI.
 * These events are meant to be consumed only once (e.g., navigation, snackbar).
 */
sealed interface MyNFTsEvent {
    data class ShowSnackbar(
        val message: String,
    ) : MyNFTsEvent
}
