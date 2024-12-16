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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.android.billingclient.api.ProductDetails
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameView
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftDetails
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftEntity
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftType
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.borderXXSmall
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen

@Composable
fun GiftRumblePremiumBottomSheet(
    premiumGiftEntity: PremiumGiftEntity,
    channelName: String,
    description: String?,
    imageUrl: String,
    verifiedBadge: Boolean,
    onClick: (ProductDetails) -> Unit,
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
                description?.let {
                    Text(
                        text = it,
                        style = h6Light,
                        color = RumbleCustomTheme.colors.secondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
            text = stringResource(
                if (premiumGiftEntity.type == PremiumGiftType.Premium)
                    R.string.gift_rumble_premium
                else
                    R.string.gift_1_month_channel_subscription_to_the_community
            ),
            style = h3,
            color = RumbleCustomTheme.colors.primary,
        )
        if (premiumGiftEntity.type == PremiumGiftType.Premium) {
            Text(
                modifier = Modifier.padding(
                    bottom = paddingXSmall10,
                ),
                text = stringResource(R.string.gift_rumble_premium_description),
                style = h6Light,
                color = RumbleCustomTheme.colors.secondary,
            )
        }
        premiumGiftEntity.giftList.forEach { giftDetails ->
            PremiumGiftItemView(
                type = premiumGiftEntity.type,
                premiumGiftDetails = giftDetails,
                onClick = onClick
            )
        }
        Spacer(modifier = Modifier.height(paddingLarge))
    }
}

@Composable
private fun PremiumGiftItemView(
    type: PremiumGiftType,
    premiumGiftDetails: PremiumGiftDetails,
    onClick: (ProductDetails) -> Unit,
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
            .clickable {
                premiumGiftDetails.productDetails?.let { productDetails ->
                    onClick(productDetails)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(paddingXXMedium),
            text = pluralStringResource(
                if (type == PremiumGiftType.Premium)
                    R.plurals.gift_x_premium_subs
                else
                    R.plurals.gift_x_subs,
                premiumGiftDetails.giftsAmount,
                premiumGiftDetails.giftsAmount
            ),
            style = h4,
            color = RumbleCustomTheme.colors.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        ActionButton(
            modifier = Modifier.padding(end = paddingSmall),
            text = premiumGiftDetails.productDetails?.oneTimePurchaseOfferDetails?.formattedPrice
                ?: "",
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
            backgroundColor = if (type == PremiumGiftType.Premium) rumbleGreen else brandedLocalsRed,
            borderColor = if (type == PremiumGiftType.Premium) rumbleGreen else brandedLocalsRed,
            textColor = if (type == PremiumGiftType.Premium) enforcedBlack else enforcedWhite,
            onClick = {
                premiumGiftDetails.productDetails?.let { productDetails ->
                    onClick(productDetails)
                }
            },
            enabled = true,
        )
    }
}