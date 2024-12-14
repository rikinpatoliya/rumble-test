package com.rumble.battles.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetHeader
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftEntity
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftType
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.bottomSheepOptionMinHeight
import com.rumble.theme.imageXMedium
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusMedium

@Composable
fun SupportChannelBottomSheet(
    premiumGiftEntity: PremiumGiftEntity,
    onRantsClick: () -> Unit,
    onGiftClick: (premiumGiftEntity: PremiumGiftEntity) -> Unit,
    onClose: () -> Unit
) {

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(RumbleCustomTheme.colors.surface)
    ) {
        BottomSheetHeader(
            modifier = Modifier
                .padding(paddingMedium),
            title = stringResource(R.string.support_channel),
            onClose = onClose
        )
        SupportChannelItemView(
            title = stringResource(R.string.send_rant),
            description = stringResource(R.string.show_support_make_your_message_stand_out),
            iconId = R.drawable.ic_rant,
            onActionClick = onRantsClick
        )
        Divider(
            color = RumbleCustomTheme.colors.backgroundHighlight
        )
        SupportChannelItemView(
            title = stringResource(R.string.gift_subscription),
            description = stringResource(
                if (premiumGiftEntity.type == PremiumGiftType.Premium)
                    R.string.gift_rumble_premium_subscriptions_to_community
                else
                    R.string.gift_channel_subscriptions_to_community
            ),
            iconId = R.drawable.ic_gift,
            onActionClick = { onGiftClick(premiumGiftEntity) }
        )
        Spacer(modifier = Modifier.height(paddingLarge))
    }
}

@Composable
private fun SupportChannelItemView(
    title: String,
    description: String,
    iconId: Int,
    onActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = bottomSheepOptionMinHeight)
            .clickable { onActionClick() }
            .padding(horizontal = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            painter = painterResource(id = iconId),
            size = imageXMedium,
            backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
            tintColor = RumbleCustomTheme.colors.primary,
            contentDescription = title,
        )

        Column {
            Text(
                modifier = Modifier.padding(horizontal = paddingMedium),
                text = title,
                style = h4,
                color = RumbleCustomTheme.colors.primary
            )
            Text(
                modifier = Modifier.padding(horizontal = paddingMedium),
                text = description,
                style = h6Light,
                color = RumbleCustomTheme.colors.secondary
            )
        }

    }
}