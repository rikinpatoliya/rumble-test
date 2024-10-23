package com.rumble.videoplayer.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.elevation
import com.rumble.theme.miniPlayerActionSize
import com.rumble.theme.miniPlayerHeight
import com.rumble.theme.miniPlayerWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXXMedium
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.presentation.RumbleVideoView
import com.rumble.videoplayer.presentation.internal.defaults.miniPlayerSeekBarHeight
import com.rumble.videoplayer.presentation.internal.views.EmbeddedSeekBar

@Composable
fun MiniPlayerView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    onClose: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .wrapContentHeight(),
        shape = RoundedCornerShape(radiusXXXXMedium),
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier
                .height(miniPlayerHeight),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RumbleCustomTheme.colors.background),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RumbleVideoView(
                    modifier = Modifier
                        .width(miniPlayerWidth)
                        .height(miniPlayerHeight),
                    rumblePlayer = rumblePlayer
                )

                MiniPlayerInfoView(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = paddingMedium),
                    rumblePlayer = rumblePlayer
                )

                MiniPlayerControlsView(
                    modifier = Modifier.padding(end = paddingMedium),
                    playerTarget = rumblePlayer.playerTarget.value,
                    isPlaying = rumblePlayer.isPlaying(),
                    onPause = { rumblePlayer.pauseVideo() },
                    onPlay = { rumblePlayer.playVideo() },
                    onClose = onClose
                )
            }

            EmbeddedSeekBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                rumblePlayer = rumblePlayer,
                seekBarHeight = miniPlayerSeekBarHeight,
                increaseSeekArea = false,
            )
        }
    }
}

@Composable
fun MiniPlayerInfoView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
) {
    val playerTarget by remember { rumblePlayer.playerTarget }
    val title = if (playerTarget == PlayerTarget.AD) stringResource(R.string.video_play_after_ad)
    else rumblePlayer.videoTitle
    val subTitle = if (playerTarget == PlayerTarget.AD) stringResource(R.string.sponsored)
    else rumblePlayer.channelName

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = h6,
            color = RumbleCustomTheme.colors.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        Text(
            text = subTitle,
            style = tinyBody,
            color = RumbleCustomTheme.colors.secondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
fun MiniPlayerControlsView(
    modifier: Modifier = Modifier,
    playerTarget: PlayerTarget?,
    isPlaying: Boolean,
    onPause: () -> Unit = {},
    onPlay: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        if (playerTarget != PlayerTarget.AD) {
            if (isPlaying) {
                Icon(
                    modifier = Modifier
                        .size(miniPlayerActionSize)
                        .clickableNoRipple { onPause() },
                    painter = painterResource(id = R.drawable.ic_pause_embedded),
                    contentDescription = stringResource(id = R.string.pause),
                    tint = RumbleCustomTheme.colors.primary
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(miniPlayerActionSize)
                        .clickableNoRipple { onPlay() },
                    painter = painterResource(id = R.drawable.ic_play_embedded),
                    contentDescription = stringResource(id = R.string.play),
                    tint = RumbleCustomTheme.colors.primary
                )
            }
        }

        Icon(
            modifier = Modifier
                .size(miniPlayerActionSize)
                .padding(paddingXXXSmall)
                .clickableNoRipple { onClose() },
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = stringResource(id = R.string.close),
            tint = RumbleCustomTheme.colors.primary
        )
    }
}

