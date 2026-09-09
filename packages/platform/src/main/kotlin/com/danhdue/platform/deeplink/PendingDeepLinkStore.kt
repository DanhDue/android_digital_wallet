/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

/**
 * Storage contract for temporary deep links awaiting authentication.
 *
 * **Security Rationale**: This store is deliberately kept in-memory only and never persisted to disk.
 * Persisting an unauthenticated deep link across application restarts or days later could cause it to fire
 * unexpectedly in an altered security context or under a different user session.
 *
 * Stored links are governed by a 10-minute TTL and have take-once semantics: retrieving a link
 * via [takeIfAny] immediately clears it from the store.
 */
interface PendingDeepLinkStore {
    /**
     * Stores [link] as the pending deep link, replacing any previously stored link.
     */
    fun put(link: String)

    /**
     * Retrieves and removes the stored link if present and within its TTL.
     *
     * Returns `null` if no link is pending, if the link has expired (past 10 minutes),
     * or if it was already taken.
     */
    fun takeIfAny(): String?
}

/**
 * Thread-safe, in-memory implementation of [PendingDeepLinkStore].
 *
 * @param clock Time provider returning milliseconds since epoch; defaults to [System.currentTimeMillis].
 */
internal class InMemoryPendingDeepLinkStore(
    private val clock: () -> Long = System::currentTimeMillis,
) : PendingDeepLinkStore {
    private val lock = Any()
    private var pending: StoredLink? = null

    override fun put(link: String) {
        synchronized(lock) {
            pending = StoredLink(link = link, timestamp = clock())
        }
    }

    override fun takeIfAny(): String? =
        synchronized(lock) {
            val current = pending
            pending = null
            if (current != null && clock() - current.timestamp <= TTL_MILLIS) {
                current.link
            } else {
                null
            }
        }

    private data class StoredLink(
        val link: String,
        val timestamp: Long,
    )

    companion object {
        const val TTL_MILLIS = 10 * 60 * 1000L // 10 minutes
    }
}
