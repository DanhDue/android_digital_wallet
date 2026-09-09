/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.di

import android.content.Context
import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.core.pref.CacheStore
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.deeplink.AuthDeepLinkGuard
import com.danhdue.platform.deeplink.DeepLinkGuard
import com.danhdue.platform.deeplink.InMemoryPendingDeepLinkStore
import com.danhdue.platform.deeplink.PendingDeepLinkStore
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.localization.DefaultAppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.DefaultAppThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {
    private const val PREFS_FILE_NAME = "app_preferences"

    @Provides
    @Singleton
    fun providePlatformCacheStore(
        @ApplicationContext context: Context,
    ): CacheStore = CacheStore(context, PREFS_FILE_NAME)

    @Provides
    @Singleton
    fun provideAppThemeManager(
        cacheStore: CacheStore,
        appEventBus: AppEventBus,
        dispatcherProvider: DispatcherProvider,
    ): AppThemeManager =
        DefaultAppThemeManager(
            cacheStore = cacheStore,
            appEventBus = appEventBus,
            dispatcherProvider = dispatcherProvider,
        )

    @Provides
    @Singleton
    fun provideAppLocalizationManager(
        @ApplicationContext context: Context,
        cacheStore: CacheStore,
        appEventBus: AppEventBus,
        dispatcherProvider: DispatcherProvider,
    ): AppLocalizationManager =
        DefaultAppLocalizationManager(
            cacheStore = cacheStore,
            appEventBus = appEventBus,
            dispatcherProvider = dispatcherProvider,
            context = context,
        )

    @Provides
    @Singleton
    fun providePendingDeepLinkStore(): PendingDeepLinkStore = InMemoryPendingDeepLinkStore()

    @Provides
    @IntoSet
    fun provideAuthDeepLinkGuard(guard: AuthDeepLinkGuard): DeepLinkGuard = guard
}
