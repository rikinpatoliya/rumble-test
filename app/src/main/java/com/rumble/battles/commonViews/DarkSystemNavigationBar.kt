package com.rumble.battles.commonViews

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DarkSystemNavigationBar() {
    val systemUiController = rememberSystemUiController()

    systemUiController.setNavigationBarColor(
        color = Color.Black,
        darkIcons = false,
    )
}