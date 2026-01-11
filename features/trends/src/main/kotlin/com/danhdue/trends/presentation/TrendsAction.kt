/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Trends feature.
 */
sealed interface TrendsAction {
    data class SearchQueryChanged(val query: String) : TrendsAction
    data class CoinClicked(val coinId: String) : TrendsAction
}
