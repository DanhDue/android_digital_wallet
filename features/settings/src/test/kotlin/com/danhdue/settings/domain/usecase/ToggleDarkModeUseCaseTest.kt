/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.AppThemeMode
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleDarkModeUseCaseTest {
    private val appThemeManager: AppThemeManager = mockk(relaxed = true)
    private lateinit var useCase: ToggleDarkModeUseCase

    @Before
    fun setUp() {
        useCase = ToggleDarkModeUseCase(appThemeManager)
    }

    @Test
    fun `invoke with isDarkMode true calls setThemeMode with DARK`() =
        runTest {
            useCase(isDarkMode = true)

            coVerify { appThemeManager.setThemeMode(AppThemeMode.DARK) }
        }

    @Test
    fun `invoke with isDarkMode false calls setThemeMode with LIGHT`() =
        runTest {
            useCase(isDarkMode = false)

            coVerify { appThemeManager.setThemeMode(AppThemeMode.LIGHT) }
        }
}
