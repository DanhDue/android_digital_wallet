/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.presentation.{{name.camelCase()}}

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize

/**
 * Defines the navigation route for the {{name.pascalCase()}} screen.
 * Used by a type-safe navigation library.
 */
@Parcelize
data object {{name.pascalCase()}}Route : NavKey, Parcelable
