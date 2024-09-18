package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.rumble.battles.FeaturedChannelsErrorTag
import com.rumble.battles.FeaturedChannelsLoadingTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.*
import com.rumble.utils.extension.ignoreHorizontalParentPadding

@Composable
fun TopChannelsView(
    modifier: Modifier,
    featuredChannels: List<ChannelDetailsEntity>,
    loading: Boolean,
    error: Boolean,
    onChannelClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(bottom = paddingSmall),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(id = R.string.top_channels).uppercase(),
                style = RumbleTypography.h4
            )
            Spacer(modifier = Modifier.weight(1f))
            RumbleTextActionButton(
                text = stringResource(id = R.string.view_all),
            ) {
                onViewAll()
            }
        }

        if (loading) {
            LoadingView(
                modifier = Modifier
                    .semantics { testTag = FeaturedChannelsLoadingTag }
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth()
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .semantics { testTag = FeaturedChannelsErrorTag }
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth(),
                onRetry = onRefresh
            )
        } else {
            LazyRow(
                modifier = Modifier
                    .ignoreHorizontalParentPadding(paddingMedium),
                contentPadding = PaddingValues(horizontal = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                items(featuredChannels) {
                    SimpleVideoView(
                        videoThumbnail = it.backSplash,
                        channelThumbnail = it.thumbnail,
                        videoStatus = VideoStatus.UPLOADED,
                        viewCount = it.followers.toLong(),
                        videoTitle = it.channelTitle,
                        channelTitle = it.channelTitle,
                        onVideoClick = { onChannelClick(it.channelId) },
                        onProfileClick = { onChannelClick(it.channelId) },
                        useChannelPlaceholderBackground = true
                    )
                }
            }
        }

    }
}