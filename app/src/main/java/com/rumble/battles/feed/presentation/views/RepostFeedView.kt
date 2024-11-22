package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.feed.domain.domainmodel.video.UserEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoLogView
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h5Medium
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingNone
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.extension.agoString
import com.rumble.utils.extension.clickableNoRipple
import java.time.LocalDateTime

@Composable
fun RepostFeedView(
    modifier: Modifier = Modifier,
    repost: RepostEntity,
    onVideoClick: (VideoEntity) -> Unit = {},
    onChannelClick: (String) -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXMedium))
            .background(RumbleCustomTheme.colors.surface)
            .border(
                width = borderXXSmall,
                shape = RoundedCornerShape(radiusXMedium),
                color = RumbleCustomTheme.colors.backgroundHighlight
            )
            .clickable { onVideoClick(repost.video) }
    ) {
        RepostFeedHeader(
            modifier = Modifier
                .padding(paddingXSmall)
                .fillMaxWidth(),
            repost = repost,
            onChannelClick = onChannelClick,
            onMoreClick = onMoreClick,
        )

        Box(
            modifier = Modifier
                .padding(horizontal = paddingXSmall)
                .padding(bottom = paddingXSmall)
                .clip(RoundedCornerShape(radiusXMedium))
                .background(RumbleCustomTheme.colors.subtleHighlight)
        ) {
            VideoCompactView(
                modifier = Modifier.padding(paddingXSmall),
                videoEntity = repost.video,
                showMoreAction = false,
                listToggleViewStyle = ListToggleViewStyle.GRID,
                onViewVideo = onVideoClick
            )
        }
    }
}

@Composable
private fun RepostFeedHeader(
    modifier: Modifier = Modifier,
    repost: RepostEntity,
    onChannelClick: (String) -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    ConstraintLayout(modifier = modifier) {
        val (icon, channelInfo, more, text) = createRefs()

        ProfileImageComponent(
            modifier = Modifier
                .padding(end = paddingXXSmall)
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .clickable { onChannelClick(repost.channel?.id ?: repost.user.id) },
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = repost.channel?.name ?: repost.user.username,
            userPicture = repost.channel?.picture ?: repost.user.thumbnail ?: "",
        )

        Icon(
            modifier = Modifier
                .constrainAs(more) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .clickableNoRipple { onMoreClick() },
            painter = painterResource(id = R.drawable.ic_more),
            contentDescription = stringResource(id = R.string.more),
            tint = MaterialTheme.colors.primary
        )

        Row(
            modifier = Modifier
                .padding(top = if (repost.message.isEmpty()) paddingXSmall else paddingNone)
                .constrainAs(channelInfo) {
                    start.linkTo(icon.end)
                    top.linkTo(parent.top)
                    end.linkTo(more.start)
                    width = Dimension.fillToConstraints
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingXXSmall)
        ) {
            UserNameViewSingleLine(
                name = repost.channel?.name ?: repost.user.username,
                verifiedBadge = repost.channel?.verifiedBadge ?: repost.user.verifiedBadge,
                textStyle = h6,
                spacerWidth = paddingXXXXSmall,
                verifiedBadgeHeight = verifiedBadgeHeightSmall
            )

            Text(
                modifier = Modifier,
                text = repost.creationDate.agoString(LocalContext.current),
                color = RumbleCustomTheme.colors.primary,
                style = h5Medium,
            )
        }

        if (repost.message.isNotBlank()) {
            Text(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .constrainAs(text) {
                        top.linkTo(channelInfo.bottom)
                        start.linkTo(channelInfo.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                text = repost.message,
                color = RumbleCustomTheme.colors.primary,
                style = h5Medium,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val video = VideoEntity(
        id = 0,
        description = "",
        videoThumbnail = "",
        numberOfView = 0,
        url = "",
        channelThumbnail = "",
        channelId = "",
        channelName = "Test channel",
        videoStatus = VideoStatus.STREAMED,
        uploadDate = LocalDateTime.now(),
        scheduledDate = null,
        watchingNow = 0,
        duration = 10000000L,
        title = "Video title",
        commentNumber = 1,
        viewsNumber = 1000,
        likeNumber = 100,
        dislikeNumber = 10,
        userVote = UserVote.LIKE,
        videoSourceList = emptyList(),
        channelFollowers = 0,
        channelFollowed = true,
        channelBlocked = false,
        portraitMode = false,
        videoWidth = 100,
        videoHeight = 100,
        livestreamStatus = LiveStreamStatus.ENDED,
        liveDateTime = null,
        liveStreamedOn = LocalDateTime.now(),
        supportsDvr = false,
        videoLogView = VideoLogView(""),
        commentList = null,
        commentsDisabled = false,
        relatedVideoList = emptyList(),
        tagList = null,
        categoriesList = null,
        verifiedBadge = true,
        ppv = null,
        ageRestricted = false,
        liveChatDisabled = false,
        lastPositionSeconds = 100,
        includeMetadata = true,
        isPremiumExclusiveContent = false,
        subscribedToCurrentChannel = false,
        hasLiveGate = false,
        repostCount = 0,
        userRepost = null,
    )

    val channel = UserEntity()
    val repost = RepostEntity(
        id = 0,
        message = "Check out this amazing new podcast. I need to watch more @Shawn Ryan Show. Joe does not disappoint with this new episode and more text.",
        video = video,
        user = channel,
        channel = null,
        creationDate = LocalDateTime.now(),
    )

    RumbleTheme {
        RepostFeedView(
            repost = repost
        )
    }
}


