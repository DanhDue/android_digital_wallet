/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import com.danhdue.plugin.domain.usecase.GetDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPluginHostApiImpl @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) : MyPluginHostApi {

    override fun getPlatformVersion(): String {
        return "Android ${android.os.Build.VERSION.RELEASE}"
    }

    override fun getData(callback: (Result<PigeonPluginData>) -> Unit) {
        coroutineScope.launch {
            try {
                val outcome = getDataUseCase.execute()
                if (outcome.isSuccess) {
                    val data = outcome.getOrNull()
                    val pigeonData = PigeonPluginData(
                        id = data?.id.orEmpty(),
                        title = data?.title.orEmpty(),
                        timestamp = data?.timestamp ?: 0L
                    )
                    callback(Result.success(pigeonData))
                } else {
                    callback(Result.failure(outcome.exceptionOrNull() ?: RuntimeException("Unknown error")))
                }
            } catch (e: Throwable) {
                callback(Result.failure(e))
            }
        }
    }
}
