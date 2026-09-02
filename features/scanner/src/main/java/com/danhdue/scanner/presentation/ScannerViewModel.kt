/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the business logic and state for the Scanner feature.
 *
 * MVI: extends [MviViewModel]; UI intents arrive through [onAction], state is
 * mutated with [reduce], one-off events go out via `sendEvent`.
 */
@HiltViewModel
class ScannerViewModel
    @Inject
    constructor(
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
