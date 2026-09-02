/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.di

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.danhdue.core.base.app.AppInitializer
import com.danhdue.core.base.app.AppInitializerImpl
import com.danhdue.core.coroutines.DefaultDispatcherProvider
import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.framework.base.app.FlipperInitializer
import com.danhdue.framework.base.app.MultiDexInitializer
import com.danhdue.framework.base.app.NetworkConfig
import com.danhdue.framework.base.app.TimberInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val IMAGE_CACHE_DIR = "image_cache"
    const val IMAGE_MEMORY_CACHE_MAX_SIZE_PERCENT = 0.25
    const val IMAGE_DISK_CACHE_MAX_SIZE_PERCENT = 0.02

    @Provides
    @Singleton
    fun providesContext(
        @ApplicationContext context: Context,
    ): Context = context

    @ExecutorNetworkIO
    @Singleton
    @Provides
    fun providesExecutorNetworkIO(): Executor = Executors.newFixedThreadPool(2)

    @Provides
    @Singleton
    fun providesNetworkConfig(): NetworkConfig = ApiNetworkConfig()

    @Provides
    @Singleton
    fun providesTimberInitializer(networkConfig: NetworkConfig) = TimberInitializer(networkConfig.isDev())

    @Provides
    @Singleton
    fun providesMultiDexInitializer() = MultiDexInitializer()

    @Provides
    @Singleton
    fun providesFlipperInitializer() = FlipperInitializer()

    @Provides
    @Singleton
    fun providesAppInitializer(
        multiDexInitializer: MultiDexInitializer,
        timberInitializer: TimberInitializer,
        flipperInitializer: FlipperInitializer,
    ): AppInitializer =
        AppInitializerImpl(
            timberInitializer,
            multiDexInitializer,
            flipperInitializer,
        )

    @Provides
    @Singleton
    fun providesImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader =
        ImageLoader
            .Builder(context)
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(context, percent = IMAGE_MEMORY_CACHE_MAX_SIZE_PERCENT)
                    .build()
            }.diskCache {
                DiskCache
                    .Builder()
                    .directory(context.cacheDir.resolve(IMAGE_CACHE_DIR))
                    .maxSizePercent(IMAGE_DISK_CACHE_MAX_SIZE_PERCENT)
                    .build()
            }.build()

    @Provides
    @Singleton
    fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
