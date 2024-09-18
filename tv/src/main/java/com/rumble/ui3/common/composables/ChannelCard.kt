@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.rumble.ui3.common.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.rumble.R
import com.rumble.theme.RumbleTvTypography.h6Tv
import com.rumble.theme.RumbleTvTypography.labelBoldTv
import com.rumble.theme.channelCardBorderWidth
import com.rumble.theme.channelCardCornerRadius
import com.rumble.theme.channelCardIconWidth
import com.rumble.theme.channelCardVerifiedBadgeIconHeight
import com.rumble.theme.channelCardWidth
import com.rumble.theme.dotSize
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.shortString

@Composable
fun ChannelCard(
    thumbnail: String,
    channelTitle: String,
    verified: Boolean,
    followers: Int,
    isFollowed: Boolean,
    focusRequester: FocusRequester,
    onClick: () -> Unit,
    onFocused: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .width(channelCardWidth)
            .wrapContentHeight()
            .background(
                color = colorResource(id = R.color.channel_card_background),
                RoundedCornerShape(channelCardCornerRadius)
            )
            .focusRequester(focusRequester)
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) {
                    onFocused()
                }
            }
            .conditional(condition = focused) {
                border(
                    width = channelCardBorderWidth,
                    color = rumbleGreen,
                    shape = RoundedCornerShape(channelCardCornerRadius)
                )
            }
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(top = paddingSmall, bottom = paddingXSmall)) {
            ProfileImageComponent(
                modifier = Modifier
                    .size(channelCardIconWidth),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageLargeStyle(),
                userName = channelTitle,
                userPicture = thumbnail
            )
        }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = paddingXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channelTitle,
                style = labelBoldTv,
                color = enforcedWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (verified) {
                Image(
                    modifier = Modifier
                        .padding(start = paddingXXXSmall)
                        .size(channelCardVerifiedBadgeIconHeight),
                    painter = painterResource(id = R.drawable.verified_badge),
                    contentDescription = "Verified Badge",
                )
            }
        }


        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(top = paddingXXXSmall, bottom = paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val followersString = if (isFollowed) {
                followers.shortString()
            } else {
                stringResource(
                    R.string.followers_pattern,
                    followers.shortString()
                )
            }

            Text(
                text = followersString,
                style = h6Tv,
                color = colorResource(id = R.color.white_60_percent),
            )

            if (isFollowed) {
                Canvas(
                    modifier = Modifier
                        .padding(start = paddingXXSmall, end = paddingXXSmall)
                ) {
                    drawCircle(
                        color = enforcedBone,
                        radius = dotSize.toPx()
                    )
                }

                Text(
                    text = stringResource(id = R.string.following),
                    color = rumbleGreen,
                    style = h6Tv,
                )
            }
        }
    }
}
