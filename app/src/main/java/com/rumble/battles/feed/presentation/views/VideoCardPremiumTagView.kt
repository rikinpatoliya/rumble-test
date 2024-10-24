package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXSmall

@Composable
fun VideoCardPremiumTagView(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXXSmall))
            .background(RumbleCustomTheme.colors.subtleHighlight)
            .padding(vertical = paddingXXXSmall, horizontal = paddingXSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingXXSmall)
    ) {
        Image(
            modifier = Modifier.size(imageXXSmall),
            painter = painterResource(R.drawable.ic_premium_content),
            contentDescription = stringResource(R.string.premium),
        )

        androidx.compose.material3.Text(
            text = stringResource(R.string.premium),
            color = RumbleCustomTheme.colors.primary,
            style = h6,
        )
    }
}