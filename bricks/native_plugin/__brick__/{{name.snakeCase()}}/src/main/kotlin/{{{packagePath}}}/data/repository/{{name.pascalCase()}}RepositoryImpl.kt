/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import {{package}}.data.worker.{{name.pascalCase()}}SyncWorker
import {{package}}.domain.model.{{name.pascalCase()}}Data
import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class {{name.pascalCase()}}RepositoryImpl @Inject constructor(
    private val context: Context
) : {{name.pascalCase()}}Repository {

    override suspend fun get{{name.pascalCase()}}Data(): Result<{{name.pascalCase()}}Data> {
        return Result.success(
            {{name.pascalCase()}}Data(
                id = "{{name.snakeCase()}}_id_1",
                title = "{{name.pascalCase()}} Native Data",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun syncData(): Result<Boolean> {
        return try {
            val workRequest = OneTimeWorkRequestBuilder<{{name.pascalCase()}}SyncWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "{{name.snakeCase()}}_sync_work",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
