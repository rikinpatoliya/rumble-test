package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.rumble.battles.*
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.*
import com.rumble.utils.extension.ignoreHorizontalParentPadding

@Composable
fun LiveNowList(
    modifier: Modifier,
    liveNowVideos: List<VideoEntity>,
    loading: Boolean,
    error: Boolean,
    onVideoClick: (VideoEntity) -> Unit,
    onChannelClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onViewAll: () -> Unit
) {

    Column(modifier = modifier.semantics { testTag = SimpleVideoViewTag }) {
        Row(
            modifier = Modifier
                .padding(bottom = paddingXSmall),
            verticalAlignment = CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(liveCircleSize)
                    .align(CenterVertically),
                painter = painterResource(id = R.drawable.ic_live_circle),
                contentDescription = stringResource(id = R.string.live_now),
                tint = fierceRed
            )
            Text(
                modifier = Modifier
                    .padding(start = paddingXSmall),
                text = stringResource(id = R.string.live_now),
                style = RumbleTypography.h1
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
                    .fillMaxWidth()
                    .height(defaultDiscoverContentLoadingHeight)
                    .semantics { testTag = LiveNowLoadingTag }
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultDiscoverContentLoadingHeight)
                    .semantics { testTag = LiveNowErrorTag },
                onRetry = onRefresh
            )
        } else {
            LazyRow(
                modifier = Modifier
                    .semantics { testTag = LiveNowRowTag }
                    .ignoreHorizontalParentPadding(paddingMedium),
                contentPadding = PaddingValues(horizontal = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                items(liveNowVideos) {
                    SimpleVideoView(
                        videoThumbnail = it.videoThumbnail,
                        channelThumbnail = it.channelThumbnail,
                        videoStatus = it.videoStatus,
                        ppv = it.ppv,
                        viewCount = it.watchingNow,
                        videoTitle = it.title,
                        channelTitle = it.channelName,
                        onVideoClick = { onVideoClick(it) },
                        onProfileClick = { onChannelClick(it.channelId) }
                    )
                }
            }
        }

    }

}




