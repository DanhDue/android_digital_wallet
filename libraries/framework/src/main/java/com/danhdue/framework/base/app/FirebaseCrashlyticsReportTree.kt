/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber

class FirebaseCrashlyticsReportTree : Timber.Tree() {
    init {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority == Log.ERROR) {
            Firebase.crashlytics.log(message)
            Firebase.crashlytics.setCustomKey(tag.toString(), message)
            t?.let {
                Firebase.crashlytics.recordException(it)
                with(Firebase.crashlytics) {
                    // optional: setCustomKey("CUSTOME_TAG", any)
                    recordException(it)
                }
            }
        }
    }
}
