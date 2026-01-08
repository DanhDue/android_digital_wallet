/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import com.danhdue.scanner.presentation.model.ScannerUiModel

/**
 * Represents the state of the Scanner screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class ScannerState(
    val isLoading: Boolean = false,
    val items: List<ScannerUiModel> = emptyList(),
)
