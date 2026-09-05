/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

/**
 * Marker for every cross-feature signal broadcast through [AppEventBus].
 *
 * All `AppEvent` subtypes are declared here in `:platform`. The cross-feature
 * event vocabulary is deliberately small and contract-like: a feature
 * subscribing to another feature's event *type* is acceptable, the same posture
 * as importing a domain entity. A feature that needs a private, feature-local
 * signal keeps it off this bus. Keeping the hierarchy `sealed` gives every
 * `when (event)` an exhaustiveness check.
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

    /**
     * The user's profile display name, as loaded (or edited) by the Settings
     * feature.
     *
     * The Task 11 pilot signal: `:features:settings` publishes this on load so a
     * host (`:shell`) can render the name in shell-owned chrome — with zero
     * `com.danhdue.settings.*` import. It lives here, not in `settings`, because
     * [AppEvent] is a `sealed interface` (a subtype in another module cannot
     * extend it) and because a profile name shown in the shell is a cross-feature
     * data contract, the same posture as a shared domain entity.
     *
     * @property displayName the profile display name; may be blank
     */
    data class ProfileNameChanged(
        val displayName: String,
    ) : AppEvent

    /**
     * The application theme mode changed.
     *
     * @property isDarkMode whether the application is now in dark mode
     */
    data class ThemeModeChanged(
        val isDarkMode: Boolean,
    ) : AppEvent

    /**
     * The application language changed.
     *
     * @property languageCode the new language code (e.g. "en", "vi", "ja_JP")
     */
    data class AppLanguageChanged(
        val languageCode: String,
    ) : AppEvent
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
