/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation

import com.danhdue.trends.presentation.model.TrendsUiModel

/**
 * Represents the state of the Trends screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class TrendsState(
    val isLoading: Boolean = false,
    val items: List<TrendsUiModel> = emptyList(),
)
