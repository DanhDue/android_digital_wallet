/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.registration

import androidx.lifecycle.viewModelScope
import com.danhdue.authentication.domain.usecase.GetRegisterDataUseCase
import com.danhdue.framework.base.mvi.BaseViewState
import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    ) : MviViewModel<BaseViewState<RegisterState>, RegisterAction, RegisterEvent>() {
        init {
            Timber.d("SignUpViewModel init")
            setState(BaseViewState.Data(RegisterState()))
        }

        override fun onAction(action: RegisterAction) {
            when (action) {
                is RegisterAction.OnFirstNameChanged -> {
                    updateState { copy(firstName = action.value) }
                }

                is RegisterAction.OnLastNameChanged -> {
                    updateState { copy(lastName = action.value) }
                }

                is RegisterAction.OnEmailChanged -> {
                    updateState { copy(email = action.value) }
                }

                is RegisterAction.OnBirthDateChanged -> {
                    updateState { copy(birthDate = action.value) }
                }

                is RegisterAction.OnPhoneNumberChanged -> {
                    updateState { copy(phoneNumber = action.value) }
                }

                is RegisterAction.OnPasswordChanged -> {
                    updateState { copy(password = action.value) }
                }

                RegisterAction.OnTogglePasswordVisibility -> {
                    updateState { copy(isPasswordVisible = !isPasswordVisible) }
                }

                RegisterAction.OnRegisterClicked -> {
                    viewModelScope.launch {
                        startLoading()
                        // Simulate network call
                        delay(1000)
                        stopLoading()
                        sendEvent(RegisterEvent.NavigateToLogin)
                    }
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

        override fun startLoading() {
            updateState { copy(isLoading = true) }
        }

        private fun stopLoading() {
            updateState { copy(isLoading = false) }
        }
    }
