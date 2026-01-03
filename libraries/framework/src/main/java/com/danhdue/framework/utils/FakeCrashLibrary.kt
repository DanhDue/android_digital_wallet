package com.danhdue.framework.utils

import com.danhdue.framework.extension.toJsonString
import timber.log.Timber

/** Not a real crash reporting library!  */
class FakeCrashLibrary private constructor() {
    init {
        throw AssertionError("No instances.")
    }

    companion object {
        fun log(priority: Int, tag: String?, message: String?) {
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
