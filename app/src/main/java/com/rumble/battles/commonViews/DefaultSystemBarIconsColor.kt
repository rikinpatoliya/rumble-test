package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DefaultSystemBarIconsColor() {
    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colors.onPrimary,
        darkIcons = MaterialTheme.colors.isLight
    )

    systemUiController.setNavigationBarColor(
        color = MaterialTheme.colors.onPrimary,
        darkIcons = MaterialTheme.colors.isLight
    )
}