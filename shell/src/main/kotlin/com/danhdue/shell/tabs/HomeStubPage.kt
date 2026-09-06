/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danhdue.uikit.R
import com.danhdue.uikit.localization.appStringResource
import com.danhdue.uikit.ui.theme.AndroidDigitalWalletTheme

/**
 * Minimal placeholder for the Home tab (registered in `ShellNavigationModule` as
 * `entry<HomeStubRoute>`). Replace with a real feature module — or a host
 * dashboard — when the project needs one.
 *
 * Provenance: what remained of the dissolved `features/home` after its tab-shell
 * UI moved into `:shell` (Task 10) and the wallet domain was stripped (Task 13).
 */
@Composable
fun HomeStubPage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = appStringResource(R.string.bottom_menu_home, "home.main.title"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeStubPagePreview() {
    AndroidDigitalWalletTheme {
        HomeStubPage()
    }
}
