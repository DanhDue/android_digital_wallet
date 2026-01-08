/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.data.di

import com.danhdue.scanner.data.repository.DefaultScannerRepository
import com.danhdue.scanner.domain.repository.ScannerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Scanner feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindScannerRepository(repository: DefaultScannerRepository): ScannerRepository
}
