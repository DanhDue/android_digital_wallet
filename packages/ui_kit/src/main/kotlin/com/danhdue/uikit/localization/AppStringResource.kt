/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource

@Suppress("UnusedParameter")
private fun defaultStringResolver(
    key: String,
    fallback: String,
): String = fallback

/**
 * CompositionLocal providing a dynamic string resolver function.
 * By default returns the local string fallback.
 */
@Suppress("CompositionLocalAllowlist")
val LocalDynamicStringResolver =
    staticCompositionLocalOf<(key: String, fallback: String) -> String> {
        ::defaultStringResolver
    }

/**
 * Resolves a localized string resource, prioritizing any dynamic OTA translations
 * provided by [LocalDynamicStringResolver] if a [remoteKey] is provided.
 */
@Composable
fun appStringResource(
    @StringRes id: Int,
    remoteKey: String? = null,
): String {
    val localString = stringResource(id)
    return if (remoteKey != null) {
        LocalDynamicStringResolver.current(remoteKey, localString)
    } else {
        localString
    }
}
