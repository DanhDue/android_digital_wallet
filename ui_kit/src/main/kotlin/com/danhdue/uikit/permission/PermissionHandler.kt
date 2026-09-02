/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.permission

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

/**
 * A composable that handles runtime permission requests with best practices.
 *
 * Features:
 * - Automatic permission status tracking
 * - Rationale dialog when permission is denied once
 * - Settings redirect when permission is permanently denied
 * - Clean state management via [PermissionState]
 *
 * Example usage:
 * ```
 * PermissionHandler(
 *     permissionState = rememberPermissionState(Manifest.permission.CAMERA),
 *     rationaleTitle = "Camera Permission Required",
 *     rationaleMessage = "We need camera access to scan QR codes.",
 *     onDismiss = { /* Handle dismissal */ },
 * ) {
 *     // Content that requires the permission
 *     CameraPreview()
 * }
 * ```
 *
 * @param permissionState The state holder created via [rememberPermissionState]
 * @param rationaleTitle Title for the rationale dialog
 * @param rationaleMessage Message explaining why the permission is needed
 * @param permanentlyDeniedTitle Title for the permanently denied dialog
 * @param permanentlyDeniedMessage Message explaining how to enable from settings
 * @param requestOnMount Whether to automatically request the permission when the composable mounts
 * @param onDismiss Callback when user dismisses any dialog without action
 * @param content The content to display, receives [PermissionState] for status checks
 */
@Suppress("LongParameterList")
@Composable
fun PermissionHandler(
    permissionState: PermissionState,
    rationaleTitle: String = "Permission Required",
    rationaleMessage: String = "This permission is required for the app to function properly.",
    permanentlyDeniedTitle: String = "Permission Required",
    permanentlyDeniedMessage: String = "Please enable this permission in app settings.",
    requestOnMount: Boolean = false,
    onDismiss: () -> Unit = {},
    content: @Composable (PermissionState) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            val shouldShowRationale =
                activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, permissionState.permission)
                } ?: false

            permissionState.handlePermissionResult(granted, shouldShowRationale)

            if (!granted) {
                if (shouldShowRationale) {
                    showRationaleDialog = true
                } else {
                    showSettingsDialog = true
                }
            }
        }

    // Set the launcher on the state
    LaunchedEffect(permissionLauncher) {
        permissionState.permissionLauncher = { permissionLauncher.launch(permissionState.permission) }
    }

    // Auto-request on mount if enabled
    LaunchedEffect(requestOnMount) {
        if (requestOnMount && !permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }

    // Rationale dialog
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = {
                showRationaleDialog = false
                onDismiss()
            },
            title = { Text(rationaleTitle) },
            text = { Text(rationaleMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        permissionState.launchPermissionRequest()
                    },
                ) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        onDismiss()
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    // Permanently denied dialog - redirect to settings
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = {
                showSettingsDialog = false
                onDismiss()
            },
            title = { Text(permanentlyDeniedTitle) },
            text = { Text(permanentlyDeniedMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        openAppSettings(context)
                    },
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        onDismiss()
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    // Render content
    content(permissionState)
}

/**
 * A simplified composable for requesting a permission on first composition.
 *
 * This is useful for permissions that are essential for the app to function,
 * like notification permissions for showing Chucker notifications.
 *
 * @param permission The permission to request
 * @param onResult Callback with the permission result
 */
@Composable
fun RequestPermissionOnMount(
    permission: String,
    onResult: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            currentOnResult(granted)
        }

    LaunchedEffect(permission) {
        if (!PermissionState.isPermissionGranted(context, permission)) {
            permissionLauncher.launch(permission)
        } else {
            currentOnResult(true)
        }
    }
}
