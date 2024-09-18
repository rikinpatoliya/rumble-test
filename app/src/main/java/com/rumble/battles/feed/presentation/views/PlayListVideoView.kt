package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingXXXSmall

@Composable
fun PlayListVideoView(
    modifier: Modifier = Modifier,
    videoNumber: Int,
    videoEntity: VideoEntity,
    onViewVideo: (VideoEntity) -> Unit = {},
    onMoreClick: (VideoEntity) -> Unit,
    onImpression: (VideoEntity) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$videoNumber",
            color = MaterialTheme.colors.secondary,
            style = RumbleTypography.h6
        )
        Spacer(modifier = Modifier.width(paddingXXXSmall))
        VideoCompactView(
            videoEntity = videoEntity,
            onViewVideo = onViewVideo,
            onMoreClick = onMoreClick,
            onImpression = onImpression,
        )
    }
}