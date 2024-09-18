package com.rumble.theme

import androidx.compose.ui.graphics.Color
import com.rumble.theme.RumbleCustomTheme.isLightMode


object RumbleCustomTheme {

    var isLightMode: Boolean = true

    val colors = RumbleColors()
}

class RumbleColors {
    private val gray25 = Color(0xFFF6F7F9)
    private val gray50 = Color(0xFFE4E8EC)
    private val gray100 = Color(0xFFCBD4DC)
    private val gray200 = Color(0xFFB2C0CB)
    private val gray400 = Color(0xFF8397AA)
    private val gray700 = Color(0xFF485B69)
    private val gray900 = Color(0xFF283139)
    private val gray950 = Color(0xFF1B2127)

    private val surfaceDark = Color.Black
    private val surfaceLight = Color.White
    private val backgroundDark = Color.Black
    private val backgroundLight = Color.White
    private val backgroundHighlightDark = gray900
    private val backgroundHighlightLight = gray50
    private val primaryDark = Color.White
    private val primaryLight = Color.Black
    private val secondaryDark = gray25
    private val secondaryLight = gray700
    private val onSecondaryDark = gray700
    private val onSecondaryLight = gray100
    private val subtleHighlightDark = gray950
    private val subtleHighlightLight = gray25

    val surface: Color
        get() = if (isLightMode) surfaceLight else surfaceDark

    val background: Color
        get() = if (isLightMode) backgroundLight else backgroundDark

    val backgroundHighlight: Color
        get() =  if (isLightMode) backgroundHighlightLight else backgroundHighlightDark

    val primary: Color
        get() =  if (isLightMode) primaryLight else primaryDark

    val secondary: Color
        get() =  if (isLightMode) secondaryLight else secondaryDark

    val onSecondary: Color
        get() =  if (isLightMode) onSecondaryLight else onSecondaryDark

    val subtleHighlight: Color
        get() = if (isLightMode) subtleHighlightLight else subtleHighlightDark
}

