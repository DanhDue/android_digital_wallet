/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.domain.di

import com.danhdue.home.domain.repository.HomeRepository
import com.danhdue.home.domain.usecase.GetHomeDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Home feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object HomeDomainModule {
    /**
     * Provides the GetHomeDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetHomeDataUseCase(repository: HomeRepository): GetHomeDataUseCase = GetHomeDataUseCase(repository)
}
