package com.rumble.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedFiardHighlight
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.tvPlayerGradientHeight
import com.rumble.videoplayer.presentation.internal.controlViews.PremiumTag
import com.rumble.videoplayer.presentation.internal.views.TvChannelDetailsView

@Composable
fun PremiumPayWall(
    modifier: Modifier,
    videoEntity: VideoEntity,
    onChannelDetails: () -> Unit = {},
) {
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .matchParentSize(),
            model = videoEntity.videoThumbnail,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
        ConstraintLayout(
            modifier = Modifier
                .matchParentSize()
        ) {
            val (gradient, title, premiumTag, channelDetails, premiumNote) = createRefs()

            Box(modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            brandedPlayerBackground.copy(alpha = 0F),
                            brandedPlayerBackground.copy(alpha = 0.9f)
                        ),
                        start = Offset(0.0f, 0.0f),
                        end = Offset(0.0f, Float.POSITIVE_INFINITY)
                    )
                )
                .height(tvPlayerGradientHeight)
                .constrainAs(gradient) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            Column(modifier = Modifier
                .wrapContentSize()
                .background(
                    color = enforcedBlack.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(radiusMedium)
                )
                .border(
                    width = borderXXSmall,
                    color = enforcedFiardHighlight,
                    shape = RoundedCornerShape(radiusMedium)
                )
                .constrainAs(premiumNote) {
                    top.linkTo(parent.top, margin = paddingXXMedium)
                    end.linkTo(parent.end, margin = paddingXLarge)
                }) {

                Text(
                    modifier = Modifier.padding(
                        top = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium
                    ),
                    text = stringResource(id = R.string.premium_only_content),
                    color = enforcedWhite,
                    style = RumbleTypography.tvH3
                )
                Text(
                    modifier = Modifier.padding(
                        top = paddingXSmall,
                        bottom = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium
                    ),
                    text = stringResource(id = R.string.premium_only_content_message),
                    color = enforcedWhite,
                    style = RumbleTypography.labelRegularTv
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = paddingLarge, start = paddingLarge, end = paddingLarge, bottom = paddingXXSmall)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        end.linkTo(premiumNote.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    },
                text = videoEntity.title,
                color = enforcedWhite,
                style = RumbleTypography.tvH2
            )

            PremiumTag(modifier = Modifier
                .constrainAs(premiumTag) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start, margin = paddingLarge)
                }
            )

            val channelFocusRequester = FocusRequester()
            TvChannelDetailsView(
                modifier = Modifier
                    .focusRequester(channelFocusRequester)
                    .constrainAs(channelDetails) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom, margin = paddingMedium)
                    }
                    .padding(start = paddingXMedium),
                isFocused = false,//focusedElement == Focusable.CHANNEL,
                onClick = { onChannelDetails() },
                channelName = videoEntity.channelName,
                channelFollowers = videoEntity.channelFollowers,
                channelIcon = videoEntity.channelThumbnail,
                displayVerifiedBadge = videoEntity.verifiedBadge
            )

            LaunchedEffect(Unit) {
                channelFocusRequester.requestFocus()
            }
        }
    }
}

