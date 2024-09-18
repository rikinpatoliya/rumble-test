package com.rumble.theme

import androidx.compose.ui.graphics.Color
import com.rumble.utils.extension.md5

fun getPlaceholderColor(name: String): Color {
    val placeholderColors =
        listOf(
            fierceRed,
            rumbleGreen,
            royalPurple,
            treePoppy,
            placeholderColor1,
            placeholderColor2,
            placeholderColor3,
            placeholderColor4,
            placeholderColor5,
            placeholderColor6,
        )
    val md5Substring = name.md5().take(15)
    val number = md5Substring.toBigInteger(16)
    val index = number % placeholderColors.size.toBigInteger()
    return placeholderColors[index.toInt()]
}