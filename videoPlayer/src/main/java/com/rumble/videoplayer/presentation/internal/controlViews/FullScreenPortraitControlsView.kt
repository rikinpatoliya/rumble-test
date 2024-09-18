package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXLarge
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playerFullScreenBottomSpace
import com.rumble.videoplayer.presentation.internal.views.DurationView
import com.rumble.videoplayer.presentation.internal.views.FullScreenButton
import com.rumble.videoplayer.presentation.internal.views.LiveDurationView
import com.rumble.videoplayer.presentation.internal.views.PlayerControls
import com.rumble.videoplayer.presentation.internal.views.ReplayButton
import com.rumble.videoplayer.presentation.internal.views.SettingsButton
import com.rumble.videoplayer.presentation.internal.views.SoundButton
import com.rumble.videoplayer.presentation.views.LiveButton
import com.rumble.videoplayer.presentation.views.VideoSeekBar

@Composable
internal fun FullScreenPortraitControlsView(
    isVisible: Boolean,
    isFullScreen: Boolean,
    rumblePlayer: RumblePlayer,
    onSeekInProgress: (Boolean) -> Unit = {},
    onSeek: () -> Unit = {},
    onChangeFullscreenMode: (Boolean) -> Unit,
    onSettings: () -> Unit
) {
    val playerState by rumblePlayer.playbackState
    val totalTime by rumblePlayer.totalTime
    val currentPosition by rumblePlayer.currentPosition
    var seekInProgress by remember { mutableStateOf(false) }
    val isLive = rumblePlayer.isLiveVideo

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .conditional(seekInProgress.not()) {
                    background(brandedPlayerBackground.copy(0.6f))
                }
        ) {
            val (bottomControls, progressControls, playerControls, duration, replay, live) = createRefs()
            Row(
                modifier = Modifier
                    .alpha(if (seekInProgress) 0f else 1f)
                    .padding(bottom = paddingLarge, top = paddingLarge)
                    .constrainAs(bottomControls) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.spacedBy(paddingXLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsButton { onSettings() }

                SoundButton(rumblePlayer = rumblePlayer)

                FullScreenButton(isFullScreen = isFullScreen) {
                    onChangeFullscreenMode(it)
                }
            }

            VideoSeekBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingLarge, end = paddingLarge)
                    .constrainAs(progressControls) {
                        bottom.linkTo(bottomControls.top)
                    },
                rumblePlayer = rumblePlayer
            ) {
                seekInProgress = it
                onSeekInProgress(seekInProgress)
            }

            if (isLive && seekInProgress.not()) {
                LiveButton(
                    modifier = Modifier
                        .padding(start = paddingXLarge, bottom = playerFullScreenBottomSpace)
                        .constrainAs(live) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(progressControls.start)
                        },
                    type = UiType.FULL_SCREEN_LANDSCAPE,
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong(),
                    dvrSupported = rumblePlayer.supportsDvr
                ) {
                    rumblePlayer.seekToPercentage(1f)
                }
            } else if (isLive) {
                LiveDurationView(
                    modifier = Modifier
                        .padding(start = paddingXLarge)
                        .constrainAs(duration) {
                            start.linkTo(progressControls.start)
                            top.linkTo(progressControls.bottom)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            } else {
                DurationView(
                    modifier = Modifier
                        .padding(start = paddingXLarge)
                        .constrainAs(duration) {
                            start.linkTo(progressControls.start)
                            top.linkTo(progressControls.bottom)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            }

            if (playerState is PlayerPlaybackState.Finished) {
                ReplayButton(
                    modifier = Modifier
                        .padding(bottom = paddingXXLarge)
                        .constrainAs(replay) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(progressControls.top)
                        },
                    onClick = { rumblePlayer.replay() }
                )
            } else if (seekInProgress.not()) {
                PlayerControls(
                    modifier = Modifier
                        .padding(bottom = paddingXXLarge)
                        .constrainAs(playerControls) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(progressControls.top)
                        },
                    uiType = UiType.EMBEDDED,
                    rumblePlayer = rumblePlayer,
                    onSeek = onSeek
                )
            }
        }
    }
}
