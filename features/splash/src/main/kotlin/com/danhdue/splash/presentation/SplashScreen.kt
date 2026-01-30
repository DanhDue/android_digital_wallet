/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.presentation

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
 * Composable entry point for the Splash feature.
 */
@Composable
fun SplashRoot(
    viewModel: SplashViewModel = hiltViewModel(),
    onEvent: (SplashEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentOnEvent by androidx.compose.runtime.rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    SplashScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the Splash feature.
 */
@Suppress("UnusedParameter")
@Composable
private fun SplashScreen(
    state: SplashState,
    onAction: (SplashAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Feature: Splash")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSplashScreen() {
    SplashScreen(
        state = SplashState(isLoading = false),
        onAction = {},
    )
}
