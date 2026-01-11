/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.components.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF2962FF)
val BlueDark = Color(0xFF0D47A1)
val PurpleHeading = Color(0xFF9C27B0)
val GrayText = Color(0xFF808080)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Transparent = Color(0x00000000)
val Blue = Color(0xFF252941)
val Red = Color(0xFFD13438)
val RedDark = Color(0xFF982626)

val CardDark = Color(0xFF3B3E43)
val CardLight = White

val BackgroundLight = Color(0xFFF5F2F5)
val BackgroundDark = Color(0xFF24292E)

val DividerLight = Color(0xFFE0E0E0)
val DividerDark = Color(0xFF6E6E6E)

val GrayCircle = Color(0xFF919191)
val RedCircle = Color(0xFFD50000)
val GreenCircle = Color(0xFF00C853)
val BorderLine = Color(0xFFE5E5EA)

val Red700 = Color(0xFFD32F2F)

val Gray25 = Color(0xFFF8F8F8)
val Gray50 = Color(0xFFF1F1F1)
val Gray75 = Color(0xFFECECEC)
val Gray100 = Color(0xFFE1E1E1)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFACACAC)
val Gray400 = Color(0xFF919191)
val Gray500 = Color(0xFF6E6E6E)
val Gray600 = Color(0xFF535353)
val Gray700 = Color(0xFF616161)
val Gray800 = Color(0xFF292929)
val Gray900 = Color(0xFF212121)
val Gray950 = Color(0xFF141414)

val LightGray = Color(0xFFD3D3D3)
val Green = Color(0xFF4CAF50)
val RedError = Color(0xFFF44336)

val selectedBottomItemColor = Red
val unselectedBottomItemColor = Gray500

val navigationBackIconDark = White
val navigationBackIconLight = Black

// Digital Wallet Colors
val PrimaryBlue = Color(0xFF007AFF)
val SuccessGreen = Color(0xFF32D583)
val ErrorRed = Color(0xFFF04438)
val NeutralGray = Color(0xFFF2F4F7)
val DarkText = Color(0xFF101828)
val LightText = Color(0xFF667085)

// Home Tab Colors
val HomePrimaryBlue = Color(0xFF1E88E5)
val HomeGrayText = Color(0xFF757575)

// Scanner FAB Colors
val ScannerGradientStart = Color(0xFF42A5F5)
val ScannerGradientEnd = Color(0xFF1976D2)

// Wallet Card Gradient
val WalletCardGradient =
    Brush.linearGradient(
        colors =
            listOf(
                Color(0xFF2E90FA),
                Color(0xFFF670C7),
            ),
    )

val ScannerFabGradient =
    Brush.verticalGradient(
        colors = listOf(ScannerGradientStart, ScannerGradientEnd),
    )
