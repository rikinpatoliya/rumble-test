package com.rumble.battles.library.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.VideoCompactLoadingView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.library.presentation.library.LibraryScreenSection
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageXXSmall
import com.rumble.theme.minDefaultEmptyViewHeight
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.utils.RumbleConstants.LIBRARY_SHORT_LIST_SIZE
import com.rumble.utils.extension.conditional

@Composable
fun LibrarySectionView(
    modifier: Modifier,
    libraryScreenSection: LibraryScreenSection,
    videoEntityList: List<VideoEntity>,
    loading: Boolean,
    error: Boolean,
    onVideoClick: (VideoEntity) -> Unit,
    onMoreClick: (VideoEntity) -> Unit,
    onRefresh: () -> Unit,
    onViewAll: (LibraryScreenSection) -> Unit,
    onImpression: (VideoEntity) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = libraryScreenSection.sectionIconId),
                contentDescription = stringResource(id = libraryScreenSection.sectionTitleId),
                modifier = Modifier
                    .padding(end = paddingXSmall)
                    .size(imageXXSmall),
                tint = MaterialTheme.colors.primary
            )
            Text(
                text = stringResource(id = libraryScreenSection.sectionTitleId),
                style = RumbleTypography.h3,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            RumbleTextActionButton(
                text = stringResource(id = R.string.view_all),
            ) {
                onViewAll(libraryScreenSection)
            }
        }

        if (loading) {
            Column(
                modifier = Modifier.padding(vertical = paddingLarge)
            ) {
                repeat(LIBRARY_SHORT_LIST_SIZE) {
                    VideoCompactLoadingView(
                        modifier = Modifier.conditional(it + 1 != LIBRARY_SHORT_LIST_SIZE) {
                            this.padding(bottom = paddingSmall)
                        }
                    )
                }
            }
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minDefaultEmptyViewHeight),
                backgroundColor = Color.Transparent,
                onRetry = onRefresh
            )
        } else if (videoEntityList.isEmpty()) {
            val text = if (libraryScreenSection.sectionEmptyTextId != null)
                stringResource(id = libraryScreenSection.sectionEmptyTextId) else ""
            Text(
                text = text,
                modifier = Modifier.padding(vertical = paddingLarge),
                style = RumbleTypography.smallBody,
                color = MaterialTheme.colors.secondary
            )
        } else {
            Column(
                modifier = Modifier.padding(vertical = paddingLarge)
            ) {
                videoEntityList.forEachIndexed { index, videoEntity ->
                    VideoCompactView(
                        modifier = Modifier.conditional(index != videoEntityList.lastIndex) {
                            this.padding(bottom = paddingSmall)
                        },
                        videoEntity = videoEntity,
                        onViewVideo = { onVideoClick(videoEntity) },
                        onMoreClick = onMoreClick,
                        onImpression = onImpression
                    )
                }
            }
        }
        if (libraryScreenSection != LibraryScreenSection.LikedVideos) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}