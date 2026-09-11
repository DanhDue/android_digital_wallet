/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.di

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PluginComponentTest {

    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        PluginComponentProvider.reset()
        mockContext = mockk(relaxed = true)
        every { mockContext.applicationContext } returns mockContext
    }

    @After
    fun tearDown() {
        PluginComponentProvider.reset()
    }

    @Test
    fun `PluginComponentProvider returns non-null component with working use cases`() = runBlocking {
        val component = PluginComponentProvider.get(mockContext)
        assertNotNull(component)

        val repo = component.getPluginRepository()
        assertNotNull(repo)

        val getDataUseCase = component.getDataUseCase()
        assertNotNull(getDataUseCase)

        val dataResult = getDataUseCase.execute()
        assertTrue(dataResult.isSuccess)
        assertEquals("sample-data-1", dataResult.getOrNull()?.id)

        val syncUseCase = component.getSyncDataUseCase()
        assertNotNull(syncUseCase)
        val syncResult = syncUseCase.execute()
        assertTrue(syncResult.isSuccess)
        assertEquals(true, syncResult.getOrNull())
    }

    @Test
    fun `PluginComponentProvider returns same singleton instance on repeated calls`() {
        val first = PluginComponentProvider.get(mockContext)
        val second = PluginComponentProvider.get(mockContext)
        assertSame(first, second)
    }

    @Test
    fun `PluginComponentProvider reset creates new instance`() {
        val first = PluginComponentProvider.get(mockContext)
        PluginComponentProvider.reset()
        val second = PluginComponentProvider.get(mockContext)
        assertNotSame(first, second)
    }

    @Test
    fun `PluginComponentProvider is thread-safe under concurrent access`() {
        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val results = mutableListOf<PluginComponent>()
        val errorRef = AtomicReference<Throwable?>(null)

        repeat(threadCount) {
            executor.submit {
                try {
                    val comp = PluginComponentProvider.get(mockContext)
                    synchronized(results) { results.add(comp) }
                } catch (t: Throwable) {
                    errorRef.set(t)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        assertTrue("Error during concurrent access: ${errorRef.get()}", errorRef.get() == null)
        assertEquals(threadCount, results.size)
        val first = results.first()
        results.forEach { assertSame(first, it) }
    }

    @Test
    fun `PluginComponentProvider handles null applicationContext gracefully`() {
        val fallbackContext: Context = mockk(relaxed = true)
        every { fallbackContext.applicationContext } returns null

        val component = PluginComponentProvider.get(fallbackContext)
        assertNotNull(component)
    }
}
