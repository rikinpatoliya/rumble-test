package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.common.borderColor
import com.rumble.battles.commonViews.PremiumExclusiveContentIconView
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.domain.channels.channeldetails.domain.domainmodel.DisplayScreenType
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.borderWidth
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMedium
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.progressBarHeightVideoView
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.onVisible
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.RumbleVideoView
import com.rumble.videoplayer.presentation.UiType

@Composable
fun VideoView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    rumblePlayer: RumblePlayer? = null,
    soundOn: Boolean = false,
    onChannelClick: (() -> Unit)? = null,
    onMoreClick: (VideoEntity) -> Unit,
    onImpression: (VideoEntity) -> Unit,
    onClick: (Feed) -> Unit,
    onSoundClick: () -> Unit = {},
    onPlayerImpression: (VideoEntity) -> Unit = {},
    displayScreenType: DisplayScreenType = DisplayScreenType.OTHER,
    featured: Boolean = false,
    isPremiumUser: Boolean = false,
) {
    var viewVisible by remember { mutableStateOf(false) }
    var impressionReported by remember { mutableStateOf(false) }
    var playerImpressionReported by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            impressionReported = false
            playerImpressionReported = false
            viewVisible = false
        }
    }

    LaunchedEffect(viewVisible, rumblePlayer) {
        if (viewVisible) {
            if (impressionReported.not()) {
                onImpression(videoEntity)
                impressionReported = true
            }

            if (playerImpressionReported.not() && rumblePlayer?.videoId == videoEntity.id) {
                onPlayerImpression(videoEntity)
                playerImpressionReported = true
            }

            if (rumblePlayer?.videoId == videoEntity.id && rumblePlayer.isPlaying().not())
                rumblePlayer.playVideo()
        }
    }

    ConstraintLayout(
        modifier = modifier
            .clickableNoRipple { onClick(videoEntity) }
            .onVisible {
                if (viewVisible.not()) viewVisible = true
            }
    ) {
        val (featuredLabel, videoThumb, tagStart, tagEnd, footer) = createRefs()

        if (featured) {
            FeaturedLabel(
                modifier = Modifier
                    .constrainAs(featuredLabel) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(start = paddingXSmall, bottom = paddingXXXSmall),
                displayScreenType = displayScreenType
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(videoThumb) {
                    top.linkTo(featuredLabel.bottom)
                }
                .clip(RoundedCornerShape(radiusMedium))
                .background(MaterialTheme.colors.primaryVariant)
                .border(
                    width = borderWidth(videoEntity.videoStatus, videoEntity.ppv),
                    color = borderColor(videoEntity.videoStatus, videoEntity.ppv),
                    shape = RoundedCornerShape(radiusMedium)
                )
                .aspectRatio(VIDEO_CARD_THUMBNAIL_ASPECT_RATION)
        ) {
            if (videoEntity.ageRestricted) {
                AgeRestrictedThumbnailView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onClick(videoEntity) },
                    listToggleViewStyle = ListToggleViewStyle.GRID,
                    url = videoEntity.videoThumbnail
                )
            } else if (rumblePlayer?.videoId == videoEntity.id
                && (videoEntity.isPremiumExclusiveContent.not() || isPremiumUser)
            ) {
                RumbleVideoView(
                    modifier = Modifier.fillMaxSize(),
                    rumblePlayer = rumblePlayer,
                    uiType = UiType.IN_LIST,
                    onClick = { onClick(videoEntity) },
                    liveChatDisabled = true
                )

                RoundIconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    painter = if (soundOn.not()) painterResource(id = R.drawable.ic_sound_off)
                    else painterResource(id = com.rumble.videoplayer.R.drawable.ic_sound_on),
                    backgroundColor = enforcedDarkmo,
                    tintColor = enforcedWhite,
                    onClick = {
                        onSoundClick()
                    }
                )
            } else {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = videoEntity.videoThumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
                VideoViewProgressIndicatorView(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = borderWidth(videoEntity.videoStatus, videoEntity.ppv))
                        .height(progressBarHeightVideoView),
                    videoEntity = videoEntity
                )
            }

            if (videoEntity.isPremiumExclusiveContent) {
                PremiumExclusiveContentIconView(
                    modifier = Modifier
                        .padding(paddingSmall)
                        .size(imageMedium)
                        .align(Alignment.TopStart),
                    iconSize = imageXXSmall
                )
            }
        }

        if (rumblePlayer?.videoId != videoEntity.id || videoEntity.ageRestricted) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(paddingSmall)
                    .constrainAs(tagStart) {
                        start.linkTo(videoThumb.start)
                        bottom.linkTo(videoThumb.bottom)
                    },
                horizontalArrangement = Arrangement.spacedBy(paddingXSmall),
            ) {
                if (videoEntity.videoStatus != VideoStatus.UPLOADED) {
                    StateTagView(
                        videoStatus = videoEntity.videoStatus,
                        scheduled = videoEntity.scheduledDate,
                        watching = videoEntity.watchingNow
                    )
                }

                if ((videoEntity.videoStatus == VideoStatus.STREAMED
                    || videoEntity.videoStatus == VideoStatus.UPLOADED)
                    && videoEntity.duration > 0
                ) {
                    DurationTagView(
                        duration = videoEntity.duration,
                        listToggleViewStyle = ListToggleViewStyle.GRID
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(paddingSmall)
                    .constrainAs(tagEnd) {
                        end.linkTo(videoThumb.end)
                        bottom.linkTo(videoThumb.bottom)
                    }, horizontalArrangement = Arrangement.spacedBy(paddingSmall)
            ) {
                PpvTagView(
                    ppv = videoEntity.ppv
                )
                LiveTagView(
                    videoStatus = videoEntity.videoStatus,
                )
            }
        }

        VideoCardFooter(
            modifier = Modifier
                .constrainAs(footer) {
                    top.linkTo(videoThumb.bottom)
                    start.linkTo(videoThumb.start)
                    end.linkTo(videoThumb.end)
                }
                .padding(top = paddingXXXSmall),
            videoEntity = videoEntity,
            onChannelClick = onChannelClick,
            onMoreClick = onMoreClick
        )
    }
}

private fun borderWidth(videoStatus: VideoStatus, ppv: PpvEntity?): Dp =
    if (videoStatus != VideoStatus.UPLOADED
        && videoStatus != VideoStatus.STREAMED
        || ppv != null
    ) borderWidth
    else 0.dp