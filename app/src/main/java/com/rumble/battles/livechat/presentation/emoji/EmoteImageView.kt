package com.rumble.battles.livechat.presentation.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.battles.R
import com.rumble.battles.commonViews.GifImage
import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.imageXXSmall
import com.rumble.theme.imageXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants

@Composable
fun EmoteImageView(
    modifier: Modifier = Modifier,
    emoteSize: Dp,
    emoteEntity: EmoteEntity,
    onClick: (EmoteEntity) -> Unit,
) {

    Box(
        modifier = modifier
            .size(emoteSize)
            .clickable { onClick(emoteEntity) }
    ) {
        if (emoteEntity.url.contains(RumbleConstants.GIF_SUFFIX)) {
            GifImage(
                modifier = modifier
                    .size(emoteSize)
                    .align(alignment = Alignment.Center),
                imageUrl = emoteEntity.url
            )
        } else {
            AsyncImage(
                modifier = modifier
                    .fillMaxHeight()
                    .size(emoteSize)
                    .align(alignment = Alignment.Center),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(emoteEntity.url)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        }

        if (emoteEntity.locked) {
            Box(
                modifier = Modifier
                    .size(imageXXSmall)
                    .clip(RoundedCornerShape(topStart = radiusMedium))
                    .background(RumbleCustomTheme.colors.surface)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(imageXXXSmall),
                    painter = painterResource(R.drawable.ic_lock),
                    tint = RumbleCustomTheme.colors.primaryVariant,
                    contentDescription = stringResource(R.string.subscribers_only)
                )
            }
        }
    }
}