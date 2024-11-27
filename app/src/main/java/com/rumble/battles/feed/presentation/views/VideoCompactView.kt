package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.common.borderColor
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.PremiumExclusiveContentIconView
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.channels.channeldetails.domain.domainmodel.DisplayScreenType
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Medium
import com.rumble.theme.RumbleTypography.tinyBodyExtraBold
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.borderXSmall
import com.rumble.theme.compactVideoHeight
import com.rumble.theme.compactVideoWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.imageSmall
import com.rumble.theme.imageXSmall14
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.progressBarHeight
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.onVisible

@Composable
fun VideoCompactView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    showMoreAction: Boolean = true,
    listToggleViewStyle: ListToggleViewStyle = ListToggleViewStyle.LIST,
    onViewVideo: (VideoEntity) -> Unit = {},
    onMoreClick: (VideoEntity) -> Unit = {},
    onImpression: (VideoEntity) -> Unit = {},
    displayScreenType: DisplayScreenType = DisplayScreenType.OTHER,
    featured: Boolean = false
) {

    var visible by remember { mutableStateOf(false) }
    var impressionReported by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            visible = false
            impressionReported = false
        }
    }

    LaunchedEffect(visible) {
        if (visible && impressionReported.not()) {
            impressionReported = true
            onImpression(videoEntity)
        }
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onVisible {
                if (visible.not()) visible = true
            }
            .clickable { onViewVideo(videoEntity) }
    ) {
        val (featuredLabel, channelThumb, videoThumb, ppvPurchase) = createRefs()
        val tabletThumbnailEnd = createGuidelineFromStart(0.35f)
        val isTablet = IsTablet()

        if (featured) {
            FeaturedLabel(
                modifier = Modifier
                    .constrainAs(featuredLabel) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(bottom = paddingXXXSmall),
                displayScreenType = displayScreenType
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(videoThumb) {
                    top.linkTo(featuredLabel.bottom)
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
                .wrapContentHeight()
                .clip(RoundedCornerShape(radiusMedium))
                .background(MaterialTheme.colors.primaryVariant)
                .border(
                    width = borderWidth(videoEntity.videoStatus, videoEntity.ppv),
                    color = borderColor(videoEntity.videoStatus, videoEntity.ppv),
                    shape = RoundedCornerShape(radiusMedium)
                )
        ) {
            if (videoEntity.ageRestricted) {
                AgeRestrictedThumbnailView(
                    modifier = Modifier.fillMaxSize(),
                    listToggleViewStyle = ListToggleViewStyle.LIST,
                    url = videoEntity.videoThumbnail
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
                        .height(progressBarHeight),
                    videoEntity = videoEntity
                )
            }

            when (videoEntity.videoStatus) {
                VideoStatus.UPLOADED -> {
                    DurationTagView(
                        modifier = Modifier
                            .padding(paddingXSmall)
                            .align(Alignment.BottomStart),
                        duration = videoEntity.duration,
                        listToggleViewStyle = ListToggleViewStyle.LIST
                    )
                }

                else -> {
                    StateTagCompactView(
                        modifier = Modifier
                            .padding(paddingXSmall)
                            .align(Alignment.BottomStart),
                        videoStatus = videoEntity.videoStatus,
                        scheduled = videoEntity.scheduledDate,
                        watching = videoEntity.watchingNow,
                        duration = videoEntity.duration
                    )
                }
            }

            if (videoEntity.videoStatus == VideoStatus.LIVE) {
                LiveTagCompactView(
                    modifier = Modifier
                        .padding(paddingXSmall)
                        .align(Alignment.BottomEnd),
                )
            }

            if (videoEntity.isPremiumExclusiveContent && videoEntity.hasLiveGate.not()) {
                PremiumExclusiveContentIconView(
                    modifier = Modifier
                        .padding(paddingXSmall)
                        .size(imageSmall)
                        .align(Alignment.TopStart),
                    iconSize = imageXSmall14
                )
            }
        }
        Box(
            modifier = Modifier
                .constrainAs(channelThumb) {
                    start.linkTo(videoThumb.end)
                    top.linkTo(videoThumb.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(start = paddingXSmall)
            ) {
                Row {
                    var videoTitle by remember { mutableStateOf(videoEntity.title) }
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(radiusSmall)),
                        text = videoTitle,
                        color = MaterialTheme.colors.primary,
                        style = h6Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            if (it.lineCount > 1 && videoTitle.contains("\n").not()) {
                                videoTitle = videoTitle.plus("\n")
                            }
                        }
                    )
                    if (showMoreAction) {
                        Icon(
                            modifier = Modifier.clickableNoRipple { onMoreClick(videoEntity) },
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = stringResource(id = R.string.more),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clip(RoundedCornerShape(radiusSmall)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserNameViewSingleLine(
                        modifier = Modifier
                            .weight(1f, fill = false),
                        name = videoEntity.channelName,
                        verifiedBadge = videoEntity.verifiedBadge,
                        textStyle = h6,
                        textColor = RumbleCustomTheme.colors.primary,
                        spacerWidth = paddingXXXSmall,
                        verifiedBadgeHeight = verifiedBadgeHeightSmall
                    )
                }
                VideoMetadataView(
                    videoEntity = videoEntity,
                    textStyle = h6Medium,
                    listToggleViewStyle = listToggleViewStyle,
                )
                if (videoEntity.isPremiumExclusiveContent) {
                    PremiumTagCompactView(videoEntity.hasLiveGate)
                }
            }
        }

        videoEntity.ppv?.let {
            PpvTagsView(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = paddingXSmall, top = paddingXXSmall)
                    .constrainAs(ppvPurchase) {
                        start.linkTo(channelThumb.start)
                        top.linkTo(channelThumb.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                ppvEntity = it,
            )
        }
    }
}

@Composable
private fun PremiumTagCompactView(
    hasLiveGate: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = paddingXXXXSmall)
            .clip(RoundedCornerShape(radiusXXSmall))
            .background(color = MaterialTheme.colors.onSurface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = paddingXXXXSmall, horizontal = paddingXXXSmall)
                .size(imageXSmall14)
                .clip(RoundedCornerShape(radiusXSmall))
                .background(color = rumbleGreen)
        ) {
            Icon(
                modifier = Modifier
                    .padding(paddingXXXXSmall)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_discover),
                contentDescription = stringResource(id = if (hasLiveGate) R.string.premium else R.string.premium_only),
                tint = MaterialTheme.colors.onSurface
            )
        }
        Text(
            modifier = Modifier.padding(end = paddingXXXSmall),
            text = stringResource(id = if (hasLiveGate) R.string.premium else R.string.premium_only),
            color = rumbleGreen,
            style = tinyBodySemiBold
        )
    }
}

@Composable
fun LiveTagCompactView(
    modifier: Modifier,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusXXSmall))
                .background(fierceRed)
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = paddingXXXXSmall,
                    horizontal = paddingXSmall
                )
            ) {
                Text(
                    text = stringResource(id = R.string.live).uppercase(),
                    style = tinyBodyExtraBold,
                    color = enforcedWhite
                )
            }
        }
    }
}

private fun borderWidth(videoStatus: VideoStatus, ppv: PpvEntity?): Dp =
    if (videoStatus != VideoStatus.UPLOADED
        && videoStatus != VideoStatus.STREAMED
        || ppv != null
    ) borderXSmall
    else 0.dp
