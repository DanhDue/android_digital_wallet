/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.data.repository

import com.danhdue.plugin.domain.model.PluginData
import com.danhdue.plugin.domain.repository.PluginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRepositoryImpl @Inject constructor() : PluginRepository {
    override suspend fun getData(): Result<PluginData> {
        return Result.success(
            PluginData(
                id = "sample-data-1",
                title = "Native Android Plugin Payload"
            )
        )
    }

    override suspend fun syncData(): Result<Boolean> {
        return Result.success(true)
    }
}
