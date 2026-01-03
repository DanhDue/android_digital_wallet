package com.danhdue.framework.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppCoroutineScope @Inject constructor() {

    val dispatcherIO: CoroutineDispatcher by lazy {
        Dispatchers.IO
    }

    val dispatcherDefault: CoroutineDispatcher by lazy {
        Dispatchers.Default
    }

    val dispatcherMain: CoroutineDispatcher by lazy {
        Dispatchers.Main
    }

    private var coroutineScope: CoroutineScope? = null

    open fun getName(): String {
        return "co-scope-default"
    }

    fun provideCoroutineScope(): CoroutineScope {
        init()
        return coroutineScope!!
    }

    @Synchronized
    fun init() {
        if (coroutineScope == null || !coroutineScope!!.isActive) {
            coroutineScope = CoroutineScope(SupervisorJob() + dispatcherMain)
            Timber.d("init app coroutine ${getName()}")
        }
    }

    fun clear() {
        Timber.w("clear ${getName()}")
        try {
            coroutineScope?.cancel()
        } catch (e: Exception) {
            Timber.e("clear scope error: ${e.message}")
        }
        coroutineScope = null
    }

    fun launch(
        delayTime: Long = 0,
        coroutineDispatcher: CoroutineDispatcher = dispatcherDefault,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return doLaunch(coroutineDispatcher, delayTime, block)
    }

    fun launchInMain(
        delayTime: Long = 0, block: suspend CoroutineScope.() -> Unit
    ): Job {
        return doLaunch(dispatcherMain, delayTime, block)
    }

    fun launchInIO(
        delayTime: Long = 0, block: suspend CoroutineScope.() -> Unit
    ): Job {
        return doLaunch(dispatcherIO, delayTime, block)
    }

    fun <T> async(delayTime: Long = 0, block: suspend CoroutineScope.() -> T): Deferred<T> {
        return doAsync(dispatcherDefault, delayTime, block)
    }

    interface CancelableElement {
        suspend fun execute(): Any?
        suspend fun cancel(): Boolean
        fun isDone(): Boolean
        fun clear()
    }

    /**
     * If there is a running job, it shall be canceled before starting a new job.
     */
    open class CancelableJob(private val task: () -> Job) : CancelableElement {
        private val curJob = AtomicReference<Job>()

        override suspend fun execute() {
            try {
                val cancelJob = curJob.get()
                if (cancelJob != null) {
                    Timber.d("cancel job ${cancelJob.hashCode()}")
                    cancelJob.cancelAndJoin()
                }
            } catch (e: Exception) {
                // Do nothing
            }
            try {
                val job = task.invoke()
                Timber.d("execute job ${job.hashCode()}")
                curJob.set(job)
            } catch (e: Exception) {
                Timber.e("execute job exception", e)
            }
        }

        override suspend fun cancel(): Boolean {
            try {
                val cancelJob = curJob.get()
                if (cancelJob != null) {
                    Timber.d("cancel job ${cancelJob.hashCode()}")
                    cancelJob.cancelAndJoin()
                }
                return true
            } catch (e: Exception) {
                // Do nothing
            }
            return false
        }

        override fun isDone(): Boolean {
            val job = curJob.get()
            return job == null || !job.isActive
        }

        override fun clear() {
            curJob.set(null)
        }
    }

    /**
     * If there is a running deferred, it shall be canceled before starting a new deferred.
     */
    open class CancelableDeferred<T>(private val task: () -> Deferred<T>) : CancelableElement {
        private val curDeferred = AtomicReference<Deferred<T>>()

        override suspend fun execute(): T? {
            try {
                val cancelDeferred = curDeferred.get()
                if (cancelDeferred != null) {
                    Timber.d("cancel def ${cancelDeferred.hashCode()}")
                    cancelDeferred.cancelAndJoin()
                }
            } catch (e: Exception) {
                // Do nothing
            }
            return try {
                val deferred = task.invoke()
                Timber.d("execute def ${deferred.hashCode()}")
                curDeferred.set(deferred)

                deferred.await()
            } catch (e: Exception) {
                Timber.e("execute def exception", e)
                null
            }
        }

        override suspend fun cancel(): Boolean {
            try {
                val cancelDeferred = curDeferred.get()
                if (cancelDeferred != null) {
                    Timber.d("cancel def ${cancelDeferred.hashCode()}")
                    cancelDeferred.cancelAndJoin()
                }
                return true
            } catch (e: Exception) {
                // Do nothing
            }
            return false
        }

        override fun isDone(): Boolean {
            val job = curDeferred.get()
            return job == null || !job.isActive
        }

        override fun clear() {
            curDeferred.set(null)
        }
    }

    companion object {
        @JvmStatic
        fun createCancelableJob(
            runTask: () -> Job
        ): CancelableJob {
            return CancelableJob(runTask)
        }

        @JvmStatic
        fun <T> createCancelableDeferred(
            runTask: () -> Deferred<T>
        ): CancelableDeferred<T> {
            return CancelableDeferred(runTask)
        }
    }
}

fun <T> AppCoroutineScope.doAsync(
    dispatcher: CoroutineDispatcher,
    delayTime: Long,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> {
    return provideCoroutineScope().async(dispatcher) {
        delay(delayTime)
        block.invoke(this)
    }
}

fun AppCoroutineScope.doLaunch(
    dispatcher: CoroutineDispatcher = dispatcherDefault,
    delayTime: Long = 0,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return provideCoroutineScope().launch(dispatcher) {
        delay(delayTime)
        block.invoke(this)
    }
}