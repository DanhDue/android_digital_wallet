/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.components.ui.theme.HomeGrayText
import com.danhdue.components.ui.theme.HomePrimaryBlue
import com.danhdue.components.ui.theme.ScannerFabGradient
import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.framework.navigation.NestedNavigator
import com.danhdue.framework.navigation.ObserveBackstackForFlipper
import com.danhdue.libraries.components.R
import com.danhdue.platform.LocalEntryProviderInstallers

@Composable
fun HomeRoot(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    val installers = LocalEntryProviderInstallers.current
    val entryProvider =
        remember(installers) {
            entryProvider { installers.forEach { it() } }
        }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedTab = state.selectedTab,
                onTabSelect = { onAction(HomeAction.TabSelected(it)) },
            )
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(bottom = 0.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            // Nested navigation for each tab
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Wallet,
                tab = HomeTab.Wallet,
                backStack = state.walletBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Transactions,
                tab = HomeTab.Transactions,
                backStack = state.transactionsBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Scanner,
                tab = HomeTab.Scanner,
                backStack = state.scannerBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Trends,
                tab = HomeTab.Trends,
                backStack = state.trendsBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
            HomeTabContent(
                isVisible = state.selectedTab == HomeTab.Settings,
                tab = HomeTab.Settings,
                backStack = state.settingsBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun HomeTabContent(
    isVisible: Boolean,
    tab: HomeTab,
    backStack: List<Any>,
    entryProvider: (Any) -> NavEntry<Any>,
    onAction: (HomeAction) -> Unit,
) {
    if (isVisible) {
        // Observe backstack changes and report to Flipper
        val tabName = tab::class.simpleName ?: "Tab"
        ObserveBackstackForFlipper(backStack = backStack, prefix = tabName)

        val nestedNavigator =
            remember(tab) {
                object : NestedNavigator {
                    override fun navigate(destination: Any) {
                        onAction(HomeAction.NavigateInTab(tab, destination))
                    }

                    override fun popBackStack() {
                        onAction(HomeAction.PopInTab(tab))
                    }
                }
            }

        CompositionLocalProvider(LocalNestedNavigator provides nestedNavigator) {
            NavDisplay(
                backStack = backStack,
                onBack = { nestedNavigator.popBackStack() },
                entryProvider = entryProvider,
            )
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: HomeTab,
    onTabSelect: (HomeTab) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.Transparent),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TabItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Wallet",
                    isSelected = selectedTab == HomeTab.Wallet,
                    onClick = { onTabSelect(HomeTab.Wallet) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
                TabItem(
                    icon = Icons.Default.Language,
                    label = "Browser",
                    isSelected = selectedTab == HomeTab.Transactions,
                    onClick = { onTabSelect(HomeTab.Transactions) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )

                Spacer(modifier = Modifier.width(80.dp))

                TabItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    label = "Trends",
                    isSelected = selectedTab == HomeTab.Trends,
                    onClick = { onTabSelect(HomeTab.Trends) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
                TabItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = selectedTab == HomeTab.Settings,
                    onClick = { onTabSelect(HomeTab.Settings) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 4.dp)
                    .shadow(elevation = 12.dp, shape = CircleShape)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(brush = ScannerFabGradient)
                    .clickable { onTabSelect(HomeTab.Scanner) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scanner",
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun TabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .width(72.dp)
                .fillMaxHeight()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) selectedColor else unselectedColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.ic_selected_bot_tab_indicator),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun TabItemPreview() {
    TabItem(
        icon = Icons.Default.AccountBalanceWallet,
        label = "Wallet",
        isSelected = true,
        onClick = { },
        selectedColor = HomePrimaryBlue,
        unselectedColor = HomeGrayText,
    )
}
