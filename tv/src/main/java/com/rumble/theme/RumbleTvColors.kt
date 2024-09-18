package com.rumble.theme

import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.darkColorScheme

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

val defaultBackgroundColor = black

@OptIn(ExperimentalTvMaterial3Api::class)
val tvColorScheme = darkColorScheme(
    surface = darkmo,
    onSurface = white,
    background = darkest,
    onBackground = white,

    primary = white,
    onPrimary = darkmo,
    primaryContainer = bone,
    onPrimaryContainer = white,

    secondary = lite,
    onSecondary = fiord,
    secondaryContainer = cloud,
    onSecondaryContainer = fiordHighlight
)