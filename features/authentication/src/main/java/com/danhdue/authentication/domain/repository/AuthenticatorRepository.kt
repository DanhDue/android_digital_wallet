/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.repository

import com.danhdue.authentication.domain.model.Authenticator
import com.danhdue.framework.network.DataState

/**
 * Interface defining the contract for the Authenticator feature's repository.
 */
interface AuthenticatorRepository {
    /**
     * Retrieves data for the Authenticator feature.
     *
     * @return A DataState object containing the Authenticator domain model on success,
     * or an exception on failure.
     */
    suspend fun getAuthenticatorData(): DataState<Authenticator>
}
