/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.repository

import com.danhdue.authentication.domain.entities.Login
import com.danhdue.framework.network.DataState

/** Interface defining the contract for the Login feature's repository. */
interface LoginRepository {
    /**
     * Retrieves data for the Login feature.
     *
     * @return A DataState object containing the Login domain model on success, or an exception on
     * failure.
     */
    suspend fun login(email: String, password: String): DataState<Login>
}
