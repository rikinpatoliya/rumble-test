package com.rumble.ui3.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXSmall

@Composable
fun LiveTagView(
    modifier: Modifier = Modifier,
    videoStatus: VideoStatus,
) {
    if (videoStatus == VideoStatus.LIVE)
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(radiusXXXSmall))
                .background(fierceRed)
        ) {
            TagText(
                modifier = Modifier
                    .padding(
                        vertical = paddingXXXSmall,
                        horizontal = paddingXSmall
                    ),
            )
        }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TagText(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.video_card_live_label_all_caps),
        style = RumbleTypography.h6Heavy,
        color = enforcedWhite
    )
}