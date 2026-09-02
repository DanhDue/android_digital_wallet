/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.platform.AppRoutes
import com.danhdue.shell.tabs.HomeStubRoute

/**
 * Represents the state of the Shell (the Host's main tabbed container).
 * All properties are immutable to follow MVI pattern.
 *
 * The template shell has three tabs — [ShellTab.Home] / [ShellTab.Scanner] /
 * [ShellTab.Settings] — and opens on [ShellTab.Settings] (the last index), matching
 * the Flutter template. Each tab's initial nested back stack is seeded from a
 * cross-feature `NavKey` in `:platform.AppRoutes` (Task 11), except Home, whose
 * stub page lives inside `:shell` and never crosses a feature boundary
 * ([HomeStubRoute]).
 *
 * @property profileName profile display name last reported by
 *   [com.danhdue.platform.AppEvent.ProfileNameChanged] — the Task 11 pilot's
 *   shell-owned label (rendered on the Settings bottom-bar tab), fed over
 *   [com.danhdue.platform.AppEventBus] without importing `com.danhdue.settings.*`.
 *   Blank until the Settings feature has loaded.
 */
data class ShellState(
    val selectedTab: ShellTab = ShellTab.Settings,
    // Immutable backstacks for each tab to support nested navigation
    val homeBackStack: List<Any> = listOf(HomeStubRoute),
    val scannerBackStack: List<Any> = listOf(AppRoutes.ScannerRoute),
    val settingsBackStack: List<Any> = listOf(AppRoutes.SettingsRoute),
    val profileName: String = "",
)

@Suppress("MagicNumber")
sealed class ShellTab(
    val index: Int,
) {
    data object Home : ShellTab(0)

    data object Scanner : ShellTab(1)

    data object Settings : ShellTab(2)
}
