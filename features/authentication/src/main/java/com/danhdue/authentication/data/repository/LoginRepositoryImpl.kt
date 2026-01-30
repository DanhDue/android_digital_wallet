/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.data.datasources.remote.AuthenticationRemoteDataSource
import com.danhdue.authentication.data.mappers.toDomain
import com.danhdue.authentication.domain.entities.Login
import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.framework.network.DataState
import javax.inject.Inject

/** Concrete implementation of the repository for the Login feature. */
class LoginRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: AuthenticationRemoteDataSource,
    ) : LoginRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): DataState<Login> {
            val request =
                com.danhdue.authentication.data.models.LoginRequestDto(
                    email = email,
                    password = password,
                )
            return when (val response = remoteDataSource.login(request)) {
                is com.danhdue.framework.network.calladapter.NetworkResponse.Success -> {
                    com
                        .danhdue
                        .framework
                        .network
                        .DataState
                        .Success(response.body.toDomain())
                }
                is com.danhdue.framework.network.calladapter.NetworkResponse.ApiError -> {
                    com
                        .danhdue
                        .framework
                        .network
                        .DataState
                        .Error(Exception(response.body.toString()))
                }
                is com.danhdue.framework.network.calladapter.NetworkResponse.NetworkError -> {
                    com
                        .danhdue
                        .framework
                        .network
                        .DataState
                        .Error(response.error)
                }
                is com.danhdue.framework.network.calladapter.NetworkResponse.UnknownError -> {
                    com.danhdue.framework.network.DataState.Error(
                        response.error ?: Exception("Unknown error"),
                    )
                }
            }
        }
    }
