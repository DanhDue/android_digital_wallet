/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.localization

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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages active application locale and dynamic OTA translation overrides.
 */
interface AppLocalizationManager {
    /** Currently selected language code (e.g., "en", "vi", "ja_JP"). */
    val currentLanguageCode: StateFlow<String>

    /**
     * Updates and persists the active [languageCode], emitting [AppEvent.AppLanguageChanged].
     */
    suspend fun setLocale(languageCode: String)

    /**
     * Applies downloaded dot-notation dynamic translation key-value pairs into memory.
     */
    suspend fun applyDynamicTranslations(translations: Map<String, String>)

    /**
     * Looks up an in-memory OTA overridden string by [key], returning [fallback] if not present.
     */
    fun getString(
        key: String,
        fallback: String,
    ): String
}

@Singleton
class DefaultAppLocalizationManager @Inject constructor(
    private val cacheStore: CacheStore,
    private val appEventBus: AppEventBus,
    private val dispatcherProvider: DispatcherProvider,
    initialLanguage: String = "en",
) : AppLocalizationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val dynamicOverrides = ConcurrentHashMap<String, String>()

    private val _currentLanguageCode = MutableStateFlow(initialLanguage)
    override val currentLanguageCode: StateFlow<String> = _currentLanguageCode.asStateFlow()

    init {
        scope.launch(dispatcherProvider.io) {
            val stored = cacheStore.read(KEY_APP_LANGUAGE, initialLanguage)
            _currentLanguageCode.value = stored
        }
    }

    override suspend fun setLocale(languageCode: String) {
        _currentLanguageCode.value = languageCode
        withContext(dispatcherProvider.io) {
            cacheStore.write(KEY_APP_LANGUAGE, languageCode)
        }
        appEventBus.publish(AppEvent.AppLanguageChanged(languageCode))
    }

    override suspend fun applyDynamicTranslations(translations: Map<String, String>) {
        dynamicOverrides.putAll(translations)
    }

    override fun getString(
        key: String,
        fallback: String,
    ): String = dynamicOverrides[key] ?: fallback

    companion object {
        const val KEY_APP_LANGUAGE = "key_app_language"
    }
}
