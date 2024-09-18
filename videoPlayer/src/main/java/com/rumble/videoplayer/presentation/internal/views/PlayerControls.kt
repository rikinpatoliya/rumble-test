package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playButtonEmbeddedSize
import com.rumble.videoplayer.presentation.internal.defaults.playButtonSize
import com.rumble.videoplayer.presentation.internal.defaults.playerControlEmbeddedSpace
import com.rumble.videoplayer.presentation.internal.defaults.playerControlSpace

@Composable
internal fun PlayerControls(
    modifier: Modifier,
    uiType: UiType,
    rumblePlayer: RumblePlayer,
    onSeek: () -> Unit
) {
    val displaySeek = remember { rumblePlayer.enableSeekBar }
    val maxWidth = if (uiType == UiType.EMBEDDED) playerControlEmbeddedSpace else playerControlSpace

    Row(
        modifier = modifier.widthIn(maxWidth),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displaySeek) {
            SeekButton(
                uiType = uiType,
                icon = painterResource(id = R.drawable.ic_backward)
            ) {
                rumblePlayer.seekBack()
                onSeek()
            }
        }

        PlayButton(
            uiType = uiType,
            rumblePlayer = rumblePlayer,
            isPlayingInitial = rumblePlayer.isPlaying()
        ) { play ->
            if (play) rumblePlayer.playVideo()
            else rumblePlayer.pauseVideo()
        }

        if (displaySeek) {
            SeekButton(
                uiType = uiType,
                icon = painterResource(id = R.drawable.ic_forward)
            ) {
                rumblePlayer.seekForward()
                onSeek()
            }
        }
    }
}

@Composable
private fun SeekButton(
    uiType: UiType,
    icon: Painter,
    onClick: () -> Unit
) {
    val iconSize = if (uiType == UiType.EMBEDDED) playButtonEmbeddedSize else playButtonSize
    Box(
        modifier = Modifier
            .size(iconSize)
            .clickableNoRipple { onClick() }) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = icon,
            contentDescription = ""
        )
    }
}