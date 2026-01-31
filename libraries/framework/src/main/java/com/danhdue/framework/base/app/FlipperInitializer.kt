/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import com.danhdue.framework.BuildConfig
import timber.log.Timber

/**
 * Initializes Facebook Flipper for debug builds only.
 *
 * This class performs initialization lazily at runtime using reflection
 * to avoid compile-time dependencies on Flipper classes in release builds.
 */
@Suppress("TooManyFunctions")
class FlipperInitializer : AppInitializer {
    override fun init(coreApp: CoreApplication) {
        if (BuildConfig.DEBUG) {
            initFlipperDebug(coreApp)
        }
    }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    private fun initFlipperDebug(coreApp: CoreApplication) {
        try {
            if (shouldEnableFlipper(coreApp)) {
                initSoLoader(coreApp)
                getFlipperClient(coreApp)?.let { client ->
                    getAddPluginMethod(client)?.let { addPluginMethod ->
                        addNetworkPlugin(client, addPluginMethod)
                        addInspectorPlugin(coreApp, client, addPluginMethod)
                        addLeakCanaryPlugin(client, addPluginMethod)
                        addSharedPreferencesPlugin(coreApp, client, addPluginMethod)
                        val navPlugin = addNavigationPlugin(client, addPluginMethod)
                        // Set navigation plugin AFTER starting client to ensure events can be sent
                        startFlipperClient(client)

                        // Wait for Flipper to be connected before setting the navigation plugin
                        // This ensures that buffered events are flushed only when the connection is ready.
                        try {
                            val listenerClass = Class.forName("com.facebook.flipper.core.FlipperStateUpdateListener")
                            val listenerProxy =
                                java.lang.reflect.Proxy.newProxyInstance(
                                    listenerClass.classLoader,
                                    arrayOf(listenerClass),
                                ) { _, method, _ ->
                                    if (method.name == "onUpdate") {
                                        val stateMethod = client.javaClass.getMethod("getState")
                                        val state = stateMethod.invoke(client)
                                        val stateString = state.toString()

                                        if (stateString == "CONNECTED" && navPlugin != null) {
                                            // Once connected, set the plugin to flush events
                                            setFlipperNavigationPlugin(navPlugin)
                                            // We could unsubscribe here, but keeping it simple is fine
                                        }
                                    }
                                    null
                                }

                            val subscribeMethod =
                                client.javaClass.getMethod(
                                    "subscribeForUpdates",
                                    listenerClass,
                                )
                            subscribeMethod.invoke(client, listenerProxy)
                        } catch (e: Exception) {
                            Timber.d(e, "Failed to subscribe for Flipper updates")
                            // Fallback: Set it immediately if listener fails
                            if (navPlugin != null) {
                                setFlipperNavigationPlugin(navPlugin)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d(e, "Flipper initialization failed")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun setFlipperNavigationPlugin(plugin: Any) {
        try {
            val flipperNavigationObjectClass =
                Class.forName(
                    "com.danhdue.framework.network.flipper.FlipperNavigationObject",
                )
            val setPluginMethod =
                flipperNavigationObjectClass.getMethod(
                    "setNavigationPlugin",
                    Any::class.java,
                )
            setPluginMethod.invoke(null, plugin)
        } catch (e: Exception) {
            Timber.d(e, "Failed to set FlipperNavigationObject plugin")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun shouldEnableFlipper(context: android.content.Context): Boolean =
        try {
            val flipperUtilsClass = Class.forName("com.facebook.flipper.android.utils.FlipperUtils")
            val shouldEnableMethod =
                flipperUtilsClass.getMethod(
                    "shouldEnableFlipper",
                    android.content.Context::class.java,
                )
            shouldEnableMethod.invoke(null, context) as Boolean
        } catch (e: Exception) {
            Timber.d(e, "FlipperUtils not available")
            false
        }

    @Suppress("TooGenericExceptionCaught")
    private fun initSoLoader(context: android.content.Context) {
        try {
            val soLoaderClass = Class.forName("com.facebook.soloader.SoLoader")
            val initMethod =
                soLoaderClass.getMethod(
                    "init",
                    android.content.Context::class.java,
                    Boolean::class.javaPrimitiveType,
                )
            initMethod.invoke(null, context, false)
        } catch (e: Exception) {
            Timber.d(e, "SoLoader init failed")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun getFlipperClient(context: android.content.Context): Any? =
        try {
            val flipperClientClass = Class.forName("com.facebook.flipper.android.AndroidFlipperClient")
            val getInstanceMethod =
                flipperClientClass.getMethod(
                    "getInstance",
                    android.content.Context::class.java,
                )
            getInstanceMethod.invoke(null, context)
        } catch (e: Exception) {
            Timber.d(e, "Failed to get Flipper client")
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun getAddPluginMethod(client: Any): java.lang.reflect.Method? =
        try {
            val flipperPluginClass = Class.forName("com.facebook.flipper.core.FlipperPlugin")
            client.javaClass.getMethod("addPlugin", flipperPluginClass)
        } catch (e: Exception) {
            Timber.d(e, "Failed to get addPlugin method")
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun addNetworkPlugin(
        client: Any,
        addPluginMethod: java.lang.reflect.Method,
    ) {
        try {
            val networkPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.network.NetworkFlipperPlugin",
                )
            val networkPlugin = networkPluginClass.getDeclaredConstructor().newInstance()

            val flipperNetworkObjectClass =
                Class.forName(
                    "com.danhdue.framework.network.flipper.FlipperNetworkObject",
                )
            val setPluginMethod =
                flipperNetworkObjectClass.getMethod(
                    "setNetworkPlugin",
                    Any::class.java,
                )
            setPluginMethod.invoke(null, networkPlugin)
            addPluginMethod.invoke(client, networkPlugin)
        } catch (e: Exception) {
            Timber.d(e, "NetworkFlipperPlugin not available")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addInspectorPlugin(
        context: android.content.Context,
        client: Any,
        addPluginMethod: java.lang.reflect.Method,
    ) {
        try {
            val inspectorPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin",
                )
            val descriptorMappingClass =
                Class.forName(
                    "com.facebook.flipper.plugins.inspector.DescriptorMapping",
                )
            val withDefaultsMethod = descriptorMappingClass.getMethod("withDefaults")
            val descriptorMapping = withDefaultsMethod.invoke(null)

            val inspectorConstructor =
                inspectorPluginClass.getConstructor(
                    android.content.Context::class.java,
                    descriptorMappingClass,
                )
            val inspectorPlugin = inspectorConstructor.newInstance(context, descriptorMapping)
            addPluginMethod.invoke(client, inspectorPlugin)
        } catch (e: Exception) {
            Timber.d(e, "InspectorFlipperPlugin not available")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addLeakCanaryPlugin(
        client: Any,
        addPluginMethod: java.lang.reflect.Method,
    ) {
        try {
            val leakCanaryPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.leakcanary.LeakCanaryFlipperPlugin",
                )
            val leakCanaryPlugin = leakCanaryPluginClass.getDeclaredConstructor().newInstance()
            addPluginMethod.invoke(client, leakCanaryPlugin)
        } catch (e: Exception) {
            Timber.d(e, "LeakCanaryFlipperPlugin not available")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addSharedPreferencesPlugin(
        context: android.content.Context,
        client: Any,
        addPluginMethod: java.lang.reflect.Method,
    ) {
        try {
            val sharedPrefsPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin",
                )
            val packageName = context.packageName
            val defaultPrefsName = "${packageName}_preferences"

            val descriptorClass =
                Class.forName(
                    "com.facebook.flipper.plugins.sharedpreferences." +
                        "SharedPreferencesFlipperPlugin\$SharedPreferencesDescriptor",
                )
            val descriptorConstructor =
                descriptorClass.getConstructor(
                    String::class.java,
                    Int::class.javaPrimitiveType,
                )
            val descriptor =
                descriptorConstructor.newInstance(
                    defaultPrefsName,
                    android.content.Context.MODE_PRIVATE,
                )

            val listConstructor =
                sharedPrefsPluginClass.getConstructor(
                    android.content.Context::class.java,
                    java.util.List::class.java,
                )
            val sharedPrefsPlugin = listConstructor.newInstance(context, listOf(descriptor))
            addPluginMethod.invoke(client, sharedPrefsPlugin)
        } catch (e: Exception) {
            Timber.d(e, "SharedPreferencesFlipperPlugin not available")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addNavigationPlugin(
        client: Any,
        addPluginMethod: java.lang.reflect.Method,
    ): Any? =
        try {
            val navigationPluginClass =
                Class.forName(
                    "com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin",
                )
            val getInstanceMethod = navigationPluginClass.getMethod("getInstance")
            val navigationPlugin = getInstanceMethod.invoke(null)

            addPluginMethod.invoke(client, navigationPlugin)
            navigationPlugin
        } catch (e: Exception) {
            Timber.d(e, "NavigationFlipperPlugin not available")
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun startFlipperClient(client: Any) {
        try {
            val startMethod = client.javaClass.getMethod("start")
            startMethod.invoke(client)
        } catch (e: Exception) {
            Timber.d(e, "Failed to start Flipper client")
        }
    }
}
