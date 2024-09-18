package com.rumble.ui3.common.views

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rumble.theme.enforcedWhite
import com.rumble.theme.indicatorWidth
import com.rumble.theme.progressIndicatorSize

@Composable
fun RumbleProgressIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(progressIndicatorSize),
        color = enforcedWhite,
        strokeWidth = indicatorWidth
    )
}