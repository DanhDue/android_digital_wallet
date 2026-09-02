/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.model

/**
 * Represents the UI model for a single item in the Shell.
 * This class is optimized for display in the Presentation Layer.
 */
data class ShellUiModel(
    val id: String,
    val title: String,
    val description: String,
)
