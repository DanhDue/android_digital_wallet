/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.domain.repository

import com.danhdue.authentication.register.domain.model.Register

/**
 * Interface defining the contract for the Register feature's repository.
 */
interface RegisterRepository {
    /**
     * Retrieves data for the Register feature.
     *
     * @return A Result object containing the Register domain model on success,
     * or an exception on failure.
     */
    suspend fun getRegisterData(): Result<Register>
}
