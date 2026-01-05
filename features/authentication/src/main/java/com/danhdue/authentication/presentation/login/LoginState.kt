/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.login

/**
 * Represents the state of the Login screen.
 */
data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoginButtonEnabled: Boolean = false,
)
