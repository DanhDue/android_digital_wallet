/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.domain.di

import com.danhdue.transactions.domain.repository.TransactionsRepository
import com.danhdue.transactions.domain.usecase.GetTransactionsDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Transactions feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object TransactionsDomainModule {
    /**
     * Provides the GetTransactionsDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetTransactionsDataUseCase(repository: TransactionsRepository): GetTransactionsDataUseCase =
        GetTransactionsDataUseCase(repository)
}
