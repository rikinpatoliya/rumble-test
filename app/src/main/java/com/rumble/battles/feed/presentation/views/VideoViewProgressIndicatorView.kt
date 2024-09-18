package com.rumble.battles.feed.presentation.views

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.enforcedFiord
import com.rumble.theme.rumbleGreen

@Composable
fun VideoViewProgressIndicatorView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity
) {
    if (videoEntity.videoStatus != VideoStatus.LIVE) {
        val lastPosition = videoEntity.lastPositionSeconds
        if (lastPosition != null && lastPosition > 0 && videoEntity.duration > 0) {
            val progress = lastPosition.toFloat() / videoEntity.duration
            LinearProgressIndicator(
                modifier = modifier,
                progress = progress,
                color = rumbleGreen,
                trackColor = enforcedFiord
            )
        }
    }
}