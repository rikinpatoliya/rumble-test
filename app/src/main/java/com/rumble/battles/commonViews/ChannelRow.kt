package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import com.rumble.battles.R
import com.rumble.theme.*
import com.rumble.theme.RumbleTypography.h4
import com.rumble.utils.extension.shortString

@Composable
fun ChannelRow(
    channelTitle: String,
    thumbnail: String,
    followers: Int,
    verifiedBadge: Boolean
) {
    Row(
        modifier = Modifier
            .padding(paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImageComponent(
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = channelTitle,
            userPicture = thumbnail
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = paddingSmall)
        ) {

            UserNameViewSingleLine(
                name = channelTitle,
                verifiedBadge = verifiedBadge,
                verifiedBadgeHeight = verifiedBadgeHeightMedium,
                spacerWidth = paddingXXXSmall,
                textStyle = h4,
            )

            Text(
                text = "${followers.shortString()} ${
                    pluralStringResource(
                        id = R.plurals.followers, followers
                    ).lowercase()
                }",
                color = MaterialTheme.colors.primaryVariant,
                style = RumbleTypography.h6Light
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = channelTitle,
            tint = MaterialTheme.colors.primary
        )
    }
}