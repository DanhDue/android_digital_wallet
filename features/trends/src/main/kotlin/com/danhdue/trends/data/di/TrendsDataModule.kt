/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.data.di

import com.danhdue.trends.data.repository.DefaultTrendsRepository
import com.danhdue.trends.domain.repository.TrendsRepository
import dagger.Provides

/**
 * Koin module that provides data layer dependencies for the Trends feature.
 */
class TrendsDataModule {
    @Provides
    fun provideTrendsRepository(): TrendsRepository = DefaultTrendsRepository()
}
