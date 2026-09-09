/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import androidx.navigation3.runtime.NavKey
import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.FeatureEntry
import com.danhdue.platform.featureEntriesFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_REDIRECT_DEPTH = 3

/**
 * Default internal implementation of [DeepLinkRouter].
 *
 * Implements the 8-stage resolution and dispatch pipeline with redirect depth and replay caps.
 */
@Singleton
internal class DefaultDeepLinkRouter @Inject constructor(
    private val appEventBus: AppEventBus,
    private val pendingStore: PendingDeepLinkStore,
    private val dispatcherProvider: DispatcherProvider,
    private val guards: @JvmSuppressWildcards Set<DeepLinkGuard> = emptySet(),
    private val resolvers: @JvmSuppressWildcards Set<DeepLinkResolver> = emptySet(),
    private val entryPoints: List<FeatureEntryPoint> = AppDeepLinks.entryPoints,
) : DeepLinkRouter {
    internal var featureEntryLoader: () -> Iterable<FeatureEntry> = {
        ServiceLoader.load(FeatureEntry::class.java, FeatureEntry::class.java.classLoader)
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val commandChannel = Channel<NavigationCommand>(Channel.BUFFERED)
    private val attemptedModules: MutableSet<String> = ConcurrentHashMap.newKeySet()

    override val commands: Flow<NavigationCommand> = commandChannel.receiveAsFlow()

    init {
        coroutineScope.launch(dispatcherProvider.main) {
            appEventBus.on<AppEvent.UserLoggedIn>().collect {
                pendingStore.takeIfAny()?.let { pendingUri ->
                    executePipeline(pendingUri, redirectDepth = 0)
                }
            }
        }
    }

    override fun dispatch(uri: String) {
        coroutineScope.launch(dispatcherProvider.main) {
            executePipeline(uri, redirectDepth = 0)
        }
    }

    private suspend fun executePipeline(
        uri: String,
        redirectDepth: Int,
    ) {
        val link = DeepLinkParser.parse(uri)
        if (link == null) {
            commandChannel.send(NavigationCommand.Failed(uri, FailureReason.Malformed))
        } else {
            processParsedLink(uri, link, redirectDepth)
        }
    }

    private suspend fun processParsedLink(
        uri: String,
        link: DeepLink,
        redirectDepth: Int,
    ) {
        val entryPoint = entryPoints.firstOrNull { it.feature == link.feature }
        if (entryPoint == null) {
            commandChannel.send(NavigationCommand.Failed(uri, FailureReason.UnknownFeature))
        } else {
            processEntryPoint(uri, link, entryPoint, redirectDepth)
        }
    }

    private suspend fun processEntryPoint(
        uri: String,
        link: DeepLink,
        entryPoint: FeatureEntryPoint,
        redirectDepth: Int,
    ) {
        val preGatePassed =
            if (entryPoint.requiresAuth) {
                val preGateTarget = DeepLinkTarget(destination = entryPoint.entryRoute, requiresAuth = true)
                when (val verdict = evaluateGuards(link, preGateTarget)) {
                    is GuardVerdict.Allow -> true
                    is GuardVerdict.Redirect -> {
                        handleRedirect(verdict.to, redirectDepth)
                        false
                    }
                    is GuardVerdict.Block -> {
                        commandChannel.send(NavigationCommand.Failed(uri, FailureReason.Blocked))
                        false
                    }
                }
            } else {
                true
            }
        if (preGatePassed) {
            processModuleAndResolve(uri, link, entryPoint, redirectDepth)
        }
    }

    private suspend fun processModuleAndResolve(
        uri: String,
        link: DeepLink,
        entryPoint: FeatureEntryPoint,
        redirectDepth: Int,
    ) {
        val dynamicModule = entryPoint.dynamicModule
        if (dynamicModule != null && attemptedModules.add(dynamicModule)) {
            commandChannel.send(NavigationCommand.EnsureModule(module = dynamicModule, replay = uri))
        } else {
            resolveAndRoute(uri, link, entryPoint, redirectDepth)
        }
    }

    private suspend fun resolveAndRoute(
        uri: String,
        link: DeepLink,
        entryPoint: FeatureEntryPoint,
        redirectDepth: Int,
    ) {
        val target = findInResolvers(resolvers, link) ?: findInFeatureEntries(featureEntryLoader(), link)
        if (target == null) {
            val reason =
                if (entryPoint.dynamicModule != null) {
                    FailureReason.InstallFailed
                } else {
                    FailureReason.NoResolver
                }
            commandChannel.send(NavigationCommand.Failed(uri, reason))
        } else {
            routeTarget(uri, link, entryPoint, target, redirectDepth)
        }
    }

    private suspend fun routeTarget(
        uri: String,
        link: DeepLink,
        entryPoint: FeatureEntryPoint,
        target: DeepLinkTarget,
        redirectDepth: Int,
    ) {
        val gatePassed =
            when (val verdict = evaluateGuards(link, target)) {
                is GuardVerdict.Allow -> true
                is GuardVerdict.Redirect -> {
                    handleRedirect(verdict.to, redirectDepth)
                    false
                }
                is GuardVerdict.Block -> {
                    commandChannel.send(NavigationCommand.Failed(uri, FailureReason.Blocked))
                    false
                }
            }
        if (gatePassed) {
            val placement = target.placement ?: derivePlacement(entryPoint)
            commandChannel.send(toCommand(target.destination, placement))
        }
    }

    private suspend fun evaluateGuards(
        link: DeepLink,
        target: DeepLinkTarget,
    ): GuardVerdict {
        for (guard in guards.sortedBy { it.order }) {
            val verdict = guard.check(link, target)
            if (verdict !is GuardVerdict.Allow) return verdict
        }
        return GuardVerdict.Allow
    }

    private suspend fun handleRedirect(
        toUri: String,
        redirectDepth: Int,
    ) {
        if (redirectDepth >= MAX_REDIRECT_DEPTH) {
            commandChannel.send(NavigationCommand.Failed(toUri, FailureReason.RedirectLoop))
        } else {
            executePipeline(toUri, redirectDepth + 1)
        }
    }
}

private fun findInResolvers(
    resolvers: Set<DeepLinkResolver>,
    link: DeepLink,
): DeepLinkTarget? = resolvers.firstNotNullOfOrNull { it.resolve(link) }

private fun findInFeatureEntries(
    loader: Iterable<FeatureEntry>,
    link: DeepLink,
): DeepLinkTarget? = featureEntriesFrom(loader).firstNotNullOfOrNull { it.resolver()?.resolve(link) }

private fun derivePlacement(entryPoint: FeatureEntryPoint): Placement =
    entryPoint.tab?.let { tab ->
        Placement.InTab(tab = tab, parents = listOf(entryPoint.entryRoute))
    } ?: Placement.RootFullScreen

private fun toCommand(
    destination: NavKey,
    placement: Placement,
): NavigationCommand =
    when (placement) {
        is Placement.InTab -> {
            val stack =
                if (placement.parents.lastOrNull() == destination) {
                    placement.parents
                } else {
                    placement.parents + destination
                }
            NavigationCommand.OpenInTab(tab = placement.tab, stack = stack)
        }
        is Placement.RootFullScreen -> NavigationCommand.OpenFullScreen(destination)
        is Placement.CurrentTab -> NavigationCommand.OpenInCurrentTab(destination)
    }
