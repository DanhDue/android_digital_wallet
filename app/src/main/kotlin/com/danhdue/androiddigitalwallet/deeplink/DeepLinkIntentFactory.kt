/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.deeplink

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.danhdue.androiddigitalwallet.ui.MainActivity

/**
 * Factory for creating [Intent]s and [PendingIntent]s that route deep links through [MainActivity].
 *
 * Used for push notifications, shortcuts, and external app integrations. All created intents
 * specify [Intent.ACTION_VIEW] and target [MainActivity] with `FLAG_IMMUTABLE` by default.
 */
object DeepLinkIntentFactory {
    /**
     * Creates an [Intent] configured with [Intent.ACTION_VIEW] for the given [uri],
     * explicitly targeting [MainActivity].
     */
    fun createIntent(
        context: Context,
        uri: String,
    ): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            setClass(context, MainActivity::class.java)
        }

    /**
     * Creates a [PendingIntent] wrapping an [Intent.ACTION_VIEW] Intent targeting [MainActivity].
     *
     * Sets [PendingIntent.FLAG_IMMUTABLE] to satisfy Android 12+ requirements and
     * [PendingIntent.FLAG_UPDATE_CURRENT] so new payload updates take effect.
     * Generates a unique request code based on [uri] hash code by default so distinct
     * notification deep links do not collapse into each other.
     *
     * @param context Host Android context
     * @param uri Deep link URI string (e.g. "myapp://settings/profile")
     * @param requestCode Optional request code, defaults to `uri.hashCode()`
     * @param flags PendingIntent flags, defaults to `FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT`
     */
    fun pendingIntent(
        context: Context,
        uri: String,
        requestCode: Int = uri.hashCode(),
        flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    ): PendingIntent {
        val intent = createIntent(context, uri)
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
}
