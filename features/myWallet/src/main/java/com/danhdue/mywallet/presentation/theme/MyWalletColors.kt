/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object MyWalletColors {
    val PrimaryBlue = Color(0xFF007AFF)
    val SuccessGreen = Color(0xFF32D583)
    val ErrorRed = Color(0xFFF04438)
    val NeutralGray = Color(0xFFF2F4F7)
    val DarkText = Color(0xFF101828)
    val LightText = Color(0xFF667085)

    val WalletCardGradient =
        Brush.linearGradient(
            colors =
                listOf(
                    Color(0xFF2E90FA),
                    Color(0xFFF670C7),
                ),
        )
}
