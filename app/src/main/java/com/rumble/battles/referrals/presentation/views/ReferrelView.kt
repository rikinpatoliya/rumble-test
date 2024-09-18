package com.rumble.battles.referrals.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.referrals.domain.domainmodel.ReferralEntity
import com.rumble.theme.*
import com.rumble.utils.extension.toCurrencyString

@Composable
fun ReferralView(
    modifier: Modifier = Modifier,
    referral: ReferralEntity,
    backgroundColor: Color,
    onChannelClick: (channelId: String) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onChannelClick.invoke(referral.id) }
            .height(referralListItemHeight)
            .background(backgroundColor, RoundedCornerShape(radiusMedium))
            .fillMaxWidth()
            .padding(horizontal = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImageComponent(
            modifier = Modifier
                .padding(vertical = paddingXSmall),
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = referral.username,
            userPicture = referral.thumb ?: ""
        )
        Text(
            modifier = Modifier
                .padding(horizontal = paddingMedium)
                .weight(1f),
            text = referral.username,
            style = RumbleTypography.body1
        )
        Text(
            text = referral.commission.toCurrencyString(referral.currencySymbol),
            style = RumbleTypography.body1Bold
        )
    }
}
