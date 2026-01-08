/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.profile

import com.danhdue.settings.presentation.model.ProfileUiModel

/**
 * Represents the state of the Profile screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class ProfileState(
    val isLoading: Boolean = false,
    val items: List<ProfileUiModel> = emptyList(),
)
