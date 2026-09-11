/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.domain.repository

import com.danhdue.plugin.domain.model.PluginData

interface PluginRepository {
    suspend fun getData(): Result<PluginData>
    suspend fun syncData(): Result<Boolean>
}
