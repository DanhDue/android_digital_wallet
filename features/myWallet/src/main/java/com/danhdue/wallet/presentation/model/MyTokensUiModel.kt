/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.model

/**
 * Represents the UI model for a single item in the MyTokens feature.
 * This class is optimized for display in the Presentation Layer.
 */
data class MyTokensUiModel(
    val id: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val balance: String,
    val fiatBalance: String,
    val price: String,
    val priceChange: String,
    val isPositive: Boolean,
    val sparklineData: List<Float>,
)
