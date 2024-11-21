package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsActionButtonsView
import com.rumble.battles.feed.presentation.views.FollowerNumberView
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun UserInfoView(
    modifier: Modifier = Modifier,
    channelName: String,
    channelThumbnail: String,
    channelId: String?,
    verifiedBadge: Boolean,
    showJoinButton: Boolean,
    followers: Int = 0,
    channelDetailsEntity: ChannelDetailsEntity? = null,
    followStatus: FollowStatus? = null,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
    onChannelClick: (String) -> Unit,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit = {},
    onChannelNotifications: (ChannelDetailsEntity) -> Unit = {},
) {
    Row(
        modifier = modifier
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImageComponent(
            modifier = Modifier.clickableNoRipple {
                channelId?.let {
                    onChannelClick(it)
                }
            },
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = channelName,
            userPicture = channelThumbnail,
        )
        Column(
            modifier = Modifier
                .clickable {
                    channelId?.let {
                        onChannelClick(it)
                    }
                }
                .padding(start = paddingXSmall, end = paddingXSmall)
                .weight(1f)
        ) {
            UserNameView(
                modifier = Modifier
                    .fillMaxWidth(),
                name = channelName,
                verifiedBadge = verifiedBadge,
                textStyle = RumbleTypography.h4,
            )

            FollowerNumberView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingXXXXSmall),
                followers = followers
            )
        }
        followStatus?.let { followStatus ->
            ChannelDetailsActionButtonsView(
                followStatus = followStatus,
                notificationActionData = channelDetailsEntity?.let {
                    NotificationActionData(
                        channelDetailsEntity = it,
                        notificationActionType = NotificationActionType.WITH_DROPDOWN
                    )
                },
                joinActionData = channelDetailsEntity?.localsCommunityEntity?.let {
                    JoinActionData(
                        localsCommunityEntity = it,
                        joinActionType = if (followStatus.followed) {
                            JoinActionType.WITH_TEXT
                        } else {
                            JoinActionType.SHOW_AS_STAR
                        }
                    )
                },
                onJoin = onJoin,
                onUpdateSubscription = onUpdateSubscription,
                onChannelNotification = {
                    channelDetailsEntity?.let {
                        onChannelNotifications(it)
                    }
                },
                showJoinButton = showJoinButton,
                showUnfollowAction = false
            )
        }
    }
}