package com.rumble.ui3.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.R
import com.rumble.theme.loadingVideoCardChannelInfoHeight
import com.rumble.theme.loadingVideoCardChannelInfoMarginTop
import com.rumble.theme.loadingVideoCardChannelInfoWidth
import com.rumble.theme.loadingVideoCardLikesBarHeight
import com.rumble.theme.loadingVideoCardLikesBarMarginTop
import com.rumble.theme.loadingVideoCardLikesBarWidth
import com.rumble.theme.loadingVideoCardStatsCommentsHeight
import com.rumble.theme.loadingVideoCardStatsCommentsMarginTop
import com.rumble.theme.loadingVideoCardStatsCommentsWidth
import com.rumble.theme.loadingVideoCardStatsViewsHeight
import com.rumble.theme.loadingVideoCardStatsViewsMarginEnd
import com.rumble.theme.loadingVideoCardStatsViewsMarginTop
import com.rumble.theme.loadingVideoCardStatsViewsWidth
import com.rumble.theme.loadingVideoCardTextLine1Height
import com.rumble.theme.loadingVideoCardTextLine1MarginTop
import com.rumble.theme.loadingVideoCardTextLine2Height
import com.rumble.theme.loadingVideoCardTextLine2MarginTop
import com.rumble.theme.loadingVideoCardTextLine3Width
import com.rumble.theme.paddingMedium


@Composable
fun PlaceholderVideoCard() {
    ConstraintLayout(
        modifier = Modifier
            .padding(end = paddingMedium)
            .width(dimensionResource(id = R.dimen.video_card_width))
            .height(dimensionResource(id = R.dimen.video_card_height))
    ) {
        val (icon, textLine1, textLine2, channelInfo, likesBar, statsViews, statsComments) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(dimensionResource(id = R.dimen.video_card_icon_width))
                .height(dimensionResource(id = R.dimen.video_card_icon_height))
                .background(colorResource(id = R.color.white_18_percent), shape = RoundedCornerShape(6.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(textLine1) {
                    top.linkTo(icon.bottom, margin = loadingVideoCardTextLine1MarginTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(dimensionResource(id = R.dimen.video_card_icon_width))
                .height(loadingVideoCardTextLine1Height)
                .background(colorResource(id = R.color.white_18_percent), shape = RoundedCornerShape(6.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(textLine2) {
                    top.linkTo(textLine1.bottom, margin = loadingVideoCardTextLine2MarginTop)
                    start.linkTo(parent.start)
                }
                .width(loadingVideoCardTextLine3Width)
                .height(loadingVideoCardTextLine2Height)
                .background(colorResource(id = R.color.white_18_percent), shape = RoundedCornerShape(6.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(channelInfo) {
                    top.linkTo(textLine2.bottom, margin = loadingVideoCardChannelInfoMarginTop)
                    start.linkTo(parent.start)
                }
                .width(loadingVideoCardChannelInfoWidth)
                .height(loadingVideoCardChannelInfoHeight)
                .background(colorResource(id = R.color.white_10_percent), shape = RoundedCornerShape(8.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(likesBar) {
                    top.linkTo(channelInfo.bottom, margin = loadingVideoCardLikesBarMarginTop)
                    start.linkTo(parent.start)
                }
                .width(loadingVideoCardLikesBarWidth)
                .height(loadingVideoCardLikesBarHeight)
                .background(colorResource(id = R.color.white_10_percent), shape = RoundedCornerShape(8.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(statsViews) {
                    top.linkTo(channelInfo.bottom, margin = loadingVideoCardStatsViewsMarginTop)
                    end.linkTo(statsComments.start, margin = loadingVideoCardStatsViewsMarginEnd)
                }
                .width(loadingVideoCardStatsViewsWidth)
                .height(loadingVideoCardStatsViewsHeight)
                .background(colorResource(id = R.color.white_10_percent), shape = RoundedCornerShape(8.dp))
        )

        Box(
            modifier = Modifier
                .constrainAs(statsComments) {
                    top.linkTo(channelInfo.bottom, margin = loadingVideoCardStatsCommentsMarginTop)
                    end.linkTo(parent.end)
                }
                .width(loadingVideoCardStatsCommentsWidth)
                .height(loadingVideoCardStatsCommentsHeight)
                .background(colorResource(id = R.color.white_10_percent), shape = RoundedCornerShape(8.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVideoCard() {
    PlaceholderVideoCard()
}