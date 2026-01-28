/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.login

import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages the business logic and state for the Login feature.
 */
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
//        private val getLoginDataUseCase: GetLoginDataUseCase,
    ) : MviViewModel<LoginState, LoginAction, LoginEvent>(
        initialState = LoginState(),
    ) {
        init {
            Timber.d("LoginViewModel init")
        }

        override fun onAction(action: LoginAction) {
            when (action) {
                is LoginAction.OnEmailChanged -> {
                    reduce {
                        copy(
                            email = action.email,
                            emailError = null,
                            isLoginButtonEnabled =
                                action.email.isNotEmpty() && password.isNotEmpty(),
                        )
                    }
                }

                is LoginAction.OnPasswordChanged -> {
                    reduce {
                        copy(
                            password = action.password,
                            passwordError = null,
                            isLoginButtonEnabled =
                                email.isNotEmpty() && action.password.isNotEmpty(),
                        )
                    }
                }

                LoginAction.OnTogglePasswordVisibility -> {
                    reduce { copy(isPasswordVisible = !isPasswordVisible) }
                }

                LoginAction.OnLoginClicked -> performLogin()

                LoginAction.OnBackClicked -> {
                    sendEvent(LoginEvent.NavigateBack)
                }

                LoginAction.OnRegisterClicked -> {
                    sendEvent(LoginEvent.NavigateToRegister)
                }

                LoginAction.OnForgotPasswordClicked -> {
                    // Handle forgot password navigation
                }
            }
        }

        private fun performLogin() {
            safeLaunch {
                reduce { copy(isLoading = true) }
                // Simulate network call
                kotlinx.coroutines.delay(1000)
                reduce { copy(isLoading = false) }
                sendEvent(LoginEvent.NavigateToHome)
            }
        }
    }
