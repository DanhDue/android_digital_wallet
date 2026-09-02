/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import com.lemonappdev.konsist.api.provider.KoFullyQualifiedNameProvider
import com.lemonappdev.konsist.api.provider.KoNameProvider
import com.lemonappdev.konsist.api.provider.KoPackageProvider

/** Best-effort stable identifier for a declaration: its FQN, or its simple name. */
fun <T> T.qualified(): String where T : KoFullyQualifiedNameProvider, T : KoNameProvider =
    fullyQualifiedName?.takeIf { it.isNotBlank() } ?: name

/** The declaring package name (`""` for the default package). Works on files and declarations. */
val KoPackageProvider.packageName: String
    get() = packagee?.name ?: ""
