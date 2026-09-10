/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.deeplink.DeepLink
import com.danhdue.platform.deeplink.DeepLinkParser
import com.danhdue.platform.deeplink.Placement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ScannerDeepLinkResolverTest {
    private lateinit var resolver: ScannerDeepLinkResolver

    @Before
    fun setUp() {
        resolver = ScannerDeepLinkResolver()
    }

    @Test
    fun `resolves scanner root to ScannerRoute with explicit InTab placement`() {
        val link = DeepLinkParser.parse("myapp://scanner")
        assertNotNull(link)
        val target = resolver.resolve(link!!)

        assertNotNull(target)
        assertEquals(AppRoutes.ScannerRoute, target!!.destination)
        assertEquals(Placement.InTab(tab = 1), target.placement)
    }

    @Test
    fun `returns null for unknown subpath under scanner`() {
        val link = DeepLinkParser.parse("myapp://scanner/unknown")
        assertNotNull(link)
        assertNull(resolver.resolve(link!!))
    }

    @Test
    fun `returns null for link targeting different feature`() {
        val link =
            DeepLink(
                raw = "myapp://settings",
                feature = "settings",
                segments = emptyList(),
                params = emptyMap(),
            )
        assertNull(resolver.resolve(link))
    }

    @Test
    fun `target carrying explicit placement preserves exact placement value`() {
        val link = DeepLinkParser.parse("myapp://scanner?qr=123")
        assertNotNull(link)
        val target = resolver.resolve(link!!)

        assertNotNull(target)
        assertEquals(Placement.InTab(tab = 1), target!!.placement)
    }
}
