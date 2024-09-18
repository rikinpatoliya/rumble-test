package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.rumble.battles.PopularVideosContentTag
import com.rumble.battles.PopularVideosErrorTag
import com.rumble.battles.PopularVideosLoadingTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.defaultDiscoverContentLoadingHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall

@Composable
fun PopularVideosView(
    modifier: Modifier,
    popularVideos: List<VideoEntity>,
    loading: Boolean,
    error: Boolean,
    onVideoClick: (VideoEntity) -> Unit,
    onMoreClick: (VideoEntity) -> Unit,
    onRefresh: () -> Unit,
    onViewAll: () -> Unit,
    onImpression: (VideoEntity) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.popular_videos).uppercase(),
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
                    .semantics { testTag = PopularVideosLoadingTag }
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth()
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .semantics { testTag = PopularVideosErrorTag }
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth(),
                onRetry = onRefresh
            )
        } else {
            Column(
                modifier = Modifier.semantics { testTag = PopularVideosContentTag },
                verticalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                popularVideos.forEachIndexed { _, videoEntity ->
                    VideoCompactView(
                        videoEntity = videoEntity,
                        onViewVideo = onVideoClick,
                        onMoreClick = onMoreClick,
                        onImpression = onImpression
                    )
                }
            }
        }
    }
}