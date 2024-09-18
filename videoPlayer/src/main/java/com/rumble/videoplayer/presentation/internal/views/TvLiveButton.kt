package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.theme.RumbleTheme
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.internal.defaults.isLiveShift

@Composable
fun TvLiveButton(
    modifier: Modifier = Modifier,
    currentPosition: Long = 0,
    totalDuration: Long = 0,
    dvrSupported: Boolean = false,
    isFocused: Boolean = false,
    onSeek: () -> Unit = {},
    onPlayPause: () -> Unit = {}
) {
    val isNotLiveNow = (totalDuration - currentPosition) > isLiveShift && dvrSupported
    Box(modifier = modifier
        .clip(RoundedCornerShape(radiusXMedium))
        .conditional(isFocused) {
            background(enforcedWhite.copy(alpha = 0.2f))
        }
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = paddingXXSmall)
                .height(14.dp),
            onClick = { if (dvrSupported && isNotLiveNow) onSeek() else onPlayPause() }
        ) {
            if (isNotLiveNow) {
                Image(
                    painter = painterResource(id = R.drawable.live_grey_tv),
                    contentDescription = stringResource(id = R.string.live)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.live_red_tv),
                    contentDescription = stringResource(id = R.string.live)
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        TvLiveButton(isFocused = true)
    }
}