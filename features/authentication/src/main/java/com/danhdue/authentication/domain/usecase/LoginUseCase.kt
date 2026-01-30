package com.danhdue.authentication.domain.usecase

import com.danhdue.authentication.domain.entities.Login
import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.framework.network.DataState
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(email: String, password: String): DataState<Login> {
        return repository.login(email, password)
    }
}
