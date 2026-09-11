/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.di

import android.content.Context
import androidx.annotation.VisibleForTesting

object PluginComponentProvider {
    @Volatile
    private var instance: PluginComponent? = null

    fun get(context: Context): PluginComponent =
        instance ?: synchronized(this) {
            instance ?: run {
                PluginModule.setContext(context)
                DaggerPluginComponent.builder()
                    .build()
                    .also { instance = it }
            }
        }

    @VisibleForTesting
    fun setComponent(component: PluginComponent) {
        instance = component
    }

    @VisibleForTesting
    fun reset() {
        instance = null
    }
}
