/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.danhdue.scanner.data.repository.DefaultScannerRepository
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase
import com.danhdue.scanner.presentation.ScannerViewModel

/**
 * Manual composition root for the on-demand `scanner` Dynamic Feature Module
 * (Task 14, design §4.4).
 *
 * A downloaded split has no Hilt graph, so the feature assembles its own object
 * graph here — deliberately in `com.danhdue.scanner.di`, outside the
 * `presentation` / `domain` / `data` layer packages, so it may legitimately
 * reference all three without tripping Konsist K2. An install-time feature would
 * do this with Hilt `@Module`s instead.
 */
fun scannerViewModelFactory(): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            ScannerViewModel(GetScannerDataUseCase(DefaultScannerRepository()))
        }
    }
