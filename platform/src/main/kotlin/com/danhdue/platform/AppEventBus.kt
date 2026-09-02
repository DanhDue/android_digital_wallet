/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import javax.inject.Inject
import javax.inject.Singleton

private const val EVENT_REPLAY = 0

private const val EVENT_EXTRA_BUFFER_CAPACITY = 64

/**
 * Application-wide, typed, broadcast event bus.
 *
 * Lets features publish and observe cross-feature signals without depending on
 * each other. Backed by a [MutableSharedFlow] configured with:
 * - `replay = `[EVENT_REPLAY]` (0)` — fire-and-forget, a late subscriber never
 *   sees past events;
 * - `extraBufferCapacity = `[EVENT_EXTRA_BUFFER_CAPACITY]` (64)` — burst
 *   headroom so [publish] never suspends or blocks its caller.
 *
 * Publishing before anyone subscribes is safe (the event is simply dropped),
 * and every active collector receives every subsequent event in publication
 * order.
 */
@Singleton
class AppEventBus @Inject constructor() {
    private val mutableEvents =
        MutableSharedFlow<AppEvent>(
            replay = EVENT_REPLAY,
            extraBufferCapacity = EVENT_EXTRA_BUFFER_CAPACITY,
        )

    /** Stream of every event published to the bus, in publication order. */
    val events: SharedFlow<AppEvent> = mutableEvents.asSharedFlow()

    /**
     * Publishes [event] to all current subscribers.
     *
     * Non-suspending and non-blocking: the [EVENT_EXTRA_BUFFER_CAPACITY] buffer
     * absorbs the event even while collectors are slow.
     */
    fun publish(event: AppEvent) {
        mutableEvents.tryEmit(event)
    }

    /** Returns a [Flow] that emits only the events of type [T]. */
    inline fun <reified T : AppEvent> on(): Flow<T> = events.filterIsInstance<T>()
}
