/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.datasources.remote

import com.danhdue.authentication.data.models.LoginRequestDto
import com.danhdue.authentication.data.models.LoginResponseDto
import com.danhdue.framework.network.calladapter.NetworkResponse
import javax.inject.Inject

class AuthenticationRemoteDataSource @Inject constructor(
    private val authApiService: AuthApiService,
) {
    suspend fun login(loginRequest: LoginRequestDto): NetworkResponse<LoginResponseDto> = authApiService.login(loginRequest)
}
