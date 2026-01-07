/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mytokens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Composable entry point for the MyTokens feature.
 */
@Composable
fun MyTokensRoot(
    viewModel: MyTokensViewModel = hiltViewModel(),
    onEvent: (MyTokensEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            onEvent(event)
        }
    }

    MyTokensScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the MyTokens feature.
 */
@Composable
@Suppress("UnusedParameter")
private fun MyTokensScreen(
    state: MyTokensState,
    onAction: (MyTokensAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Feature: MyTokens")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyTokensScreen() {
    MyTokensScreen(
        state = MyTokensState(isLoading = false),
        onAction = {},
    )
}
