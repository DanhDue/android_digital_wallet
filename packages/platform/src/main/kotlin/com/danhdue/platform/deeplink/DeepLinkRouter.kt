/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import kotlinx.coroutines.flow.Flow

/**
 * Entry point for deep link routing within the application.
 *
 * Dispatches deep link URIs through a multi-stage validation and resolution pipeline,
 * emitting [NavigationCommand]s consumed exclusively by the host navigation shell.
 */
interface DeepLinkRouter {
    /**
     * Stream of navigation commands emitted by the routing pipeline.
     *
     * Backed by a buffered channel so cold-start commands sent prior to subscriber
     * attachment are preserved and delivered once without duplication across configuration changes.
     */
    val commands: Flow<NavigationCommand>

    /**
     * Initiates asynchronous deep link processing for [uri].
     *
     * Evaluates guards, resolves destinations, enforces dynamic feature module presence,
     * and derives back stack placement before emitting a command to [commands].
     */
    fun dispatch(uri: String)
}
