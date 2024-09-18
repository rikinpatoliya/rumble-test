package com.rumble.ui3.library

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.theme.RumbleTvTypography
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.borderSmall
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedGray950
import com.rumble.theme.enforcedWhite
import com.rumble.theme.minVideoCardProgressWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingNone
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.videoCardFocusOutlineCornerRadius
import com.rumble.theme.videoCardFocusOutlineWidth
import com.rumble.theme.videoCardIconHeight
import com.rumble.theme.videoCardIconWidth
import com.rumble.theme.videoCardWidth
import com.rumble.ui3.common.borderColor
import com.rumble.utils.extension.agoString
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.getMediumDateTimeString
import com.rumble.utils.extension.shortString
import com.rumble.videoplayer.player.RumbleLiveStreamStatus
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.config.RumbleVideoStatus
import com.rumble.videoplayer.presentation.internal.controlViews.PremiumTag
import com.rumble.videoplayer.presentation.internal.views.NowPlayingView
import java.time.LocalDateTime

@Composable
fun VideoCard(
    videoEntity: VideoEntity,
    onFocused: (VideoEntity) -> Unit,
    onSelected: (VideoEntity) -> Unit,
    focusRequester: FocusRequester,
) {
    VideoCard(
        videoStatus = videoEntity.videoStatus,
        videoThumbnail = videoEntity.videoThumbnail,
        lastPositionSeconds = videoEntity.lastPositionSeconds,
        duration = videoEntity.duration,
        scheduledDate = videoEntity.scheduledDate,
        watchingNow = videoEntity.watchingNow,
        ppvEntity = videoEntity.ppv,
        title = videoEntity.title,
        channelName = videoEntity.channelName,
        verifiedBadge = videoEntity.verifiedBadge,
        uploadDate = videoEntity.uploadDate,
        viewsNumber = videoEntity.viewsNumber,
        likeNumber = videoEntity.likeNumber,
        onFocused = { onFocused(videoEntity) },
        onSelected = { onSelected(videoEntity) },
        focusRequester = focusRequester,
        livestreamStatus = videoEntity.livestreamStatus,
        livestreamedOn = videoEntity.liveStreamedOn,
        liveDateTime = videoEntity.liveDateTime,
        isPremiumExclusiveContent = videoEntity.isPremiumExclusiveContent,
    )
}

@Composable
fun VideoCard(
    video: RumbleVideo,
    playing: Boolean,
    onFocused: () -> Unit,
    onClick: (Long) -> Unit,
) {
    val videoStatus = when (video.videoStatus) {
        RumbleVideoStatus.UPLOADED -> VideoStatus.UPLOADED
        RumbleVideoStatus.UPCOMING -> VideoStatus.UPCOMING
        RumbleVideoStatus.SCHEDULED -> VideoStatus.SCHEDULED
        RumbleVideoStatus.STARTING -> VideoStatus.STARTING
        RumbleVideoStatus.LIVE -> VideoStatus.LIVE
        RumbleVideoStatus.STREAMED -> VideoStatus.STREAMED
    }

    val liveStreamStatus = when (video.livestreamStatus) {
        RumbleLiveStreamStatus.UNKNOWN -> LiveStreamStatus.UNKNOWN
        RumbleLiveStreamStatus.ENDED -> LiveStreamStatus.ENDED
        RumbleLiveStreamStatus.OFFLINE -> LiveStreamStatus.OFFLINE
        RumbleLiveStreamStatus.LIVE -> LiveStreamStatus.LIVE
    }

    VideoCard(
        videoStatus = videoStatus,
        videoThumbnail = video.videoThumbnailUri,
        lastPositionSeconds = video.lastPosition,
        duration = video.duration,
        scheduledDate = video.scheduledDate,
        watchingNow = video.watchingNow,
        ppvEntity = null,
        title = video.title,
        channelName = video.channelName,
        verifiedBadge = video.verifiedBadge,
        uploadDate = video.uploadDate,
        viewsNumber = video.viewsNumber,
        likeNumber = video.likeNumber,
        onFocused = onFocused,
        onSelected = { onClick(video.videoId) },
        focusRequester = FocusRequester(),
        playing = playing,
        livestreamStatus = liveStreamStatus,
        livestreamedOn = video.liveStreamedOn,
        liveDateTime = video.liveDateTime,
        isPremiumExclusiveContent = video.isPremiumExclusiveContent,
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(
    videoStatus: VideoStatus,
    videoThumbnail: String?,
    lastPositionSeconds: Long?,
    duration: Long,
    scheduledDate: LocalDateTime?,
    watchingNow: Long,
    ppvEntity: PpvEntity?,
    title: String,
    channelName: String,
    verifiedBadge: Boolean,
    uploadDate: LocalDateTime,
    viewsNumber: Long,
    likeNumber: Long,
    livestreamStatus: LiveStreamStatus,
    livestreamedOn: LocalDateTime?,
    liveDateTime: LocalDateTime?,
    isPremiumExclusiveContent: Boolean,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
    focusRequester: FocusRequester,
    displaySelection: Boolean = true,
    playing: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .width(videoCardWidth)
            .background(Color.Transparent)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onFocused()
                }
            }
            .clickableNoRipple { onSelected() },
    ) {
        val (metadata, videoThumb, tagStart, lockTag, live, ppv, nowPlaying) = createRefs()

        Box(
            modifier = Modifier
                .wrapContentSize()
                .conditional(isFocused) {
                    border(
                        width = videoCardFocusOutlineWidth,
                        color = rumbleGreen,
                        shape = RoundedCornerShape(videoCardFocusOutlineCornerRadius)
                    )
                }
                .padding(paddingXXSmall)
                .constrainAs(videoThumb) {},
        ) {

            Box(
                modifier = Modifier
                    .width(videoCardIconWidth)
                    .height(videoCardIconHeight)
                    .clip(RoundedCornerShape(radiusXSmall))
                    .border(
                        width = borderWidth(videoStatus),
                        color = borderColor(videoStatus),
                        shape = RoundedCornerShape(radiusXSmall)
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(radiusXSmall)),
                    model = videoThumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )

                if (lastPositionSeconds != null && lastPositionSeconds > 0 && duration > 0) {
                    val minProgress = minVideoCardProgressWidth.value / videoCardIconWidth.value
                    LinearProgressIndicator(
                        progress = { maxOf(lastPositionSeconds.toFloat() / duration, minProgress) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = rumbleGreen,
                        trackColor = enforcedFiord
                    )
                }
            }
        }

        if (isPremiumExclusiveContent) {
            Icon(
                modifier = Modifier.constrainAs(lockTag) {
                    top.linkTo(parent.top, margin = paddingSmall)
                    start.linkTo(parent.start, margin = paddingSmall)
                },
                painter = painterResource(id = R.drawable.ic_locked_content),
                contentDescription = stringResource(id = R.string.premium_only),
                tint = Color.Unspecified
            )
        }

        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(paddingMedium)
                .constrainAs(tagStart) {
                    bottom.linkTo(videoThumb.bottom)
                    start.linkTo(videoThumb.start)
                },
            verticalArrangement = Arrangement.spacedBy(paddingXSmall),
        ) {
            if (videoStatus != VideoStatus.UPLOADED && videoStatus != VideoStatus.STREAMED) {
                StateTagView(
                    videoStatus = videoStatus,
                    scheduled = scheduledDate,
                    watching = watchingNow
                )
            }

            if ((videoStatus == VideoStatus.STREAMED || videoStatus == VideoStatus.UPLOADED) && duration > 0) {
                DurationTagView(
                    duration = duration
                )
            }
        }

        LiveTagView(
            modifier = Modifier
                .padding(start = paddingNone, top = paddingNone, end = paddingMedium, bottom = paddingMedium)
                .constrainAs(live) {
                    bottom.linkTo(videoThumb.bottom)
                    end.linkTo(parent.end)
                },
            videoStatus = videoStatus
        )

        ppvEntity?.let {
            PpvTagView(
                modifier = Modifier
                    .padding(end = paddingMedium, bottom = paddingMedium)
                    .constrainAs(ppv) {
                        bottom.linkTo(videoThumb.bottom)
                        end.linkTo(parent.end)
                    },
                ppv = it
            )
        }

        if (displaySelection && playing) {
            NowPlayingView(
                modifier = Modifier
                    .constrainAs(nowPlaying) {
                        start.linkTo(videoThumb.start)
                        end.linkTo(videoThumb.end)
                        top.linkTo(videoThumb.top)
                        bottom.linkTo(videoThumb.bottom)
                    }
                    .padding(paddingXXSmall)
                    .clip(RoundedCornerShape(radiusXSmall))
                    .width(videoCardIconWidth)
                    .height(videoCardIconHeight)
            )
        }

        VideoCardMetaData(
            Modifier
                .constrainAs(metadata) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(videoThumb.bottom)
                }
                .padding(horizontal = paddingXXSmall),
            isFocused = isFocused,
            videoStatus = videoStatus,
            title = title,
            channelName = channelName,
            verifiedBadge = verifiedBadge,
            uploadDate = uploadDate,
            viewsNumber = viewsNumber,
            likeNumber = likeNumber,
            livestreamStatus = livestreamStatus,
            livestreamOn = livestreamedOn,
            liveDateTime = liveDateTime,
            watchingNow = watchingNow,
            isPremiumExclusiveContent = isPremiumExclusiveContent,
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCardMetaData(
    modifier: Modifier,
    isFocused: Boolean,
    videoStatus: VideoStatus,
    title: String,
    channelName: String,
    verifiedBadge: Boolean,
    uploadDate: LocalDateTime,
    viewsNumber: Long,
    likeNumber: Long,
    livestreamStatus: LiveStreamStatus,
    livestreamOn: LocalDateTime?,
    liveDateTime: LocalDateTime?,
    watchingNow: Long,
    isPremiumExclusiveContent: Boolean,
) {
    ConstraintLayout(modifier) {
        val (titleRef, dot, username, viewsStats) = createRefs()
        Text(
            modifier = Modifier
                .padding(top = paddingXXSmall)
                .fillMaxWidth()
                .constrainAs(titleRef) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
            text = title,
            style = RumbleTvTypography.h3Tv,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = if (isFocused) {
                rumbleGreen
            } else {
                enforcedWhite
            }
        )

        UserNameViewSingleLine(
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = paddingSmall, bottom = paddingXSmall)
                .constrainAs(username) {
                    start.linkTo(parent.start)
                    end.linkTo(dot.start)
                    top.linkTo(titleRef.bottom)
                    width = Dimension.preferredWrapContent
                },
            name = channelName,
            verifiedBadge = verifiedBadge,
            verifiedBadgeHeight = dimensionResource(id = R.dimen.video_card_verified_badge_icon_height),
            spacerWidth = dimensionResource(id = R.dimen.video_card_dot_margin_start),
            textStyle = RumbleTvTypography.labelBoldTv
        )

        Row(
            Modifier
                .constrainAs(viewsStats) {
                    start.linkTo(parent.start)
                    top.linkTo(username.bottom)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isPremiumExclusiveContent) {
                PremiumTag(modifier = Modifier.background(enforcedGray950, shape = RoundedCornerShape(radiusXXSmall)))
                Spacer(modifier = Modifier.size(paddingXXSmall))
            }

            MetadataLabelText(
                text = getMetadataLabelText(
                    LocalContext.current,
                    livestreamStatus = livestreamStatus,
                    livestreamedOn = livestreamOn,
                    liveDateTime = liveDateTime,
                    watchingNow = watchingNow,
                    likeNumber = likeNumber,
                    isPremiumExclusiveContent = isPremiumExclusiveContent,
                    uploadDate = uploadDate,
                    viewsNumber = viewsNumber,
                )
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MetadataLabelText(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        color = enforcedBone,
        style = h6Light,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

fun getMetadataLabelText(
    context: Context,
    livestreamStatus: LiveStreamStatus,
    livestreamedOn: LocalDateTime?,
    liveDateTime: LocalDateTime?,
    watchingNow: Long,
    likeNumber: Long,
    isPremiumExclusiveContent: Boolean,
    uploadDate: LocalDateTime,
    viewsNumber: Long,
): String {

    if (livestreamStatus == LiveStreamStatus.OFFLINE && livestreamedOn == null) {
        return if (liveDateTime == null) {
            return context.getString(R.string.upcoming) +
                    getWaitingText(context, watchingNow) +
                    getLikesText(context, likeNumber)
        } else if (liveDateTime > LocalDateTime.now()) {
            return (context.getString(R.string.scheduled_for) +
                    " " + liveDateTime.getMediumDateTimeString())
        } else {
            return context.getString(R.string.starting) + getWaitingText(context, watchingNow) +
                    getLikesText(context, likeNumber)
        }
    } else if ((livestreamStatus == LiveStreamStatus.OFFLINE || livestreamStatus == LiveStreamStatus.LIVE) && livestreamedOn != null) {
        return context.getString(R.string.started) + " " + livestreamedOn.agoString(context) +
                getViewsText(context, viewsNumber)
    } else if (livestreamStatus == LiveStreamStatus.UNKNOWN || livestreamStatus == LiveStreamStatus.ENDED) {
        return if (isPremiumExclusiveContent) {
            uploadDate.agoString(context)
        } else {
            uploadDate.agoString(context) +
                    getViewsText(context, viewsNumber) +
                    getLikesText(context, likeNumber)
        }
    }

    return ""
}

private fun getLikesText(context: Context, likes: Long): String {
    if (likes > 0) {
        return context.resources.getQuantityString(
            R.plurals.likes,
            likes.toInt(),
            likes.shortString(false)
        )
    }
    return ""
}

private fun getViewsText(context: Context, viewsNumber: Long): String {
    if (viewsNumber > 0) {
        return context.resources.getQuantityString(
            R.plurals.views,
            viewsNumber.toInt(),
            viewsNumber.shortString(true)
        )
    }
    return ""
}

private fun getWaitingText(context: Context, watchingNow: Long): String {
    if (watchingNow > 0) {
        return context.getString(
            R.string.waiting,
            watchingNow.shortString(true)
        )
    }
    return ""
}

private fun borderWidth(videoStatus: VideoStatus): Dp =
    if (videoStatus == VideoStatus.LIVE) borderSmall
    else 0.dp