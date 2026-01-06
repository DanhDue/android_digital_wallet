/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.domain.di

import com.danhdue.trends.domain.repository.TrendsRepository
import com.danhdue.trends.domain.usecase.GetTrendsDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Koin module that provides domain layer dependencies (use cases) for the Trends feature.
 */
@Module
@InstallIn(SingletonComponent::class)
class TrendsDomainModule {
    @Provides
    fun provideGetTrendsDataUseCase(repo: TrendsRepository): GetTrendsDataUseCase = GetTrendsDataUseCase(repo)
}
