/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.platform

import {{package}}.domain.model.{{name.pascalCase()}}Data
import {{package}}.domain.usecase.Get{{name.pascalCase()}}DataUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class {{name.pascalCase()}}HostApiImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockGetDataUseCase = mockk<Get{{name.pascalCase()}}DataUseCase>()

    private lateinit var hostApi: {{name.pascalCase()}}HostApiImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hostApi = {{name.pascalCase()}}HostApiImpl(
            getDataUseCase = mockGetDataUseCase,
            coroutineScope = testScope
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPlatformVersion returns Android version string`() {
        val version = hostApi.getPlatformVersion()
        assertTrue(version.startsWith("Android "))
    }

    @Test
    fun `getData returns success when use case succeeds`() = testScope.runTest {
        val testData = {{name.pascalCase()}}Data(
            id = "test_1",
            title = "Test Title",
            timestamp = 12345L
        )
        coEvery { mockGetDataUseCase.execute() } returns Result.success(testData)

        var result: Result<Pigeon{{name.pascalCase()}}Data>? = null
        hostApi.getData { result = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(result)
        assertTrue(result!!.isSuccess)
        val pigeonData = result!!.getOrNull()
        assertEquals("test_1", pigeonData?.id)
        assertEquals("Test Title", pigeonData?.title)
        assertEquals(12345L, pigeonData?.timestamp)
    }

    private fun assertNotNull(actual: Any?) {
        org.junit.Assert.assertNotNull(actual)
    }
}
