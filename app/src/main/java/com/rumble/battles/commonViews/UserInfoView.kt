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
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
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
    followers: Int = 0,
    followStatus: FollowStatus? = null,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
    onChannelClick: (String) -> Unit,
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
                onJoin = {},
                onUpdateSubscription = onUpdateSubscription,
                onChannelNotification = {},
                showDrawable = false,
            )
        }
    }
}