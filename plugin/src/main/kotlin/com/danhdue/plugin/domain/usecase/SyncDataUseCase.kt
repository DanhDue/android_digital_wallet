/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.domain.usecase

import com.danhdue.plugin.domain.repository.PluginRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val repository: PluginRepository
) {
    suspend fun execute(): Result<Boolean> = repository.syncData()
}
