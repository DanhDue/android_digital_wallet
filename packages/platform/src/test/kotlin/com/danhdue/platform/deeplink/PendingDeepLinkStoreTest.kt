/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [InMemoryPendingDeepLinkStore].
 */
class PendingDeepLinkStoreTest {
    private var currentTime = 1_000_000L
    private val store = InMemoryPendingDeepLinkStore(clock = { currentTime })

    @Test
    fun `put then takeIfAny returns stored link`() {
        store.put("myapp://scanner/qr")
        assertEquals("myapp://scanner/qr", store.takeIfAny())
    }

    @Test
    fun `takeIfAny has take-once semantics`() {
        store.put("myapp://scanner/qr")
        assertEquals("myapp://scanner/qr", store.takeIfAny())
        assertNull(store.takeIfAny())
    }

    @Test
    fun `takeIfAny returns null past 10-minute TTL`() {
        store.put("myapp://scanner/qr")

        // Advance time by 10 minutes + 1 millisecond
        currentTime += 10 * 60 * 1000L + 1L

        assertNull(store.takeIfAny())
    }

    @Test
    fun `takeIfAny returns link just before 10-minute TTL expires`() {
        store.put("myapp://scanner/qr")

        // Advance time by exactly 10 minutes
        currentTime += 10 * 60 * 1000L

        assertEquals("myapp://scanner/qr", store.takeIfAny())
    }

    @Test
    fun `put overwrites an existing pending link and refreshes timestamp`() {
        store.put("myapp://settings/profile")

        currentTime += 5 * 60 * 1000L
        store.put("myapp://scanner/qr")

        currentTime += 6 * 60 * 1000L // 11m from first put, but 6m from second put

        assertEquals("myapp://scanner/qr", store.takeIfAny())
    }
}
