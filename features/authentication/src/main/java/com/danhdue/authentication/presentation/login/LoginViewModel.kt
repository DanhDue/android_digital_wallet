/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.login

import com.danhdue.authentication.domain.usecase.GetLoginDataUseCase
import com.danhdue.framework.base.mvi.BaseViewState
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
        private val getLoginDataUseCase: GetLoginDataUseCase,
    ) : MviViewModel<BaseViewState<LoginState>, LoginAction, LoginEvent>() {
        init {
            Timber.d("LoginViewModel init")
            setState(BaseViewState.Data(LoginState()))
        }

        override fun onAction(action: LoginAction) {
            when (action) {
                is LoginAction.OnEmailChanged -> {
                    updateState {
                        copy(
                            email = action.email,
                            emailError = null,
                            isLoginButtonEnabled =
                                action.email.isNotEmpty() && password.isNotEmpty(),
                        )
                    }
                }

                is LoginAction.OnPasswordChanged -> {
                    updateState {
                        copy(
                            password = action.password,
                            passwordError = null,
                            isLoginButtonEnabled =
                                email.isNotEmpty() && action.password.isNotEmpty(),
                        )
                    }
                }

                LoginAction.OnTogglePasswordVisibility -> {
                    updateState { copy(isPasswordVisible = !isPasswordVisible) }
                }

                LoginAction.OnLoginClicked -> {
                    // TODO: Trigger login use case
                    startLoading()
                }

                LoginAction.OnBackClicked -> {
                    sendEvent(LoginEvent.NavigateBack)
                }

                LoginAction.OnRegisterClicked -> {
                    sendEvent(LoginEvent.NavigateToRegister)
                }

                LoginAction.OnForgotPasswordClicked -> {
                    // TODO: Handle forgot password navigation
                }
            }
        }

        private fun updateState(reducer: LoginState.() -> LoginState) {
            val currentState = (uiState.value as? BaseViewState.Data<*>)?.value as? LoginState ?: LoginState()
            val newState = currentState.reducer()
            setState(BaseViewState.Data(newState))
        }
    }
