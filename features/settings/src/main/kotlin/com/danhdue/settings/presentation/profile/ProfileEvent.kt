/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.profile

/**
 * Defines the one-off events that the ViewModel can send to the UI.
 * These events are meant to be consumed only once (e.g., navigation, snackbar).
 */
sealed interface ProfileEvent {
    data object NavigateBack : ProfileEvent
}
