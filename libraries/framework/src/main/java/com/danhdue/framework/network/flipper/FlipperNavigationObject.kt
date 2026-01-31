/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.network.flipper

import com.danhdue.framework.BuildConfig
import timber.log.Timber

/**
 * Object to hold the Flipper navigation plugin instance.
 *
 * This allows the Navigator class to report navigation events to Flipper.
 * Uses reflection-based approach to avoid compile-time Flipper dependencies
 * in release builds.
 */
object FlipperNavigationObject {
    private var flipperNavigationPlugin: Any? = null
    private val pendingEvents = java.util.Collections.synchronizedList(mutableListOf<String>())

    /**
     * Sets the navigation plugin instance. Called from FlipperInitializer.
     */
    @JvmStatic
    fun setNavigationPlugin(plugin: Any?) {
        flipperNavigationPlugin = plugin
        if (plugin != null) {
            flushPendingEvents()
        }
    }

    private fun flushPendingEvents() {
        synchronized(pendingEvents) {
            Timber.d("Flushing ${pendingEvents.size} pending navigation events to Flipper")
            if (pendingEvents.isNotEmpty()) {
                val iterator = pendingEvents.iterator()
                while (iterator.hasNext()) {
                    val route = iterator.next()
                    Timber.d("Flushing event: $route")
                    sendNavigationInternal(route)
                    iterator.remove()
                }
            }
        }
    }

    /**
     * Reports a navigation event to Flipper.
     *
     * @param route The route/destination that was navigated to.
     */
    @Suppress("TooGenericExceptionCaught")
    fun sendNavigation(route: String) {
        if (!BuildConfig.DEBUG) {
            return
        }

        if (flipperNavigationPlugin == null) {
            // Buffer event if plugin not ready yet
            pendingEvents.add(route)
            Timber.d("Buffered navigation event: %s", route)
            return
        }

        sendNavigationInternal(route)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun sendNavigationInternal(route: String) {
        try {
            // Try single-param method (standard API)
            val sendMethod =
                flipperNavigationPlugin?.javaClass?.getMethod(
                    "sendNavigationEvent",
                    String::class.java,
                )
            sendMethod?.invoke(flipperNavigationPlugin, route)
        } catch (e: NoSuchMethodException) {
            // Method doesn't exist, try alternative approach
            Timber.d(e, "sendNavigationEvent(String) not found, trying 3-param version")
            trySendNavigationWithContext(route)
        } catch (e: Exception) {
            Timber.d(e, "Flipper navigation event failed for route: %s", route)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun trySendNavigationWithContext(route: String) {
        try {
            val sendMethod =
                flipperNavigationPlugin?.javaClass?.getMethod(
                    "sendNavigationEvent",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                )
            sendMethod?.invoke(flipperNavigationPlugin, route, null, null)
        } catch (e: Exception) {
            Timber.d(e, "Flipper navigation event (3-param) failed for route: %s", route)
        }
    }

    /**
     * Reports a navigation event with the destination object.
     * Automatically extracts the route name from the destination class.
     *
     * @param destination The destination object (route class or sealed class).
     */
    fun sendNavigationForDestination(destination: Any) {
        val routeName = destination::class.simpleName ?: destination.toString()
        sendNavigation(routeName)
    }
}
