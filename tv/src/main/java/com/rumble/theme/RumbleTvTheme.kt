package com.rumble.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RumbleTvTheme(
    content: @Composable () -> Unit,
) {

    MaterialTheme(
        colorScheme = tvColorScheme,
        content = content
    )
}