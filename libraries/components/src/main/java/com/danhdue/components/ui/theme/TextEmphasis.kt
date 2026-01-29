/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.components.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Material 3 Text Emphasis Utilities
 *
 * Material Design uses opacity levels to indicate emphasis:
 * - High emphasis: 100% opacity (most important content)
 * - Medium emphasis: 60% opacity (secondary content)
 * - Disabled: 38% opacity (disabled/inactive content)
 */
object TextEmphasis {
    const val High = 1.00f
    const val Medium = 0.60f
    const val Disabled = 0.38f
}

/**
 * Apply emphasis to any TextStyle by adjusting the color opacity
 */
@Composable
@ReadOnlyComposable
fun TextStyle.withEmphasis(emphasis: Float): TextStyle {
    return this.copy(
        color = LocalContentColor.current.copy(alpha = emphasis)
    )
}

/**
 * Apply font weight emphasis to any TextStyle
 */
fun TextStyle.withFontWeight(fontWeight: FontWeight): TextStyle {
    return this.copy(fontWeight = fontWeight)
}

/**
 * Common emphasis variations for body text
 */
object BodyTextEmphasis {
    @Composable
    @ReadOnlyComposable
    fun bodyLargeEmphasis(): TextStyle {
        return MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    @ReadOnlyComposable
    fun bodyMediumEmphasis(): TextStyle {
        return MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold
        )
    }

    @Composable
    @ReadOnlyComposable
    fun bodySmallEmphasis(): TextStyle {
        return MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        )
    }
}
