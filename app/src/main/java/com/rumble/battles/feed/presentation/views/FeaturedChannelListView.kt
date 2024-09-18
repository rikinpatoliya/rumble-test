package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelsHandler
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.imageWidthXXLarge
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun FeaturedChannelListView(
    modifier: Modifier = Modifier,
    contentHandler: ContentHandler,
    recommendedChannelsHandler: RecommendedChannelsHandler,
    onChannelClick: (id: String) -> Unit,
    onViewAllClick: () -> Unit
) {
    val channelPagingItems: LazyPagingItems<ChannelDetailsEntity> =
        recommendedChannelsHandler.channels.collectAsLazyPagingItems()

    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.onPrimary)
                .padding(bottom = paddingLarge)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingLarge,
                        start = paddingMedium,
                        end = paddingMedium,
                        bottom = paddingMedium
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.may_we_recommend_channels),
                    style = h4
                )
                RumbleTextActionButton(
                    text = stringResource(id = R.string.view_all),
                    textStyle = h6Light
                ) {
                    onViewAllClick()
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                items(count = channelPagingItems.itemCount) { index ->
                    channelPagingItems[index]?.let { channel ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ProfileImageComponent(
                                modifier = Modifier.clickableNoRipple {
                                    onChannelClick(channel.channelId)
                                },
                                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
                                userName = channel.channelTitle,
                                userPicture = channel.thumbnail
                            )

                            UserNameViewSingleLine(
                                modifier = Modifier
                                    .width(imageWidthXXLarge)
                                    .padding(vertical = paddingXSmall),
                                name = channel.channelTitle,
                                verifiedBadge = channel.verifiedBadge,
                                textStyle = h6,
                                spacerWidth = paddingXXXSmall,
                                verifiedBadgeHeight = verifiedBadgeHeightSmall,
                                horizontalArrangement = Arrangement.Center
                            )

                            ActionButton(
                                text = if (channel.followed) stringResource(id = R.string.unfollow) else stringResource(
                                    id = R.string.follow
                                ),
                                backgroundColor = if (channel.followed) MaterialTheme.colors.background else rumbleGreen,
                                textColor = if (channel.followed) MaterialTheme.colors.primary else enforcedDarkmo,
                                borderColor = rumbleGreen
                            ) {
                                contentHandler.onUpdateSubscription(
                                    channel,
                                    if (channel.followed) {
                                        UpdateChannelSubscriptionAction.UNSUBSCRIBE
                                    } else {
                                        UpdateChannelSubscriptionAction.SUBSCRIBE
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}