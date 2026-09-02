/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.split

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * RED → GREEN behaviour spec for [FeatureInstallerImpl] (Task 14). A fake
 * [SplitInstallManager] (mockk) stands in for Play Feature Delivery; the
 * `SplitCompat.install` call is captured through the injected seam.
 *
 * Scenarios (BVA / equivalence partitioning):
 *  - module already installed → `onReady` immediately, no `startInstall`
 *  - module NOT installed → `startInstall`, then `onReady` only on `INSTALLED`
 *  - `SplitCompat.install` runs before `onReady` on both paths
 *  - `FAILED` / `CANCELED` / `REQUIRES_USER_CONFIRMATION` → error surfaced, `onReady` NOT called
 *  - a state update for a DIFFERENT session id is ignored (app-global manager)
 *  - listener unregistered after every terminal state (no leak)
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
// Vanilla Application — do NOT boot the `@HiltAndroidApp` `DigitalWalletApp`
// (its `attachBaseContext` runs `SplitCompat.install` + Hilt).
@Config(application = Application::class, manifest = Config.NONE)
class FeatureInstallerImplTest {
    private companion object {
        const val MODULE = "scanner"
        const val SESSION_ID = 42
    }

    private lateinit var context: Context
    private lateinit var manager: SplitInstallManager
    private val calls = mutableListOf<String>()

    private val listenerSlot = slot<SplitInstallStateUpdatedListener>()
    private val onSuccessSlot = slot<OnSuccessListener<in Int>>()

    private fun installer(): FeatureInstallerImpl =
        FeatureInstallerImpl(
            context = context,
            splitInstallManager = manager,
            splitCompatInstall = { calls += "splitCompat" },
        )

    private fun sessionState(
        status: Int,
        sessionId: Int = SESSION_ID,
        module: String = MODULE,
    ): SplitInstallSessionState =
        mockk(relaxed = true) {
            every { status() } returns status
            every { sessionId() } returns sessionId
            every { moduleNames() } returns listOf(module)
        }

    /** Wires the manager so `startInstall` is not-installed and captures the callbacks. */
    private fun givenNotInstalled() {
        every { manager.installedModules } returns emptySet()
        every { manager.registerListener(capture(listenerSlot)) } returns Unit
        val task = mockk<Task<Int>>(relaxed = true)
        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { manager.startInstall(any()) } returns task
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        manager = mockk(relaxed = true)
        calls.clear()
    }

    @Test
    fun `already installed - runs onReady immediately without startInstall`() =
        runTest {
            every { manager.installedModules } returns setOf(MODULE)

            installer().ensureInstalled(MODULE) { calls += "onReady" }

            assertEquals(listOf("splitCompat", "onReady"), calls)
            verify(exactly = 0) { manager.startInstall(any()) }
        }

    @Test
    fun `not installed - startInstall then onReady only after INSTALLED`() =
        runTest {
            givenNotInstalled()

            val job = launch { installer().ensureInstalled(MODULE) { calls += "onReady" } }
            runCurrent()

            verify { manager.startInstall(any()) }
            assertFalse("onReady must not run before INSTALLED", calls.contains("onReady"))

            onSuccessSlot.captured.onSuccess(SESSION_ID)
            listenerSlot.captured.onStateUpdate(sessionState(SplitInstallSessionStatus.INSTALLED))
            runCurrent()
            job.join()

            assertEquals(listOf("splitCompat", "onReady"), calls)
            verify { manager.unregisterListener(listenerSlot.captured) }
        }

    @Test
    fun `ignores a state update for a different session id`() =
        runTest {
            givenNotInstalled()

            val job = launch { installer().ensureInstalled(MODULE) { calls += "onReady" } }
            runCurrent()
            onSuccessSlot.captured.onSuccess(SESSION_ID)

            // A sibling on-demand module's session reports INSTALLED — must be ignored.
            listenerSlot.captured.onStateUpdate(
                sessionState(SplitInstallSessionStatus.INSTALLED, sessionId = 99, module = "other"),
            )
            runCurrent()
            assertFalse(calls.contains("onReady"))

            // Our own session finishing does resume it.
            listenerSlot.captured.onStateUpdate(sessionState(SplitInstallSessionStatus.INSTALLED))
            runCurrent()
            job.join()
            assertEquals(listOf("splitCompat", "onReady"), calls)
        }

    @Test
    fun `install FAILED - surfaces error and never calls onReady`() = assertTerminalFailure(SplitInstallSessionStatus.FAILED)

    @Test
    fun `install CANCELED - surfaces error and never calls onReady`() = assertTerminalFailure(SplitInstallSessionStatus.CANCELED)

    @Test
    fun `install REQUIRES_USER_CONFIRMATION - surfaces error, no hang`() =
        assertTerminalFailure(SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION)

    private fun assertTerminalFailure(status: Int) =
        runTest {
            givenNotInstalled()

            var error: Throwable? = null
            val job =
                launch {
                    runCatching { installer().ensureInstalled(MODULE) { calls += "onReady" } }
                        .onFailure { error = it }
                }
            runCurrent()
            onSuccessSlot.captured.onSuccess(SESSION_ID)

            listenerSlot.captured.onStateUpdate(sessionState(status))
            runCurrent()
            job.join()

            assertTrue("expected an IllegalStateException for status $status", error is IllegalStateException)
            assertFalse(calls.contains("onReady"))
            verify { manager.unregisterListener(listenerSlot.captured) }
        }
}
