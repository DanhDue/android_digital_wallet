/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.network.environment

import androidx.annotation.IntDef

@IntDef(
    Environment.Companion.DEVELOPMENT,
    Environment.Companion.STAGING,
    Environment.Companion.PRODUCTION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class Environment {
    companion object {
        const val DEVELOPMENT = 1
        const val STAGING = 2
        const val PRODUCTION = 3
    }
}
