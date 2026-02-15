/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.pref

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.internal.RegistryConfiguration

/**
 * Manages Tink AEAD (Authenticated Encryption with Associated Data) initialization.
 */
public object AeadManager {
    private const val KEYSET_NAME = "encrypted_store_keyset"
    private const val PREFERENCE_FILE_NAME = "tink_keystore_pref"
    private const val MASTER_KEY_URI = "android-keystore://tink_master_key"

    init {
        AeadConfig.register()
    }

    /**
     * Creates or retrieves an Aead instance.
     * @param context Application context
     * @return Aead instance
     */
    public fun getAead(context: Context): Aead =
        AndroidKeysetManager
            .Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
}
