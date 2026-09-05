/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BootstrapSettingsUseCaseTest {
    private val repository: SettingsRepository = mockk()
    private lateinit var useCase: BootstrapSettingsUseCase

    @Before
    fun setUp() {
        useCase = BootstrapSettingsUseCase(repository)
    }

    @Test
    fun `invoke calls repository bootstrap and returns success with languages`() =
        runTest {
            val languages =
                listOf(
                    SupportedLanguage(code = "en", name = "English", version = "1.0.0"),
                    SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
                )
            coEvery { repository.bootstrap() } returns Result.success(languages)

            val result = useCase()

            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()?.size)
        }

    @Test
    fun `invoke returns failure when repository bootstrap fails`() =
        runTest {
            val error = RuntimeException("Bootstrap failed")
            coEvery { repository.bootstrap() } returns Result.failure(error)

            val result = useCase()

            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
        }
}
