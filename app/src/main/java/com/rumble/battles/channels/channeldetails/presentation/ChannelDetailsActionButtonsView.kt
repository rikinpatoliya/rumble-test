package com.rumble.battles.channels.channeldetails.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.ChannelDetailsActionButtonsTag
import com.rumble.battles.commonViews.JoinActionButton
import com.rumble.battles.commonViews.JoinActionData
import com.rumble.battles.commonViews.NotificationActionButton
import com.rumble.battles.commonViews.NotificationActionData
import com.rumble.battles.commonViews.SubscriptionStatusActionButton
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.paddingXSmall

@Composable
fun ChannelDetailsActionButtonsView(
    modifier: Modifier = Modifier,
    followStatus: FollowStatus,
    notificationActionData: NotificationActionData?,
    joinActionData: JoinActionData?,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
    onChannelNotification: (id: String) -> Unit,
    showJoinButton: Boolean = true,
    showUnfollowAction: Boolean = true
) {
    Row(
        modifier = modifier
            .semantics { testTag = ChannelDetailsActionButtonsTag }
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
    ) {
        if (showJoinButton) {
            joinActionData?.let {
                JoinActionButton(joinActionData) { onJoin(it) }
            }
        }

        if ((!showUnfollowAction && (followStatus.isBlocked || followStatus.followed.not())) ||
            showUnfollowAction
        ) {
            SubscriptionStatusActionButton(
                modifier = Modifier
                    .widthIn(min = channelActionsButtonWidth),
                followStatus = followStatus,
                onUpdateSubscription = onUpdateSubscription,
            )
        }

        notificationActionData?.let {
            NotificationActionButton(
                notificationActionData = notificationActionData,
                onClick = onChannelNotification
            )
        }
    }
}


@Composable
@Preview
fun NotificationEnabledPreView() {

    ChannelDetailsActionButtonsView(
        modifier = Modifier.wrapContentWidth(),
        followStatus = FollowStatus("", true),
        notificationActionData = null,
        joinActionData = null,
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
        followStatus = FollowStatus("", false),
        notificationActionData = null,
        joinActionData = null,
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
        followStatus = FollowStatus("", false),
        notificationActionData = null,
        joinActionData = null,
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
        followStatus = FollowStatus("", true),
        notificationActionData = null,
        joinActionData = null,
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
        followStatus = FollowStatus("", false, true),
        notificationActionData = null,
        joinActionData = null,
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
        followStatus = FollowStatus("", true, true),
        notificationActionData = null,
        joinActionData = null,
        {},
        {},
        {}
    )
}
