package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.shortString
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.internal.defaults.channelIconSize

@Composable
fun TvChannelDetailsView(
    modifier: Modifier = Modifier,
    channelName: String,
    channelIcon: String,
    displayVerifiedBadge: Boolean,
    channelFollowers: Int,
    isFocused: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .conditional(isFocused) {
                background(enforcedWhite.copy(alpha = 0.2f))
            }
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(
                end = paddingMedium,
                top = paddingXSmall,
                bottom = paddingXSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = paddingSmall)
                        .size(channelIconSize)
                        .clip(CircleShape)
                ) {
                    UserNamePlaceholderView(
                        modifier = Modifier.fillMaxSize(),
                        userName = channelName
                    )
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channelIcon)
                            .crossfade(true)
                            .build(),
                        contentDescription = channelName,
                        contentScale = ContentScale.Crop
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = channelName,
                            style = RumbleTypography.h4,
                            color = enforcedWhite
                        )

                        if (displayVerifiedBadge) {
                            Image(
                                modifier = Modifier.padding(start = paddingSmall),
                                painter = painterResource(id = R.drawable.tv_verified),
                                contentDescription = ""
                            )
                        }
                    }

                    Text(
                        text = "${channelFollowers.shortString()} ${
                            pluralStringResource(
                                id = R.plurals.channel_followers, channelFollowers
                            )
                        }",
                        color = enforcedWhite,
                        style = RumbleTypography.h5,
                        textAlign = TextAlign.Center
                    )
                }
            }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        TvChannelDetailsView(
            channelIcon = "data:image/jpeg;base64,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",
            channelName = "Test Channel Name",
            channelFollowers = 123_456,
            displayVerifiedBadge = true,
        )
    }
}