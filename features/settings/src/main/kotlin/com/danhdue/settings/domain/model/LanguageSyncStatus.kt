/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.model

/**
 * Observable synchronization states for on-the-air language switching.
 */
sealed interface LanguageSyncStatus {
    /** Initial or idle status when no language sync operation is occurring. */
    data object Idle : LanguageSyncStatus

    /** Network download is in progress for the specified [languageCode]. */
    data class Loading(
        val languageCode: String,
    ) : LanguageSyncStatus

    /** Optimistic UI state: locally cached or bundled translations applied immediately. */
    data class CachedApplied(
        val languageCode: String,
    ) : LanguageSyncStatus

    /** Translations successfully fetched, cached, and applied for [languageCode]. */
    data class Success(
        val languageCode: String,
    ) : LanguageSyncStatus

    /** Translation download failed for [languageCode]. */
    data class Error(
        val languageCode: String,
        val message: String,
    ) : LanguageSyncStatus
}
