/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.mywallet.presentation.mynfts.MyNFTsRoot
import com.danhdue.mywallet.presentation.mytokens.MyTokensRoot
import com.danhdue.mywallet.presentation.theme.MyWalletColors

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
            WalletHeader(
                networkName = state.networkName,
                onSearchClick = {},
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            WalletCard(
                totalBalance = state.totalBalance,
                walletAddress = state.walletAddress,
                isBalanceVisible = state.isBalanceVisible,
                onToggleVisibility = { onAction(MyWalletAction.ToggleBalanceVisibility) },
                onCopyAddress = { onAction(MyWalletAction.CopyAddress) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuickActionRow()

            Spacer(modifier = Modifier.height(24.dp))

            TokenNFTTabs(
                selectedTabIndex = state.selectedTabIndex,
                onTabSelected = { onAction(MyWalletAction.TabChanged(it)) },
            )

            Box(modifier = Modifier.weight(1.0f)) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                    .background(MyWalletColors.NeutralGray),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.align(Alignment.Center),
                tint = MyWalletColors.LightText,
            )
        }

        // Network Selector
        Surface(
            onClick = { /* TODO */ },
            shape = RoundedCornerShape(20.dp),
            color = MyWalletColors.NeutralGray,
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
                            .background(MyWalletColors.PrimaryBlue),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = networkName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MyWalletColors.DarkText,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MyWalletColors.DarkText,
                )
            }
        }

        // Search Icon
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MyWalletColors.DarkText,
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
                .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MyWalletColors.WalletCardGradient)
                    .padding(24.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "USD",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                            tint = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    onClick = onCopyAddress,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = walletAddress,
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        QuickActionButton(icon = Icons.AutoMirrored.Filled.CallReceived, label = "Receive")
        QuickActionButton(icon = Icons.Default.ShoppingCart, label = "Buy")
        QuickActionButton(icon = Icons.AutoMirrored.Filled.Send, label = "Send")
        QuickActionButton(icon = Icons.Default.SwapHoriz, label = "Swap")
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MyWalletColors.PrimaryBlue)
                    .clickable { /* TODO */ },
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MyWalletColors.DarkText,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun TokenNFTTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val tabs = listOf("TOKENS", "NFTs")

    Surface(
        color = MyWalletColors.NeutralGray,
        shape = RoundedCornerShape(12.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MyWalletColors.PrimaryBlue else Color.Transparent)
                            .clickable { onTabSelected(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else MyWalletColors.LightText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
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
