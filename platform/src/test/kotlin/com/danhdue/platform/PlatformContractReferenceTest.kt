/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Compile-time reference checks for the declaration-only parts of the
 * `:platform` contract: [AppRoutes], the [AppEvent] hierarchy and [FeatureEntry].
 */
class PlatformContractReferenceTest {
    @Test
    fun `AppRoutes exposes cross-feature NavKeys`() {
        val keys: List<NavKey> =
            listOf(
                AppRoutes.LoginRoute,
                AppRoutes.ShellRoute,
                AppRoutes.MyWalletRoute,
                AppRoutes.TransactionListRoute,
                AppRoutes.ScannerRoute,
                AppRoutes.TrendsRoute,
                AppRoutes.SettingsRoute,
            )

        assertEquals(7, keys.toSet().size)
        assertNotNull(AppRoutes.SettingsRoute)
        assertNotNull(AppRoutes.ScannerRoute)
    }

    @Test
    fun `AppEvent hierarchy covers the minimal cross-feature vocabulary`() {
        val events: List<AppEvent> =
            listOf(
                AppEvent.ShellTabVisibilityChanged(tabIndex = 0, isVisible = true),
                AppEvent.AppLifecycleChanged(AppLifecycleState.STARTED),
                AppEvent.UserLoggedOut,
                AppEvent.ProfileNameChanged(displayName = "Ada Lovelace"),
            )

        assertEquals(4, events.size)
    }

    @Test
    fun `FeatureEntry yields an EntryProviderInstaller`() {
        val entry =
            object : FeatureEntry {
                override fun installer(): EntryProviderInstaller = {}
            }

        assertNotNull(entry.installer())
    }
}
