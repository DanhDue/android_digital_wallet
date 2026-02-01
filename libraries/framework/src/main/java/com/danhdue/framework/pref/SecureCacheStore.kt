/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.pref

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * A secure wrapper around DataStore that encrypts and decrypts values using Tink AEAD.
 */
class SecureCacheStore(
    context: Context,
    fileName: String,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = fileName)
    private val dataStore = context.dataStore
    private val aead: Aead = AeadManager.getAead(context)

    /**
     * Reads and decrypts a value from DataStore.
     * @param key The key to read
     * @param defaultValue The default value if not found or decryption fails
     * @return Decrypted value or defaultValue
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> read(
        key: String,
        defaultValue: T,
    ): T {
        val encryptedValue = dataStore.data.map { it[stringPreferencesKey(key)] }.first() ?: return defaultValue
        val decryptedData = decrypt(encryptedValue) ?: return defaultValue

        return try {
            when (defaultValue) {
                is String -> decryptedData as T
                is Int -> decryptedData.toInt() as T
                is Boolean -> decryptedData.toBoolean() as T
                is Long -> decryptedData.toLong() as T
                is Double -> decryptedData.toDouble() as T
                is Float -> decryptedData.toFloat() as T
                else -> defaultValue
            }
        } catch (e: Exception) {
            Timber.e(e)
            defaultValue
        }
    }

    /**
     * Encrypts and writes a value to DataStore.
     * @param key The key to write
     * @param value The value to encrypt and store
     */
    suspend fun <T> write(
        key: String,
        value: T,
    ) {
        val stringValue = value.toString()
        val encryptedValue = encrypt(stringValue) ?: return
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = encryptedValue
        }
    }

    /**
     * Encrypts and writes multiple values to DataStore in a single transaction.
     * @param keyValues A map of keys to values to write.
     * @return Set of keys that were successfully encrypted and written.
     */
    suspend fun writeBatch(keyValues: Map<String, Any>): Set<String> {
        val encryptedMap = mutableMapOf<String, String>()
        keyValues.forEach { (key, value) ->
            val stringValue = value.toString()
            encrypt(stringValue)?.let { encryptedValue ->
                encryptedMap[key] = encryptedValue
            }
        }

        if (encryptedMap.isNotEmpty()) {
            dataStore.edit { preferences ->
                encryptedMap.forEach { (key, value) ->
                    preferences[stringPreferencesKey(key)] = value
                }
            }
        }
        return encryptedMap.keys
    }

    /**
     * Clears a specific key.
     */
    suspend fun clear(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }

    /**
     * Clears all data in this DataStore.
     */
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    private fun encrypt(data: String): String? =
        try {
            val encrypted = aead.encrypt(data.toByteArray(Charsets.UTF_8), null)
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    private fun decrypt(encryptedData: String): String? =
        try {
            val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
            val decrypted = aead.decrypt(decoded, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
}
