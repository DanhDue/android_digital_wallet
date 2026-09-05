/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.androiddigitalwallet.R
import com.danhdue.framework.navigation.Navigator
import com.danhdue.framework.navigation.ObserveBackstackForFlipper
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.LocalEntryProviderInstallers
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.localization.LocalAppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.LocalAppThemeManager
import com.danhdue.uikit.SetLanguage
import com.danhdue.uikit.localization.LocalDynamicStringResolver
import com.danhdue.uikit.permission.RequestPermissionOnMount
import com.danhdue.uikit.ui.theme.AndroidDigitalWalletTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var installers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var sessionManager: com.danhdue.core.session.SessionManager

    @Inject
    lateinit var appEventBus: AppEventBus

    @Inject
    lateinit var appThemeManager: AppThemeManager

    @Inject
    lateinit var appLocalizationManager: AppLocalizationManager

    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Logout signalling is kept as template plumbing even though the template
        // ships no auth flow: the legacy in-process SessionManager channel and the
        // cross-feature AppEventBus signal (Task 11: `:network` publishes
        // AppEvent.UserLoggedOut on an unrecovered 401).
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionManager.logoutEvent.collect {
                    // Template has no auth flow — point this at your project's login route.
                    navigator.navigateAndClearBackStack(AppRoutes.ShellRoute)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appEventBus.on<AppEvent.UserLoggedOut>().collect {
                    // Template has no auth flow — point this at your project's login route.
                    navigator.navigateAndClearBackStack(AppRoutes.ShellRoute)
                }
            }
        }

        // Ensure backstack is not empty before content is set
        if (navigator.backStack.isEmpty()) {
            navigator.navigateTo(AppRoutes.ShellRoute)
        }

        setContent {
            val isDarkMode by appThemeManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
            val currentLanguageCode by appLocalizationManager.currentLanguageCode.collectAsStateWithLifecycle(initialValue = "en")

            SetLanguage(languageCode = currentLanguageCode)

            AndroidDigitalWalletTheme(darkTheme = isDarkMode, dynamicColor = false) {
                // Request notification permission on Android 13+ for Chucker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    RequestPermissionOnMount(
                        permission = Manifest.permission.POST_NOTIFICATIONS,
                    )
                }

                CompositionLocalProvider(
                    LocalEntryProviderInstallers provides installers,
                    LocalAppThemeManager provides appThemeManager,
                    LocalAppLocalizationManager provides appLocalizationManager,
                    LocalDynamicStringResolver provides { key, fallback ->
                        appLocalizationManager.getString(key, fallback)
                    },
                ) {
                    // Explicitly handle hardware back press when at the root of the app
                    BackHandler(enabled = navigator.backStack.size <= 1) {
                        handleExit()
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets(bottom = 0.dp),
                    ) { paddingValues ->
                        // NavDisplay throws an exception if the backstack is empty.
                        // We guard against this by checking the size.
                        if (navigator.backStack.isNotEmpty()) {
                            // Observe root backstack changes and report to Flipper
                            ObserveBackstackForFlipper(backStack = navigator.backStack, prefix = "Root")

                            NavDisplay(
                                backStack = navigator.backStack,
                                modifier = Modifier.padding(paddingValues),
                                onBack = {
                                    if (navigator.backStack.size > 1) {
                                        navigator.popBackStack()
                                    } else {
                                        handleExit()
                                    }
                                },
                                entryProvider = entryProvider { installers.forEach { it() } },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleExit() {
        if (backPressedTime + BACK_PRESS_THRESHOLD > System.currentTimeMillis()) {
            finish()
        } else {
            Toast
                .makeText(
                    this,
                    getString(R.string.press_back_again_to_exit),
                    Toast.LENGTH_SHORT,
                ).show()
            backPressedTime = System.currentTimeMillis()
        }
    }

    companion object {
        private const val BACK_PRESS_THRESHOLD = 2000 // 2 seconds
    }
}
