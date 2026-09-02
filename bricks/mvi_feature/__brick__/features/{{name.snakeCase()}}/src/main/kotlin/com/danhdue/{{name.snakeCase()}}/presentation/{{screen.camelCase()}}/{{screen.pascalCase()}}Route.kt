/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.{{screen.camelCase()}}

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize

/**
 * Defines the navigation route for the {{screen.pascalCase()}} screen.
 * Used by a type-safe navigation library.
 */
@Parcelize
data object {{screen.pascalCase()}}Route : NavKey, Parcelable
