package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListOption
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.h6Medium
import com.rumble.theme.compactVideoHeight
import com.rumble.theme.compactVideoWidth
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.agoString
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional

@Composable
fun PlayListView(
    modifier: Modifier = Modifier,
    playListEntity: PlayListEntity,
    playListOptions: List<PlayListOption>,
    onViewChannel: ((String) -> Unit),
    onViewPlayList: ((String) -> Unit),
    onMoreClick: (List<PlayListOption>) -> Unit,
) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onViewPlayList(playListEntity.id) }
    ) {
        val (playListInfo, videoThumb) = createRefs()
        val tabletThumbnailEnd = createGuidelineFromStart(0.35f)
        val isTablet = IsTablet()

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
                .wrapContentHeight()
                .clip(RoundedCornerShape(radiusMedium))
                .background(MaterialTheme.colors.primaryVariant),
            contentAlignment = Alignment.BottomStart
        ) {
            if (playListEntity.thumbnail.isEmpty()) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = R.drawable.empty_playlist_humbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
            } else {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = playListEntity.thumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(enforcedBlack.copy(alpha = 0.7F))
                    .padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${playListEntity.videosQuantity}",
                    color = enforcedWhite,
                    style = h6Medium
                )
                Spacer(modifier = Modifier.weight(1F))
                Icon(
                    painter = painterResource(id = R.drawable.ic_playlist),
                    contentDescription = stringResource(id = R.string.playlist),
                    modifier = Modifier.size(imageXXSmall),
                    tint = enforcedWhite
                )
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(playListInfo) {
                    start.linkTo(videoThumb.end)
                    top.linkTo(videoThumb.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(start = paddingSmall)
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .clip(RoundedCornerShape(radiusSmall)),
                        text = playListEntity.title,
                        style = h6,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (playListOptions.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickableNoRipple { onMoreClick(playListOptions) },
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = stringResource(id = R.string.more),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clip(RoundedCornerShape(radiusSmall))
                        .clickable { onViewChannel(playListEntity.channelId) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserNameViewSingleLine(
                        name = playListEntity.channelName ?: playListEntity.username,
                        verifiedBadge = playListEntity.verifiedBadge,
                        textStyle = h6,
                        textColor = MaterialTheme.colors.primary,
                        spacerWidth = paddingXXXSmall,
                        verifiedBadgeHeight = verifiedBadgeHeightSmall
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clip(RoundedCornerShape(radiusSmall)),
                    color = MaterialTheme.colors.secondary,
                    text = "${stringResource(id = R.string.updated)} ${
                        playListEntity.updatedDate.agoString(
                            LocalContext.current
                        )
                    }",
                    style = h6Light,
                )
            }
        }
    }
}