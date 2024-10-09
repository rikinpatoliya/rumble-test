package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.afterSeekDelay
import com.rumble.videoplayer.presentation.internal.views.DurationView
import com.rumble.videoplayer.presentation.internal.views.EmbeddedSeekBar
import com.rumble.videoplayer.presentation.internal.views.FullScreenButton
import com.rumble.videoplayer.presentation.internal.views.LiveDurationView
import com.rumble.videoplayer.presentation.internal.views.PlayerControls
import com.rumble.videoplayer.presentation.internal.views.ReplayButton
import com.rumble.videoplayer.presentation.views.CastView
import com.rumble.videoplayer.presentation.views.LiveButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun EmbeddedControlsView(
    isVisible: Boolean,
    isFullScreen: Boolean,
    rumblePlayer: RumblePlayer,
    onChangeFullscreenMode: (Boolean) -> Unit,
    onMore: () -> Unit,
    onSeekInProgress: (Boolean) -> Unit = {},
    onSeek: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var seekInProgress by remember { mutableStateOf(false) }
    val displaySeek = rumblePlayer.enableSeekBar
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        EmbeddedControls(
            isVisible,
            isFullScreen,
            seekInProgress,
            rumblePlayer,
            onChangeFullscreenMode,
            onMore,
            onBack,
            onSeek
        )

        if (displaySeek) {
            EmbeddedSeekBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                displayThumb = isVisible  && rumblePlayer.isLiveVideo.not(),
                rumblePlayer = rumblePlayer
            ) {
                onSeekInProgress(it)
                if (it) seekInProgress = true
                else {
                    coroutineScope.launch {
                        delay(afterSeekDelay)
                        seekInProgress = false
                    }
                }
            }
        }
    }
}

@Composable
private fun EmbeddedControls(
    isVisible: Boolean,
    isFullScreen: Boolean,
    seekInProgress: Boolean,
    rumblePlayer: RumblePlayer,
    onChangeFullscreenMode: (Boolean) -> Unit,
    onMore: () -> Unit,
    onBack: () -> Unit,
    onSeek: () -> Unit,
) {
    val playerState by rumblePlayer.playbackState
    val totalTime by rumblePlayer.totalTime
    val currentPosition by rumblePlayer.currentPosition
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
            val (playerControls, fullScreenButton, replay, more, cast, duration, live, back) = createRefs()

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
                    uiType = UiType.EMBEDDED,
                    rumblePlayer = rumblePlayer,
                    onSeek = onSeek
                )
            }

            FullScreenButton(
                modifier = Modifier
                    .alpha(if (seekInProgress.not()) 1f else 0f)
                    .constrainAs(fullScreenButton) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                isFullScreen = isFullScreen,
                onClick = onChangeFullscreenMode
            )

            IconButton(
                modifier = Modifier
                    .alpha(if (seekInProgress.not()) 1f else 0f)
                    .constrainAs(more) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onClick = onMore
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = stringResource(id = R.string.more),
                    tint = enforcedWhite
                )
            }

            CastView(modifier = Modifier
                .alpha(if (seekInProgress.not()) 1f else 0f)
                .constrainAs(cast) {
                    top.linkTo(more.top)
                    end.linkTo(more.start)
                    bottom.linkTo(more.bottom)
                }
            )

            IconButton(
                modifier = Modifier
                    .alpha(if (seekInProgress.not()) 1f else 0f)
                    .constrainAs(back) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_caret_down),
                    contentDescription = stringResource(id = R.string.collapse),
                    tint = enforcedWhite
                )
            }

            if (isLive && seekInProgress.not()) {
                LiveButton(
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .constrainAs(live) {
                            start.linkTo(parent.start)
                            top.linkTo(fullScreenButton.top)
                            bottom.linkTo(fullScreenButton.bottom)
                        },
                    type = UiType.EMBEDDED,
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong(),
                    dvrSupported = rumblePlayer.supportsDvr
                ) {
                    rumblePlayer.seekToPercentage(1f)
                }
            } else if (isLive) {
                LiveDurationView(
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .constrainAs(duration) {
                            start.linkTo(parent.start)
                            top.linkTo(fullScreenButton.top)
                            bottom.linkTo(fullScreenButton.bottom)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            } else {
                DurationView(
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .constrainAs(duration) {
                            start.linkTo(parent.start)
                            top.linkTo(fullScreenButton.top)
                            bottom.linkTo(fullScreenButton.bottom)
                        },
                    currentPosition = currentPosition.toLong(),
                    totalDuration = totalTime.toLong()
                )
            }
        }
    }
}
