/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.profile

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Profile feature.
 */
sealed interface ProfileAction {
    data object OnBackClicked : ProfileAction
}
