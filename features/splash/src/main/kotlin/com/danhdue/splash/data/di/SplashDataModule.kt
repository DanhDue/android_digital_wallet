/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.data.di

import com.danhdue.splash.data.repository.DefaultSplashRepository
import com.danhdue.splash.domain.repository.SplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Splash feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SplashDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindSplashRepository(repository: DefaultSplashRepository): SplashRepository
}
