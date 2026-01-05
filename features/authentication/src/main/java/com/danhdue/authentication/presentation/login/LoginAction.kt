/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.login

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the Login feature.
 */
sealed interface LoginAction {
    data class OnEmailChanged(
        val email: String,
    ) : LoginAction

    data class OnPasswordChanged(
        val password: String,
    ) : LoginAction

    data object OnTogglePasswordVisibility : LoginAction

    data object OnLoginClicked : LoginAction

    data object OnRegisterClicked : LoginAction

    data object OnForgotPasswordClicked : LoginAction

    data object OnBackClicked : LoginAction
}
