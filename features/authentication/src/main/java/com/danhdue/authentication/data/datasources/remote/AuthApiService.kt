/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.datasources.remote

import com.danhdue.authentication.data.models.LoginRequestDto
import com.danhdue.authentication.data.models.LoginResponseDto
import com.danhdue.authentication.data.models.RefreshTokenRequestDto
import com.danhdue.authentication.data.models.RefreshTokenResponseDto
import com.danhdue.core.network.calladapter.NetworkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto,
    ): NetworkResponse<LoginResponseDto>

    @POST("users/register")
    suspend fun register(
        @Body registerRequest: Map<String, String>,
    ): Response<Unit>

    @POST("users/refresh")
    suspend fun refresh(
        @Body refreshTokenRequest: RefreshTokenRequestDto,
    ): NetworkResponse<RefreshTokenResponseDto>
}
