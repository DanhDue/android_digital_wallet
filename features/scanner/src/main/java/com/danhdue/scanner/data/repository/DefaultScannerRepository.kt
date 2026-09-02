/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.data.repository

import com.danhdue.scanner.domain.model.Scanner
import com.danhdue.scanner.domain.repository.ScannerRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Scanner feature.
 */
internal class DefaultScannerRepository @Inject constructor() : ScannerRepository {
    override suspend fun getScannerData(): Result<Scanner> =
        try {
            val domainModel = Scanner(id = "1", data = "Sample data from repository")
            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
