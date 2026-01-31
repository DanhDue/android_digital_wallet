/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.data.datasources.remote.AuthenticationRemoteDataSource
import com.danhdue.authentication.data.mappers.toDomain
import com.danhdue.authentication.data.models.LoginRequestDto
import com.danhdue.authentication.domain.entities.Login
import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.framework.network.DataState
import com.danhdue.framework.network.calladapter.toDataState
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
            val request = LoginRequestDto(email = email, password = password)
            return remoteDataSource.login(request).toDataState { it.toDomain() }
        }
    }
