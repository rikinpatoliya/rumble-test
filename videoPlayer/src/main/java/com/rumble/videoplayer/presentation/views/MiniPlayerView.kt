package com.rumble.videoplayer.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import com.rumble.theme.indicatorWidthMini
import com.rumble.theme.miniPlayerActionSize
import com.rumble.theme.miniPlayerHeight
import com.rumble.theme.miniPlayerWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXXMedium
import com.rumble.theme.rumbleGreen
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
    rumblePlayer: RumblePlayer?,
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
                .fillMaxWidth()
                .height(miniPlayerHeight),
        ) {
            rumblePlayer?.let {
                val showUpNext by rumblePlayer.showUpNext
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
                        rumblePlayer = rumblePlayer,
                        showUpNext = showUpNext
                    )

                    MiniPlayerControlsView(
                        modifier = Modifier.padding(end = paddingMedium),
                        playerTarget = rumblePlayer.playerTarget.value,
                        isPlaying = rumblePlayer.isPlaying(),
                        showPlayPause = !showUpNext,
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
            } ?: run {
                MiniPlayerPlaceholder()
            }
        }
    }
}

@Composable
private fun MiniPlayerPlaceholder() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(RumbleCustomTheme.colors.background)) {
        Box(modifier = Modifier
            .width(miniPlayerWidth)
            .height(miniPlayerHeight)
            .background(MaterialTheme.colors.primaryVariant)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = rumbleGreen,
                strokeWidth = indicatorWidthMini
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MiniPlayerInfoView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    showUpNext: Boolean = false
) {
    val nextRelatedVideo = rumblePlayer.nextRelatedVideo
    val playerTarget by remember { rumblePlayer.playerTarget }
    val title: String
    val subTitle: String
    when  {
        playerTarget == PlayerTarget.AD -> {
            title = stringResource(R.string.video_play_after_ad)
            subTitle = stringResource(R.string.sponsored)
        }
        showUpNext && nextRelatedVideo != null -> {
            title = nextRelatedVideo.title
            subTitle = stringResource(R.string.up_next)
        }
        else -> {
            title = rumblePlayer.videoTitle
            subTitle = rumblePlayer.channelName
        }
    }

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
    showPlayPause: Boolean = true,
    onPause: () -> Unit = {},
    onPlay: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        if (showPlayPause && playerTarget != PlayerTarget.AD) {
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

