/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

/**
 * Defines the one-off events that the ViewModel can send to the UI.
 * These events are meant to be consumed only once (e.g., navigation, snackbar).
 */
sealed interface SettingsEvent {
    data object NavigateToProfile : SettingsEvent

    data object NavigateToSecurity : SettingsEvent

    data object NavigateToDeveloperOptions : SettingsEvent

    data class ShowToast(
        val message: String,
    ) : SettingsEvent
}
