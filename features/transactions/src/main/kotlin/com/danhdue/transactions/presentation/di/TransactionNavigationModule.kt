/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoot
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides navigation entries for the Transactions feature.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object TransactionNavigationModule {
    @Provides
    @IntoSet
    fun provideTransactionEntries(): EntryProviderInstaller =
        {
            entry<TransactionListRoute> {
                TransactionListRoot(onEvent = {})
            }
        }
}
