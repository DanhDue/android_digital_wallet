/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.register

import com.danhdue.authentication.domain.usecase.GetRegisterDataUseCase
import com.danhdue.framework.base.mvi.BaseViewState
import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the business logic and state for the Register feature.
 */
@HiltViewModel
@Suppress("UnusedPrivateProperty")
class RegisterViewModel
    @Inject
    constructor(
        private val getRegisterDataUseCase: GetRegisterDataUseCase,
    ) : MviViewModel<BaseViewState<RegisterState>, RegisterAction, RegisterEvent>() {
        init {
            setState(BaseViewState.Data(RegisterState()))
        }

        override fun onAction(action: RegisterAction) {
            when (action) {
                is RegisterAction.OnFirstNameChanged -> {
                    updateState { copy(firstName = action.value, firstNameError = null) }
                }

                is RegisterAction.OnLastNameChanged -> {
                    updateState { copy(lastName = action.value, lastNameError = null) }
                }

                is RegisterAction.OnEmailChanged -> {
                    updateState { copy(email = action.value, emailError = null) }
                }

                is RegisterAction.OnBirthDateChanged -> {
                    updateState { copy(birthDate = action.value, birthDateError = null) }
                }

                is RegisterAction.OnPhoneNumberChanged -> {
                    updateState { copy(phoneNumber = action.value, phoneNumberError = null) }
                }

                is RegisterAction.OnPasswordChanged -> {
                    updateState { copy(password = action.value, passwordError = null) }
                }

                RegisterAction.OnTogglePasswordVisibility -> {
                    updateState { copy(isPasswordVisible = !isPasswordVisible) }
                }

                RegisterAction.OnRegisterClicked -> {
                    // Implement registration logic
                }

                RegisterAction.OnBackClicked -> {
                    sendEvent(RegisterEvent.NavigateBack)
                }

                RegisterAction.OnLoginClicked -> {
                    sendEvent(RegisterEvent.NavigateToLogin)
                }
            }
        }

        private fun updateState(reducer: RegisterState.() -> RegisterState) {
            val currentState = (uiState.value as? BaseViewState.Data<*>)?.value as? RegisterState ?: RegisterState()
            val newState = currentState.reducer()
            setState(BaseViewState.Data(newState))
        }
    }
