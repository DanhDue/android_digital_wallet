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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

/**
 * A composable that handles multiple runtime permission requests.
 *
 * Example usage:
 * ```
 * val permissionsState = rememberMultiplePermissionsState(
 *     listOf(
 *         Manifest.permission.CAMERA,
 *         Manifest.permission.RECORD_AUDIO,
 *     )
 * )
 *
 * MultiplePermissionsHandler(
 *     permissionsState = permissionsState,
 *     rationaleTitle = "Permissions Required",
 *     rationaleMessage = "Camera and microphone access are needed for video calls.",
 * ) { state ->
 *     if (state.allPermissionsGranted) {
 *         VideoCallScreen()
 *     } else {
 *         Button(onClick = { state.launchPermissionsRequest() }) {
 *             Text("Grant Permissions")
 *         }
 *     }
 * }
 * ```
 *
 * @param permissionsState The state holder created via [rememberMultiplePermissionsState]
 * @param rationaleTitle Title for the rationale dialog
 * @param rationaleMessage Message explaining why the permissions are needed
 * @param permanentlyDeniedTitle Title for the permanently denied dialog
 * @param permanentlyDeniedMessage Message explaining how to enable from settings
 * @param requestOnMount Whether to automatically request permissions when the composable mounts
 * @param onDismiss Callback when user dismisses any dialog without action
 * @param content The content to display, receives [MultiplePermissionsState] for status checks
 */
@Suppress("LongParameterList")
@Composable
fun MultiplePermissionsHandler(
    permissionsState: MultiplePermissionsState,
    rationaleTitle: String = "Permissions Required",
    rationaleMessage: String = "These permissions are required for the app to function properly.",
    permanentlyDeniedTitle: String = "Permissions Required",
    permanentlyDeniedMessage: String = "Please enable these permissions in app settings.",
    requestOnMount: Boolean = false,
    onDismiss: () -> Unit = {},
    content: @Composable (MultiplePermissionsState) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Permissions launcher
    val permissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { results ->
            val rationaleNeeded =
                results.filterValues { !it }.keys.filter { permission ->
                    activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                    } ?: false
                }

            permissionsState.handlePermissionsResult(results, rationaleNeeded)

            val hasAnyDenied = results.values.any { !it }
            if (hasAnyDenied) {
                if (rationaleNeeded.isNotEmpty()) {
                    showRationaleDialog = true
                } else {
                    showSettingsDialog = true
                }
            }
        }

    // Set the launcher on the state
    LaunchedEffect(permissionsLauncher) {
        permissionsState.permissionsLauncher = {
            permissionsLauncher.launch(permissionsState.permissions.toTypedArray())
        }
    }

    // Auto-request on mount if enabled
    LaunchedEffect(requestOnMount) {
        if (requestOnMount && !permissionsState.allPermissionsGranted) {
            permissionsState.launchPermissionsRequest()
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
                        permissionsState.launchPermissionsRequest()
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
    content(permissionsState)
}
