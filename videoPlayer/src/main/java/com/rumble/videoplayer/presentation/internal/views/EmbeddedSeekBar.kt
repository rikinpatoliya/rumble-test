package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.rumble.theme.enforcedWhite
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.internal.defaults.embeddedSeekBarHeight
import com.rumble.videoplayer.presentation.internal.defaults.embeddedThumbOffset
import com.rumble.videoplayer.presentation.internal.defaults.embeddedThumbSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmbeddedSeekBar(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    seekBarHeight: Dp = embeddedSeekBarHeight,
    displayThumb: Boolean = false,
    onSeekInProgress: (Boolean) -> Unit = {}
) {
    val progressPercentage by rumblePlayer.progressPercentage
    var progress by rememberSaveable { mutableFloatStateOf(0f) }
    var isDragging by rememberSaveable { mutableStateOf(false) }
    val playerState by rumblePlayer.playbackState

    BoxWithConstraints(modifier = modifier) {
        val widthInPx = with(LocalDensity.current) { maxWidth.toPx() }

        Box(modifier = Modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                onDragStarted = { offset ->
                    isDragging = true
                    onSeekInProgress(true)
                    progress = offset.x / widthInPx
                    rumblePlayer.onSeekChanged(true)
                    rumblePlayer.seekToPercentage(progress)
                },
                onDragStopped = {
                    isDragging = false
                    onSeekInProgress(false)
                    rumblePlayer.onSeekChanged(false)
                },
                state = rememberDraggableState {
                    val delta = it / (widthInPx / 100) / 100
                    progress += delta

                    val seekCoefficient = 0.05f
                    val seekProgress = if (delta > 0) progress + seekCoefficient
                    else progress - seekCoefficient

                    if (seekProgress < 0) progress = 0f
                    else if (seekProgress > 1) progress = 1f
                    rumblePlayer.seekToPercentage(progress)
                }
            )) {

            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(seekBarHeight.times(3)))

                Box {
                    if (playerState.isBuffering) {
                        BufferingView(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(seekBarHeight)
                        )
                    }

                    LinearProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(seekBarHeight),
                        progress = if (isDragging) progress else progressPercentage,
                        color = rumbleGreen,
                        trackColor = enforcedWhite.copy(0.6f)
                    )
                }
            }

            AnimatedVisibility(
                visible = displayThumb,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                Slider(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .offset(y = embeddedThumbOffset),
                    value = if (isDragging) progress else progressPercentage,
                    enabled = false,
                    onValueChange = {},
                    valueRange = 0f..1.0f,
                    colors = SliderDefaults.colors(
                        disabledActiveTrackColor = Color.Transparent,
                        disabledInactiveTrackColor = Color.Transparent,
                    ),
                    thumb = {
                        Icon(
                            modifier = Modifier.size(embeddedThumbSize),
                            painter = painterResource(id = R.drawable.ic_slider_thumb),
                            contentDescription = "",
                            tint = rumbleGreen
                        )
                    }
                )
            }
        }
    }
}

