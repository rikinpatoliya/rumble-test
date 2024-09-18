package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.fullScreenSeekBarHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TvSeekBar(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    isFocused: Boolean = true,
    onSeekInProgress: (Boolean) -> Unit = {}
) {
    val totalTime by rumblePlayer.totalTime
    val currentPosition by rumblePlayer.currentPosition
    val bufferedPercentage by rumblePlayer.buggeredPercentage
    var dragPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val enableSeek = remember { rumblePlayer.enableSeekBar }
    val playerState by rumblePlayer.playbackState

    Box(modifier = modifier.fillMaxWidth()) {
        if (enableSeek) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = bufferedPercentage,
                enabled = false,
                onValueChange = {},
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledActiveTrackColor = enforcedWhite,
                    disabledInactiveTrackColor = enforcedWhite.copy(0.2f),
                ),
                thumb = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_slider_thumb),
                        contentDescription = "",
                        tint = Color.Transparent
                    )
                }
            )

            if (playerState.isBuffering) {
                BufferingView(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(fullScreenSeekBarHeight)
                        .padding(start = paddingSmall, end = paddingSmall)
                )
            }

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = if (isDragging) dragPosition else currentPosition,
                onValueChange = { value: Float ->
                    dragPosition = value
                    rumblePlayer.onSeekChanged(true)
                    rumblePlayer.seekTo(dragPosition.toLong())
                    isDragging = true
                    onSeekInProgress(true)
                },
                onValueChangeFinished = {
                    isDragging = false
                    rumblePlayer.onSeekChanged(false)
                    onSeekInProgress(false)
                },
                valueRange = 0f..totalTime,
                colors = SliderDefaults.colors(
                    thumbColor = rumbleGreen,
                    activeTrackColor = rumbleGreen,
                    inactiveTrackColor = Color.Transparent
                ),
                thumb = {
                    if (isFocused) {
                        Image(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_slider_thumb),
                            contentDescription = ""
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_slider_thumb),
                            contentDescription = "",
                            tint = Color.Transparent
                        )
                    }
                }
            )
        } else {
            DisabledVideoSeekBar(uiType = UiType.TV, isFocused = isFocused)
        }
    }
}

