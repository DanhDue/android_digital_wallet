/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation.model

/**
 * Represents the UI model for a single coin in the Trends feature.
 */
data class TrendsUiModel(
    val id: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: String,
    val marketCap: String,
    val priceChangePercent: Double,
    val priceChangeFormatted: String,
)
