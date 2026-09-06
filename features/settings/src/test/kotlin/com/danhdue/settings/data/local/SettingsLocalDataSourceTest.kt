/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.local

import android.content.Context
import android.content.SharedPreferences
import com.danhdue.core.pref.CacheStore
import com.danhdue.settings.domain.model.SupportedLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsLocalDataSourceTest {
    private val cacheStore: CacheStore = mockk(relaxed = true)
    private lateinit var dataSource: SettingsLocalDataSource

    @Before
    fun setUp() {
        dataSource = SettingsLocalDataSource(cacheStore)
    }

    @Test
    fun `getSupportedLanguages returns bundled defaults if cache is empty`() =
        runTest {
            coEvery { cacheStore.read("key_supported_languages", "") } returns ""

            val languages = dataSource.getSupportedLanguages()

            assertEquals(2, languages.size)
            assertTrue(languages.any { it.code == "en" })
            assertTrue(languages.any { it.code == "vi" })
        }

    @Test
    fun `saveTranslations saves flattened JSON map to cacheStore`() =
        runTest {
            val translations = mapOf("settings.title" to "設定")

            dataSource.saveTranslations("ja_JP", translations)

            coVerify { cacheStore.write(match { it.contains("ja_JP") }, any<String>()) }
        }

    @Test
    fun `saveSupportedLanguages and getSupportedLanguages roundtrips correctly`() =
        runTest {
            val list =
                listOf(
                    SupportedLanguage(code = "en", name = "English", version = "1.0.0", isDefault = true),
                    SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
                    SupportedLanguage(code = "ja_JP", name = "日本語", version = "1.0.0"),
                )

            var storedJson = ""
            coEvery { cacheStore.write("key_supported_languages", any<String>()) } answers {
                storedJson = secondArg()
            }
            coEvery { cacheStore.read("key_supported_languages", "") } answers { storedJson }

            dataSource.saveSupportedLanguages(list)
            val result = dataSource.getSupportedLanguages()

            assertEquals(3, result.size)
            assertEquals("ja_JP", result[2].code)
        }

    @Test
    fun `saveSupportedLanguages writes to prefs and getSupportedLanguagesSync reads from prefs`() =
        runTest {
            val context: Context = mockk(relaxed = true)
            val prefs: SharedPreferences = mockk(relaxed = true)
            val editor: SharedPreferences.Editor = mockk(relaxed = true)

            var prefsStored = ""
            every { context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE) } returns prefs
            every { prefs.edit() } returns editor
            every { editor.putString("key_supported_languages", any()) } answers {
                prefsStored = secondArg()
                editor
            }
            every { prefs.getString("key_supported_languages", null) } answers { prefsStored.ifEmpty { null } }

            val dataSourceWithContext = SettingsLocalDataSource(cacheStore, context)

            val list =
                listOf(
                    SupportedLanguage(code = "en", name = "English", version = "1.0.0", isDefault = true),
                    SupportedLanguage(code = "ja_JP", name = "日本語", version = "1.0.0"),
                )

            dataSourceWithContext.saveSupportedLanguages(list)

            val resultSync = dataSourceWithContext.getSupportedLanguagesSync()
            assertEquals(2, resultSync.size)
            assertEquals("ja_JP", resultSync[1].code)
        }
}
