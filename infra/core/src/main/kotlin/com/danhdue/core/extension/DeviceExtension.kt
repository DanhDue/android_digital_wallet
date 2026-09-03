/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import timber.log.Timber

@Suppress("StringLiteralDuplication", "suppressLintsFor")
fun isEmulator(): Boolean =
    (
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.MODEL.startsWith("sdk_") ||
            Build.DEVICE.startsWith("emulator") ||
            (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
            "google_sdk" == Build.PRODUCT
    )

fun Context.developerEnabled(): Boolean =
    Settings.Secure.getInt(
        this.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0,
    ) != 0

@SuppressLint("HardwareIds")
fun Context.deviceId(): String {
    val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    return androidId.orEmpty()
}

fun Context.appVersion(): String? =
    try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (ex: PackageManager.NameNotFoundException) {
        Timber.d(ex.toString())
        ""
    }

@RequiresApi(Build.VERSION_CODES.P)
fun Context.appVersionCode(): Long =
    try {
        packageManager.getPackageInfo(packageName, 0).longVersionCode
    } catch (ex: PackageManager.NameNotFoundException) {
        Timber.d(ex.toString())
        0L
    }
