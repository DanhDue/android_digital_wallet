/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.extension

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

fun <T> delayFlow(
    timeout: Long,
    value: T,
): Flow<T> =
    flow {
        delay(timeout)
        emit(value)
    }

fun flowInterval(
    interval: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
): Flow<Int> {
    val delayMillis = timeUnit.toMillis(interval)
    return channelFlow {
        var tick = 0
        send(tick)
        while (true) {
            delay(delayMillis)
            send(++tick)
        }
    }
}

fun <T> CoroutineScope.lazyAsync(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> =
    lazy {
        async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }

/**
 * Alias to stateIn with defaults
 */
fun <T> Flow<T>.stateInDefault(
    scope: CoroutineScope,
    initialValue: T,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
) = stateIn(scope, started, initialValue)

/**
 * Delays given [target]'s emission for [timeMillis]
 * i.e skips emission of [target] if something else is emitted before [timeMillis]
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.delayItem(
    timeMillis: Long,
    target: T,
) = mapLatest {
    if (it == target) {
        delay(timeMillis)
        it
    } else {
        it
    }
}

fun <T> Flow<T>.sampleAndKeepLast(periodMillis: Long): Flow<T> {
    require(periodMillis > 0) { "Sample period should be positive" }
    return channelFlow {
        var isDone = false
        val empty = Any()
        var lastValue: Any? = empty
        onEach { lastValue = it }
            .onCompletion { isDone = true }
            .launchIn(this)
        while (!isDone) {
            delay(periodMillis)
            if (lastValue !== empty) {
                @Suppress("UNCHECKED_CAST")
                send(lastValue as T)
                lastValue = empty
            }
        }
        if (lastValue !== empty) {
            @Suppress("UNCHECKED_CAST")
            send(lastValue as T)
        }
    }
}
