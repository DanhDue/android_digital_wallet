/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.registration

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
