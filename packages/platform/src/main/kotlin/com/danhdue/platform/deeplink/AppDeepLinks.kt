/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import androidx.navigation3.runtime.NavKey
import com.danhdue.platform.AppRoutes

/**
 * Tier-1 entry point registry for features accessible via deep linking.
 *
 * @property feature Unique routing key for the feature (e.g., "settings", "scanner").
 * @property entryRoute Default navigation key for this feature's entry point.
 * @property tab Shell tab index (0-based) hosting this feature, or null if it does not belong to a tab.
 * @property dynamicModule Name of the on-demand split module if this feature is a DFM, null otherwise.
 * @property requiresAuth Coarse feature-level authentication gate evaluated before downloading splits.
 */
data class FeatureEntryPoint(
    val feature: String,
    val entryRoute: NavKey,
    val tab: Int?,
    val dynamicModule: String? = null,
    val requiresAuth: Boolean = false,
)

/**
 * Tier-1 shared deep link registry.
 *
 * Each feature registers one [FeatureEntryPoint] here. Adding a feature requires only one line.
 */
object AppDeepLinks {
    val entryPoints: List<FeatureEntryPoint> =
        listOf(
            FeatureEntryPoint(
                feature = "settings",
                entryRoute = AppRoutes.SettingsRoute,
                tab = 2,
            ),
            FeatureEntryPoint(
                feature = "scanner",
                entryRoute = AppRoutes.ScannerRoute,
                tab = 1,
                dynamicModule = "scanner",
            ),
        )
}
