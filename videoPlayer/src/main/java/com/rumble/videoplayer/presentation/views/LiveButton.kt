package com.rumble.videoplayer.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.liveShift

@Composable
fun LiveButton(
    modifier: Modifier = Modifier,
    type: UiType = UiType.EMBEDDED,
    currentPosition: Long = 0,
    totalDuration: Long = 0,
    dvrSupported: Boolean = false,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier,
        onClick = { if (dvrSupported) onClick() }) {
        if ((totalDuration - currentPosition) > liveShift && dvrSupported) {
            when (type) {
                UiType.EMBEDDED -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_grey_embedded),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
                UiType.TV -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_grey_tv),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_grey_full_screen),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
            }
        } else {
            when (type) {
                UiType.EMBEDDED -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_red_embedded),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
                UiType.TV -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_red_tv),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.live_red_full_screen),
                        contentDescription = stringResource(id = R.string.live)
                    )
                }
            }
        }
    }
}