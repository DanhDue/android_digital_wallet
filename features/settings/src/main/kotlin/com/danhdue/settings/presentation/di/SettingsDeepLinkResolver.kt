/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.deeplink.DeepLink
import com.danhdue.platform.deeplink.DeepLinkResolver
import com.danhdue.platform.deeplink.DeepLinkTarget
import com.danhdue.settings.presentation.profile.ProfileRoute
import javax.inject.Inject

/**
 * Install-time deep link resolver for the settings feature.
 *
 * Serves as the exemplar implementation for install-time modules.
 * Resolves URIs under the `settings` feature:
 * - `myapp://settings` -> [AppRoutes.SettingsRoute]
 * - `myapp://settings/profile` -> [ProfileRoute]
 *
 * Neither target specifies an explicit placement, relying on default tier-1 placement
 * (InTab in Settings tab with parents=[SettingsRoute]).
 *
 * For on-demand dynamic feature modules, see `ScannerFeatureEntry.resolver()` (Task 12).
 */
class SettingsDeepLinkResolver @Inject constructor() : DeepLinkResolver {
    override fun resolve(link: DeepLink): DeepLinkTarget? {
        if (link.feature != FEATURE) return null

        return when (link.segments) {
            emptyList<String>() -> DeepLinkTarget(destination = AppRoutes.SettingsRoute)
            listOf(PATH_PROFILE) -> DeepLinkTarget(destination = ProfileRoute)
            else -> null
        }
    }

    companion object {
        private const val FEATURE = "settings"
        private const val PATH_PROFILE = "profile"
    }
}
