/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.{{screen.camelCase()}}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Composable entry point for the {{screen.pascalCase()}} feature.
 */
@Composable
fun {{screen.pascalCase()}}Root(
    viewModel: {{screen.pascalCase()}}ViewModel = hiltViewModel(),
    onEvent: ({{screen.pascalCase()}}Event) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnEvent by rememberUpdatedState(onEvent)

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    {{screen.pascalCase()}}Screen(
        state = state,
    )
}

/**
 * A stateless composable that draws the UI for the {{screen.pascalCase()}} feature.
 */
@Composable
private fun {{screen.pascalCase()}}Screen(state: {{screen.pascalCase()}}State) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "{{screen.pascalCase()}} Screen")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview{{screen.pascalCase()}}Screen() {
    {{screen.pascalCase()}}Screen(
        state = {{screen.pascalCase()}}State(),
    )
}
