/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Composable entry point for the Scanner feature.
 */
@Composable
fun ScannerRoot(
    viewModel: ScannerViewModel = hiltViewModel(),
    onEvent: (ScannerEvent) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val currentOnEvent by androidx.compose.runtime.rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    ScannerScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

/**
 * A stateless composable that draws the UI for the Scanner feature.
 */
@Composable
@Suppress("UnusedParameter")
private fun ScannerScreen(
    state: ScannerState,
    onAction: (ScannerAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Feature: Scanner")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScannerScreen() {
    ScannerScreen(
        state = ScannerState(isLoading = false),
        onAction = {},
    )
}
