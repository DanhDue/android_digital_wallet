/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.session

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session state and global events like logout.
 */
@Singleton
class SessionManager @Inject constructor() {
    private val _logoutEvent =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val logoutEvent = _logoutEvent.asSharedFlow()

    @Volatile
    var isLoggedIn: Boolean = false

    fun logout() {
        isLoggedIn = false
        _logoutEvent.tryEmit(Unit)
    }
}
