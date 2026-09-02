/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoot
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
            entry<AppRoutes.TransactionListRoute> {
                TransactionListRoot(onEvent = {})
            }
        }
}
