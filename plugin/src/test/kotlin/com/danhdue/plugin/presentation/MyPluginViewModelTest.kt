/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.presentation

import app.cash.turbine.test
import com.danhdue.plugin.domain.model.PluginData
import com.danhdue.plugin.domain.usecase.GetDataUseCase
import com.danhdue.plugin.domain.usecase.SyncDataUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPluginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockGetDataUseCase: GetDataUseCase = mockk()
    private val mockSyncDataUseCase: SyncDataUseCase = mockk()

    private lateinit var viewModel: MyPluginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyPluginViewModel(mockGetDataUseCase, mockSyncDataUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default empty values`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.data)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onAction LoadData emits loading then success state`() = runTest {
        val expectedData = PluginData(id = "101", title = "Wallet Info")
        coEvery { mockGetDataUseCase.execute() } returns Result.success(expectedData)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            viewModel.onAction(MyPluginAction.LoadData)

            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(expectedData, successState.data)
            assertNull(successState.errorMessage)
        }
    }

    @Test
    fun `onAction LoadData emits loading then error state when use case returns failure`() = runTest {
        coEvery { mockGetDataUseCase.execute() } returns Result.failure(RuntimeException("Network timeout"))

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            viewModel.onAction(MyPluginAction.LoadData)

            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNull(errorState.data)
            assertEquals("Network timeout", errorState.errorMessage)
        }
    }

    @Test
    fun `onAction Sync emits ShowToast event on success`() = runTest {
        coEvery { mockSyncDataUseCase.execute() } returns Result.success(true)

        viewModel.eventFlow.test {
            viewModel.onAction(MyPluginAction.Sync)
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is MyPluginEvent.ShowToast)
            assertEquals("Data synchronized successfully", (event as MyPluginEvent.ShowToast).message)
        }
    }

    @Test
    fun `rapid multi-tap Refresh debounces and only processes the latest request`() = runTest {
        val expectedData = PluginData(id = "102", title = "Refreshed")
        coEvery { mockGetDataUseCase.execute() } returns Result.success(expectedData)

        viewModel.uiState.test {
            awaitItem() // initial

            // Rapid taps
            repeat(5) {
                viewModel.onAction(MyPluginAction.Refresh)
            }

            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(expectedData, finalState.data)
        }
    }
}
