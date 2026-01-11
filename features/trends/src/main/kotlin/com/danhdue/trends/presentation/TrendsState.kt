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
 * @property searchQuery The current search query string.
 * @property items The list of UI models to be displayed.
 */
data class TrendsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val items: List<TrendsUiModel> = emptyList(),
) {
    val filteredItems: List<TrendsUiModel>
        get() = if (searchQuery.isEmpty()) {
            items
        } else {
            items.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
}
