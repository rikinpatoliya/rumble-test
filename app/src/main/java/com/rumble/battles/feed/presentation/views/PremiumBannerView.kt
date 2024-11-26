package com.rumble.battles.feed.presentation.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedLite
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXLarge
import com.rumble.theme.imageXXXSmall
import com.rumble.theme.paddingGiant
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen

@Composable
fun PremiumBannerView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(visible = visible) {
        Box(modifier = modifier
            .heightIn(max = imageXXLarge)
            .clip(RoundedCornerShape(radiusSmall))) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.premium_banner_ackground),
                contentDescription = stringResource(id = R.string.rumble_premium_plan),
                contentScale = ContentScale.FillWidth
            )

            IconButton(
                modifier = Modifier
                    .padding(paddingXXSmall)
                    .align(Alignment.TopEnd)
                    .size(imageXXXSmall),
                onClick = {
                    visible = false
                    onDismiss()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = enforcedLite
                )
            }

            Column(modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(vertical = paddingXXSmall, horizontal = paddingMedium)
                .clickable { onClick() },
                verticalArrangement = Arrangement.spacedBy(paddingXXSmall)
            ) {
                Text(
                    text = stringResource(id = R.string.rumble_premium),
                    style = RumbleTypography.h4,
                    color = enforcedWhite
                )

                Text(
                    modifier = Modifier.padding(end = paddingGiant),
                    text = stringResource(id = R.string.enjoy_ad_free_viewing),
                    style = RumbleTypography.tinyBody,
                    color = enforcedWhite
                )

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.padding(end = paddingSmall),
                        text = stringResource(id = R.string.upgrade_now),
                        style = RumbleTypography.h6,
                        color = rumbleGreen
                    )

                    Image(painter = painterResource(id = R.drawable.ic_long_arrow_right), contentDescription = "")
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        PremiumBannerView()
    }
}