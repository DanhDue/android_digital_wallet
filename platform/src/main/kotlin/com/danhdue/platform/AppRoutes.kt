/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Central registry of [NavKey]s that are navigated to from more than one feature.
 *
 * Feature-private destinations stay inside their own module; only routes that
 * cross a feature boundary belong here, so features never depend on each other
 * directly (Goal G4). The existing `HomeRoute` / `LoginRoute` keys are relocated
 * here by a later task; this object seeds the registry with the forward-looking
 * cross-feature routes the template needs.
 */
object AppRoutes {
    /** Entry point of the Settings feature. */
    @Serializable
    data object SettingsRoute : NavKey

    /** Entry point of the Scanner feature (an on-demand dynamic feature module). */
    @Serializable
    data object ScannerRoute : NavKey
}
