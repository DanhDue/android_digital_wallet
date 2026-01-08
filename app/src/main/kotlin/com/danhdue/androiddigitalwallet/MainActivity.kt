/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.danhdue.androiddigitalwallet.ui.theme.AndroidDigitalWalletTheme
import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.LocalEntryProviderInstallers
import com.danhdue.framework.navigation.LoginRoute
import com.danhdue.framework.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var installers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure backstack is not empty before content is set
        if (navigator.backStack.isEmpty()) {
            navigator.navigateTo(LoginRoute)
        }

        enableEdgeToEdge()
        setContent {
            AndroidDigitalWalletTheme {
                CompositionLocalProvider(LocalEntryProviderInstallers provides installers) {
                    // Explicitly handle hardware back press when at the root of the app
                    BackHandler(enabled = navigator.backStack.size <= 1) {
                        handleExit()
                    }

                    Scaffold { paddingValues ->
                        // NavDisplay throws an exception if the backstack is empty.
                        // We guard against this by checking the size.
                        if (navigator.backStack.isNotEmpty()) {
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
