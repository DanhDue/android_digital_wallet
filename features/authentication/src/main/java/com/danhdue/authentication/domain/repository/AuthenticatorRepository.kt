/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.repository

import com.danhdue.authentication.domain.model.Authenticator

/**
 * Interface defining the contract for the Authenticator feature's repository.
 */
interface AuthenticatorRepository {

    /**
     * Retrieves data for the Authenticator feature.
     *
     * @return A Result object containing the Authenticator domain model on success,
     * or an exception on failure.
     */
    suspend fun getAuthenticatorData(): Result<Authenticator>
}