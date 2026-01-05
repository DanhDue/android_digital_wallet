/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.register

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Register feature.
 */
sealed interface RegisterAction {
    data class OnFirstNameChanged(
        val value: String,
    ) : RegisterAction

    data class OnLastNameChanged(
        val value: String,
    ) : RegisterAction

    data class OnEmailChanged(
        val value: String,
    ) : RegisterAction

    data class OnBirthDateChanged(
        val value: String,
    ) : RegisterAction

    data class OnPhoneNumberChanged(
        val value: String,
    ) : RegisterAction

    data class OnPasswordChanged(
        val value: String,
    ) : RegisterAction

    data object OnTogglePasswordVisibility : RegisterAction

    data object OnRegisterClicked : RegisterAction

    data object OnBackClicked : RegisterAction

    data object OnLoginClicked : RegisterAction
}
