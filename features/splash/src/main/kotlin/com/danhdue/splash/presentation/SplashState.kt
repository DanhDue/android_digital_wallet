/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.presentation

import com.danhdue.splash.presentation.model.SplashUiModel

/**
 * Represents the state of the Splash screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class SplashState(
    val isLoading: Boolean = false,
    val items: List<SplashUiModel> = emptyList(),
)
