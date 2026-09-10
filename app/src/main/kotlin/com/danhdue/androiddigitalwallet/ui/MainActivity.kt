/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.ui

import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
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
import com.danhdue.platform.deeplink.DeepLinkRouter
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.localization.LocalAppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.LocalAppThemeManager
import com.danhdue.uikit.localization.LocalDynamicStringResolver
import com.danhdue.uikit.ui.theme.AndroidDigitalWalletTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
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

    @Inject
    lateinit var deepLinkRouter: DeepLinkRouter

    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeLogoutSignals()

        // Ensure backstack is not empty before content is set
        if (navigator.backStack.isEmpty()) {
            navigator.navigateTo(AppRoutes.ShellRoute)
        }

        handleDeepLink(intent)

        setContent {
            MainAppContent()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    @Composable
    private fun MainAppContent() {
        val isDarkMode by appThemeManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
        val currentLanguageCode by appLocalizationManager.currentLanguageCode.collectAsStateWithLifecycle(
            initialValue = appLocalizationManager.currentLanguageCode.value,
        )
        val translationsVersion by appLocalizationManager.translationsVersion.collectAsStateWithLifecycle(initialValue = 0)

        LaunchedEffect(currentLanguageCode) {
            val localeTag = currentLanguageCode.replace('_', '-')
            val appLocales = LocaleListCompat.forLanguageTags(localeTag)
            if (AppCompatDelegate.getApplicationLocales() != appLocales) {
                AppCompatDelegate.setApplicationLocales(appLocales)
            }
        }

        val locale = Locale.forLanguageTag(currentLanguageCode.replace('_', '-'))
        val baseConfiguration = LocalConfiguration.current
        val configuration =
            remember(currentLanguageCode, baseConfiguration) {
                Configuration(baseConfiguration).apply {
                    setLocale(locale)
                    setLayoutDirection(locale)
                }
            }
        val localizedContext =
            remember(currentLanguageCode, configuration) {
                LocalizedActivityContext(this@MainActivity, configuration)
            }

        val stringResolver =
            remember(currentLanguageCode, translationsVersion) {
                appLocalizationManager::getString
            }

        CompositionLocalProvider(
            LocalConfiguration provides configuration,
            LocalContext provides localizedContext,
            LocalActivityResultRegistryOwner provides this@MainActivity,
            LocalEntryProviderInstallers provides installers,
            LocalAppThemeManager provides appThemeManager,
            LocalAppLocalizationManager provides appLocalizationManager,
            LocalDynamicStringResolver provides stringResolver,
        ) {
            AndroidDigitalWalletTheme(darkTheme = isDarkMode, dynamicColor = false) {
                BackHandler(enabled = navigator.backStack.size <= 1) {
                    handleExit()
                }
                RootNavigationScaffold()
            }
        }
    }

    @Composable
    private fun RootNavigationScaffold() {
        Scaffold(
            contentWindowInsets = WindowInsets(bottom = 0.dp),
        ) { paddingValues ->
            if (navigator.backStack.isNotEmpty()) {
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

    private fun observeLogoutSignals() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionManager.logoutEvent.collect {
                    navigator.navigateAndClearBackStack(AppRoutes.ShellRoute)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appEventBus.on<AppEvent.UserLoggedOut>().collect {
                    navigator.navigateAndClearBackStack(AppRoutes.ShellRoute)
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

    private fun handleDeepLink(intent: Intent?) {
        if (intent == null) return
        if (intent.getBooleanExtra(EXTRA_CONSUMED, false)) return

        val uri = intent.dataString ?: return
        intent.putExtra(EXTRA_CONSUMED, true)
        deepLinkRouter.dispatch(uri)
    }

    companion object {
        private const val BACK_PRESS_THRESHOLD = 2000 // 2 seconds
        internal const val EXTRA_CONSUMED = "com.danhdue.androiddigitalwallet.deeplink.EXTRA_CONSUMED"
    }
}

private class LocalizedActivityContext(
    private val activity: ComponentActivity,
    private val configuration: Configuration,
) : ContextWrapper(activity),
    ActivityResultRegistryOwner {
    private val localizedResources: Resources by lazy {
        activity.createConfigurationContext(configuration).resources
    }

    override val activityResultRegistry: ActivityResultRegistry
        get() = activity.activityResultRegistry

    override fun getResources(): Resources = localizedResources
}
