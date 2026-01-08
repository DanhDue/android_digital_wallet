/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.scanner.presentation.ScannerRoot
import com.danhdue.scanner.presentation.ScannerRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object ScannerNavigationModule {
    @Provides
    @IntoSet
    fun provideScannerEntries(): EntryProviderInstaller =
        {
            entry<ScannerRoute> {
                ScannerRoot(onEvent = {})
            }
        }
}
