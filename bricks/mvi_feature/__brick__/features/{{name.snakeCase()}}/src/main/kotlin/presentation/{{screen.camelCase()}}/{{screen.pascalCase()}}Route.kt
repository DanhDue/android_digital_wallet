/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{name.snakeCase()}}.presentation.{{screen.camelCase()}}

import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Defines the navigation route for the {{screen.pascalCase()}} screen.
 * Used by a type-safe navigation library.
 */
@Parcelize
data object {{screen.pascalCase()}}Route : NavKey, Parcelable
