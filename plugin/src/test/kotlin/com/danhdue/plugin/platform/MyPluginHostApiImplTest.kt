/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import com.danhdue.plugin.domain.model.PluginData
import com.danhdue.plugin.domain.usecase.GetDataUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPluginHostApiImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockGetDataUseCase: GetDataUseCase = mockk()

    private lateinit var hostApi: MyPluginHostApiImpl

    @Before
    fun setUp() {
        hostApi = MyPluginHostApiImpl(
            getDataUseCase = mockGetDataUseCase,
            coroutineScope = testScope
        )
    }

    @Test
    fun `getPlatformVersion returns non-empty Android platform version`() {
        val version = hostApi.getPlatformVersion()
        assertNotNull(version)
        assertTrue(version.startsWith("Android"))
    }

    @Test
    fun `getData returns success with typed PigeonPluginData (Scenario 1)`() = runTest(testDispatcher) {
        val domainData = PluginData(id = "123", title = "Pigeon Title", timestamp = 12345L)
        coEvery { mockGetDataUseCase.execute() } returns Result.success(domainData)

        var callbackResult: Result<PigeonPluginData>? = null
        hostApi.getData { result ->
            callbackResult = result
        }

        advanceUntilIdle()

        assertNotNull(callbackResult)
        assertTrue(callbackResult!!.isSuccess)
        val data = callbackResult!!.getOrNull()
        assertEquals("123", data?.id)
        assertEquals("Pigeon Title", data?.title)
        assertEquals(12345L, data?.timestamp)
    }

    @Test
    fun `getData handles empty properties without errors (Scenario 5)`() = runTest(testDispatcher) {
        val domainData = PluginData(id = "", title = "", timestamp = 0L)
        coEvery { mockGetDataUseCase.execute() } returns Result.success(domainData)

        var callbackResult: Result<PigeonPluginData>? = null
        hostApi.getData { result ->
            callbackResult = result
        }

        advanceUntilIdle()

        assertNotNull(callbackResult)
        assertTrue(callbackResult!!.isSuccess)
        val data = callbackResult!!.getOrNull()
        assertEquals("", data?.id)
        assertEquals("", data?.title)
        assertEquals(0L, data?.timestamp)
    }

    @Test
    fun `getData propagates exception as Result failure`() = runTest(testDispatcher) {
        coEvery { mockGetDataUseCase.execute() } returns Result.failure(RuntimeException("Pigeon IPC Failure"))

        var callbackResult: Result<PigeonPluginData>? = null
        hostApi.getData { result ->
            callbackResult = result
        }

        advanceUntilIdle()

        assertNotNull(callbackResult)
        assertTrue(callbackResult!!.isFailure)
        assertEquals("Pigeon IPC Failure", callbackResult!!.exceptionOrNull()?.message)
    }
}
