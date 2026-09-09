/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import androidx.navigation3.runtime.NavKey

/**
 * Strategy defining how a resolved deep link destination should be placed into the navigation hierarchy.
 */
sealed interface Placement {
    /**
     * Switches to [tab] and replaces that tab's nested back stack with [parents] + destination.
     *
     * @property tab zero-based tab index in the shell
     * @property parents synthesised parent routes placed below the destination
     */
    data class InTab(
        val tab: Int,
        val parents: List<NavKey> = emptyList(),
    ) : Placement

    /**
     * Pushes the destination onto the root back stack (above all tabs) as a full-screen screen.
     */
    data object RootFullScreen : Placement

    /**
     * Appends the destination to the currently active tab's nested back stack without switching tabs.
     */
    data object CurrentTab : Placement
}

/**
 * Navigation destination and policy resolved from a [DeepLink].
 *
 * @property destination Target navigation key.
 * @property placement Back-stack placement strategy. If null, the router derives a default:
 *                     `InTab(tab = entryPoint.tab, parents = listOf(entryPoint.entryRoute))` when tab is non-null,
 *                     or `RootFullScreen` when tab is null.
 * @property requiresAuth Fine-grained gate specific to this destination.
 */
data class DeepLinkTarget(
    val destination: NavKey,
    val placement: Placement? = null,
    val requiresAuth: Boolean = false,
)

/**
 * Tier-2 contract implemented by features to declare their internal URL patterns.
 *
 * Install-time features contribute their resolver via Hilt `@Provides @IntoSet DeepLinkResolver`.
 * Dynamic feature modules contribute via [com.danhdue.platform.FeatureEntry.resolver].
 */
fun interface DeepLinkResolver {
    /**
     * Resolves the given [link] into a [DeepLinkTarget], or returns `null`
     * if the link is not recognized by this feature resolver.
     */
    fun resolve(link: DeepLink): DeepLinkTarget?
}
