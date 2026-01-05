/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.framework.base.mvi.BaseViewState
import com.danhdue.framework.extension.cast
import com.danhdue.authentication.R


/**
 * Composable entry point for the Register feature.
 */
@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = hiltViewModel(),
    onEvent: (RegisterEvent) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            onEvent(event)
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * A stateless composable that handles the UI state for the Register feature.
 */
@Composable
private fun RegisterScreen(
    state: BaseViewState<*>,
    onAction: (RegisterAction) -> Unit,
) {
    when (state) {
        is BaseViewState.Data<*> -> {
            val data = state.cast<BaseViewState.Data<RegisterState>>().value
            RegisterContent(
                state = data,
                onAction = onAction,
            )
        }

        is BaseViewState.Empty -> {
            Spacer(modifier = Modifier.height(0.dp))
        }

        is BaseViewState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BaseViewState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Feature: Register is fail")
            }
        }
    }
}

/**
 * A stateless composable that draws the UI for the Register feature.
 */
@Composable
private fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop, // Scales the image to fill the bounds
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logoipsum",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Card Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    IconButton(
                        onClick = { onAction(RegisterAction.OnBackClicked) },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = stringResource(R.string.sign_up),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.already_have_an_account),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = stringResource(R.string.login),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2962FF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onAction(RegisterAction.OnLoginClicked) }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Form Fields
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.first_name), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            OutlinedTextField(
                                value = state.firstName,
                                onValueChange = { onAction(RegisterAction.OnFirstNameChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.last_name), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            OutlinedTextField(
                                value = state.lastName,
                                onValueChange = { onAction(RegisterAction.OnLastNameChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.email), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onAction(RegisterAction.OnEmailChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.birth_of_date), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = state.birthDate,
                        onValueChange = { onAction(RegisterAction.OnBirthDateChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.phone_number), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = state.phoneNumber,
                        onValueChange = { onAction(RegisterAction.OnPhoneNumberChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        prefix = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🇬🇧")
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                VerticalDivider(modifier = Modifier.height(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.set_password), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onAction(RegisterAction.OnPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { onAction(RegisterAction.OnTogglePasswordVisibility) }) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { onAction(RegisterAction.OnRegisterClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
                    ) {
                        Text(text = stringResource(R.string.register), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewRegisterScreen() {
    RegisterContent(
        state = RegisterState(isLoading = false),
        onAction = {},
    )
}
