package com.rumble.ui3.common

import androidx.compose.ui.graphics.Color
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.fierceRed

fun borderColor(videoStatus: VideoStatus): Color =
    if (videoStatus == VideoStatus.LIVE || videoStatus == VideoStatus.STARTING) fierceRed
    else Color.Transparent
