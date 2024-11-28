package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.R

@Composable
fun PremiumTag(
    modifier: Modifier = Modifier,
    hasLiveGate: Boolean
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .background(enforcedDarkmo, shape = RoundedCornerShape(radiusXXSmall))
            .padding(vertical = paddingXXXSmall, horizontal = paddingXXSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_premium_content),
            contentDescription = stringResource(id = if (hasLiveGate) R.string.premium else R.string.premium_only),
            tint = rumbleGreen
        )
        Spacer(modifier = Modifier.width(paddingXXSmall))
        Text(
            text = stringResource(id = if (hasLiveGate) R.string.premium else R.string.premium_only),
            color = enforcedWhite,
            style = RumbleTypography.tinyBodySemiBold
        )
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PremiumTag(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(paddingLarge),
                false
            )
        }
    }
}