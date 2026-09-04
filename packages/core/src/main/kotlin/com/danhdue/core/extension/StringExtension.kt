/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.extension

import android.os.Build
import android.text.Html
import android.text.Spanned

const val EMPTY = ""

fun String?.safe(): String = this ?: EMPTY

fun String?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()

@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }

val String.isPhone get() = matches("^[0-9+]*$".toRegex())
