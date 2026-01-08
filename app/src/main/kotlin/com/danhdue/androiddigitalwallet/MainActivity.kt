/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.androiddigitalwallet.ui.theme.AndroidDigitalWalletTheme
import com.danhdue.authentication.presentation.login.LoginRoute
import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.LocalEntryProviderInstallers
import com.danhdue.framework.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var installers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (navigator.backStack.isEmpty()) {
            navigator.navigateTo(LoginRoute)
        }

        enableEdgeToEdge()
        setContent {
            AndroidDigitalWalletTheme {
                CompositionLocalProvider(LocalEntryProviderInstallers provides installers) {
                    Scaffold { paddingValues ->
                        NavDisplay(
                            backStack = navigator.backStack,
                            modifier = Modifier.padding(paddingValues),
                            onBack = { navigator.popBackStack() },
                            entryProvider = entryProvider { installers.forEach { it() } },
                        )
                    }
                }
            }
        }
    }
}
