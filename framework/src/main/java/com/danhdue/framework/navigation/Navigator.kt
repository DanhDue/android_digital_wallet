/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * A stateful navigator that manages the backstack for Navigation 3.
 * Scoped to [ActivityRetainedScoped] to survive configuration changes.
 *
 * Note: Flipper navigation tracking is handled by ObserveBackstackForFlipper
 * composable which observes backstack changes using snapshotFlow.
 */
@ActivityRetainedScoped
class Navigator
    @Inject
    constructor(
        startDestination: Any,
    ) {
        val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

        /**
         * Navigates to a new destination by adding it to the backstack.
         */
        fun navigateTo(destination: Any) {
            backStack.add(destination)
        }

        /**
         * Navigates to a new destination and clears the current backstack.
         * Useful for switching from auth flows to the main app flow.
         */
        fun navigateAndClearBackStack(destination: Any) {
            backStack.clear()
            backStack.add(destination)
        }

        /**
         * Removes the top destination from the backstack.
         */
        fun popBackStack() {
            if (backStack.isNotEmpty()) {
                backStack.removeAt(backStack.size - 1)
            }
        }
    }
