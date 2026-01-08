/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.framework.navigation.LocalEntryProviderInstallers

@Composable
fun HomeRoot(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    val installers = LocalEntryProviderInstallers.current
    val entryProvider = remember(installers) {
        entryProvider { installers.forEach { it() } }
    }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(HomeAction.TabSelected(it)) }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Nested navigation for each tab
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Wallet,
                backStack = state.walletBackStack,
                entryProvider = entryProvider,
                onBack = { onAction(HomeAction.PopInTab(HomeTab.Wallet)) }
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Transactions,
                backStack = state.transactionsBackStack,
                entryProvider = entryProvider,
                onBack = { onAction(HomeAction.PopInTab(HomeTab.Transactions)) }
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Scanner,
                backStack = state.scannerBackStack,
                entryProvider = entryProvider,
                onBack = { onAction(HomeAction.PopInTab(HomeTab.Scanner)) }
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Trends,
                backStack = state.trendsBackStack,
                entryProvider = entryProvider,
                onBack = { onAction(HomeAction.PopInTab(HomeTab.Trends)) }
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Settings,
                backStack = state.settingsBackStack,
                entryProvider = entryProvider,
                onBack = { onAction(HomeAction.PopInTab(HomeTab.Settings)) }
            )
        }
    }
}

@Composable
private fun HomeTabContent(
    isVisible: Boolean,
    backStack: List<Any>,
    entryProvider: (Any) -> NavEntry<Any>,
    onBack: () -> Unit
) {
    if (isVisible) {
        NavDisplay(
            backStack = backStack,
            onBack = onBack,
            entryProvider = entryProvider
        )
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val primaryBlue = Color(0xFF1E88E5)
    val grayText = Color(0xFF757575)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color.Transparent)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Wallet",
                    isSelected = selectedTab == HomeTab.Wallet,
                    onClick = { onTabSelected(HomeTab.Wallet) },
                    selectedColor = primaryBlue,
                    unselectedColor = grayText
                )
                TabItem(
                    icon = Icons.Default.Language,
                    label = "Browser",
                    isSelected = selectedTab == HomeTab.Transactions,
                    onClick = { onTabSelected(HomeTab.Transactions) },
                    selectedColor = primaryBlue,
                    unselectedColor = grayText
                )

                Spacer(modifier = Modifier.width(80.dp))

                TabItem(
                    icon = Icons.Default.TrendingUp,
                    label = "Trends",
                    isSelected = selectedTab == HomeTab.Trends,
                    onClick = { onTabSelected(HomeTab.Trends) },
                    selectedColor = primaryBlue,
                    unselectedColor = grayText
                )
                TabItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = selectedTab == HomeTab.Settings,
                    onClick = { onTabSelected(HomeTab.Settings) },
                    selectedColor = primaryBlue,
                    unselectedColor = grayText
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .shadow(elevation = 12.dp, shape = CircleShape)
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                    )
                )
                .clickable { onTabSelected(HomeTab.Scanner) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scanner",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) selectedColor else unselectedColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 4.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = selectedColor, spotColor = selectedColor)
                    .clip(CircleShape)
                    .background(selectedColor)
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
