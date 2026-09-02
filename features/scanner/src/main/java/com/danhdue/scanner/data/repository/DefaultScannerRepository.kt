/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.data.repository

import com.danhdue.scanner.domain.model.Scanner
import com.danhdue.scanner.domain.repository.ScannerRepository

/**
 * Concrete implementation of the repository for the Scanner feature.
 *
 * No `@Inject` — `scanner` is an on-demand Dynamic Feature Module (Task 14) with
 * no Hilt graph of its own; [ScannerViewModel] constructs this directly.
 */
internal class DefaultScannerRepository : ScannerRepository {
    override suspend fun getScannerData(): Result<Scanner> =
        try {
            val domainModel = Scanner(id = "1", data = "Sample data from repository")
            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
