/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
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
import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.framework.navigation.NestedNavigator
import com.danhdue.framework.navigation.ObserveBackstackForFlipper
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import com.danhdue.platform.LocalEntryProviderInstallers
import com.danhdue.uikit.R
import com.danhdue.uikit.ui.theme.HomeGrayText
import com.danhdue.uikit.ui.theme.HomePrimaryBlue
import java.util.ServiceConfigurationError
import java.util.ServiceLoader

@Composable
fun ShellRoot(viewModel: ShellViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ShellScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

/**
 * Navigation entries contributed at runtime by installed on-demand Dynamic
 * Feature Module splits (Task 14, design §4.4). A downloaded split never joins
 * the host Hilt graph, so its [FeatureEntry] is discovered through
 * [ServiceLoader] once [ShellState.scannerReady] is set and merged into the set
 * that feeds `NavDisplay` — the install-time features keep plain Hilt
 * `@IntoSet` multibinding ([LocalEntryProviderInstallers]).
 */
private fun loadDynamicFeatureInstallers(): List<EntryProviderInstaller> =
    installersFrom(
        ServiceLoader.load(FeatureEntry::class.java, FeatureEntry::class.java.classLoader),
    )

/**
 * Collects the [EntryProviderInstaller] of every currently-loadable
 * [FeatureEntry] in [loader], **skipping** any whose class cannot be loaded yet.
 *
 * The `META-INF/services/com.danhdue.platform.FeatureEntry` file is owned by
 * `:app` and lists EVERY on-demand `FeatureEntry` FQCN (bundletool forbids two
 * feature splits shipping the same root resource — design §4.4), so at any
 * moment some of those classes belong to splits that are not installed.
 * `ServiceLoader`'s own iterator raises [ServiceConfigurationError] lazily from
 * `next()` for those; a `for` / `forEach` over it cannot recover, so `hasNext()`
 * and `next()` are driven by hand and the un-loadable element is dropped.
 */
internal fun installersFrom(loader: ServiceLoader<FeatureEntry>): List<EntryProviderInstaller> {
    val installers = mutableListOf<EntryProviderInstaller>()
    val iterator = loader.iterator()
    while (hasNextOrStop(iterator)) {
        nextOrNull(iterator)?.let { installers += it.installer() }
    }
    return installers
}

// A bad line in the aggregated service file (a split that is not installed)
// surfaces as ServiceConfigurationError; LinkageError covers a half-loaded class.
// Both mean "this on-demand entry is not usable right now" — skip it, don't fail
// the whole load. The `_` name opts out of detekt's SwallowedException rule
// deliberately: there is nothing to log, this is the expected steady state.
// Asymmetry: hasNext() only throws on a corrupt services file -> give up on the
// whole iteration; a per-element next() failure (split not installed yet — the
// steady-state case) is skipped in nextOrNull() and iteration continues.
private fun hasNextOrStop(iterator: Iterator<FeatureEntry>): Boolean =
    try {
        iterator.hasNext()
    } catch (_: ServiceConfigurationError) {
        false
    } catch (_: LinkageError) {
        false
    }

private fun nextOrNull(iterator: Iterator<FeatureEntry>): FeatureEntry? =
    try {
        iterator.next()
    } catch (_: ServiceConfigurationError) {
        null
    } catch (_: LinkageError) {
        null
    }

@Composable
private fun ShellScreen(
    state: ShellState,
    onAction: (ShellAction) -> Unit,
) {
    val installers = LocalEntryProviderInstallers.current
    val dynamicInstallers =
        remember(state.scannerReady) {
            if (state.scannerReady) loadDynamicFeatureInstallers() else emptyList()
        }
    val entryProvider =
        remember(installers, dynamicInstallers) {
            entryProvider { (installers + dynamicInstallers).forEach { it() } }
        }

    Scaffold(
        bottomBar = {
            ShellBottomBar(
                selectedTab = state.selectedTab,
                settingsLabel = state.profileName.ifBlank { "Settings" },
                onTabSelect = { onAction(ShellAction.TabSelected(it)) },
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
            ShellTabContent(
                isVisible = state.selectedTab == ShellTab.Home,
                tab = ShellTab.Home,
                backStack = state.homeBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
            // Scanner is an on-demand Dynamic Feature Module (Task 14): show a
            // progress indicator while the split installs, then its real content
            // once the `ServiceLoader`-loaded entry is in `entryProvider`.
            if (state.selectedTab == ShellTab.Scanner && !state.scannerReady) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ShellTabContent(
                    isVisible = state.selectedTab == ShellTab.Scanner,
                    tab = ShellTab.Scanner,
                    backStack = state.scannerBackStack,
                    entryProvider = entryProvider,
                    onAction = onAction,
                )
            }
            ShellTabContent(
                isVisible = state.selectedTab == ShellTab.Settings,
                tab = ShellTab.Settings,
                backStack = state.settingsBackStack,
                entryProvider = entryProvider,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun ShellTabContent(
    isVisible: Boolean,
    tab: ShellTab,
    backStack: List<Any>,
    entryProvider: (Any) -> NavEntry<Any>,
    onAction: (ShellAction) -> Unit,
) {
    if (isVisible) {
        // Observe backstack changes and report to Flipper
        val tabName = tab::class.simpleName ?: "Tab"
        ObserveBackstackForFlipper(backStack = backStack, prefix = tabName)

        val nestedNavigator =
            remember(tab) {
                object : NestedNavigator {
                    override fun navigate(destination: Any) {
                        onAction(ShellAction.NavigateInTab(tab, destination))
                    }

                    override fun popBackStack() {
                        onAction(ShellAction.PopInTab(tab))
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
private fun ShellBottomBar(
    selectedTab: ShellTab,
    settingsLabel: String,
    onTabSelect: (ShellTab) -> Unit,
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
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = selectedTab == ShellTab.Home,
                    onClick = { onTabSelect(ShellTab.Home) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
                TabItem(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Scanner",
                    isSelected = selectedTab == ShellTab.Scanner,
                    onClick = { onTabSelect(ShellTab.Scanner) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
                TabItem(
                    icon = Icons.Default.Settings,
                    label = settingsLabel,
                    isSelected = selectedTab == ShellTab.Settings,
                    onClick = { onTabSelect(ShellTab.Settings) },
                    selectedColor = HomePrimaryBlue,
                    unselectedColor = HomeGrayText,
                )
            }
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
        icon = Icons.Default.Home,
        label = "Home",
        isSelected = true,
        onClick = { },
        selectedColor = HomePrimaryBlue,
        unselectedColor = HomeGrayText,
    )
}
