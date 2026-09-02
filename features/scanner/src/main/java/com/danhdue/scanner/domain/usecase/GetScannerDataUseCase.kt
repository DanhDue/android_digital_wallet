/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.domain.usecase

import com.danhdue.scanner.domain.model.Scanner
import com.danhdue.scanner.domain.repository.ScannerRepository

/**
 * Use case that encapsulates the business logic for fetching the Scanner feature data.
 *
 * No `@Inject` — `scanner` is an on-demand Dynamic Feature Module (Task 14)
 * without a Hilt graph; it is constructed directly by [ScannerViewModel].
 */
class GetScannerDataUseCase(
    private val repository: ScannerRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<Scanner> = repository.getScannerData()
}
