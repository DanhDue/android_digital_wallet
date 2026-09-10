/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.deeplink.DeepLink
import com.danhdue.platform.deeplink.DeepLinkParser
import com.danhdue.settings.presentation.profile.ProfileRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SettingsDeepLinkResolverTest {
    private lateinit var resolver: SettingsDeepLinkResolver

    @Before
    fun setUp() {
        resolver = SettingsDeepLinkResolver()
    }

    @Test
    fun `resolves settings root to SettingsRoute with null placement and false requiresAuth`() {
        val link = DeepLinkParser.parse("myapp://settings")
        assertNotNull(link)
        val target = resolver.resolve(link!!)

        assertNotNull(target)
        assertEquals(AppRoutes.SettingsRoute, target!!.destination)
        assertNull(target.placement)
        assertFalse(target.requiresAuth)
    }

    @Test
    fun `resolves settings profile to ProfileRoute with null placement and false requiresAuth`() {
        val link = DeepLinkParser.parse("myapp://settings/profile")
        assertNotNull(link)
        val target = resolver.resolve(link!!)

        assertNotNull(target)
        assertEquals(ProfileRoute, target!!.destination)
        assertNull(target.placement)
        assertFalse(target.requiresAuth)
    }

    @Test
    fun `returns null for unknown subpath under settings`() {
        val link = DeepLinkParser.parse("myapp://settings/unknown")
        assertNotNull(link)
        assertNull(resolver.resolve(link!!))
    }

    @Test
    fun `returns null for nested path beyond profile`() {
        val link = DeepLinkParser.parse("myapp://settings/profile/extra")
        assertNotNull(link)
        assertNull(resolver.resolve(link!!))
    }

    @Test
    fun `returns null for link targeting different feature`() {
        val link =
            DeepLink(
                raw = "myapp://scanner",
                feature = "scanner",
                segments = emptyList(),
                params = emptyMap(),
            )
        assertNull(resolver.resolve(link))
    }

    @Test
    fun `unknown query params do not alter resolution outcome`() {
        val linkWithQuery = DeepLinkParser.parse("myapp://settings/profile?ref=promo&source=push")
        assertNotNull(linkWithQuery)
        val target = resolver.resolve(linkWithQuery!!)

        assertNotNull(target)
        assertEquals(ProfileRoute, target!!.destination)
        assertNull(target.placement)
        assertFalse(target.requiresAuth)
    }
}
