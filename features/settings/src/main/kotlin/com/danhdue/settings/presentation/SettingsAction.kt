/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Settings feature.
 */
sealed interface SettingsAction {
    data object OpenProfile : SettingsAction
}
