/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.repository

import com.danhdue.authentication.domain.entities.Register
import com.danhdue.core.network.DataState

/**
 * Interface defining the contract for the Register feature's repository.
 */
interface RegisterRepository {
    /**
     * Retrieves data for the Register feature.
     *
     * @return A DataState object containing the Register domain model on success,
     * or an exception on failure.
     */
    suspend fun getRegisterData(): DataState<Register>
}
