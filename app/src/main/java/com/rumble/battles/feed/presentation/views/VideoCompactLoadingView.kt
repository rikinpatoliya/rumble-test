package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.commonViews.IsTablet
import com.rumble.theme.compactVideoHeight
import com.rumble.theme.compactVideoWidth
import com.rumble.theme.imageXXMini
import com.rumble.theme.paddingSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.conditional

private const val HEIGHT_DIVIDER = 5

@Composable
fun VideoCompactLoadingView(
    modifier: Modifier = Modifier,
) {
    val isTablet = IsTablet()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (videoThumb, item1, item2, item3, item4, item5, midSpacer) = createRefs()
        val tabletThumbnailEnd = createGuidelineFromStart(0.35f)
        var height by remember { mutableIntStateOf(0) }
        val density = LocalContext.current.resources.displayMetrics.density

        Box(
            modifier = Modifier
                .constrainAs(videoThumb) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    if (isTablet) {
                        end.linkTo(tabletThumbnailEnd)
                        width = Dimension.fillToConstraints
                    }
                }
                .conditional(isTablet) {
                    this.aspectRatio(
                        ratio = RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION,
                        matchHeightConstraintsFirst = false
                    )
                }
                .conditional(!isTablet) {
                    this
                        .height(compactVideoHeight)
                        .width(compactVideoWidth)
                }
                .clip(RoundedCornerShape(radiusSmall))
                .background(MaterialTheme.colors.secondaryVariant)
                .onGloballyPositioned {
                    height = it.size.height
                }
        )
        Box(modifier = Modifier
            .constrainAs(item1) {
                start.linkTo(videoThumb.end)
                top.linkTo(videoThumb.top)
            }
            .padding(start = paddingSmall)
            .height((height / HEIGHT_DIVIDER / density).dp)
            .fillMaxWidth(0.2F)
            .clip(RoundedCornerShape(radiusMedium))
            .background(color = MaterialTheme.colors.secondaryVariant))
        Spacer(modifier = Modifier
            .constrainAs(midSpacer) {
                start.linkTo(videoThumb.end)
                top.linkTo(videoThumb.top)
                bottom.linkTo(videoThumb.bottom)
            }
            .padding(start = paddingSmall)
            .height(imageXXMini)
            .fillMaxWidth()
        )
        Box(modifier = Modifier
            .constrainAs(item2) {
                start.linkTo(videoThumb.end)
                end.linkTo(parent.end)
                bottom.linkTo(midSpacer.top)
                width = Dimension.fillToConstraints
            }
            .padding(start = paddingSmall)
            .height((height / HEIGHT_DIVIDER / density).dp)
            .clip(RoundedCornerShape(radiusMedium))
            .background(color = MaterialTheme.colors.secondaryVariant))
        Box(modifier = Modifier
            .constrainAs(item3) {
                start.linkTo(videoThumb.end)
                end.linkTo(parent.end)
                top.linkTo(midSpacer.bottom)
                width = Dimension.fillToConstraints
            }
            .padding(start = paddingSmall)
            .height((height / HEIGHT_DIVIDER / density).dp)
            .clip(RoundedCornerShape(radiusMedium))
            .background(color = MaterialTheme.colors.secondaryVariant))
        Box(modifier = Modifier
            .constrainAs(item4) {
                start.linkTo(videoThumb.end)
                bottom.linkTo(videoThumb.bottom)
            }
            .padding(start = paddingSmall)
            .height((height / HEIGHT_DIVIDER / density).dp)
            .fillMaxWidth(0.3F)
            .clip(RoundedCornerShape(radiusMedium))
            .background(color = MaterialTheme.colors.secondaryVariant))
        Box(modifier = Modifier
            .constrainAs(item5) {
                end.linkTo(parent.end)
                bottom.linkTo(videoThumb.bottom)
            }
            .height((height / HEIGHT_DIVIDER / density).dp)
            .fillMaxWidth(0.15F)
            .clip(RoundedCornerShape(radiusMedium))
            .background(color = MaterialTheme.colors.secondaryVariant))
    }
}

@Composable
@Preview
private fun PreviewVideoCompactLoadingView() {
    VideoCompactLoadingView()
}