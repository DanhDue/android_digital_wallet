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
 * The template ships three destinations: the Host tab shell (`ShellRoute`) plus
 * the two surviving feature entry points (`ScannerRoute`, `SettingsRoute`) that
 * `:shell` seeds its Scanner / Settings tab back stacks from. The Home tab is a
 * `:shell`-local stub (`HomeStubRoute`) and is deliberately **not** listed here —
 * it never crosses a feature boundary. Add a project's own auth/login route back
 * here when the template gains an authentication flow.
 */
object AppRoutes {
    /** Navigation route for the Host tab shell (`:shell` — `ShellRoot`). */
    @Serializable
    data object ShellRoute : NavKey

    /** Entry point of the Scanner feature (becomes an on-demand dynamic feature module in Task 14). */
    @Serializable
    data object ScannerRoute : NavKey

    /** Entry point of the Settings feature — the default shell tab, and the Task 11 pilot. */
    @Serializable
    data object SettingsRoute : NavKey
}
