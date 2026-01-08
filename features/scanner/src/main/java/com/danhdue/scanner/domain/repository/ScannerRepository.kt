/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.domain.repository

import com.danhdue.scanner.domain.model.Scanner

/**
 * Interface defining the contract for the Scanner feature's repository.
 */
interface ScannerRepository {
    /**
     * Retrieves data for the Scanner feature.
     *
     * @return A Result object containing the Scanner domain model on success,
     * or an exception on failure.
     */
    suspend fun getScannerData(): Result<Scanner>
}
