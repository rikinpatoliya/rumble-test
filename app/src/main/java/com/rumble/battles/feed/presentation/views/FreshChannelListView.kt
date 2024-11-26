package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.common.borderColorFreshContent
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannel
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional

@Composable
fun FreshChannelListView(
    modifier: Modifier = Modifier,
    freshChannels: List<FreshChannel>,
    numberOfColumns: Int,
    onFreshContentChannelClick: (id: String) -> Unit,
    onPlusChannelsClick: () -> Unit
) {
    if (numberOfColumns < RumbleConstants.HOME_SCREEN_ROWS_3) {
        LazyRow(
            modifier = modifier
                .padding(vertical = paddingXSmall)
                .conditional(freshChannels.isNotEmpty()) { fillMaxWidth() },
            contentPadding = PaddingValues(horizontal = paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FreshChannelPlusButton(onClick = onPlusChannelsClick)
            }

            if (freshChannels.isEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.follow_new_channels),
                        style = h4
                    )
                }
            } else {
                items(
                    count = freshChannels.count()
                ) {
                    val freshChannel = freshChannels[it]
                    ProfileImageComponent(
                        modifier = Modifier
                            .clickableNoRipple {
                                onFreshContentChannelClick(freshChannel.channelDetailsEntity.channelId)
                            },
                        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageLargeStyle(
                            borderColor = borderColorFreshContent(
                                freshChannel.freshContent,
                                freshChannel.channelDetailsEntity.latestVideo?.videoStatus
                            )
                        ),
                        userName = freshChannel.channelDetailsEntity.channelTitle,
                        userPicture = freshChannel.channelDetailsEntity.thumbnail
                    )
                }
            }
        }
    } else {
        Row(modifier = modifier) {
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(borderXXSmall),
                color = MaterialTheme.colors.secondaryVariant
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = paddingLarge),
                verticalArrangement = Arrangement.spacedBy(paddingXSmall),
            ) {
                item {
                    Spacer(modifier = Modifier.height(paddingLarge))
                }
                item {
                    FreshChannelPlusButton(onClick = onPlusChannelsClick)
                }
                items(
                    count = freshChannels.count()
                ) {
                    val freshChannel = freshChannels[it]
                    ProfileImageComponent(
                        modifier = Modifier
                            .clickableNoRipple {
                                onFreshContentChannelClick(freshChannel.channelDetailsEntity.channelId)
                            },
                        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageLargeStyle(
                            borderColor = borderColorFreshContent(
                                freshChannel.freshContent,
                                freshChannel.channelDetailsEntity.latestVideo?.videoStatus
                            )
                        ),
                        userName = freshChannel.channelDetailsEntity.channelTitle,
                        userPicture = freshChannel.channelDetailsEntity.thumbnail
                    )
                }
            }
        }
    }
}

