/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.jetframework.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Opens the app's settings page in the system settings.
 * This is useful when a permission has been permanently denied
 * and the user needs to manually enable it.
 *
 * @param context The context to use for starting the activity
 */
fun openAppSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    context.startActivity(intent)
}

/**
 * Checks if a permission is currently granted.
 *
 * @param context The context to use for checking the permission
 * @param permission The permission to check (e.g., Manifest.permission.CAMERA)
 * @return True if the permission is granted, false otherwise
 */
fun isPermissionGranted(
    context: Context,
    permission: String,
): Boolean = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Checks if all permissions in the list are granted.
 *
 * @param context The context to use for checking permissions
 * @param permissions The list of permissions to check
 * @return True if all permissions are granted, false otherwise
 */
fun areAllPermissionsGranted(
    context: Context,
    permissions: List<String>,
): Boolean = permissions.all { isPermissionGranted(context, it) }

/**
 * Returns a map of permissions to their granted status.
 *
 * @param context The context to use for checking permissions
 * @param permissions The list of permissions to check
 * @return A map where keys are permission strings and values are their granted status
 */
fun getPermissionsStatus(
    context: Context,
    permissions: List<String>,
): Map<String, Boolean> = permissions.associateWith { isPermissionGranted(context, it) }
