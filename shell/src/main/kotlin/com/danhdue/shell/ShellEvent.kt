/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import androidx.annotation.StringRes

/**
 * Defines the one-off events that the ViewModel can send to the UI for the Shell.
 */
sealed interface ShellEvent {
    /**
     * Instructs the UI to display a user-facing informational or error message.
     *
     * @property messageRes String resource ID of the localized message.
     * @property localizationKey Optional remote key for OTA translations.
     */
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val localizationKey: String? = null,
    ) : ShellEvent
}
