/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.data.di

import com.danhdue.mywallet.data.repository.DefaultMyWalletRepository
import com.danhdue.mywallet.domain.repository.MyWalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the MyWallet feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MyWalletDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindMyWalletRepository(repository: DefaultMyWalletRepository): MyWalletRepository
}
