/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.data.di

import com.danhdue.home.data.repository.DefaultHomeRepository
import com.danhdue.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Home feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindHomeRepository(repository: DefaultHomeRepository): HomeRepository
}
