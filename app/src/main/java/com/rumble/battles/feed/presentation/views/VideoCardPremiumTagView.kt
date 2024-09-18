package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun VideoCardPremiumTagView(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXXSmall))
            .background(color = MaterialTheme.colors.onSurface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = paddingXXXSmall, horizontal = paddingXXSmall)
                .size(imageXXSmall)
                .clip(RoundedCornerShape(radiusXSmall))
                .background(color = rumbleGreen)
        ) {
            Icon(
                modifier = Modifier
                    .padding(paddingXXXXSmall)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_discover),
                contentDescription = stringResource(id = R.string.premium_only),
                tint = MaterialTheme.colors.onSurface
            )
        }
        Text(
            modifier = Modifier.padding(end = paddingXSmall),
            text = stringResource(id = R.string.premium_only),
            color = rumbleGreen,
            style = RumbleTypography.h6
        )
    }
}