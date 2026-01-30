/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactionlist

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
 * Composable entry point for the TransactionList feature.
 */
@Composable
fun TransactionListRoot(
    viewModel: TransactionListViewModel = hiltViewModel(),
    onEvent: (TransactionListEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentOnEvent by androidx.compose.runtime.rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    TransactionListScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the TransactionList feature.
 */
@Suppress("UnusedParameter")
@Composable
private fun TransactionListScreen(
    state: TransactionListState,
    onAction: (TransactionListAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Feature: TransactionList")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionListScreen() {
    TransactionListScreen(
        state = TransactionListState(isLoading = false),
        onAction = {},
    )
}
