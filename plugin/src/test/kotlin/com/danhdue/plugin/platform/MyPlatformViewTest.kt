/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import android.content.Context
import com.danhdue.plugin.presentation.MyPlatformView
import com.danhdue.plugin.presentation.MyPluginViewModel
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MyPlatformViewTest {

    private lateinit var mockContext: Context
    private lateinit var mockViewModel: MyPluginViewModel

    @Before
    fun setUp() {
        mockContext = RuntimeEnvironment.getApplication()
        mockViewModel = mockk(relaxed = true)
    }

    @Test
    fun `MyPlatformViewFactory creates MyPlatformView successfully`() {
        val factory = MyPlatformViewFactory(mockViewModel)
        val platformView = factory.create(mockContext, 1, null)

        assertNotNull(platformView)
        assertNotNull(platformView.view)
    }

    @Test
    fun `MyPlatformView disposes cleanly without exceptions (Scenario 3)`() {
        val platformView = MyPlatformView(mockContext, 1, null, mockViewModel)
        platformView.dispose()
    }
}
