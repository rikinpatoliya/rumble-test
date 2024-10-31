package com.rumble.battles.channels.channeldetails.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.ChannelDetailsActionButtonsTag
import com.rumble.battles.R
import com.rumble.battles.common.getNotificationIcon
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.SubscriptionStatusActionButton
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun ChannelDetailsActionButtonsView(
    modifier: Modifier = Modifier,
    channelDetailsEntity: ChannelDetailsEntity? = null,
    followStatus: FollowStatus,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
    onChannelNotification: (id: String) -> Unit,
    showDrawable: Boolean = true,
    showJoinButton: Boolean = true,
) {
    Row(
        modifier = modifier
            .semantics { testTag = ChannelDetailsActionButtonsTag }
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        channelDetailsEntity?.localsCommunityEntity?.let {
            if (showJoinButton) {
                ActionButton(
                    modifier = Modifier
                        .width(channelActionsButtonWidth)
                        .padding(start = paddingXXXSmall),
                    text = stringResource(id = R.string.join),
                    backgroundColor = brandedLocalsRed,
                    borderColor = brandedLocalsRed,
                    textColor = enforcedWhite
                ) { onJoin(it) }
            }
        }
        SubscriptionStatusActionButton(
            modifier = Modifier
                .widthIn(min = channelActionsButtonWidth)
                .padding(start = paddingXXXSmall),
            followStatus = followStatus,
            onUpdateSubscription = onUpdateSubscription,
        )
        if (showDrawable && channelDetailsEntity != null) {
            Box(
                modifier = Modifier
                    .padding(start = paddingXSmall)
                    .clip(CircleShape)
                    .background(if (followStatus.followed) rumbleGreen else MaterialTheme.colors.secondaryVariant)
                    .clickable(followStatus.followed) {
                        onChannelNotification.invoke(
                            followStatus.channelId
                        )
                    },
            ) {
                Image(
                    modifier = Modifier
                        .padding(paddingXSmall)
                        .size(imageXXSmall),
                    painter = painterResource(
                        id = if (followStatus.followed) getNotificationIcon(
                            channelDetailsEntity
                        ) else R.drawable.ic_notifications_off
                    ),
                    contentDescription = stringResource(id = R.string.notifications),
                    colorFilter = ColorFilter.tint(if (followStatus.followed) enforcedWhite else MaterialTheme.colors.primary)
                )
            }
        }
    }
}

@Composable
@Preview
fun NotificationEnabledPreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", true),
        {},
        {},
        {}
    )
}

@Composable
@Preview
fun NotificationDisabledPreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", false),
        {},
        {},
        {}
    )
}

@Composable
@Preview
fun FollowButtonStatePreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", false),
        {},
        {},
        {}
    )
}

@Composable
@Preview
fun UnFollowButtonStatePreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", true),
        {},
        {},
        {}
    )
}

@Composable
@Preview
fun BlockButtonStatePreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", false, true),
        {},
        {},
        {}
    )
}

@Composable
@Preview
fun BlockButtonStateFollowingPreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        channelDetailsEntity = null,
        followStatus = FollowStatus("", true, true),
        {},
        {},
        {}
    )
}
