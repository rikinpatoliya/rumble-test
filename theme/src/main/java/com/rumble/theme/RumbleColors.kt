package com.rumble.theme

import android.annotation.SuppressLint
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color


// Rich colors
val rumbleGreen = Color(0xFF85C742)
val wokeGreen = Color(0xFF4C8800)
val royalPurple = Color(0xFF6E5CE0)
val fierceRed = Color(0xFFF23160)
val darkFierceRed = Color(0xFFE02D59)
val treePoppy = Color(0xFFFF8C26)
val localsPurple = Color(0xFF421C52)
val newPurple = Color(0xFF7338F0)
val brandedLocalsRed = Color(0xFFE73348)
val brandedLocalsLogoBackground = Color(0xFFFFF7F7)
val brandedPlayerBackground = Color(0xFF020D16)
val brandedBufferLight = Color(0xFFE5E5E5).copy(alpha = 0.5f)
val brandedBufferDark = Color(0xFF303030).copy(alpha = 0.5f)
val highlightRed = Color(0xFFE32B3D)
val darkGreen = Color(0xFF486E21)

//Intentionally made private in order to enforce usage of MaterialTheme.colors.....
//In case design requires theme-independent greyscale colors, then use colors from Enforced colors
// Greyscale colors
private val black = Color(0xFF000000)
private val darkest = Color(0xFF000312)
private val darkmo = Color(0xFF061726)
private val fiord = Color(0xFF495A6A)
private val fiordHighlight = Color(0xFF2C3640)
private val cloud = Color(0xFF88A0B8)
private val bone = Color(0xFFD6E0EA)
private val boneHighlight = Color(0xFFE6ECF2)
private val lite = Color(0xFFF3F5F8)
private val white = Color(0xFFFFFFFF)
private val gray950 = Color(0xFF1B2127)
private val gray900 = Color(0xFF283139)
private val gray100 = Color(0xFFCCD4DC)

// Enforced colors - use only if sure about theme-independent color
val enforcedBlack = black
val enforcedDarkest = darkest
val enforcedDarkmo = darkmo
val enforcedFiord = fiord
val enforcedFiardHighlight = fiordHighlight
val enforcedCloud = cloud
val enforcedBone = bone
val enforcedLite = lite
val enforcedWhite = white
val enforcedGray950 = gray950
val enforcedGray900 = gray900
val enforcedGray100 = gray100

// Placeholders colors
val placeholderColor1 = Color(0xFF42A7C7)
val placeholderColor2 = Color(0xFF089B3A)
val placeholderColor3 = Color(0xFF9B087B)
val placeholderColor4 = Color(0xFF08179B)
val placeholderColor5 = Color(0xFFF0B10E)
val placeholderColor6 = Color(0xFFE40EA8)

/*
* The table below describes a set of colors aliases with their respective values for dark and light modes.
*
| Name                                      | Value in light mode   | Value in dark mode
| ---                                       | ---                   | ---
| background                                | white                 | darkest
| surface                                   | white                 | darkest
| onSurface                                 | boneHighlight         | fiordHighlight
| primary                                   | darkest               | white
| secondary                                 | fiord                 | lite
| primaryVariant                            | cloud                 | bone
| onPrimary                                 | white                 | darkest
| onSecondary                               | bone                  | fiord
| secondaryVariant                          | bone                  | cloud
*
*
* Use colors from the color scheme table instead of greyscale colors from the designs to support dark mode.
* Only greyscale colors will differ between the light and dark modes. Rich and placeholders colors will stay the same
*
* */

@SuppressLint("ConflictingOnColor")
val lightColorPalette = lightColors(
    surface = white,
    onSurface = boneHighlight,
    background = white,
    primary = darkest,
    primaryVariant = cloud,
    secondary = fiord,
    secondaryVariant = bone,
    onPrimary = white,
    onSecondary = bone
)

@SuppressLint("ConflictingOnColor")
val darkColorPalette = darkColors(
    surface = darkest,
    onSurface = fiordHighlight,
    background = darkest,
    primary = white,
    primaryVariant = bone,
    secondary = lite,
    secondaryVariant = cloud,
    onPrimary = darkest,
    onSecondary = fiord
)
