package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rumble.battles.commonViews.CategoryChip
import com.rumble.battles.commonViews.TagChip
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoTagListView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall),
        verticalArrangement = Arrangement.spacedBy(paddingXSmall)
    ) {
        videoEntity.ppv?.let {
            PpvTagsView(
                ppvEntity = it,
                textStyle = tinyBodySemiBold,
                radius = radiusSmall,
                withFilledBackground = true
            )
        }

        videoEntity.categoriesList?.forEach { category ->
            CategoryChip(
                text = category.title
            ) {
                onCategoryClick(category.slug)
            }
        }

        videoEntity.tagList?.forEach { tag ->
            TagChip(
                text = tag
            ) {
                onTagClick(tag)
            }
        }
    }
}