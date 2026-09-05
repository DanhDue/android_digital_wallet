/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.AppThemeMode
import javax.inject.Inject

class ToggleDarkModeUseCase @Inject constructor(
    private val appThemeManager: AppThemeManager,
) {
    suspend operator fun invoke(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppThemeMode.DARK else AppThemeMode.LIGHT
        appThemeManager.setThemeMode(mode)
    }
}
