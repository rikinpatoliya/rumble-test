package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.rumble.battles.common.getStringTitleByStatus
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.h6Light

@Composable
fun VideoTimestampLabelView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    textStyle: TextStyle = h6Light,
    textColor: Color = MaterialTheme.colors.secondary
) {
    Text(
        modifier = modifier,
        text = getStringTitleByStatus(videoEntity, ListToggleViewStyle.GRID),
        style = textStyle,
        color = textColor,
    )
}