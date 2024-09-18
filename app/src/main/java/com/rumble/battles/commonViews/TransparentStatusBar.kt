package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TransparentStatusBar(withOnDispose: Boolean = true, darkIcons: Boolean = true) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    val isLight = MaterialTheme.colors.isLight
    val color = MaterialTheme.colors.onPrimary
    if (withOnDispose) {
        DisposableEffect(LocalLifecycleOwner.current) {
            onDispose {
                systemUiController.setStatusBarColor(
                    color = color,
                    darkIcons = isLight
                )
            }
        }
    }
}