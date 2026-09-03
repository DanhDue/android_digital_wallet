/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.log

/**
 * The logging abstraction for every module that builds on `:core`.
 *
 * `:core` is the dependency floor and must not pin a concrete logging library.
 * Upper layers (`:framework`, `:network`, `:app`) provide the Timber-backed
 * implementation and bind it into the DI graph; `:core` code depends only on
 * this contract.
 *
 * Levels mirror the standard Android priorities (verbose → assert). Each method
 * accepts an optional [throwable] so call sites can attach the cause.
 */
interface Logger {
    fun verbose(
        message: String,
        throwable: Throwable? = null,
    )

    fun debug(
        message: String,
        throwable: Throwable? = null,
    )

    fun info(
        message: String,
        throwable: Throwable? = null,
    )

    fun warn(
        message: String,
        throwable: Throwable? = null,
    )

    fun error(
        message: String,
        throwable: Throwable? = null,
    )
}
