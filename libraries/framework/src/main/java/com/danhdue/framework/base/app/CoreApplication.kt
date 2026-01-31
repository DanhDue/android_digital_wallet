/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.multidex.MultiDexApplication

abstract class CoreApplication :
    MultiDexApplication(),
    LifecycleEventObserver {
    var isAppInForeground: Boolean = true

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityLifecycleCallback())
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
    ) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> Unit
            Lifecycle.Event.ON_START -> onAppForegrounded()
            Lifecycle.Event.ON_RESUME -> Unit
            Lifecycle.Event.ON_PAUSE -> Unit
            Lifecycle.Event.ON_STOP -> onAppBackgrounded()
            Lifecycle.Event.ON_DESTROY -> Unit
            Lifecycle.Event.ON_ANY -> Unit
        }
    }

    open fun onAppBackgrounded() {
        isAppInForeground = false
    }

    open fun onAppForegrounded() {
        isAppInForeground = true
    }
}
