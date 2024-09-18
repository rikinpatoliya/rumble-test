package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.*

@Composable
fun AdTagView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(paddingXSmall)
            .clip(RoundedCornerShape(radiusSmall))
            .background(enforcedDarkmo)

    ) {
        Text(
            modifier = Modifier.padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingXXXSmall,
                bottom = paddingXXXSmall
            ),
            text = stringResource(id = R.string.ad).uppercase(),
            style = RumbleTypography.h6Heavy,
            color = enforcedWhite
        )
    }
}