/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.danhdue.plugin.di.PluginComponent
import com.danhdue.plugin.di.PluginComponentProvider
import com.danhdue.plugin.domain.usecase.SyncDataUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DataSyncWorkerTest {

    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams: WorkerParameters
    private lateinit var mockComponent: PluginComponent
    private lateinit var mockSyncUseCase: SyncDataUseCase

    @Before
    fun setUp() {
        PluginComponentProvider.reset()
        mockContext = mockk(relaxed = true)
        mockWorkerParams = mockk(relaxed = true)
        mockComponent = mockk(relaxed = true)
        mockSyncUseCase = mockk(relaxed = true)

        every { mockContext.applicationContext } returns mockContext
    }

    @After
    fun tearDown() {
        PluginComponentProvider.reset()
    }

    @Test
    fun `DataSyncWorker returns success when sync succeeds`() = runBlocking {
        every { mockComponent.getSyncDataUseCase() } returns mockSyncUseCase
        coEvery { mockSyncUseCase.execute() } returns Result.success(true)
        PluginComponentProvider.setComponent(mockComponent)

        val worker = DataSyncWorker(mockContext, mockWorkerParams)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `DataSyncWorker returns retry when sync fails`() = runBlocking {
        every { mockComponent.getSyncDataUseCase() } returns mockSyncUseCase
        coEvery { mockSyncUseCase.execute() } returns Result.failure(RuntimeException("Network timeout"))
        PluginComponentProvider.setComponent(mockComponent)

        val worker = DataSyncWorker(mockContext, mockWorkerParams)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `DataSyncWorker returns retry when sync throws exception`() = runBlocking {
        every { mockComponent.getSyncDataUseCase() } returns mockSyncUseCase
        coEvery { mockSyncUseCase.execute() } throws RuntimeException("Unexpected error")
        PluginComponentProvider.setComponent(mockComponent)

        val worker = DataSyncWorker(mockContext, mockWorkerParams)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
