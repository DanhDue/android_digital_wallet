/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.navigation3.runtime.EntryProviderScope

/**
 * A lambda that allows a module to install its navigation entries into the global entry provider.
 */
typealias EntryProviderInstaller = EntryProviderScope<Any>.() -> Unit
