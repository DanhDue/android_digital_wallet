/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mynfts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.components.ui.theme.DarkText
import com.danhdue.components.ui.theme.LightText
import com.danhdue.components.ui.theme.NeutralGray
import com.danhdue.mywallet.presentation.model.MyNFTsUiModel

/**
 * Composable entry point for the MyNFTs feature.
 */
@Composable
fun MyNFTsRoot(
    viewModel: MyNFTsViewModel = hiltViewModel(),
    onEvent: (MyNFTsEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            onEvent(event)
        }
    }

    MyNFTsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the MyNFTs feature.
 */
@Composable
@Suppress("UnusedParameter")
private fun MyNFTsScreen(
    state: MyNFTsState,
    onAction: (MyNFTsAction) -> Unit,
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.items) { nft ->
                NFTGridItem(nft = nft, onClick = { /* TODO */ })
            }
        }
    }
}

@Composable
fun NFTGridItem(
    nft: MyNFTsUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralGray),
    ) {
        Column {
            // NFT Image Placeholder
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.LightGray),
            ) {
                Text(
                    text = "NFT",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = nft.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                )
                Text(
                    text = nft.collectionName,
                    fontSize = 12.sp,
                    color = LightText,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyNFTsScreen() {
    MyNFTsScreen(
        state =
            MyNFTsState(
                isLoading = false,
                items =
                    listOf(
                        MyNFTsUiModel(
                            id = "1",
                            name = "Ape #1",
                            collectionName = "Bored Ape Yacht Club",
                            imageUrl = "",
                        ),
                        MyNFTsUiModel(
                            id = "2",
                            name = "Punk #2",
                            collectionName = "CryptoPunks",
                            imageUrl = "",
                        ),
                    ),
            ),
        onAction = {},
    )
}
