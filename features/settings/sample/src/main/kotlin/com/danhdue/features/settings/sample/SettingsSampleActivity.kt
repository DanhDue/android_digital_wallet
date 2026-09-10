/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.features.settings.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.framework.navigation.Navigator
import com.danhdue.framework.navigation.NestedNavigator
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.LocalEntryProviderInstallers
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.localization.LocalAppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.LocalAppThemeManager
import com.danhdue.settings.presentation.profile.ProfileRoute
import com.danhdue.uikit.localization.LocalDynamicStringResolver
import com.danhdue.uikit.ui.theme.AndroidDigitalWalletTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsSampleActivity : ComponentActivity() {
    @Inject
    lateinit var installers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var appThemeManager: AppThemeManager

    @Inject
    lateinit var appLocalizationManager: AppLocalizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SampleContent()
        }
    }

    @Composable
    private fun SampleContent() {
        val isDarkMode by appThemeManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
        val currentLanguageCode by appLocalizationManager.currentLanguageCode.collectAsStateWithLifecycle(
            initialValue = appLocalizationManager.currentLanguageCode.value,
        )
        val translationsVersion by appLocalizationManager.translationsVersion.collectAsStateWithLifecycle(initialValue = 0)

        val stringResolver =
            remember(currentLanguageCode, translationsVersion) {
                appLocalizationManager::getString
            }

        val navigator = remember { Navigator(AppRoutes.SettingsRoute) }
        val context = LocalContext.current
        val nestedNavigator =
            remember(navigator, context) {
                object : NestedNavigator {
                    override fun navigate(destination: Any) {
                        if (destination is ProfileRoute) {
                            navigator.navigateTo(destination)
                        } else {
                            val msg = "Sample Sandbox: Out-of-module navigation to ${destination::class.simpleName}"
                            Timber.w(msg)
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun popBackStack() {
                        if (navigator.backStack.size > 1) {
                            navigator.popBackStack()
                        } else {
                            finish()
                        }
                    }
                }
            }

        CompositionLocalProvider(
            LocalEntryProviderInstallers provides installers,
            LocalAppThemeManager provides appThemeManager,
            LocalAppLocalizationManager provides appLocalizationManager,
            LocalDynamicStringResolver provides stringResolver,
            LocalNestedNavigator provides nestedNavigator,
        ) {
            AndroidDigitalWalletTheme(darkTheme = isDarkMode, dynamicColor = false) {
                BackHandler(enabled = true) {
                    if (navigator.backStack.size > 1) {
                        navigator.popBackStack()
                    } else {
                        finish()
                    }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets(bottom = 0.dp),
                ) { paddingValues ->
                    NavDisplay(
                        backStack = navigator.backStack,
                        modifier = Modifier.padding(paddingValues),
                        onBack = {
                            if (navigator.backStack.size > 1) {
                                navigator.popBackStack()
                            } else {
                                finish()
                            }
                        },
                        entryProvider = entryProvider { installers.forEach { it() } },
                    )
                }
            }
        }
    }
}
