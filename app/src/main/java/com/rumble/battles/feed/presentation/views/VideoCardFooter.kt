package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.h5Medium
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Medium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.RumbleConstants.MAX_LINES_TITLE_REGULAR_VIDEO_CARD
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun VideoCardFooter(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    onChannelClick: (() -> Unit)?,
    onMoreClick: (VideoEntity) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = paddingXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = videoEntity.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAX_LINES_TITLE_REGULAR_VIDEO_CARD,
                style = h5Medium,
                color = MaterialTheme.colors.primary
            )
            Icon(
                modifier = Modifier
                    .clickableNoRipple { onMoreClick(videoEntity) },
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = stringResource(id = R.string.more),
                tint = MaterialTheme.colors.primary
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingXSmall,
                    end = paddingXSmall,
                    top = paddingXXSmall,
                )
                .clickableNoRipple { onChannelClick?.invoke() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageComponent(
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                userName = videoEntity.channelName,
                userPicture = videoEntity.channelThumbnail
            )
            Column(modifier = Modifier.padding(start = paddingXSmall)) {
                UserNameViewSingleLine(
                    name = videoEntity.channelName,
                    verifiedBadge = videoEntity.verifiedBadge,
                    textStyle = h6,
                    spacerWidth = paddingXXXXSmall,
                    verifiedBadgeHeight = verifiedBadgeHeightSmall
                )
                VideoMetadataView(
                    videoEntity = videoEntity,
                    textStyle = h6Medium,
                    listToggleViewStyle = ListToggleViewStyle.GRID
                )
            }
        }

        videoEntity.ppv?.let {
            PpvTagsView(
                modifier = Modifier.padding(start = paddingXSmall, top = paddingXXSmall),
                ppvEntity = it
            )
        }

        if (videoEntity.isPremiumExclusiveContent) {
            VideoCardPremiumTagView(
                modifier = Modifier
                    .padding(top = paddingXXSmall, start = paddingXSmall)
            )
        }
    }
}