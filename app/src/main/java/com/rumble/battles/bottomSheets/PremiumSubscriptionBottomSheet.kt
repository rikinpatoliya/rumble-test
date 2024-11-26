package com.rumble.battles.bottomSheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.rumble.battles.PremiumTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.premium.presentation.SubscriptionTypeView
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.enforcedDarkest
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedLite
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingGiant
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXXLarge
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.TAG_URL

@Composable
fun PremiumSubscriptionBottomSheet(
    handler: ContentHandler,
    activityHandler: RumbleActivityHandler
) {
    var selected by remember { mutableStateOf(handler.subscriptionList.firstOrNull()) }
    val notes = buildAnnotatedString {
        withStyle(SpanStyle(color = enforcedLite)) {
            append(stringResource(id = R.string.subscription_will_renew))
        }
        append(" ")
        withStyle(SpanStyle(color = rumbleGreen)) {
            pushStringAnnotation(TAG_URL, stringResource(id = R.string.rumble_mobile_terms))
            append(stringResource(id = R.string.terms_of_service_capital))
            pop()
        }
    }

    Box(
        modifier = Modifier
            .testTag(PremiumTag)
            .systemBarsPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(enforcedDarkest)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.premium_sheet_background),
            contentDescription = ""
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            DrawerCloseIndicatorView(modifier = Modifier.padding(top = paddingSmall))

            Text(
                modifier = Modifier.padding(top = paddingLarge, start = paddingLarge),
                text = stringResource(id = R.string.rumble_premium_plan),
                color = enforcedWhite,
                style = RumbleTypography.h1
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = paddingSmall,
                        start = paddingLarge,
                        end = paddingGiant,
                        bottom = paddingXXXXLarge
                    ),
                text = stringResource(id = R.string.enjoy_ad_free_viewing),
                color = enforcedWhite,
                style = RumbleTypography.h6Light
            )

            handler.subscriptionList.forEach {
                SubscriptionTypeView(
                    modifier = Modifier.padding(
                        horizontal = paddingLarge,
                        vertical = paddingXXSmall
                    ),
                    subscriptionData = it,
                    selected = it == selected,
                    onClick = { newSelection ->
                        selected = newSelection
                    }
                )
            }

            Text(
                modifier = Modifier
                    .padding(horizontal = paddingLarge, vertical = paddingXXSmall)
                    .clickable {
                        activityHandler.onOpenWebView(PremiumSubscription.RESTORE_SUBSCRIPTION_LINK)
                    },
                text = stringResource(id = R.string.restore_subscription),
                color = enforcedLite,
                style = h6
            )

            ClickableText(
                modifier = Modifier
                    .padding(horizontal = paddingLarge)
                    .padding(top = paddingMedium),
                text = notes,
                style = tinyBody,
                onClick = { offset ->
                    notes.getStringAnnotations(start = offset, end = offset).firstOrNull()?.let {
                        activityHandler.onOpenWebView(it.item)
                    }
                }
            )

            ActionButton(
                modifier = Modifier
                    .padding(paddingLarge)
                    .fillMaxWidth(),
                textModifier = Modifier.padding(vertical = paddingMedium),
                text = stringResource(id = R.string.subscribe_now),
                textColor = enforcedDarkmo,
                textStyle = h3,
                onClick = {
                    selected?.let {
                        handler.onSubscribe(it)
                    }
                }
            )
        }
    }
}
