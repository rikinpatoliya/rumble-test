package com.rumble.battles.referrals.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.referrals.domain.domainmodel.ReferralDetailsEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.commissionsGroupHeight
import com.rumble.theme.paddingLarge
import com.rumble.theme.radiusMedium
import com.rumble.utils.extension.toCurrencyString

@Composable
fun ReferralDetailsView(referralDetails: ReferralDetailsEntity) {
    Column(
        modifier = Modifier
            .height(commissionsGroupHeight)
            .fillMaxWidth()
            .border(
                borderXXSmall,
                MaterialTheme.colors.secondaryVariant,
                shape = RoundedCornerShape(radiusMedium)
            )
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(radiusMedium)
            ),
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.onSecondary,
                    shape = RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = referralDetails.commissionTotal.toCurrencyString(referralDetails.currencySymbol),
                style = RumbleTypography.text26Bold
            )
            Text(
                text = stringResource(id = R.string.total_commissions),
                style = RumbleTypography.h6
            )
        }

        Divider(color = MaterialTheme.colors.secondaryVariant, thickness = borderXXSmall)

        Row(
            modifier = Modifier
                .fillMaxHeight(0.25f)
                .fillMaxWidth()
                .weight(.5f)
                .padding(horizontal = paddingLarge),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.referral_link_clicks),
                style = RumbleTypography.h5
            )
            Text(
                text = referralDetails.impressionCount.toString(),
                style = RumbleTypography.h4
            )
        }

        DottedLineDivider(
            Modifier
                .fillMaxWidth()
                .height(borderXXSmall)
                .padding(horizontal = paddingLarge)
        )

        Row(
            modifier = Modifier
                .fillMaxHeight(0.25f)
                .fillMaxWidth()
                .weight(.5f)
                .padding(horizontal = paddingLarge),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.referrals_accepted),
                style = RumbleTypography.h5
            )
            Text(
                text = referralDetails.referrals.count().toString(),
                style = RumbleTypography.h4
            )
        }
    }
}