/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.deeplink

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.danhdue.androiddigitalwallet.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, manifest = Config.NONE)
class DeepLinkIntentFactoryTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `createIntent produces Intent with ACTION_VIEW targeting MainActivity with given URI`() {
        val uriString = "myapp://settings/profile"
        val intent = DeepLinkIntentFactory.createIntent(context, uriString)

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(Uri.parse(uriString), intent.data)
        assertEquals(MainActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun `pendingIntent creates PendingIntent wrapping ACTION_VIEW Intent with FLAG_IMMUTABLE`() {
        val uriString = "myapp://settings/profile"
        val pendingIntent = DeepLinkIntentFactory.pendingIntent(context, uriString)

        assertNotNull(pendingIntent)
        val shadowPendingIntent = shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivity)
        assertEquals(Intent.ACTION_VIEW, shadowPendingIntent.savedIntent.action)
        assertEquals(Uri.parse(uriString), shadowPendingIntent.savedIntent.data)
        assertEquals(MainActivity::class.java.name, shadowPendingIntent.savedIntent.component?.className)
        assertEquals(PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT, shadowPendingIntent.flags)
    }

    @Test
    fun `two different URIs yield distinct PendingIntents`() {
        val uri1 = "myapp://settings"
        val uri2 = "myapp://scanner"

        val pi1 = DeepLinkIntentFactory.pendingIntent(context, uri1)
        val pi2 = DeepLinkIntentFactory.pendingIntent(context, uri2)

        assertNotEquals(pi1, pi2)
        val shadow1 = shadowOf(pi1)
        val shadow2 = shadowOf(pi2)
        assertNotEquals(shadow1.requestCode, shadow2.requestCode)
        assertNotEquals(shadow1.savedIntent.data, shadow2.savedIntent.data)
    }
}
