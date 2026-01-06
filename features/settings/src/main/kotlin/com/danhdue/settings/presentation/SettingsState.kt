/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import com.danhdue.settings.presentation.model.SettingsUiModel

/**
 * Represents the state of the Settings screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val items: List<SettingsUiModel> = emptyList(),
)
