/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.flipper

import com.danhdue.network.BuildConfig
import okhttp3.Interceptor
import timber.log.Timber

/**
 * Object to hold the Flipper network plugin instance.
 *
 * This allows the NetworkCoreModule to access the network interceptor
 * that was initialized by FlipperInitializer.
 *
 * Uses reflection-based approach to avoid compile-time Flipper dependencies
 * in release builds.
 */
object FlipperNetworkObject {
    private var flipperNetworkPlugin: Any? = null

    /**
     * Sets the network plugin instance. Called via reflection from FlipperInitializer.
     */
    @JvmStatic
    fun setNetworkPlugin(plugin: Any?) {
        flipperNetworkPlugin = plugin
    }

    /**
     * Creates an OkHttp interceptor for Flipper network inspection.
     *
     * @return The Flipper OkHttp interceptor, or null if Flipper is not initialized.
     */
    @Suppress("TooGenericExceptionCaught")
    fun getInterceptor(): Interceptor? {
        if (!BuildConfig.DEBUG || flipperNetworkPlugin == null) {
            return null
        }

        return try {
            val interceptorClass =
                Class.forName(
                    "com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor",
                )
            val networkPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.network.NetworkFlipperPlugin",
                )
            val constructor = interceptorClass.getConstructor(networkPluginClass)
            constructor.newInstance(flipperNetworkPlugin) as Interceptor
        } catch (e: Exception) {
            Timber.d(e, "Flipper network interceptor not available")
            null
        }
    }
}
