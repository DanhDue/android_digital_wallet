/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.base.app

import android.app.Application

/**
 * `androidx.startup`-style one-shot initialization contract.
 *
 * Each implementation performs a single piece of application bootstrap work
 * (multidex install, logging tree, tooling, …). The host composition root
 * fans them out from `Application.onCreate()`.
 *
 * The parameter is the raw [Application]; `:core` is the dependency floor and
 * must not know about the `:framework` `CoreApplication` subclass.
 */
interface AppInitializer {
    fun init(application: Application)
}
