/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.utils

import com.danhdue.core.extension.toJsonString
import timber.log.Timber

/** Not a real crash reporting library!  */
class FakeCrashLibrary private constructor() {
    init {
        throw AssertionError("No instances.")
    }

    companion object {
        fun log(
            priority: Int,
            tag: String?,
            message: String?,
        ) {
            Timber.d("log(priority: $priority, tag: $tag, message: $message)")
        }

        fun logWarning(t: Throwable?) {
            Timber.w("logWarning(t: ${t.toJsonString()})")
        }

        fun logError(t: Throwable?) {
            Timber.e("logWarning(t: ${t.toJsonString()})")
        }
    }
}
