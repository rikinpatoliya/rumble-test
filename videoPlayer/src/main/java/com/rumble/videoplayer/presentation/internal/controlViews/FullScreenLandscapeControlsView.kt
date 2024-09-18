package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXGiant
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playerBottomGuideline
import com.rumble.videoplayer.presentation.internal.views.DurationView
import com.rumble.videoplayer.presentation.internal.views.FullScreenButton
import com.rumble.videoplayer.presentation.internal.views.LiveChatButton
import com.rumble.videoplayer.presentation.internal.views.LiveDurationView
import com.rumble.videoplayer.presentation.internal.views.PlayerControls
import com.rumble.videoplayer.presentation.internal.views.ReplayButton
import com.rumble.videoplayer.presentation.internal.views.SettingsButton
import com.rumble.videoplayer.presentation.internal.views.SoundButton
import com.rumble.videoplayer.presentation.views.LiveButton
import com.rumble.videoplayer.presentation.views.VideoSeekBar

@Composable
internal fun FullScreenLandscapeControlsView(
    isVisible: Boolean,
    isFullScreen: Boolean,
    rumblePlayer: RumblePlayer,
    onSeekInProgress: (Boolean) -> Unit = {},
    onSeek: () -> Unit = {},
    onChangeFullscreenMode: (Boolean) -> Unit,
    onLiveChatClicked: () -> Unit,
    liveChatDisabled: Boolean,
    onSettings: () -> Unit,
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
            val (playerControls, setting, progress, sound, fullScreen, duration, replay, live, chat) = createRefs()

            if (playerState is PlayerPlaybackState.Finished) {
                ReplayButton(
                    modifier = Modifier.constrainAs(replay) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    },
                    onClick = { rumblePlayer.replay() }
                )
            } else if (seekInProgress.not()) {
                PlayerControls(
                    modifier = Modifier.constrainAs(playerControls) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    },
                    uiType = UiType.FULL_SCREEN_LANDSCAPE,
                    rumblePlayer = rumblePlayer,
                    onSeek = onSeek
                )
            }

            val bottomGuideline = createGuidelineFromBottom(playerBottomGuideline)

            SettingsButton(
                modifier = Modifier
                    .alpha(if (seekInProgress) 0f else 1f)
                    .padding(start = paddingMedium, top = paddingLarge)
                    .constrainAs(setting) {
                        start.linkTo(parent.start)
                        top.linkTo(bottomGuideline)
                    },
                onClick = onSettings
            )

            if (isLive && seekInProgress.not()) {
                LiveButton(
                    modifier = Modifier
                        .padding(bottom = paddingXXXGiant)
                        .constrainAs(live) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(progress.start)
                        },
                    type = UiType.FULL_SCREEN_LANDSCAPE,
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong(),
                    dvrSupported = rumblePlayer.supportsDvr
                ) {
                    rumblePlayer.seekToPercentage(1f)
                }
                if (liveChatDisabled.not()) {
                    LiveChatButton(
                        modifier = Modifier
                            .padding(paddingMedium)
                            .constrainAs(chat) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            },
                        watchingNumber = rumblePlayer.watchingNow,
                        onClick = onLiveChatClicked
                    )
                }
            } else if (isLive) {
                LiveDurationView(
                    modifier = Modifier
                        .constrainAs(duration) {
                            top.linkTo(progress.bottom)
                            start.linkTo(progress.start)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            } else {
                DurationView(
                    modifier = Modifier
                        .constrainAs(duration) {
                            top.linkTo(progress.bottom)
                            start.linkTo(progress.start)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            }

            SoundButton(
                modifier = Modifier
                    .padding(top = paddingLarge)
                    .alpha(if (seekInProgress) 0f else 1f)
                    .constrainAs(sound) {
                        top.linkTo(bottomGuideline)
                        end.linkTo(fullScreen.start)
                    },
                rumblePlayer = rumblePlayer
            )

            FullScreenButton(
                modifier = Modifier
                    .alpha(if (seekInProgress) 0f else 1f)
                    .padding(end = paddingMedium, top = paddingLarge)
                    .constrainAs(fullScreen) {
                        top.linkTo(bottomGuideline)
                        end.linkTo(parent.end)
                    },
                isFullScreen = isFullScreen
            ) {
                onChangeFullscreenMode(it)
            }

            VideoSeekBar(
                modifier = Modifier
                    .padding(top = paddingLarge)
                    .constrainAs(progress) {
                        top.linkTo(bottomGuideline)
                        start.linkTo(setting.end)
                        end.linkTo(sound.start)
                        width = Dimension.fillToConstraints
                    },
                rumblePlayer = rumblePlayer
            ) {
                seekInProgress = it
                onSeekInProgress(seekInProgress)
            }
        }
    }
}
