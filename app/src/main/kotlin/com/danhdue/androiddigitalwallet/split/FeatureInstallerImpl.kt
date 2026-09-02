/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.split

import android.content.Context
import com.danhdue.platform.FeatureInstaller
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Production [FeatureInstaller] — the real Play Feature Delivery bridge for the
 * template's one on-demand Dynamic Feature Module, `:features:scanner`
 * (Task 14, design §4.4).
 *
 * [ensureInstalled] is idempotent and self-cleaning:
 *
 * * **already installed** — calls [SplitCompat.install] (so a split installed in
 *   a previous process is linked into this one) and runs `onReady` synchronously,
 *   without touching [SplitInstallManager.startInstall].
 * * **not installed** — fires a [SplitInstallRequest], registers a
 *   [SplitInstallStateUpdatedListener] **scoped to this install's session id**
 *   ([SplitInstallManager] is app-global, so a sibling module's session must not
 *   trip this one), and on [SplitInstallSessionStatus.INSTALLED] calls
 *   [SplitCompat.install] then `onReady`. The listener is unregistered on every
 *   terminal state ([SplitInstallSessionStatus.INSTALLED] /
 *   [SplitInstallSessionStatus.FAILED] / [SplitInstallSessionStatus.CANCELED] /
 *   [SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION]) so it never leaks.
 * * **failed / canceled / needs-confirmation** — surfaces an
 *   [IllegalStateException]; `onReady` is NOT called.
 *
 * Unit tests / plain-JVM callers keep
 * [com.danhdue.platform.NoOpFeatureInstaller] (treats every module as present).
 */
@Singleton
class FeatureInstallerImpl internal constructor(
    private val context: Context,
    private val splitInstallManager: SplitInstallManager,
    private val splitCompatInstall: (Context) -> Unit,
) : FeatureInstaller {
    @Inject
    constructor(
        @ApplicationContext context: Context,
        splitInstallManager: SplitInstallManager,
    ) : this(context, splitInstallManager, { SplitCompat.install(it) })

    override suspend fun ensureInstalled(
        module: String,
        onReady: () -> Unit,
    ) {
        if (splitInstallManager.installedModules.contains(module)) {
            Timber.d("Dynamic feature '%s' already installed", module)
            splitCompatInstall(context)
            onReady()
            return
        }
        awaitSessionInstall(module, onReady)
    }

    private suspend fun awaitSessionInstall(
        module: String,
        onReady: () -> Unit,
    ) {
        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule(module)
                .build()

        suspendCancellableCoroutine { continuation ->
            // Session id of *this* install — set once `startInstall` succeeds.
            // `SplitInstallManager` is app-global; without this filter scanner's
            // listener would react to a sibling on-demand module's INSTALLED and
            // fire `onReady` for the wrong split (ClassNotFoundException on nav).
            var sessionId: Int? = null

            lateinit var listener: SplitInstallStateUpdatedListener
            listener =
                SplitInstallStateUpdatedListener { state ->
                    if (!isThisSession(state, sessionId, module)) {
                        return@SplitInstallStateUpdatedListener
                    }
                    when (state.status()) {
                        SplitInstallSessionStatus.INSTALLED -> {
                            splitInstallManager.unregisterListener(listener)
                            splitCompatInstall(context)
                            onReady()
                            if (continuation.isActive) continuation.resume(Unit)
                        }

                        SplitInstallSessionStatus.FAILED,
                        SplitInstallSessionStatus.CANCELED,
                        -> {
                            splitInstallManager.unregisterListener(listener)
                            resumeError(continuation, "Dynamic feature '$module' install ended with ${state.status()}")
                        }

                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                            // This template installer does not drive the Play
                            // confirmation dialog — fail loudly instead of hanging.
                            splitInstallManager.unregisterListener(listener)
                            resumeError(
                                continuation,
                                "Dynamic feature '$module' install requires user confirmation, " +
                                    "which FeatureInstallerImpl does not handle",
                            )
                        }

                        else -> Timber.d("Dynamic feature '%s' install: %s", module, state.status())
                    }
                }

            splitInstallManager.registerListener(listener)
            continuation.invokeOnCancellation { splitInstallManager.unregisterListener(listener) }

            splitInstallManager
                .startInstall(request)
                .addOnSuccessListener { id -> sessionId = id }
                .addOnFailureListener { error ->
                    splitInstallManager.unregisterListener(listener)
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
        }
    }

    /**
     * True when [state] belongs to *this* install. Once [sessionId] is known
     * (the `startInstall` success callback landed) it is the authority; before
     * that we fall back to matching [module] against the session's module list.
     */
    private fun isThisSession(
        state: SplitInstallSessionState,
        sessionId: Int?,
        module: String,
    ): Boolean = if (sessionId != null) state.sessionId() == sessionId else module in state.moduleNames()

    private fun resumeError(
        continuation: CancellableContinuation<Unit>,
        message: String,
    ) {
        if (continuation.isActive) continuation.resumeWithException(IllegalStateException(message))
    }
}
