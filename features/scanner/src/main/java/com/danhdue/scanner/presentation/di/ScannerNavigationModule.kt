/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.scanner.presentation.ScannerRoot
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
            entry<AppRoutes.ScannerRoute> {
                ScannerRoot(onEvent = {})
            }
        }
}
