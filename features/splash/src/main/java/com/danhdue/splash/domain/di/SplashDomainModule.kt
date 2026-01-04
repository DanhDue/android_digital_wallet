/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.domain.di

import com.danhdue.splash.domain.repository.SplashRepository
import com.danhdue.splash.domain.usecase.GetSplashDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Splash feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object SplashDomainModule {
    /**
     * Provides the GetSplashDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetSplashDataUseCase(repository: SplashRepository): GetSplashDataUseCase = GetSplashDataUseCase(repository)
}
