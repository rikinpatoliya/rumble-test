package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.PillView
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.network.queryHelpers.VideoCollectionId
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun VideoCollectionSelectorView(
    modifier: Modifier = Modifier,
    videoCollections: List<VideoCollectionType>,
    selectedCollection: VideoCollectionType?,
    onCollectionClick: (VideoCollectionType) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
    ) {
        items(videoCollections) {
            when (it) {
                VideoCollectionType.MyFeed -> {
                    PillView(
                        modifier = Modifier.clickableNoRipple { onCollectionClick(it) },
                        text = stringResource(id = R.string.home_category_my_feed),
                        selected = it == selectedCollection
                    )
                }
                VideoCollectionType.Reposts -> {
                    PillView(
                        modifier = Modifier.clickableNoRipple { onCollectionClick(it) },
                        text = stringResource(id = R.string.home_category_reposts),
                        selected = it == selectedCollection
                    )
                }
                is VideoCollectionType.VideoCollectionEntity -> {
                    if (it.id == VideoCollectionId.Live.value) {
                        PillView(
                            modifier = Modifier.clickableNoRipple { onCollectionClick(it) },
                            text = it.name,
                            selected = it == selectedCollection,
                            color = fierceRed,
                            textColor = enforcedWhite,
                            live = true,
                        )
                    } else {
                        PillView(
                            modifier = Modifier.clickableNoRipple { onCollectionClick(it) },
                            text = it.name,
                            selected = it == selectedCollection
                        )
                    }
                }
            }
        }
    }
}