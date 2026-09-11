/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.sample

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.danhdue.plugin.data.worker.DataSyncWorker
import com.danhdue.plugin.di.PluginComponentProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        PluginComponentProvider.reset()
        PluginComponentProvider.get(context)
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun `SampleApp initializes PluginComponentProvider successfully`() {
        val component = PluginComponentProvider.get(context)
        assertNotNull(component)
        assertNotNull(component.getMyPluginViewModel())
    }

    @Test
    fun `MainActivity launches successfully without crashing (Scenario 1)`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
        val activity = controller.get()

        assertNotNull(activity)
        assertEquals(false, activity.isFinishing)
    }

    @Test
    fun `Triggering DataSyncWorker enqueues unique work with KEEP policy (Scenario 2 & 4)`() {
        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .addTag(MainActivity.WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            MainActivity.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )

        val statuses = workManager.getWorkInfosByTag(MainActivity.WORK_TAG).get()
        assertNotNull(statuses)
        assertEquals(1, statuses.size)
    }
}
