/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.deeplink.DeepLink
import com.danhdue.platform.deeplink.DeepLinkResolver
import com.danhdue.platform.deeplink.DeepLinkTarget
import com.danhdue.platform.deeplink.Placement

/**
 * On-demand deep link resolver for the scanner dynamic feature module.
 *
 * Serves as the exemplar implementation for dynamic feature modules (DFM).
 * Unlike install-time features that use Hilt `@IntoSet` (see `SettingsDeepLinkResolver` in Task 11),
 * this resolver is delivered via [ScannerFeatureEntry.resolver] and discovered reflectively
 * through [java.util.ServiceLoader] after the split is installed at runtime.
 *
 * Demonstrates an explicit [Placement.InTab] strategy targeting tab index 1 (Scanner tab).
 */
class ScannerDeepLinkResolver : DeepLinkResolver {
    override fun resolve(link: DeepLink): DeepLinkTarget? {
        if (link.feature != FEATURE) return null

        return when (link.segments) {
            emptyList<String>() ->
                DeepLinkTarget(
                    destination = AppRoutes.ScannerRoute,
                    placement = Placement.InTab(tab = SCANNER_TAB_INDEX),
                )
            else -> null
        }
    }

    companion object {
        private const val FEATURE = "scanner"
        private const val SCANNER_TAB_INDEX = 1
    }
}
