package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.rumbleGreen

@Composable
fun RumbleRadioSelectionRow(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = RumbleCustomTheme.colors.primary,
            style = RumbleTypography.h4
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onSelected()
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_radio),
                contentDescription = stringResource(
                    id = R.string.select
                ),
                tint = if (selected) RumbleCustomTheme.colors.secondary else RumbleCustomTheme.colors.onSecondary
            )
            if (selected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_radio_dot),
                    contentDescription = stringResource(
                        id = R.string.select
                    ),
                    tint = rumbleGreen
                )
            }
        }
    }
}