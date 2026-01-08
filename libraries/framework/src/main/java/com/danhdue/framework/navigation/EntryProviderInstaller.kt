/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope

/**
 * A typealias for a function that installs navigation entries into an EntryProviderScope.
 */
typealias EntryProviderInstaller = EntryProviderScope<Any>.() -> Unit

/**
 * CompositionLocal to provide the set of navigation installers throughout the app.
 */
val LocalEntryProviderInstallers =
    staticCompositionLocalOf<Set<EntryProviderInstaller>> {
        emptySet()
    }
