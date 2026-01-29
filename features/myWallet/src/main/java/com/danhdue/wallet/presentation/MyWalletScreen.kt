/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.BiasAlignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.components.ui.theme.DarkText
import com.danhdue.components.ui.theme.LightText
import com.danhdue.components.ui.theme.NeutralGray
import com.danhdue.components.ui.theme.PrimaryBlue
import com.danhdue.components.ui.theme.TrueBlue
import com.danhdue.components.ui.theme.WalletCardGradient
import com.danhdue.components.ui.widgets.WalletActionBar
import com.danhdue.components.ui.widgets.WalletHomeHeaderBar
import com.danhdue.libraries.components.R
import com.danhdue.wallet.presentation.mynfts.MyNFTsRoot
import com.danhdue.wallet.presentation.mytokens.MyTokensRoot

/**
 * Composable entry point for the MyWallet feature.
 */
@Composable
fun MyWalletRoot(
    viewModel: MyWalletViewModel = hiltViewModel(),
    onEvent: (MyWalletEvent) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            onEvent(event)
        }
    }

    MyWalletScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that draws the UI for the MyWallet feature.
 */
@Composable
private fun MyWalletScreen(
    state: MyWalletState,
    onAction: (MyWalletAction) -> Unit,
) {
    Scaffold(
        topBar = {
            WalletHomeHeaderBar()
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            WalletCard(
                totalBalance = state.totalBalance,
                walletAddress = state.walletAddress,
                isBalanceVisible = state.isBalanceVisible,
                onToggleVisibility = { onAction(MyWalletAction.ToggleBalanceVisibility) },
                onCopyAddress = { onAction(MyWalletAction.CopyAddress) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            WalletActionBar()
            Spacer(modifier = Modifier.height(16.dp))
            TokenNFTTabs(
                selectedTabIndex = state.selectedTabIndex,
                onTabSelected = { onAction(MyWalletAction.TabChanged(it)) },
            )

            Box(modifier = Modifier.weight(1.0f)) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    if (LocalInspectionMode.current) {
                        // In preview, just show a placeholder to avoid ViewModel instantiation crashes
                        Text(
                            text = if (state.selectedTabIndex == 0) "My Tokens Content" else "My NFTs Content",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        when (state.selectedTabIndex) {
                            0 -> MyTokensRoot(onEvent = {})
                            1 -> MyNFTsRoot(onEvent = {})
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletHeader(
    networkName: String,
    onSearchClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Profile Image Placeholder
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeutralGray),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.align(Alignment.Center),
                tint = LightText,
            )
        }

        // Network Selector
        Surface(
            onClick = { /* TODO */ },
            shape = RoundedCornerShape(20.dp),
            color = NeutralGray,
            modifier = Modifier.height(36.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                // Network Logo Placeholder
                Box(
                    modifier =
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = networkName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkText,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = DarkText,
                )
            }
        }

        // Search Icon
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = DarkText,
            )
        }
    }
}

@Composable
fun WalletCard(
    totalBalance: String,
    walletAddress: String,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onCopyAddress: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(), // Increased height to accommodate new content
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(WalletCardGradient),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_finger_print_1),
                contentDescription = null,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-68).dp, y = (-12).dp),
                alpha = 0.6f,
            )

            Image(
                painter = painterResource(id = R.drawable.ic_finger_print_2),
                contentDescription = null,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 36.dp, y = 8.dp),
                alpha = 0.6f,
            )

            Image(
                painter = painterResource(id = R.drawable.ic_card_arrow_down),
                contentDescription = null,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 24.dp),
                alpha = 0.8f,
            )

            Column(
                modifier =
                    Modifier
                        .padding(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Account 1",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_vertical_dots),
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBalanceVisible) totalBalance else "••••••••",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onToggleVisibility,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Balance",
                            tint = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isBalanceVisible) {
                    Surface(
                        color = Color.White.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp, end = 6.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_alt_ldown),
                                contentDescription = "Balance",
                                tint = Color(0xFF8A0000),
                            )
                            Text(
                                text = "$29358926.27 (-6.03%)", // Hardcoded as per request/image
                                color = Color(0xFF8A0000), // Dark Red text
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCopyAddress() },
                ) {
                    Text(
                        text = walletAddress,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun TokenNFTTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val tabs = listOf("TOKENS", "NFTs")

    // Animate the horizontal bias from -1f (left) to 1f (right)
    val animatedBias by animateFloatAsState(
        targetValue = if (selectedTabIndex == 0) -1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "tabIndicatorAnimation"
    )

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, TrueBlue),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
    ) {
        Box(modifier = Modifier.padding(6.dp)) {
            // Animated sliding indicator
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .align(BiasAlignment(animatedBias, 0f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(TrueBlue)
            )

            // Tab buttons
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onTabSelected(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else LightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WalletCardPreview() {
    WalletCard(
        totalBalance = "$51,245.89",
        walletAddress = "0xB38...844d",
        isBalanceVisible = true,
        onToggleVisibility = {},
        onCopyAddress = {})
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyWalletScreen() {
    MyWalletScreen(
        state =
            MyWalletState(
                isLoading = false,
                totalBalance = "$51,245.89",
                walletAddress = "0xB38...844d",
            ),
        onAction = {},
    )
}
