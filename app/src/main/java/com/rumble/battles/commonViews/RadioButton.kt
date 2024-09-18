package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.rumbleGreen

@Composable
fun RadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colors.secondary,
    unselectedColor: Color = MaterialTheme.colors.secondaryVariant
) {
    Box(
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_radio),
            contentDescription = stringResource(
                id = R.string.select
            ),
            tint = if (selected) selectedColor else unselectedColor
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