package com.rumble.battles.premium.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.livechat.presentation.content.LiveChatContentView
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedGray500
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXXMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall

@Composable
fun GiverMessageView(
    modifier: Modifier = Modifier,
    channelName: String = "",
    channelAvatar: String? = null,
    badges: Map<String, BadgeEntity> = emptyMap(),
    subscriptionsNumber: Int = 0,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        enforcedFiord,
                        enforcedGray500,
                    )
                )
            )
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(imageXXXMedium),
            painter = painterResource(R.drawable.presents),
            contentDescription = "",
            contentScale = ContentScale.FillHeight
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileImageComponent(
                modifier = Modifier.padding(paddingXSmall),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                userName = channelName,
                userPicture = channelAvatar ?: ""
            )

            Column {
                LiveChatContentView(
                    userName = channelName,
                    badges = badges,
                    userNameColor = enforcedWhite,
                    textStyle = h6Bold
                )

                Text(
                    text = pluralStringResource(
                        R.plurals.gifted_premium_subscriptions,
                        subscriptionsNumber,
                        subscriptionsNumber
                    ),
                    color = enforcedWhite,
                    style = h6Light,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        GiverMessageView(
            modifier = Modifier.width(400.dp),
            channelName = "Test channel name",
            subscriptionsNumber = 10,
        )
    }
}