/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.danhdue.framework.network.flipper.FlipperNavigationObject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Observes a Navigation3 backstack and reports changes to Flipper.
 *
 * This composable should be placed alongside NavDisplay to automatically
 * track navigation events when the backstack changes.
 *
 * @param backStack The backstack to observe.
 * @param prefix Optional prefix for the route name (e.g., tab name).
 */
@Composable
fun ObserveBackstackForFlipper(
    backStack: List<Any>,
    prefix: String = "",
) {
    LaunchedEffect(backStack) {
        snapshotFlow { backStack.toList() }
            .map { it.lastOrNull() }
            .distinctUntilChanged()
            .collect { currentDestination ->
                currentDestination?.let { destination ->
                    val routeName = destination::class.simpleName ?: destination.toString()
                    val fullRoute = if (prefix.isNotEmpty()) "$prefix/$routeName" else routeName
                    Timber.d("ObserveBackstackForFlipper: Emitting $fullRoute")
                    FlipperNavigationObject.sendNavigation(fullRoute)
                }
            }
    }
}

/**
 * Observes a Navigation3 backstack and reports the full stack to Flipper.
 *
 * This variant reports the entire backstack, useful for debugging complex navigation flows.
 *
 * @param backStack The backstack to observe.
 * @param stackName Name to identify this backstack in logs.
 */
@Composable
fun ObserveFullBackstackForFlipper(
    backStack: List<Any>,
    stackName: String = "Main",
) {
    LaunchedEffect(backStack) {
        snapshotFlow { backStack.toList() }
            .distinctUntilChanged()
            .collect { stack ->
                val routes = stack.map { it::class.simpleName ?: it.toString() }
                val stackStr = routes.joinToString(" → ")
                FlipperNavigationObject.sendNavigation("[$stackName] $stackStr")
            }
    }
}
