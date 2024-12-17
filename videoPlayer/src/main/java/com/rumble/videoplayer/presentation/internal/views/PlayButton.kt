package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedWhite
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playButtonEmbeddedSize
import com.rumble.videoplayer.presentation.internal.defaults.playButtonSize
import com.rumble.videoplayer.presentation.internal.defaults.playControl
import com.rumble.videoplayer.presentation.internal.defaults.playControlEmbedded

private enum class PlayButtonState {
    NONE,
    EMBEDDED_PLAYING,
    EMBEDDED_PAUSED,
    FULL_SCREEN_PLAYING,
    FULL_SCREEN_PAUSED,
    TV_PLAYING,
    TV_PAUSED
}

@Composable
internal fun PlayButton(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    uiType: UiType,
    isPlayingInitial: Boolean,
    onClick: (Boolean) -> Unit = {}
) {
    var isPlaying by remember { mutableStateOf(isPlayingInitial) }
    val state = defineButtonState(uiType, isPlaying)
    val playerState by rumblePlayer.playbackState

    // Hides play pause button when the player is buffering for a smoother UX that avoids overlaying the LoadingScreen that is usually shown at buffering state.
    if (playerState.isBuffering) {
        return
    }

    LaunchedEffect(playerState) {
        if (uiType == UiType.TV) {
            if (rumblePlayer.isPlaying()) isPlaying = true
            else if (rumblePlayer.isPaused()) isPlaying = false
        }
    }

    Box(
        modifier = modifier
            .size(if (uiType == UiType.EMBEDDED) playButtonEmbeddedSize else playButtonSize)
            .clip(CircleShape)
            .background(enforcedCloud.copy(0.4f))
            .clickable {
                isPlaying = isPlaying.not()
                onClick(isPlaying)
            }
    ) {
        when (state) {
            PlayButtonState.EMBEDDED_PLAYING -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause_embedded),
                    contentDescription = stringResource(id = R.string.pause),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(playControlEmbedded),
                    tint = enforcedWhite
                )
            }

            PlayButtonState.EMBEDDED_PAUSED -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play_embedded),
                    contentDescription = stringResource(id = R.string.play),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(playControlEmbedded),
                    tint = enforcedWhite
                )
            }

            PlayButtonState.FULL_SCREEN_PLAYING, PlayButtonState.TV_PLAYING -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = stringResource(id = R.string.pause),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(playControl),
                    tint = enforcedWhite
                )
            }

            PlayButtonState.FULL_SCREEN_PAUSED, PlayButtonState.TV_PAUSED -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play_fullscreen),
                    contentDescription = stringResource(id = R.string.play),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(playControl),
                    tint = enforcedWhite
                )
            }

            else -> return
        }
    }
}

private fun defineButtonState(
    uiType: UiType,
    isPlaying: Boolean
): PlayButtonState =
    when {
        uiType == UiType.EMBEDDED && isPlaying -> PlayButtonState.EMBEDDED_PLAYING
        uiType == UiType.EMBEDDED -> PlayButtonState.EMBEDDED_PAUSED
        uiType == UiType.FULL_SCREEN_LANDSCAPE && isPlaying -> PlayButtonState.FULL_SCREEN_PLAYING
        uiType == UiType.FULL_SCREEN_LANDSCAPE -> PlayButtonState.FULL_SCREEN_PAUSED
        uiType == UiType.TV && isPlaying -> PlayButtonState.TV_PLAYING
        uiType == UiType.TV -> PlayButtonState.TV_PAUSED
        else -> PlayButtonState.NONE
    }
