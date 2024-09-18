package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.imageXSmall14
import com.rumble.theme.imageXXXSmall

@Composable
fun StateTagImageView(
    videoStatus: VideoStatus,
    listToggleViewStyle: ListToggleViewStyle
) {
    Image(
        painter = painterResource(
            id = when (videoStatus) {
                VideoStatus.SCHEDULED, VideoStatus.UPCOMING, VideoStatus.STARTING -> R.drawable.ic_clock
                VideoStatus.LIVE -> R.drawable.ic_view
                else -> R.drawable.ic_streamed
            }
        ),
        contentDescription = stringResource(
            id = when (videoStatus) {
                VideoStatus.SCHEDULED, VideoStatus.UPCOMING -> R.string.upcoming
                VideoStatus.STARTING -> R.string.starting
                VideoStatus.LIVE -> R.string.live
                else -> R.string.streamed
            }
        ),
        modifier = Modifier.size(if (listToggleViewStyle == ListToggleViewStyle.GRID) imageXSmall14 else imageXXXSmall)
    )
}