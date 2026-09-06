/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.localization

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.core.pref.CacheStore
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages active application locale and dynamic OTA translation overrides.
 */
interface AppLocalizationManager {
    /** Currently selected language code (e.g., "en", "vi", "ja_JP"). */
    val currentLanguageCode: StateFlow<String>

    /** Version counter that increments whenever dynamic translations are applied. */
    val translationsVersion: StateFlow<Int>

    /**
     * Updates and persists the active [languageCode], emitting [AppEvent.AppLanguageChanged].
     */
    suspend fun setLocale(languageCode: String)

    /**
     * Applies downloaded dot-notation dynamic translation key-value pairs into memory.
     * If [languageCode] is null, defaults to [currentLanguageCode].
     */
    suspend fun applyDynamicTranslations(
        translations: Map<String, String>,
        languageCode: String? = null,
    )

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
    @ApplicationContext private val context: Context? = null,
    initialLanguage: String? = null,
) : AppLocalizationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val dynamicOverrides = ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()

    private val _currentLanguageCode: MutableStateFlow<String>
    override val currentLanguageCode: StateFlow<String>

    private val _translationsVersion = MutableStateFlow(0)
    override val translationsVersion: StateFlow<Int> = _translationsVersion.asStateFlow()

    init {
        val resolvedInitial = resolveInitialLanguage(initialLanguage)
        _currentLanguageCode = MutableStateFlow(resolvedInitial)
        currentLanguageCode = _currentLanguageCode.asStateFlow()

        loadCachedTranslationsFromPrefs(resolvedInitial)

        scope.launch(dispatcherProvider.io) {
            val stored = cacheStore.read(KEY_APP_LANGUAGE, resolvedInitial)
            if (stored.isNotBlank() && stored != _currentLanguageCode.value) {
                _currentLanguageCode.value = stored
            }
            loadCachedTranslationsFromStore(_currentLanguageCode.value)
        }
    }

    private fun resolveInitialLanguage(initialLanguage: String?): String {
        val savedInPrefs =
            context
                ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.getString(KEY_APP_LANGUAGE, null)

        val appLocalesTag =
            runCatching { AppCompatDelegate.getApplicationLocales() }
                .getOrNull()
                ?.takeIf { !it.isEmpty }
                ?.get(0)
                ?.toLanguageTag()
                ?.replace('-', '_')

        val fallback = if (Locale.getDefault().language.startsWith("vi")) "vi" else "en"

        return initialLanguage?.takeIf { it.isNotBlank() }
            ?: savedInPrefs?.takeIf { it.isNotBlank() }
            ?: appLocalesTag?.takeIf { it.isNotBlank() }
            ?: fallback
    }

    private fun loadCachedTranslationsFromPrefs(languageCode: String) {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val rawJson = prefs.getString(keyForLanguage(languageCode), null) ?: return
        populateDynamicOverridesFromJson(rawJson, languageCode)
    }

    private suspend fun loadCachedTranslationsFromStore(languageCode: String) {
        val rawJson = cacheStore.read(keyForLanguage(languageCode), "")
        if (rawJson.isNotBlank()) {
            val hasChanges = populateDynamicOverridesFromJson(rawJson, languageCode)
            if (hasChanges) {
                _translationsVersion.value += 1
            }
        }
    }

    private fun populateDynamicOverridesFromJson(
        rawJson: String,
        targetLanguage: String,
    ): Boolean =
        runCatching {
            val jsonObject = JSONObject(rawJson)
            val map = mutableMapOf<String, String>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = jsonObject.optString(key)
            }
            if (map.isNotEmpty()) {
                applyDynamicTranslationsInternal(map, targetLanguage, notifyVersion = false)
            } else {
                false
            }
        }.getOrDefault(false)

    override suspend fun setLocale(languageCode: String) {
        if (_currentLanguageCode.value == languageCode && dynamicOverrides[languageCode]?.isNotEmpty() == true) {
            return
        }

        _currentLanguageCode.value = languageCode
        loadCachedTranslationsFromPrefs(languageCode)
        _translationsVersion.value += 1

        context
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_APP_LANGUAGE, languageCode)
            ?.apply()

        withContext(dispatcherProvider.io) {
            cacheStore.write(KEY_APP_LANGUAGE, languageCode)
            loadCachedTranslationsFromStore(languageCode)
        }
        appEventBus.publish(AppEvent.AppLanguageChanged(languageCode))
    }

    override suspend fun applyDynamicTranslations(
        translations: Map<String, String>,
        languageCode: String?,
    ) {
        applyDynamicTranslationsInternal(translations, languageCode, notifyVersion = true)
    }

    private fun applyDynamicTranslationsInternal(
        translations: Map<String, String>,
        languageCode: String?,
        notifyVersion: Boolean,
    ): Boolean {
        if (translations.isEmpty()) return false
        val target = languageCode ?: _currentLanguageCode.value
        val map = dynamicOverrides.getOrPut(target) { ConcurrentHashMap() }

        var changed = false
        translations.forEach { (k, v) ->
            if (map[k] != v) {
                map[k] = v
                changed = true
            }
        }

        val prefix = target.substringBefore('_').substringBefore('-')
        if (prefix != target) {
            val prefixMap = dynamicOverrides.getOrPut(prefix) { ConcurrentHashMap() }
            translations.forEach { (k, v) ->
                if (prefixMap[k] != v) {
                    prefixMap[k] = v
                    changed = true
                }
            }
        }

        if (changed) {
            persistTranslationsToPrefs(target, map)
            if (notifyVersion) {
                _translationsVersion.value += 1
            }
        }
        return changed
    }

    private fun persistTranslationsToPrefs(
        target: String,
        map: Map<String, String>,
    ) {
        runCatching {
            val jsonObject = JSONObject(map as Map<*, *>)
            context
                ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.edit()
                ?.putString(keyForLanguage(target), jsonObject.toString())
                ?.apply()
        }
    }

    override fun getString(
        key: String,
        fallback: String,
    ): String {
        val current = _currentLanguageCode.value
        val map =
            dynamicOverrides[current]
                ?: dynamicOverrides[current.substringBefore('_').substringBefore('-')]

        val direct = map?.get(key)
        val aliased =
            when (key) {
                "home.nav.home" -> map?.get("home.main.title")
                "home.main.title" -> map?.get("home.nav.home")
                else -> null
            }

        return direct ?: aliased ?: fallback
    }

    private fun keyForLanguage(languageCode: String) = "translations_$languageCode"

    companion object {
        const val KEY_APP_LANGUAGE = "key_app_language"
        private const val PREFS_NAME = "app_localization_prefs"
    }
}
