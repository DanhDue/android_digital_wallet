/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactiondetail

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
 * Composable entry point for the TransactionDetail feature.
 */
@Composable
fun TransactionDetailRoot(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onEvent: (TransactionDetailEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            onEvent(event)
        }
    }

    TransactionDetailScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the TransactionDetail feature.
 */
@Suppress("UnusedParameter")
@Composable
private fun TransactionDetailScreen(
    state: TransactionDetailState,
    onAction: (TransactionDetailAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Feature: TransactionDetail")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionDetailScreen() {
    TransactionDetailScreen(
        state = TransactionDetailState(isLoading = false),
        onAction = {},
    )
}
