/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danhdue.settings.R
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.presentation.components.LanguagePickerBottomSheet
import com.danhdue.settings.presentation.components.LoadingDialog
import com.danhdue.settings.presentation.components.SettingsItemRow
import com.danhdue.settings.presentation.components.SettingsSectionCard
import com.danhdue.settings.presentation.components.TrailingWidget
import com.danhdue.uikit.localization.appStringResource
import com.danhdue.uikit.ui.theme.AndroidDigitalWalletTheme
import com.danhdue.uikit.ui.theme.Red

/**
 * Composable entry point for the Settings feature.
 */
@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel = hiltViewModel(),
    onEvent: (SettingsEvent) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    SettingsScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

/**
 * A stateless composable that draws the UI for the Settings feature.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appStringResource(R.string.settings_title, "settings.title"),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Section 1: Account
            SettingsSectionCard(
                title = appStringResource(R.string.settings_section_account, "settings.account.title"),
            ) {
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_edit_profile, "settings.account.profile"),
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF2962FF),
                    onClick = { onAction(SettingsAction.OpenProfile) },
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_change_password, "settings.account.changePassword"),
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFFFF9800),
                    onClick = { onAction(SettingsAction.OpenSecurity) },
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_2fa, "settings.account.twoFactorAuth"),
                    icon = Icons.Default.Shield,
                    iconColor = Color(0xFF00C853),
                    showDivider = false,
                    onClick = { onAction(SettingsAction.OpenSecurity) },
                )
            }

            // Section 2: Preferences
            SettingsSectionCard(
                title = appStringResource(R.string.settings_section_preferences, "settings.preferences.title"),
            ) {
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_dark_mode, "settings.preferences.darkMode"),
                    icon = Icons.Default.DarkMode,
                    iconColor = Color(0xFF9C27B0),
                    trailingWidget =
                        TrailingWidget.SwitchToggle(
                            isChecked = state.isDarkMode,
                            onCheckedChange = { onAction(SettingsAction.ToggleDarkMode(it)) },
                        ),
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_language, "settings.preferences.language"),
                    icon = Icons.Default.Language,
                    iconColor = Color(0xFF00BCD4),
                    trailingWidget = TrailingWidget.ValueWithChevron(state.selectedLanguageName),
                    onClick = { onAction(SettingsAction.OpenLanguagePicker) },
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_currency, "settings.preferences.currency"),
                    icon = Icons.Default.AttachMoney,
                    iconColor = Color(0xFF4CAF50),
                    trailingWidget =
                        TrailingWidget.Label(
                            appStringResource(R.string.settings_currency_usd, "settings.preferences.currencyUsd"),
                        ),
                    showDivider = false,
                )
            }

            // Section 3: Developer
            SettingsSectionCard(
                title = appStringResource(R.string.settings_section_developer, "settings.developer.title"),
            ) {
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_developer_options, "settings.developer.debugMode"),
                    icon = Icons.Default.Code,
                    iconColor = Color(0xFF795548),
                    showDivider = false,
                    onClick = { onAction(SettingsAction.OpenDeveloperOptions) },
                )
            }

            // Section 4: App Info
            SettingsSectionCard(
                title = appStringResource(R.string.settings_section_app_info, "settings.appInfo.title"),
            ) {
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_privacy_policy, "settings.appInfo.privacyPolicy"),
                    icon = Icons.Default.Policy,
                    iconColor = Color(0xFF607D8B),
                    onClick = { /* Info link */ },
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_terms_of_service, "settings.appInfo.termsOfService"),
                    icon = Icons.Default.Description,
                    iconColor = Color(0xFF3F51B5),
                    onClick = { /* Info link */ },
                )
                SettingsItemRow(
                    title = appStringResource(R.string.settings_item_about, "settings.appInfo.aboutApp"),
                    icon = Icons.Default.Info,
                    iconColor = Color(0xFFE91E63),
                    showDivider = false,
                    onClick = { /* Info link */ },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Standalone Red Logout Button
            OutlinedButton(
                onClick = { onAction(SettingsAction.Logout) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Red.copy(alpha = 0.6f)),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = Red,
                        containerColor = Red.copy(alpha = 0.05f),
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Red,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appStringResource(R.string.settings_button_logout, "settings.logout"),
                    style = MaterialTheme.typography.titleMedium,
                    color = Red,
                )
            }

            Spacer(modifier = Modifier.height(56.dp))
        }
    }

    if (state.isLanguagePickerVisible) {
        LanguagePickerBottomSheet(
            availableLanguages = state.availableLanguages,
            selectedLanguageCode = state.selectedLanguageCode,
            onDismiss = { onAction(SettingsAction.DismissLanguagePicker) },
            onSelectLanguage = { onAction(SettingsAction.SelectLanguage(it)) },
        )
    }

    if (state.isLoadingLanguage) {
        LoadingDialog()
    }
}

private val PREVIEW_LANGUAGES =
    listOf(
        SupportedLanguage(code = "en", name = "English", version = "1.0.0", isDefault = true),
        SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
    )

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreenLight() {
    AndroidDigitalWalletTheme(darkTheme = false) {
        SettingsScreen(
            state =
                SettingsState(
                    isLoading = false,
                    isDarkMode = false,
                    selectedLanguageCode = "en",
                    selectedLanguageName = "English",
                    availableLanguages = PREVIEW_LANGUAGES,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreenDark() {
    AndroidDigitalWalletTheme(darkTheme = true) {
        SettingsScreen(
            state =
                SettingsState(
                    isLoading = false,
                    isDarkMode = true,
                    selectedLanguageCode = "vi",
                    selectedLanguageName = "Tiếng Việt",
                    availableLanguages = PREVIEW_LANGUAGES,
                ),
            onAction = {},
        )
    }
}
