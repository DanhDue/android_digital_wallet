/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.domain.di

import com.danhdue.scanner.domain.repository.ScannerRepository
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Scanner feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ScannerDomainModule {
    /**
     * Provides the GetScannerDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetScannerDataUseCase(repository: ScannerRepository): GetScannerDataUseCase = GetScannerDataUseCase(repository)
}
