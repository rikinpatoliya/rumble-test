package com.rumble.battles.common

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.fierceRed
import com.rumble.theme.newPurple
import com.rumble.theme.rumbleGreen

fun borderColor(videoStatus: VideoStatus, ppv: PpvEntity?): Color =
    if (videoStatus == VideoStatus.LIVE) fierceRed
    else if (ppv != null) newPurple
    else Color.Transparent

@Composable
fun borderColorFreshContent(fresh: Boolean, videoStatus: VideoStatus?): Color =
    if (videoStatus != null && (videoStatus == VideoStatus.LIVE ||
                videoStatus == VideoStatus.STARTING)
    ) {
        fierceRed
    } else if (fresh) {
        rumbleGreen
    } else {
        MaterialTheme.colors.secondaryVariant
    }