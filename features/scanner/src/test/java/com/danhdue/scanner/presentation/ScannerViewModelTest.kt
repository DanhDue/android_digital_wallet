/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation

import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.scanner.domain.model.Scanner
import com.danhdue.scanner.domain.repository.ScannerRepository
import com.danhdue.scanner.domain.usecase.GetScannerDataUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Minimal coverage for the MVI [ScannerViewModel]: the initial load flips
 * `isLoading` on and back off, mutating [ScannerViewModel.uiState] through the
 * `MviViewModel` `reduce` mechanism.
 */
class ScannerViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private class FakeScannerRepository(
        private val result: Result<Scanner>,
    ) : ScannerRepository {
        override suspend fun getScannerData(): Result<Scanner> = result
    }

    private fun viewModel(result: Result<Scanner> = Result.success(Scanner(id = "1", data = "ok"))): ScannerViewModel =
        ScannerViewModel(GetScannerDataUseCase(FakeScannerRepository(result)))

    @Test
    fun `clears the loading flag once the initial load settles`() =
        coroutineRule.runTest {
            assertEquals(false, viewModel().uiState.value.isLoading)
        }

    @Test
    fun `clears the loading flag even when the initial load fails`() =
        coroutineRule.runTest {
            val vm = viewModel(result = Result.failure(IllegalStateException("offline")))

            assertEquals(false, vm.uiState.value.isLoading)
        }
}
