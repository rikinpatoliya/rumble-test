package com.rumble.battles.bottomSheets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.PremiumPromoTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.enforcedDarkest
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.paddingXXXXLarge
import com.rumble.theme.radiusXMedium

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PremiumOptionsBottomSheet(
    bottomSheetState: ModalBottomSheetState,
    isPremiumUser: Boolean,
    onClose: () -> Unit = {},
    onActionButtonClicked: () -> Unit
) {
    BackHandler(bottomSheetState.isVisible) {
        onClose()
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (bottomSheetState.isVisible.not())
            onClose()
    }

    Box(
        modifier = Modifier
            .testTag(PremiumPromoTag)
            .systemBarsPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(enforcedDarkest)
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.premium_options_sheet_background),
            contentDescription = ""
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawerCloseIndicatorView(modifier = Modifier.padding(top = paddingSmall))

            if (!isPremiumUser) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = paddingMedium)
                        .clickable { onClose() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = enforcedWhite
                )
            }

            Text(
                modifier = Modifier.padding(top = if (isPremiumUser) paddingLarge else paddingXSmall10),
                text = stringResource(id = if (isPremiumUser) R.string.rumble_premium_plan else R.string.rumble_premium),
                color = enforcedWhite,
                style = h1
            )

            Text(
                modifier = Modifier
                    .padding(top = paddingSmall)
                    .padding(horizontal = paddingXXXXLarge),
                text = stringResource(id = R.string.enjoy_ad_free_viewing),
                color = enforcedWhite,
                style = h5,
                textAlign = TextAlign.Center
            )

            ActionButton(
                modifier = Modifier
                    .padding(vertical = paddingXXXLarge, horizontal = paddingLarge)
                    .fillMaxWidth(),
                textModifier = Modifier.padding(vertical = paddingMedium),
                text = stringResource(id = if (isPremiumUser) R.string.manage_subscription else R.string.get_premium),
                textColor = enforcedDarkmo,
                textStyle = h3,
                onClick = onActionButtonClicked
            )
        }
    }
}