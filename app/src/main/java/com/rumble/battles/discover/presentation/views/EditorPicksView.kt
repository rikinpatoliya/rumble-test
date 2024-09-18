package com.rumble.battles.discover.presentation.views

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
import com.rumble.battles.EditorPicksContentTag
import com.rumble.battles.EditorPicksErrorTag
import com.rumble.battles.EditorPicksLoadingTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.editorPicksPlaceholderHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall

@Composable
fun EditorPicksView(
    modifier: Modifier,
    editorPicks: List<VideoEntity>,
    loading: Boolean,
    error: Boolean,
    onVideoClick: (VideoEntity) -> Unit,
    onMoreClick: (VideoEntity) -> Unit,
    onRefresh: () -> Unit,
    onViewCategory: () -> Unit,
    onImpression: (VideoEntity) -> Unit
) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(bottom = paddingSmall)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.editor_picks).uppercase(),
                style = RumbleTypography.h4
            )
            Spacer(modifier = Modifier.weight(1f))
            RumbleTextActionButton(
                text = stringResource(id = R.string.view_all),
            ) {
                onViewCategory()
            }
        }

        if (loading) {
            LoadingView(
                modifier = Modifier
                    .semantics { testTag = EditorPicksLoadingTag }
                    .fillMaxWidth()
                    .height(editorPicksPlaceholderHeight)
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .semantics { testTag = EditorPicksErrorTag }
                    .fillMaxWidth()
                    .height(editorPicksPlaceholderHeight),
                onRetry = onRefresh
            )
        } else {
            Column(modifier = Modifier
                .semantics { testTag = EditorPicksContentTag }) {
                editorPicks.forEach { videoEntity ->
                    VideoCompactView(
                        modifier = Modifier.padding(bottom = paddingSmall),
                        videoEntity = videoEntity,
                        onViewVideo = { onVideoClick(videoEntity) },
                        onMoreClick = onMoreClick,
                        onImpression = onImpression
                    )
                }
            }
        }
    }

}

