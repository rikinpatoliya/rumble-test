package com.rumble.videoplayer.presentation.internal.controlViews

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.barCompactHeight
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.radiusSmall
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumbleLiveStreamStatus
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.presentation.internal.defaults.playerBottomGuideline
import com.rumble.videoplayer.presentation.internal.views.TvChannelDetailsView
import java.time.LocalDateTime

@Composable
fun TvErrorView(
    modifier: Modifier = Modifier,
    rumbleVideo: RumbleVideo,
    onChannelDetailsClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, progress, channel, gradient, error) = createRefs()

        AsyncImage(
            modifier = Modifier
                .background(brandedPlayerBackground)
                .fillMaxSize(),
            model = rumbleVideo.videoThumbnailUri,
            contentDescription = "",
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .background(brandedPlayerBackground.copy(0.6f))
                .fillMaxSize()
        )

        Text(
            modifier = Modifier
                .padding(paddingLarge)
                .constrainAs(title) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                },
            text = rumbleVideo.title,
            color = enforcedWhite,
            style = RumbleTypography.tvH2
        )

        Box(modifier = Modifier
            .constrainAs(gradient) {
                top.linkTo(channel.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        brandedPlayerBackground.copy(
                            alpha = 0f
                        ), brandedPlayerBackground
                    )
                )
            ))

        val bottomGuideline = createGuidelineFromBottom(playerBottomGuideline)

        Box(
            modifier = Modifier
                .height(barCompactHeight)
                .padding(horizontal = paddingLarge)
                .constrainAs(progress) {
                    top.linkTo(bottomGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clip(RoundedCornerShape(radiusSmall))
                .background(enforcedWhite.copy(alpha = 0.2f))
        )


        TvChannelDetailsView(
            modifier = Modifier
                .padding(bottom = paddingMedium)
                .constrainAs(channel) {
                    start.linkTo(parent.start)
                    bottom.linkTo(progress.top)
                }
                .focusRequester(focusRequester)
                .padding(start = paddingSmall),
            onClick = onChannelDetailsClick,
            channelName = rumbleVideo.channelName,
            channelFollowers = rumbleVideo.channelFollowers,
            channelIcon = rumbleVideo.channelIcon,
            displayVerifiedBadge = rumbleVideo.displayVerifiedBadge
        )

        Text(
            modifier = Modifier
                .padding(paddingLarge)
                .constrainAs(error) {
                    start.linkTo(parent.start)
                    top.linkTo(progress.bottom)
                },
            text = stringResource(id = R.string.general_error_message),
            color = enforcedWhite,
            style = RumbleTypography.tinyBodyBold
        )
    }
}

@SuppressLint("NewApi")
@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        TvErrorView(
            rumbleVideo = RumbleVideo(
                0L,
                title = "Test Video",
                watchingNow = 0,
                uploadDate = LocalDateTime.now(),
                scheduledDate = LocalDateTime.now(),
                isPremiumExclusiveContent = false,
                liveStreamedOn = LocalDateTime.now(),
                liveDateTime = LocalDateTime.now(),
                livestreamStatus = RumbleLiveStreamStatus.ENDED
            )
        ) {}
    }
}


