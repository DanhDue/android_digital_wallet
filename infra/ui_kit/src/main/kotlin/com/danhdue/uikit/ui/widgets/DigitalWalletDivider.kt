/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danhdue.uikit.ui.theme.BluePrimary

@Composable
fun DigitalWalletDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier =
            modifier
                .fillMaxWidth()
                .height(1.dp),
        color = BluePrimary,
    )
}

@Preview("default", showBackground = true)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DividerPreview() {
    MaterialTheme {
        Box(Modifier.size(height = 10.dp, width = 100.dp)) {
            DigitalWalletDivider(Modifier.align(Alignment.Center))
        }
    }
}
