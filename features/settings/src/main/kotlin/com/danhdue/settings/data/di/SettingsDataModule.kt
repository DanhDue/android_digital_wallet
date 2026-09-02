/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.di

import com.danhdue.settings.data.repository.DefaultSettingsRepository
import com.danhdue.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Settings feature.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(repository: DefaultSettingsRepository): SettingsRepository
}
