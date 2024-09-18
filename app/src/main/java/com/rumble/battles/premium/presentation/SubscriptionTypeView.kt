package com.rumble.battles.premium.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.R
import com.rumble.battles.commonViews.RadioButton
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData
import com.rumble.domain.premium.domain.domainmodel.SubscriptionType
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.RumbleTypography.tinyBodyBold
import com.rumble.theme.borderXSmall
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen

@Composable
fun SubscriptionTypeView(
    modifier: Modifier = Modifier,
    subscriptionData: PremiumSubscriptionData,
    selected: Boolean,
    onClick: (PremiumSubscriptionData) -> Unit
) {
    ConstraintLayout(modifier = modifier.clickable { onClick(subscriptionData) }) {
        val (content, discount) = createRefs()

        Row(
            modifier = Modifier
                .padding(top = paddingSmall)
                .border(
                    width = borderXSmall,
                    color = if (selected) rumbleGreen else enforcedFiord,
                    shape = RoundedCornerShape(radiusSmall))
                .padding(paddingSmall)
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(paddingXXSmall)) {
                Text(
                    text = getTitle(subscriptionType = subscriptionData.type),
                    color = enforcedBone,
                    style = h6
                )

                Text(
                    text = getPriceString(subscriptionData = subscriptionData),
                    color = enforcedWhite,
                    style = h4
                )

                if (subscriptionData.type == SubscriptionType.Annually && subscriptionData.monthlyPrice.isNullOrEmpty().not()) {
                    Text(
                        text = stringResource(id = R.string.per_month_paying_annually, subscriptionData.monthlyPrice ?: ""),
                        color = enforcedBone,
                        style = tinyBody
                    )
                }
            }

            RadioButton(
                selected = selected,
                selectedColor = enforcedWhite,
                unselectedColor = enforcedFiord
            )
        }

        if (subscriptionData.type == SubscriptionType.Annually) {
            Box(modifier = Modifier
                .padding(end = paddingMedium)
                .clip(RoundedCornerShape(radiusSmall))
                .background(rumbleGreen)
                .constrainAs(discount) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Text(
                    modifier = Modifier.padding(vertical = paddingXXSmall, horizontal = paddingXSmall),
                    text = stringResource(id = R.string.save).uppercase(),
                    style = tinyBodyBold,
                    color = enforcedWhite
                )
            }
        }
    }
}

@Composable
private fun getTitle(subscriptionType: SubscriptionType) =
    when (subscriptionType) {
        SubscriptionType.Annually -> stringResource(id = R.string.annually)
        SubscriptionType.Monthly -> stringResource(id = R.string.monthly)
    }

@Composable
private fun getPriceString(subscriptionData: PremiumSubscriptionData) =
    when (subscriptionData.type) {
        SubscriptionType.Annually -> stringResource(id = R.string.per_year, subscriptionData.price)
        SubscriptionType.Monthly -> stringResource(id = R.string.per_month, subscriptionData.price)
    }
