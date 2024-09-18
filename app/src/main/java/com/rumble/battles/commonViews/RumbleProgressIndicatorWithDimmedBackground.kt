package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.theme.enforcedGray950

@Composable
fun RumbleProgressIndicatorWithDimmedBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = enforcedGray950.copy(alpha = 0.6F))
    ) {
        RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}