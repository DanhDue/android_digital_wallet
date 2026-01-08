/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.Navigator
import com.danhdue.mywallet.presentation.MyWalletRoot
import com.danhdue.mywallet.presentation.MyWalletRoute
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
            entry<MyWalletRoute> {
                MyWalletRoot(
                    onEvent = { event ->
                        // Handle events here
                    },
                )
            }
        }
}
