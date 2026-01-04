/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.presentation

import com.danhdue.authentication.register.presentation.model.RegisterUiModel

/**
 * Represents the state of the Register screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val items: List<RegisterUiModel> = emptyList(),
)
