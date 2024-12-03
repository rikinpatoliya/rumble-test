package com.rumble.battles.feed.presentation.repost

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.IsTablet
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.titleLarge
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingUpsellMessageTablet
import com.rumble.theme.paddingXLarge
import com.rumble.theme.repostUpsellIcon
import com.rumble.theme.repostUpsellIconTablet
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun RepostUpsellScreen(
    onGoPremium: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val isTablet = IsTablet()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(RumbleCustomTheme.colors.background)
    ) {
        val contentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .padding(horizontal = contentPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isTablet.not()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.padding(paddingMedium),
                        onClick = onClose
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.close),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                Image(
                    modifier = Modifier
                        .size(if (isTablet) repostUpsellIconTablet else repostUpsellIcon)
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.repost_render),
                    contentDescription = stringResource(R.string.go_premium)
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall),
                text = stringResource(R.string.want_try_repost),
                style = if (isTablet) titleLarge else h1,
                color = RumbleCustomTheme.colors.primary,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isTablet) paddingUpsellMessageTablet else paddingLarge),
                text = stringResource(R.string.sing_up_to_repost),
                style = body1,
                color = RumbleCustomTheme.colors.secondary,
                textAlign = TextAlign.Center,
            )

            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = paddingLarge)
                    .padding(horizontal = if (isTablet) paddingUpsellMessageTablet else paddingLarge),
                textModifier = Modifier.padding(paddingMedium),
                text = stringResource(R.string.go_premium),
                textStyle = h3,
                textColor = enforcedDarkmo,
                onClick = onGoPremium,
            )

            Text(
                modifier = Modifier
                    .padding(bottom = paddingXLarge)
                    .clickableNoRipple { onClose() },
                text = stringResource(R.string.maybe_later),
                style = h3,
                color = RumbleCustomTheme.colors.primary,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        RepostUpsellScreen()
    }
}

@Composable
@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
private fun PreviewTablet() {
    RumbleTheme {
        RepostUpsellScreen()
    }
}

@Composable
@Preview(name = "10-inch Tablet Portrait", widthDp = 600, heightDp = 960)
private fun PreviewTabletPortrait() {
    RumbleTheme {
        RepostUpsellScreen()
    }
}