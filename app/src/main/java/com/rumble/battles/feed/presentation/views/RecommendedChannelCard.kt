package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.shortString

@Composable
fun RecommendedChannelCard(
    modifier: Modifier = Modifier,
    channel: ChannelDetailsEntity,
    onChannelClick: (id: String) -> Unit,
    onSubscriptionUpdate: (channel: ChannelDetailsEntity, action: UpdateChannelSubscriptionAction) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(radiusXMedium)
            )
            .clickableNoRipple { onChannelClick(channel.channelId) }
            .padding(paddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileImageComponent(
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
            userName = channel.channelTitle,
            userPicture = channel.thumbnail
        )

        UserNameViewSingleLine(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingXSmall),
            name = channel.channelTitle,
            verifiedBadge = channel.verifiedBadge,
            textStyle = RumbleTypography.h6,
            textColor = MaterialTheme.colors.primary,
            spacerWidth = paddingXXXSmall,
            verifiedBadgeHeight = verifiedBadgeHeightSmall,
            horizontalArrangement = Arrangement.Center
        )

        Text(
            modifier = Modifier
                .padding(bottom = paddingLarge),
            text = "${channel.followers.shortString()} ${
                pluralStringResource(
                    id = R.plurals.followers, channel.followers
                ).lowercase()
            }",
            style = RumbleTypography.h6,
            color = wokeGreen
        )
        ActionButton(
            text = if (channel.followed) stringResource(id = R.string.unfollow) else stringResource(
                id = R.string.follow
            ),
            backgroundColor = if (channel.followed) MaterialTheme.colors.onSurface else rumbleGreen,
            textColor = if (channel.followed) MaterialTheme.colors.primary else enforcedDarkmo,
            borderColor = rumbleGreen
        ) {
            onSubscriptionUpdate(
                channel,
                if (channel.followed) UpdateChannelSubscriptionAction.UNSUBSCRIBE else UpdateChannelSubscriptionAction.SUBSCRIBE
            )
        }
    }
}