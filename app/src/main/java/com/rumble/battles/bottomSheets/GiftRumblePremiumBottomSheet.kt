package com.rumble.battles.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameView
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedBlack
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall

@Composable
fun GiftRumblePremiumBottomSheet(
    channelName: String,
    imageUrl: String,
    verifiedBadge: Boolean,
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(RumbleCustomTheme.colors.surface)
            .padding(horizontal = paddingMedium)
    ) {
        DrawerCloseIndicatorView(
            modifier = Modifier.padding(
                top = paddingXSmall,
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageComponent(
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXXMediumStyle(),
                userName = channelName,
                userPicture = imageUrl
            )
            Column(
                modifier = Modifier
                    .padding(start = paddingSmall)
            ) {
                UserNameView(
                    modifier = Modifier
                        .fillMaxWidth(),
                    name = channelName,
                    verifiedBadge = verifiedBadge,
                    textStyle = h3,
                )
                Text(
                    text = "where is this info coming from?",//TODO: WIP@Kostia
                    style = h6Light,
                    color = RumbleCustomTheme.colors.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Divider(
            color = RumbleCustomTheme.colors.backgroundHighlight
        )
        Text(
            modifier = Modifier.padding(
                top = paddingMedium,
                bottom = paddingXSmall,
            ),
            text = stringResource(R.string.gift_rumble_premium),
            style = h3,
            color = RumbleCustomTheme.colors.primary,
        )
        Text(
            modifier = Modifier.padding(
                bottom = paddingXSmall10,
            ),
            text = stringResource(R.string.gift_rumble_premium_description),
            style = h6Light,
            color = RumbleCustomTheme.colors.secondary,
        )
        PremiumGiftItemView(
            title = stringResource(R.string.gift_x_premium_sub, 1),
            price = 9.99,//TODO: WIP@Kostia ????
            onClick = {}//TODO: WIP@Kostia ????
        )
        PremiumGiftItemView(
            title = stringResource(R.string.gift_x_premium_sub, 5),
            price = 49.95,//TODO: WIP@Kostia ????
            onClick = {}//TODO: WIP@Kostia ????
        )
        PremiumGiftItemView(
            title = stringResource(R.string.gift_x_premium_sub, 10),
            price = 99.90,//TODO: WIP@Kostia ????
            onClick = {}//TODO: WIP@Kostia ????
        )
        PremiumGiftItemView(
            title = stringResource(R.string.gift_x_premium_sub, 20),
            price = 199.80,//TODO: WIP@Kostia ????
            onClick = {}//TODO: WIP@Kostia ????
        )
        PremiumGiftItemView(
            title = stringResource(R.string.gift_x_premium_sub, 50),
            price = 499.50,//TODO: WIP@Kostia ????
            onClick = {}//TODO: WIP@Kostia ????
        )
        Spacer(modifier = Modifier.height(paddingLarge))
    }
}

@Composable
private fun PremiumGiftItemView(
    title: String,
    price: Double,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingXXSmall)
            .clip(RoundedCornerShape(radiusSmall))
            .border(
                color = RumbleCustomTheme.colors.backgroundHighlight,
                width = borderXXSmall,
                shape = RoundedCornerShape(radiusSmall)
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(paddingXXMedium),
            text = title,
            style = h4,
            color = RumbleCustomTheme.colors.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        ActionButton(
            modifier = Modifier.padding(end = paddingSmall),
            text = String.format(
                "${stringResource(R.string.dollar_sign)}%.2f",
                price
            ),
            contentModifier = Modifier
                .padding(
                    top = paddingXSmall10,
                    bottom = paddingXSmall10,
                    start = paddingMedium,
                    end = paddingXXMedium
                ),
            textModifier = Modifier
                .padding(start = paddingXSmall),
            leadingIconPainter = painterResource(id = R.drawable.ic_gift),
            textColor = enforcedBlack,
            onClick = onClick,
            enabled = true,
        )
    }
}