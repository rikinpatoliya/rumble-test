package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.rumble.battles.R
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.radiusXSmall

@Composable
fun PremiumExclusiveContentIconView(
    modifier: Modifier = Modifier,
    iconSize: Dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXSmall))
            .background(color = enforcedDarkmo)
    ) {
        Icon(
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = stringResource(id = R.string.premium_only),
            tint = enforcedWhite
        )
    }
}