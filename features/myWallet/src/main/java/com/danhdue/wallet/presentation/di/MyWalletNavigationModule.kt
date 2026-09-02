/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.di

import com.danhdue.framework.navigation.Navigator
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.wallet.presentation.MyWalletRoot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides navigation entries for the MyWallet feature.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object MyWalletNavigationModule {
    @Provides
    @IntoSet
    @Suppress("UnusedParameter")
    fun provideMyWalletEntries(navigator: Navigator): EntryProviderInstaller =
        {
            entry<AppRoutes.MyWalletRoute> {
                MyWalletRoot(
                    onEvent = { event ->
                        // Handle events here
                    },
                )
            }
        }
}
