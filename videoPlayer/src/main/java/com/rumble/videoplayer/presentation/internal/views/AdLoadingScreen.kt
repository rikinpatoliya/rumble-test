package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.presentation.internal.defaults.progressBarSize
import com.rumble.videoplayer.presentation.internal.defaults.progressBarWidth

@Composable
internal fun AdLoadingScreen(
    modifier: Modifier,
) {
    Box(modifier = modifier.background(brandedPlayerBackground.copy(0.6f))) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(progressBarSize),
            strokeWidth = progressBarWidth,
            color = rumbleGreen
        )
    }
}

