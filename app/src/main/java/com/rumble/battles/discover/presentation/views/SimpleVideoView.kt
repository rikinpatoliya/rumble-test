package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.common.borderColor
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.borderSmall
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.simpleVideoViewChannelThumbnailOffset
import com.rumble.theme.simpleVideoViewHeight
import com.rumble.theme.simpleVideoViewWidth
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.shortString

@Composable
fun SimpleVideoView(
    videoThumbnail: String,
    channelThumbnail: String,
    videoStatus: VideoStatus,
    ppv: PpvEntity? = null,
    viewCount: Long,
    videoTitle: String,
    channelTitle: String,
    onVideoClick: () -> Unit,
    onProfileClick: () -> Unit,
    useChannelPlaceholderBackground: Boolean = false,
) {

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(bottom = paddingLarge)
    ) {
        Box(
            modifier = Modifier
                .height(simpleVideoViewHeight)
                .width(simpleVideoViewWidth)
                .clip(RoundedCornerShape(radiusMedium))
                .background(MaterialTheme.colors.primaryVariant)
                .border(
                    width = borderWidth(videoStatus),
                    color = borderColor(videoStatus, ppv),
                    shape = RoundedCornerShape(radiusMedium)
                )
                .clickable { onVideoClick() }
        ) {
            AsyncImage(
                modifier = Modifier
                    .matchParentSize(),
                model = if (videoThumbnail.isEmpty() && useChannelPlaceholderBackground) {
                    R.drawable.channel_placeholder
                } else {
                    videoThumbnail
                },
                contentDescription = videoTitle,
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier
                .offset(y = simpleVideoViewChannelThumbnailOffset)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(paddingXSmall)
        ) {

            Row(
                modifier = Modifier
                    .background(color = enforcedDarkmo.copy(alpha = 0.8f), shape = CircleShape)
                    .padding(paddingXXXSmall)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_view),
                    contentDescription = stringResource(id = R.string.views),
                    tint = fierceRed
                )
                Text(
                    text = viewCount.shortString(),
                    color = enforcedWhite,
                    style = h6
                )
            }

            ProfileImageComponent(
                modifier = Modifier
                    .clickableNoRipple { onProfileClick.invoke() }
                    .align(Alignment.CenterHorizontally),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXXMediumStyle(
                    borderColor = MaterialTheme.colors.onPrimary
                ),
                userName = channelTitle,
                userPicture = channelThumbnail
            )
        }
    }
}

fun borderWidth(videoStatus: VideoStatus): Dp =
    if (videoStatus != VideoStatus.UPLOADED && videoStatus != VideoStatus.STREAMED) borderSmall
    else 0.dp

@Preview
@Composable
private fun PreviewSimpleVideoView() {
    SimpleVideoView(
        videoThumbnail = "https://sp.rmbl.ws/s8/6/M/n/Y/K/MnYKg.OvCc.jpg",
        channelThumbnail = "https://sp.rmbl.ws/z0/r/r/C/c/rrCcf.asF-1ebosx-rjglmz.jpeg",
        videoStatus = VideoStatus.LIVE,
        null,
        240000,
        "",
        "",
        {},
        {}
    )
}