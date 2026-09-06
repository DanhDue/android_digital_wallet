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
import org.junit.Before
import org.junit.Test

class GetCachedLanguagesUseCaseTest {
    private val repository: SettingsRepository = mockk()
    private lateinit var useCase: GetCachedLanguagesUseCase

    @Before
    fun setUp() {
        useCase = GetCachedLanguagesUseCase(repository)
    }

    @Test
    fun `invoke calls repository getCachedLanguages and returns cached languages`() =
        runTest {
            val languages =
                listOf(
                    SupportedLanguage(code = "en", name = "English", version = "1.0.0"),
                    SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
                    SupportedLanguage(code = "ja_JP", name = "日本語", version = "1.0.0"),
                )
            coEvery { repository.getCachedLanguages() } returns languages

            val result = useCase()

            assertEquals(3, result.size)
            assertEquals("ja_JP", result[2].code)
            assertEquals("日本語", result[2].name)
        }
}
