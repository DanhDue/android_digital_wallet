/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.danhdue.components.ui.theme.GrayText
import com.danhdue.components.ui.theme.Green
import com.danhdue.components.ui.theme.LightGray
import com.danhdue.components.ui.theme.RedError
import com.danhdue.trends.presentation.model.TrendsUiModel

/**
 * Composable entry point for the Trends feature.
 */
@Composable
fun TrendsRoot(
    viewModel: TrendsViewModel = hiltViewModel(),
    onEvent: (TrendsEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentOnEvent by androidx.compose.runtime.rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    TrendsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the Trends feature.
 */
@Composable
private fun TrendsScreen(
    state: TrendsState,
    onAction: (TrendsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onAction(TrendsAction.SearchQueryChanged(it)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0.dp),
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                CoinList(
                    coins = state.filteredItems,
                    onCoinClick = { onAction(TrendsAction.CoinClicked(it)) },
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search...", color = GrayText) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
        trailingIcon = { Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(30.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = LightGray.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = LightGray.copy(alpha = 0.1f),
                focusedContainerColor = LightGray.copy(alpha = 0.1f),
            ),
        singleLine = true,
    )
}

@Composable
private fun CoinList(
    coins: List<TrendsUiModel>,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(coins, key = { it.id }) { coin ->
            CoinItem(coin = coin, onClick = { onCoinClick(coin.id) })
        }
    }
}

@Composable
private fun CoinItem(
    coin: TrendsUiModel,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = coin.name,
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = coin.marketCap,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = coin.price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isPositive = coin.priceChangePercent >= 0
                val color = if (isPositive) Green else RedError
                val icon = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = coin.priceChangeFormatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("MagicNumber")
private fun PreviewTrendsScreen() {
    TrendsScreen(
        state =
            TrendsState(
                isLoading = false,
                items =
                    listOf(
                        TrendsUiModel(
                            "1",
                            "BTC",
                            "Bitcoin",
                            "https://cryptologos.cc/logos/bitcoin-btc-logo.png",
                            "$90,921.15",
                            "$39.33B",
                            -1.81,
                            "-1.81%",
                        ),
                        TrendsUiModel(
                            "2",
                            "ETH",
                            "Ethereum",
                            "https://cryptologos.cc/logos/ethereum-eth-logo.png",
                            "$3,150.55",
                            "$22.07B",
                            -3.20,
                            "-3.20%",
                        ),
                    ),
            ),
        onAction = {},
    )
}
