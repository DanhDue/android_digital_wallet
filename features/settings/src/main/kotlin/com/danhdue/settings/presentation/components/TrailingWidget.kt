/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.components

sealed interface TrailingWidget {
    data object Chevron : TrailingWidget

    data class SwitchToggle(
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
    ) : TrailingWidget

    data class ValueWithChevron(
        val valueText: String,
    ) : TrailingWidget

    data class Label(
        val text: String,
    ) : TrailingWidget
}
