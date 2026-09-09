/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import com.danhdue.platform.AppEvent
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Invariant tests for Tier-1 [AppDeepLinks] and contract extensions.
 */
class AppDeepLinksTest {
    @Test
    fun `all feature keys in AppDeepLinks entryPoints are strictly unique`() {
        val entryPoints = AppDeepLinks.entryPoints
        assertTrue("entryPoints must not be empty", entryPoints.isNotEmpty())

        val featureKeys = entryPoints.map { it.feature }
        val uniqueKeys = featureKeys.toSet()

        assertEquals(
            "Duplicate feature keys found in AppDeepLinks.entryPoints",
            featureKeys.size,
            uniqueKeys.size,
        )
    }

    @Test
    fun `all entryRoute classes are declared in platform package per Konsist K9`() {
        val entryPoints = AppDeepLinks.entryPoints
        assertTrue("entryPoints must not be empty", entryPoints.isNotEmpty())

        for (entryPoint in entryPoints) {
            val routeClassName = entryPoint.entryRoute::class.java.name
            assertTrue(
                "entryRoute '$routeClassName' for feature '${entryPoint.feature}' must be in 'com.danhdue.platform' package",
                routeClassName.startsWith("com.danhdue.platform."),
            )
        }
    }

    @Test
    fun `FeatureEntry resolver default implementation returns null`() {
        val entry =
            object : FeatureEntry {
                override fun installer(): EntryProviderInstaller = {}
            }
        assertNull(entry.resolver())
    }

    @Test
    fun `AppEvent hierarchy includes UserLoggedIn`() {
        val event: AppEvent = AppEvent.UserLoggedIn
        assertNotNull(event)
    }
}
