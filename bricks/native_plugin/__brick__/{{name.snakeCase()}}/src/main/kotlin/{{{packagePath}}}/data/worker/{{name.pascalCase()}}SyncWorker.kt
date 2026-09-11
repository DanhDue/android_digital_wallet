/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class {{name.pascalCase()}}SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Background sync logic
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
