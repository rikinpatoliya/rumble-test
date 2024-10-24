package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.tinyBodyNormal
import com.rumble.theme.darkGreen
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun PremiumOnlyContentView(
    modifier: Modifier = Modifier,
    onSubscribe: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(
                Brush.horizontalGradient(
                    colors = listOf(darkGreen, rumbleGreen)
                )
            )
            .padding(horizontal = paddingSmall, vertical = paddingXXSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.premium_only_content),
                color = enforcedWhite,
                style = h4
            )

            Text(
                text = stringResource(R.string.subscribe_to_rumble_premium),
                color = enforcedWhite,
                style = tinyBodyNormal,
            )
        }

        ActionButton(
            text = stringResource(R.string.subscribe),
            textColor = darkGreen,
            backgroundColor = enforcedWhite,
            borderColor = enforcedWhite,
            onClick = onSubscribe
        )
    }
}

@Composable
@Preview
private fun PreviewLight() {
    RumbleTheme {
        PremiumOnlyContentView()
    }
}