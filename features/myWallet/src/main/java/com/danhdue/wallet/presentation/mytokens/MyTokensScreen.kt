/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mytokens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.uikit.ui.theme.DarkText
import com.danhdue.uikit.ui.theme.ErrorRed
import com.danhdue.uikit.ui.theme.LightText
import com.danhdue.uikit.ui.theme.NeutralGray
import com.danhdue.uikit.ui.theme.PrimaryBlue
import com.danhdue.uikit.ui.theme.SuccessGreen
import com.danhdue.wallet.presentation.model.MyTokensUiModel

/**
 * Composable entry point for the MyTokens feature.
 */
@Composable
fun MyTokensRoot(
    viewModel: MyTokensViewModel = hiltViewModel(),
    onEvent: (MyTokensEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentOnEvent by androidx.compose.runtime.rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
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
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            items(state.items) { token ->
                TokenListItem(token = token, onClick = { /* TODO */ })
            }
        }
    }
}

@Composable
fun TokenListItem(
    token: MyTokensUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Token Icon
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeutralGray),
        ) {
            // Icon placeholder
            Text(
                text = token.symbol.take(1),
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Balance & Name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = token.balance,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
            )
            Text(
                text = token.fiatBalance,
                fontSize = 13.sp,
                color = LightText,
            )
        }

        // Sparkline Chart
        SparklineChart(
            data = token.sparklineData,
            color = if (token.isPositive) SuccessGreen else ErrorRed,
            modifier =
                Modifier
                    .width(60.dp)
                    .height(30.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Price & Change
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = token.price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
            )
            Text(
                text = token.priceChange,
                fontSize = 13.sp,
                color = if (token.isPositive) SuccessGreen else ErrorRed,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = NeutralGray,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
@Suppress("MagicNumber")
fun SparklineChart(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val distance = size.width / (data.size - 1)
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(0.1f)

        val path =
            Path().apply {
                data.forEachIndexed { index, value ->
                    val x = index * distance
                    val y = size.height - ((value - minVal) / range) * size.height
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("MagicNumber")
private fun PreviewMyTokensScreen() {
    MyTokensScreen(
        state =
            MyTokensState(
                isLoading = false,
                items =
                    listOf(
                        MyTokensUiModel(
                            id = "1",
                            symbol = "ETH",
                            name = "Ethereum",
                            iconUrl = "",
                            balance = "1.3135 ETH",
                            fiatBalance = "$2,430.34",
                            price = "$1,850.45",
                            priceChange = "+4.86%",
                            isPositive = true,
                            sparklineData = listOf(0.1f, 0.3f, 0.2f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f),
                        ),
                    ),
            ),
        onAction = {},
    )
}
