package com.rumble.battles.bottomSheets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.BottomSheetHeader
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.landing.RumbleActivityAlertReason
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.radiusMedium

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PremiumSubscriptionDetailsBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
    onClose: () -> Unit
) {
    val uiState by contentHandler.subscriptionUiState

    BackHandler(bottomSheetState.isVisible) {
        onClose()
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.padding(paddingMedium)
        ) {
            BottomSheetHeader(
                title = stringResource(R.string.subscription_details),
                onClose = onClose
            )
            SubscriptionDetailsRow(
                modifier = Modifier.padding(top = paddingXLarge),
                title = stringResource(id = R.string.plan),
                details = uiState.plan
            )
            Divider(
                modifier = Modifier.padding(vertical = paddingSmall),
                color = MaterialTheme.colors.onSurface
            )
            SubscriptionDetailsRow(
                title = stringResource(id = R.string.member_since),
                details = uiState.memberSinceDate
            )
            Divider(
                modifier = Modifier.padding(vertical = paddingSmall),
                color = MaterialTheme.colors.onSurface
            )
            SubscriptionDetailsRow(
                title = stringResource(id = R.string.expiration),
                details = uiState.expirationDate
            )
            Divider(
                modifier = Modifier.padding(vertical = paddingSmall),
                color = MaterialTheme.colors.onSurface
            )
            SubscriptionDetailsRow(
                title = stringResource(id = R.string.payment),
                details = uiState.paymentMethod
            )
            Text(
                modifier = Modifier.padding(vertical = paddingLarge),
                text = stringResource(id = R.string.manage_subscription_description_text),
                color = MaterialTheme.colors.secondary,
                style = h6Light
            )
            ActionButton(
                modifier = Modifier
                    .padding(bottom = paddingXLarge)
                    .fillMaxWidth(),
                textModifier = Modifier.padding(vertical = paddingMedium),
                backgroundColor = MaterialTheme.colors.onSurface,
                borderColor = MaterialTheme.colors.onSurface,
                text = stringResource(id = R.string.manage_subscription),
                textColor = MaterialTheme.colors.primary,
                textStyle = h3,
                onClick = {
                    if (uiState.isSubscribedFromApple) {
                        activityHandler.onShowAlertDialog(RumbleActivityAlertReason.AppleInAppSubscription)
                    } else {
                        activityHandler.onOpenWebView(PremiumSubscription.RESTORE_SUBSCRIPTION_LINK)
                    }
                }
            )
        }
    }
}

@Composable
private fun SubscriptionDetailsRow(
    modifier: Modifier = Modifier,
    title: String,
    details: String
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            style = h4
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = details,
            color = MaterialTheme.colors.secondary,
            style = h5
        )
    }
}
