package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.UploadDateView
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingXXXSmall
import java.time.LocalDateTime

@Composable
fun LiveStatusView(
    modifier: Modifier = Modifier,
    status: VideoStatus,
    date: LocalDateTime
) {

    val text = when (status) {
        VideoStatus.UPCOMING, VideoStatus.SCHEDULED -> stringResource(id = R.string.upcoming)
        VideoStatus.STARTING -> stringResource(id = R.string.starting)
        VideoStatus.LIVE -> stringResource(id = R.string.live)
        VideoStatus.STREAMED -> stringResource(id = R.string.streamed)
        else -> ""
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == VideoStatus.LIVE) {
            Text(
                text = text,
                style = RumbleTypography.h6Bold,
                color = fierceRed
            )
        } else {
            Text(
                text = text,
                style = RumbleTypography.h6Light,
                color = MaterialTheme.colors.secondary,
            )
        }

        if (status == VideoStatus.STREAMED) {
            UploadDateView(
                modifier = Modifier.padding(start = paddingXXXSmall),
                date = date
            )
        }
    }
}