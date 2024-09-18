package com.rumble.battles.commonViews

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.rumble.theme.borderSmall
import com.rumble.theme.borderWidth
import com.rumble.theme.borderXSmall
import com.rumble.theme.borderXXSmall
import com.rumble.theme.collapsedImageSize
import com.rumble.theme.collapsingImageVerticalPadding
import com.rumble.theme.imageXXLarge
import com.rumble.theme.maxTitleOffset
import com.rumble.theme.minImageOffset
import com.rumble.theme.minTitleOffset
import com.rumble.utils.RumbleConstants.PROFILE_IMAGE_BITMAP_MAX_WIDTH
import com.rumble.utils.extension.scaleToMaxWidth
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun ChannelDetailsCollapsingImage(
    modifier: Modifier = Modifier,
    channelName: String,
    imageUrl: String,
    bitmap: Bitmap? = null,
    scrollState: LazyListState,
) {
    val collapseRange = with(LocalDensity.current) { (maxTitleOffset - minTitleOffset).toPx() }
    val collapseFractionProvider = {
        if (scrollState.firstVisibleItemIndex == 0)
            (scrollState.firstVisibleItemScrollOffset / collapseRange).coerceIn(0f, 1f)
        else
            (1000F / collapseRange).coerceIn(0f, 1f)
    }
    val borderWidthProvider = {
        if (scrollState.firstVisibleItemIndex > 0)
            borderXXSmall
        else when (scrollState.firstVisibleItemScrollOffset) {
            in 0..50 -> borderWidth
            in 50..100 -> borderSmall
            in 100..150 -> borderXSmall
            in 150..200 -> borderXXSmall
            else -> borderXXSmall
        }
    }

    Column(modifier = modifier) {
        CollapsingImageLayout(
            collapseFractionProvider = collapseFractionProvider,
            modifier = Modifier.padding(vertical = collapsingImageVerticalPadding)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        borderWidthProvider.invoke(),
                        color = MaterialTheme.colors.background,
                        shape = CircleShape
                    )
            ) {
                bitmap?.let {
                    AsyncImage(
                        modifier = ProfileImageComponentStyle.CircleImageXXLargeStyle().modifier
                            .then(Modifier.padding(borderWidthProvider.invoke())),
                        model = bitmap.scaleToMaxWidth(PROFILE_IMAGE_BITMAP_MAX_WIDTH),
                        contentDescription = channelName
                    )
                } ?: run {
                    ProfileImageComponent(
                        modifier = Modifier.padding(borderWidthProvider.invoke()),
                        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
                        userName = channelName,
                        userPicture = imageUrl
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFractionProvider: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        check(measurables.size == 1)

        val collapseFraction = collapseFractionProvider()

        val imageMaxSize = min(imageXXLarge.roundToPx(), constraints.maxWidth)
        val imageMinSize = max(collapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(minTitleOffset, minImageOffset, collapseFraction).roundToPx()
        val imageX = (constraints.maxWidth - imageWidth) / 2 // centered
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(imageX, imageY)
        }
    }
}