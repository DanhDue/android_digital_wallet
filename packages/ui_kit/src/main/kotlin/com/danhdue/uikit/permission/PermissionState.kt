/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * State holder for a single permission request.
 *
 * Example usage:
 * ```
 * val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA) { granted ->
 *     if (granted) {
 *         // Permission granted, proceed with camera
 *     }
 * }
 *
 * Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
 *     Text("Request Camera")
 * }
 * ```
 */
@Stable
class PermissionState(
    val permission: String,
    private val context: Context,
    private val onPermissionResult: (Boolean) -> Unit,
) {
    /**
     * Whether the permission is currently granted.
     */
    var hasPermission by mutableStateOf(isPermissionGranted(context, permission))
        internal set

    /**
     * Whether the app should show a rationale for the permission.
     * This is true if the user has denied the permission once but not permanently.
     */
    var shouldShowRationale by mutableStateOf(false)
        internal set

    /**
     * Whether the permission has been permanently denied.
     * This is true if the user has denied the permission and selected "Don't ask again".
     */
    var isPermanentlyDenied by mutableStateOf(false)
        internal set

    /**
     * Callback to launch the permission request.
     * This should be set by the PermissionHandler composable.
     */
    internal var permissionLauncher: (() -> Unit)? = null

    /**
     * Launch the permission request dialog.
     */
    fun launchPermissionRequest() {
        permissionLauncher?.invoke()
    }

    /**
     * Refresh the permission status from the system.
     */
    fun refreshPermissionStatus() {
        hasPermission = isPermissionGranted(context, permission)
    }

    /**
     * Handle the result from the permission request.
     */
    internal fun handlePermissionResult(
        granted: Boolean,
        shouldShowRationale: Boolean,
    ) {
        hasPermission = granted
        this.shouldShowRationale = shouldShowRationale
        isPermanentlyDenied = !granted && !shouldShowRationale
        onPermissionResult(granted)
    }

    companion object {
        fun isPermissionGranted(
            context: Context,
            permission: String,
        ): Boolean = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * State holder for multiple permission requests.
 */
@Stable
class MultiplePermissionsState(
    val permissions: List<String>,
    private val context: Context,
    private val onPermissionsResult: (Map<String, Boolean>) -> Unit,
) {
    /**
     * Whether all permissions are currently granted.
     */
    var allPermissionsGranted by mutableStateOf(
        permissions.all { PermissionState.isPermissionGranted(context, it) },
    )
        internal set

    /**
     * Map of permission to its granted status.
     */
    var permissionResults by mutableStateOf<Map<String, Boolean>>(
        permissions.associateWith { PermissionState.isPermissionGranted(context, it) },
    )
        internal set

    /**
     * List of permissions that should show a rationale.
     */
    var permissionsNeedingRationale by mutableStateOf<List<String>>(emptyList())
        internal set

    /**
     * Callback to launch the permissions request.
     */
    internal var permissionsLauncher: (() -> Unit)? = null

    /**
     * Launch the permissions request dialog.
     */
    fun launchPermissionsRequest() {
        permissionsLauncher?.invoke()
    }

    /**
     * Refresh all permission statuses from the system.
     */
    fun refreshPermissionStatus() {
        permissionResults = permissions.associateWith { PermissionState.isPermissionGranted(context, it) }
        allPermissionsGranted = permissionResults.values.all { it }
    }

    /**
     * Handle the result from the permissions request.
     */
    internal fun handlePermissionsResult(
        results: Map<String, Boolean>,
        rationaleNeeded: List<String>,
    ) {
        permissionResults = results
        allPermissionsGranted = results.values.all { it }
        permissionsNeedingRationale = rationaleNeeded
        onPermissionsResult(results)
    }
}

/**
 * Remember a [PermissionState] for a single permission.
 *
 * @param permission The permission to request (e.g., Manifest.permission.CAMERA)
 * @param onPermissionResult Callback invoked when permission result is received
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {},
): PermissionState {
    val context = LocalContext.current
    return remember(permission) {
        PermissionState(permission, context, onPermissionResult)
    }
}

/**
 * Remember a [MultiplePermissionsState] for multiple permissions.
 *
 * @param permissions The list of permissions to request
 * @param onPermissionsResult Callback invoked when permission results are received
 */
@Composable
fun rememberMultiplePermissionsState(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {},
): MultiplePermissionsState {
    val context = LocalContext.current
    return remember(permissions) {
        MultiplePermissionsState(permissions, context, onPermissionsResult)
    }
}
