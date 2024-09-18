package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.rumbleGreen

@Composable
fun SubscriptionStatusActionButton(
    modifier: Modifier = Modifier,
    followStatus: FollowStatus,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
) {
    ActionButton(
        modifier = modifier,
        text = getFollowButtonText(followStatus),
        backgroundColor = getFollowButtonColor(followStatus),
        borderColor = getFollowButtonBorderColor(followStatus),
        textColor = getFollowButtonTextColor(followStatus)
    ) { onUpdateSubscription(followStatus.updateAction()) }
}

@Composable
private fun getFollowButtonColor(followStatus: FollowStatus): Color {
    return when {
        followStatus.isBlocked -> fierceRed
        followStatus.followed -> Color.Transparent
        else -> rumbleGreen
    }
}

@Composable
private fun getFollowButtonTextColor(followStatus: FollowStatus): Color {
    return when {
        followStatus.isBlocked -> enforcedWhite
        followStatus.followed -> MaterialTheme.colors.primary
        else -> enforcedDarkmo
    }
}

@Composable
private fun getFollowButtonBorderColor(followStatus: FollowStatus): Color {
    return when {
        followStatus.isBlocked -> fierceRed
        followStatus.followed -> rumbleGreen
        else -> Color.Transparent
    }
}

@Composable
private fun getFollowButtonText(followStatus: FollowStatus): String {
    val id = when {
        followStatus.isBlocked -> R.string.unblock
        followStatus.followed -> R.string.unfollow
        else -> R.string.follow
    }
    return stringResource(id = id)
}