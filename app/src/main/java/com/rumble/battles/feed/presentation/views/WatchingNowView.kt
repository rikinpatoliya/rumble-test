package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.extension.shortString

@Composable
fun WatchingNowView(
    modifier: Modifier = Modifier,
    watchingCount: Long,
    videoStatus : VideoStatus,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_watching_now),
            contentDescription = stringResource(id = R.string.watching),
            tint = fierceRed
        )

        Text(
            modifier = Modifier.padding(start = paddingXXXSmall),
            text = "${watchingCount.shortString(withDecimal = true)} ${
                when (videoStatus) {
                    VideoStatus.LIVE -> stringResource(id = R.string.watching)
                    VideoStatus.UPCOMING, VideoStatus.STARTING -> stringResource(
                        id = R.string.waiting
                    )
                    else -> ""
                }
            }",
            style = RumbleTypography.h6,
            color = fierceRed,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingMedium)
        ) {
            WatchingNowView(watchingCount = 100_000, videoStatus = VideoStatus.LIVE)
            WatchingNowView(watchingCount = 50_000, videoStatus = VideoStatus.UPCOMING)
            WatchingNowView(watchingCount = 30_000, videoStatus = VideoStatus.STARTING)
        }
    }
}