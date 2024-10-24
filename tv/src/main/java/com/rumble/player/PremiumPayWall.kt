package com.rumble.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXMedium
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.tvPlayerGradientHeight
import com.rumble.videoplayer.presentation.internal.controlViews.PremiumNoteView
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

            Row(
                modifier = Modifier
                    .padding(horizontal = paddingLarge, vertical = paddingXXMedium)
                    .constrainAs(premiumTag) {
                        top.linkTo(parent.top)
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PremiumTag(modifier = Modifier.padding(bottom = paddingMedium))
                Spacer(modifier = Modifier.weight(1f))
                PremiumNoteView()
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

