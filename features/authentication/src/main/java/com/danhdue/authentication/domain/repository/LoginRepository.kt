/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.repository

import com.danhdue.authentication.domain.model.Login

/**
 * Interface defining the contract for the Login feature's repository.
 */
interface LoginRepository {
    /**
     * Retrieves data for the Login feature.
     *
     * @return A Result object containing the Login domain model on success,
     * or an exception on failure.
     */
    suspend fun getLoginData(): Result<Login>
}
