/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.data.di

import com.danhdue.transactions.data.repository.DefaultTransactionsRepository
import com.danhdue.transactions.domain.repository.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Transactions feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionsDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(repository: DefaultTransactionsRepository): TransactionsRepository
}
