package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.R
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsActionButtonsView
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsUIState
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.titleOffset
import com.rumble.utils.extension.shortString

@Composable
internal fun ChannelDetailsHeader(
    modifier: Modifier = Modifier,
    currentDestinationRoute: String?,
    state: ChannelDetailsUIState,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit = {},
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit = {},
    onChannelNotification: (id: String) -> Unit = {},
) {
    Column(modifier = modifier) {
        Spacer(
            Modifier
                .height(titleOffset)
        )
        Column(modifier = modifier.background(color = MaterialTheme.colors.background)) {
            UserNameView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingXSmall, start = paddingXSmall, end = paddingXSmall),
                name = state.channelDetailsEntity?.channelTitle ?: "",
                verifiedBadge = state.channelDetailsEntity?.verifiedBadge ?: false,
                textStyle = h1,
                textAlign = TextAlign.Center
            )
            if (currentDestinationRoute == RumbleScreens.Videos.rootName
                || state.channelDetailsEntity?.type == ChannelType.USER
            ) {
                UserChannelStats(
                    modifier = modifier,
                    state = state,
                )
            } else if (state.channelDetailsEntity?.type == ChannelType.CHANNEL) {
                Spacer(modifier.height(paddingXXXSmall))
                Text(
                    text = "${state.channelDetailsEntity.followers.shortString()} ${
                        pluralStringResource(
                            id = R.plurals.followers, state.channelDetailsEntity.followers
                        )
                    }",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.secondary,
                    style = RumbleTypography.h6Light,
                    textAlign = TextAlign.Center
                )
            }
            if (currentDestinationRoute == RumbleScreens.Channel.rootName && state.channelDetailsEntity != null) {
                ChannelActionButtons(
                    modifier = modifier,
                    channelDetailsEntity = state.channelDetailsEntity,
                    onJoin = onJoin,
                    onUpdateSubscription = onUpdateSubscription,
                    onChannelNotification = onChannelNotification
                )
            }
            Spacer(
                modifier
                    .height(paddingMedium)
            )
            Divider(
                modifier = modifier,
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}

@Composable
fun VideosCountView(
    state: ChannelDetailsUIState,
    modifier: Modifier,
    videoListToggleViewStyle: ListToggleViewStyle,
    onToggleViewStyle: (listToggleViewStyle: ListToggleViewStyle) -> Unit
) {
    val videoCount = state.channelDetailsEntity?.videoCount
    if (videoCount != null && videoCount > 0) {
        Spacer(
            modifier
                .height(paddingXLarge)
        )
        VideosRow(
            modifier = modifier,
            videoCount = videoCount,
            selectedViewStyle = videoListToggleViewStyle,
            onToggleViewStyle = onToggleViewStyle
        )
    }
}

@Composable
private fun UserChannelStats(
    modifier: Modifier = Modifier,
    state: ChannelDetailsUIState
) {
    Spacer(modifier.height(paddingMedium))
    FollowingContentView(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = paddingMedium, end = paddingMedium),
        likes = state.channelDetailsEntity?.rumbles ?: 0,
        followers = state.channelDetailsEntity?.followers ?: 0,
        following = state.channelDetailsEntity?.following ?: 0
    )
}

@Composable
private fun ChannelActionButtons(
    modifier: Modifier = Modifier,
    channelDetailsEntity: ChannelDetailsEntity,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit,
    onChannelNotification: (id: String) -> Unit,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(paddingSmall))
        ChannelDetailsActionButtonsView(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally),
            channelDetailsEntity = channelDetailsEntity,
            followStatus = FollowStatus(
                channelId = channelDetailsEntity.channelId,
                followed = channelDetailsEntity.followed,
                isBlocked = channelDetailsEntity.blocked
            ),
            onJoin = onJoin,
            onUpdateSubscription = onUpdateSubscription,
            onChannelNotification = onChannelNotification,
        )
    }
}

@Composable
private fun VideosRow(
    modifier: Modifier = Modifier,
    videoCount: Int,
    selectedViewStyle: ListToggleViewStyle,
    onToggleViewStyle: (listToggleViewStyle: ListToggleViewStyle) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(start = paddingXLarge, end = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithBoxedCount(
            title = stringResource(id = R.string.videos),
            count = "$videoCount"
        )
        Spacer(modifier = Modifier.weight(1f))
        ListToggleView(
            modifier = Modifier
                .padding(end = paddingMedium),
            selectedViewStyle = selectedViewStyle,
        ) {
            onToggleViewStyle(it)
        }
    }
}
