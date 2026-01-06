/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.di

import com.danhdue.settings.domain.repository.SettingsRepository
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Settings feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object SettingsDomainModule {
    /**
     * Provides the GetSettingsDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetSettingsDataUseCase(repository: SettingsRepository): GetSettingsDataUseCase =
        GetSettingsDataUseCase(
            repository,
        )
}
