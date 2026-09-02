/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Central registry of [NavKey]s that are navigated to from more than one module.
 *
 * Feature-private destinations stay inside their own module; only routes that
 * cross a feature/host boundary belong here, so features never depend on each
 * other directly (Goal G4, Konsist K9). Every key is a `@Serializable data object`
 * so it round-trips through the navigation3 back stack unchanged.
 *
 * Task 11 relocated the following keys here, replacing the Phase 1 stubs:
 * - `ShellRoute` / `LoginRoute` — moved from `com.danhdue.framework.navigation`;
 * - `MyWalletRoute` / `TransactionListRoute` / `ScannerRoute` / `TrendsRoute` /
 *   `SettingsRoute` — moved from each feature's `presentation` package, since the
 *   `:shell` tab shell seeds every tab's back stack from them.
 */
object AppRoutes {
    /** Navigation route for the Login screen (owned by `:features:authentication`). */
    @Serializable
    data object LoginRoute : NavKey

    /** Navigation route for the Host tab shell (`:shell` — `ShellRoot`). */
    @Serializable
    data object ShellRoute : NavKey

    /** Entry point of the My Wallet feature — the first shell tab. */
    @Serializable
    data object MyWalletRoute : NavKey

    /** Entry point of the Transactions feature — the second shell tab. */
    @Serializable
    data object TransactionListRoute : NavKey

    /** Entry point of the Scanner feature (an on-demand dynamic feature module). */
    @Serializable
    data object ScannerRoute : NavKey

    /** Entry point of the Trends feature — the fourth shell tab. */
    @Serializable
    data object TrendsRoute : NavKey

    /** Entry point of the Settings feature — the fifth shell tab, and the Task 11 pilot. */
    @Serializable
    data object SettingsRoute : NavKey
}
