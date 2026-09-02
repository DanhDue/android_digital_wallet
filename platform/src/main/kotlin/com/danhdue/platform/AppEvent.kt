/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

/**
 * Marker for every cross-feature signal broadcast through [AppEventBus].
 *
 * Concrete subtypes are data contracts: a feature subscribing to another
 * feature's event *type* is acceptable, the same posture as importing a domain
 * entity. Features may also declare their own [AppEvent] subtypes inside their
 * own `presentation` package.
 */
sealed interface AppEvent {
    /**
     * A shell bottom-navigation tab changed visibility.
     *
     * @property tabIndex zero-based index of the affected tab
     * @property isVisible whether that tab is now visible
     */
    data class ShellTabVisibilityChanged(
        val tabIndex: Int,
        val isVisible: Boolean,
    ) : AppEvent

    /**
     * The host application's coarse lifecycle state changed.
     *
     * Carries the platform-local [AppLifecycleState] rather than
     * `androidx.lifecycle.Lifecycle.State` so the `:platform` public contract
     * stays free of an androidx.lifecycle dependency.
     *
     * @property state the new lifecycle state
     */
    data class AppLifecycleChanged(
        val state: AppLifecycleState,
    ) : AppEvent

    /** The current user signed out; features should clear user-scoped state. */
    data object UserLoggedOut : AppEvent
}

/**
 * Coarse application lifecycle states broadcast via [AppEvent.AppLifecycleChanged].
 *
 * A deliberately small, platform-local vocabulary; it is not a mirror of
 * `androidx.lifecycle.Lifecycle.State`.
 */
enum class AppLifecycleState {
    /** The app moved to the foreground and is interactive. */
    RESUMED,

    /** The app is visible but not the foreground task. */
    STARTED,

    /** The app moved entirely to the background. */
    STOPPED,
}
