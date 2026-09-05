/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import com.danhdue.settings.domain.model.SupportedLanguage

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Settings feature.
 */
sealed interface SettingsAction {
    data object OpenProfile : SettingsAction

    data class ToggleDarkMode(
        val isDarkMode: Boolean,
    ) : SettingsAction

    data object OpenLanguagePicker : SettingsAction

    data object DismissLanguagePicker : SettingsAction

    data class SelectLanguage(
        val language: SupportedLanguage,
    ) : SettingsAction

    data object OpenSecurity : SettingsAction

    data object OpenDeveloperOptions : SettingsAction

    data object Logout : SettingsAction
}
