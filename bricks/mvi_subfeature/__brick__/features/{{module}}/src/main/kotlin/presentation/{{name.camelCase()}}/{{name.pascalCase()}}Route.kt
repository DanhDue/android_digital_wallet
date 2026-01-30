/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.presentation.{{name.camelCase()}}

import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Defines the navigation route for the {{name.pascalCase()}} screen.
 * Used by a type-safe navigation library.
 */
@Parcelize
data object {{name.pascalCase()}}Route : NavKey, Parcelable
