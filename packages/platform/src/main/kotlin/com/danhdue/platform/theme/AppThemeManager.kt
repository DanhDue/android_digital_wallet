/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.theme

import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.core.pref.CacheStore
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the application-wide theme state and persistence.
 */
interface AppThemeManager {
    /** Observable high-level theme mode ([AppThemeMode.SYSTEM], [AppThemeMode.LIGHT], [AppThemeMode.DARK]). */
    val themeMode: StateFlow<AppThemeMode>

    /** Resolved boolean indicating whether dark theme is currently active. */
    val isDarkMode: StateFlow<Boolean>

    /** Sets and persists the target [mode], updating [isDarkMode] and emitting [AppEvent.ThemeModeChanged]. */
    suspend fun setThemeMode(mode: AppThemeMode)

    /** Synchronizes system dark mode configuration when [themeMode] is set to [AppThemeMode.SYSTEM]. */
    fun syncSystemDarkMode(isSystemDark: Boolean)
}

@Singleton
class DefaultAppThemeManager @Inject constructor(
    private val cacheStore: CacheStore,
    private val appEventBus: AppEventBus,
    private val dispatcherProvider: DispatcherProvider,
    isSystemDarkInitial: Boolean = false,
) : AppThemeManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private var isSystemDark: Boolean = isSystemDarkInitial

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    override val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(isSystemDarkInitial)
    override val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        scope.launch(dispatcherProvider.io) {
            val storedName = cacheStore.read(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
            val mode = runCatching { AppThemeMode.valueOf(storedName) }.getOrDefault(AppThemeMode.SYSTEM)
            _themeMode.value = mode
            _isDarkMode.value = resolveIsDark(mode, isSystemDark)
        }
    }

    override suspend fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        val newDark = resolveIsDark(mode, isSystemDark)
        _isDarkMode.value = newDark

        withContext(dispatcherProvider.io) {
            cacheStore.write(KEY_THEME_MODE, mode.name)
        }

        appEventBus.publish(AppEvent.ThemeModeChanged(newDark))
    }

    override fun syncSystemDarkMode(isSystemDark: Boolean) {
        this.isSystemDark = isSystemDark
        if (_themeMode.value == AppThemeMode.SYSTEM) {
            val newDark = resolveIsDark(AppThemeMode.SYSTEM, isSystemDark)
            if (_isDarkMode.value != newDark) {
                _isDarkMode.value = newDark
                appEventBus.publish(AppEvent.ThemeModeChanged(newDark))
            }
        }
    }

    private fun resolveIsDark(
        mode: AppThemeMode,
        systemDark: Boolean,
    ): Boolean =
        when (mode) {
            AppThemeMode.SYSTEM -> systemDark
            AppThemeMode.LIGHT -> false
            AppThemeMode.DARK -> true
        }

    companion object {
        const val KEY_THEME_MODE = "key_app_theme_mode"
    }
}
