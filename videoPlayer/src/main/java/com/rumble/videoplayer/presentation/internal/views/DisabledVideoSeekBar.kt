package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.rumble.theme.enforcedWhite
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.seekbarFocusHeight
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.presentation.UiType

@Composable
fun DisabledVideoSeekBar(
    modifier: Modifier = Modifier,
    uiType: UiType = UiType.FULL_SCREEN_LANDSCAPE,
    isFocused: Boolean = false
) {
    Box {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(seekbarFocusHeight)
                .clip(RoundedCornerShape(radiusSmall))
                .align(Alignment.Center)
                .conditional(isFocused) {
                    background(enforcedWhite.copy(alpha = 0.2f))
                }
        )
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .conditional(uiType == UiType.TV) {
                    focusable(enabled = true)
                },
            value = 100f,
            enabled = false,
            onValueChange = {},
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                disabledActiveTrackColor = if (uiType == UiType.TV) rumbleGreen else enforcedWhite.copy(0.6f),
                disabledInactiveTrackColor = Color.Transparent,
                disabledThumbColor = Color.Transparent,
            )
        )
    }
}