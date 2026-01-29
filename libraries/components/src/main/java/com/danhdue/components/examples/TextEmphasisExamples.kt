/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.components.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danhdue.components.ui.theme.BodyTextEmphasis
import com.danhdue.components.ui.theme.TextEmphasis
import com.danhdue.components.ui.theme.withEmphasis
import com.danhdue.components.ui.theme.withFontWeight

/**
 * Examples demonstrating different ways to use emphasized text in Material 3
 */
@Composable
fun TextEmphasisExamples() {
    Column(modifier = Modifier.padding(16.dp)) {

        // Method 1: Using FontWeight directly in Text composable
        Text(
            text = "Emphasized with Bold",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Emphasized with SemiBold",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Emphasized with Medium",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        // Method 2: Using style.copy() to modify the TextStyle
        Text(
            text = "Modified TextStyle with Bold",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        // Method 3: Using the custom extension function
        Text(
            text = "Using withFontWeight extension",
            style = MaterialTheme.typography.bodyLarge.withFontWeight(FontWeight.Bold)
        )

        // Method 4: Using opacity-based emphasis (Material Design recommendation)
        Text(
            text = "High Emphasis (100% opacity)",
            style = MaterialTheme.typography.bodyLarge.withEmphasis(TextEmphasis.High)
        )

        Text(
            text = "Medium Emphasis (60% opacity)",
            style = MaterialTheme.typography.bodyLarge.withEmphasis(TextEmphasis.Medium)
        )

        Text(
            text = "Disabled (38% opacity)",
            style = MaterialTheme.typography.bodyLarge.withEmphasis(TextEmphasis.Disabled)
        )

        // Method 5: Using pre-defined emphasized body text styles
        Text(
            text = "Body Large Emphasis",
            style = BodyTextEmphasis.bodyLargeEmphasis()
        )

        Text(
            text = "Body Medium Emphasis",
            style = BodyTextEmphasis.bodyMediumEmphasis()
        )

        Text(
            text = "Body Small Emphasis",
            style = BodyTextEmphasis.bodySmallEmphasis()
        )

        // Method 6: Combining font weight and opacity
        Text(
            text = "Bold + Medium Emphasis",
            style = MaterialTheme.typography.bodyLarge
                .withFontWeight(FontWeight.Bold)
                .withEmphasis(TextEmphasis.Medium)
        )

        // Method 7: Using Title styles (they have Medium weight by default)
        Text(
            text = "Title Medium (naturally emphasized)",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Title Small (naturally emphasized)",
            style = MaterialTheme.typography.titleSmall
        )

        // Method 8: Using Label styles (they have Medium weight by default)
        Text(
            text = "Label Large (naturally emphasized)",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Example showing emphasized text in a real-world scenario
 */
@Composable
fun RealWorldExample() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Heading with natural emphasis
        Text(
            text = "Account Balance",
            style = MaterialTheme.typography.titleLarge
        )

        // Amount with bold emphasis
        Text(
            text = "$1,234.56",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        // Label with medium weight (naturally emphasized)
        Text(
            text = "Available Balance",
            style = MaterialTheme.typography.labelMedium
        )

        // Secondary text with medium emphasis (reduced opacity)
        Text(
            text = "Last updated 5 minutes ago",
            style = MaterialTheme.typography.bodySmall.withEmphasis(TextEmphasis.Medium)
        )
    }
}
