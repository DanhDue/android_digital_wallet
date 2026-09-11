/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.domain.model

data class PluginData(
    val id: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
