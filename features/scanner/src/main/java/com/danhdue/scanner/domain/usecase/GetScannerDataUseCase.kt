/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.domain.usecase

import com.danhdue.scanner.domain.model.Scanner
import com.danhdue.scanner.domain.repository.ScannerRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Scanner feature data.
 */
class GetScannerDataUseCase @Inject constructor(
    private val repository: ScannerRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<Scanner> = repository.getScannerData()
}
