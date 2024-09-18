package com.rumble.battles.notifications.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.profileItemIconContentPadding

@Composable
fun NotificationItemView(
    modifier: Modifier = Modifier,
    iconId: Int = 0,
    labelId: Int = 0,
    descriptionId: Int = 0,
    onClick: () -> Unit = {},
    trailingView: @Composable (() -> Unit)? = null,
    tint: Color? = null
) {
    Box(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RumbleCustomTheme.colors.backgroundHighlight),
            ) {
                Icon(
                    modifier = Modifier
                        .padding(profileItemIconContentPadding)
                        .size(imageXXSmall),
                    painter = painterResource(id = iconId),
                    contentDescription = stringResource(id = labelId),
                    tint = tint ?: RumbleCustomTheme.colors.primary
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = paddingXSmall),
            ) {
                Text(
                    text = stringResource(id = labelId),
                    color = RumbleCustomTheme.colors.primary,
                    style = RumbleTypography.h4
                )
                Text(
                    text = stringResource(id = descriptionId),
                    color = RumbleCustomTheme.colors.secondary,
                    style = RumbleTypography.h6Light
                )
            }

            Spacer(modifier = Modifier.weight(1F))

            trailingView?.invoke()
        }
    }
}