package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.rumble.battles.R
import com.rumble.battles.common.getStringTitleByStatus
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.imageXXMini
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.extension.shortString

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoMetadataView(
    videoEntity: VideoEntity,
    textStyle: TextStyle,
    listToggleViewStyle: ListToggleViewStyle
) {
    FlowRow(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall)
    ) {
        Text(
            text = getStringTitleByStatus(
                videoEntity,
                listToggleViewStyle
            ).replaceFirstChar { it.uppercaseChar() },
            color = MaterialTheme.colors.secondary,
            style = textStyle,
        )
        if (shouldShowViews(videoEntity)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = "",
                modifier = Modifier.size(imageXXMini).align(Alignment.CenterVertically),
                tint = MaterialTheme.colors.secondary
            )

            Text(
                text = getViewsNumberText(videoEntity),
                color = MaterialTheme.colors.secondary,
                style = textStyle,
            )
        }
        if (shouldShowLikes(listToggleViewStyle, videoEntity)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = "",
                modifier = Modifier.size(imageXXMini).align(Alignment.CenterVertically),
                tint = MaterialTheme.colors.secondary
            )
            Text(
                text = "${videoEntity.likeNumber.shortString()} ${stringResource(id = R.string.likes).lowercase()}",
                color = MaterialTheme.colors.secondary,
                style = textStyle,
            )
        }
    }
}

@Composable
private fun shouldShowLikes(
    listToggleViewStyle: ListToggleViewStyle,
    videoEntity: VideoEntity
) = (listToggleViewStyle == ListToggleViewStyle.GRID &&
    (videoEntity.videoStatus == VideoStatus.UPLOADED
        || (videoEntity.videoStatus == VideoStatus.UPCOMING && videoEntity.likeNumber > 0)
        || (videoEntity.videoStatus == VideoStatus.STARTING && videoEntity.likeNumber > 0)
        || videoEntity.videoStatus == VideoStatus.STREAMED)
    )

@Composable
private fun shouldShowViews(videoEntity: VideoEntity) = when (videoEntity.videoStatus) {
    VideoStatus.UPLOADED, VideoStatus.LIVE, VideoStatus.STREAMED -> true
    VideoStatus.UPCOMING, VideoStatus.STARTING -> videoEntity.watchingNow > 0
    VideoStatus.SCHEDULED -> false
}

@Composable
private fun getViewsNumberText(videoEntity: VideoEntity): String {
    val number = when (videoEntity.videoStatus) {
        VideoStatus.STARTING, VideoStatus.UPCOMING -> videoEntity.watchingNow
        else -> videoEntity.viewsNumber
    }
    val textId = when (videoEntity.videoStatus) {
        VideoStatus.STARTING, VideoStatus.UPCOMING -> R.string.waiting
        else -> R.string.views
    }
    return "${number.shortString()} ${stringResource(id = textId).lowercase()}"
}