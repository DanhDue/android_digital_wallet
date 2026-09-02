/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase

/**
 * Manages the business logic and state for the Scanner feature.
 *
 * Unlike an install-time feature, `scanner` is an on-demand Dynamic Feature
 * Module (Task 14): its `@Module`s never reach the host Hilt graph, so this
 * ViewModel is deliberately Hilt-free. It is assembled by
 * `com.danhdue.scanner.di.scannerViewModelFactory` and built through the plain
 * AndroidX `viewModel()` factory in [ScannerRoot].
 */
class ScannerViewModel(
    private val getScannerDataUseCase: GetScannerDataUseCase,
) : MviViewModel<ScannerState, ScannerAction, ScannerEvent>(
        initialState = ScannerState(),
    ) {
    init {
        loadInitialData()
    }

    override fun onAction(action: ScannerAction) {
        // No actions yet — see ScannerAction.
        when (action) {
            else -> Unit
        }
    }

    private fun loadInitialData() {
        safeLaunch {
            reduce { copy(isLoading = true) }

            getScannerDataUseCase()
                .onSuccess { }
                .onFailure { }

            reduce { copy(isLoading = false) }
        }
    }
}
