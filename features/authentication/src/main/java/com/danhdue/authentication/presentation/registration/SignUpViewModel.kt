/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.registration

import com.danhdue.authentication.domain.usecase.GetRegisterDataUseCase
import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages the business logic and state for the Registration feature.
 */
@HiltViewModel
@Suppress("UnusedPrivateProperty")
class SignUpViewModel
    @Inject
    constructor(
        private val getRegisterDataUseCase: GetRegisterDataUseCase,
    ) : MviViewModel<RegisterState, RegisterAction, RegisterEvent>(
        initialState = RegisterState(),
    ) {
        init {
            Timber.d("SignUpViewModel init")
        }

        override fun onAction(action: RegisterAction) {
            when (action) {
                is RegisterAction.OnFirstNameChanged -> {
                    reduce { copy(firstName = action.value) }
                }

                is RegisterAction.OnLastNameChanged -> {
                    reduce { copy(lastName = action.value) }
                }

                is RegisterAction.OnEmailChanged -> {
                    reduce { copy(email = action.value) }
                }

                is RegisterAction.OnBirthDateChanged -> {
                    reduce { copy(birthDate = action.value) }
                }

                is RegisterAction.OnPhoneNumberChanged -> {
                    reduce { copy(phoneNumber = action.value) }
                }

                is RegisterAction.OnPasswordChanged -> {
                    reduce { copy(password = action.value) }
                }

                RegisterAction.OnTogglePasswordVisibility -> {
                    reduce { copy(isPasswordVisible = !isPasswordVisible) }
                }

                RegisterAction.OnRegisterClicked -> performRegistration()

                RegisterAction.OnBackClicked -> {
                    sendEvent(RegisterEvent.NavigateBack)
                }

                RegisterAction.OnLoginClicked -> {
                    sendEvent(RegisterEvent.NavigateToLogin)
                }
            }
        }

        private fun performRegistration() {
            safeLaunch {
                reduce { copy(isLoading = true) }
                // Simulate network call
                kotlinx.coroutines.delay(1000)
                reduce { copy(isLoading = false) }
                sendEvent(RegisterEvent.NavigateToLogin)
            }
        }
    }
