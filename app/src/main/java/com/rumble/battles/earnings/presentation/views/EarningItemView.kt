package com.rumble.battles.earnings.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium

@Composable
fun EarningItemView(
    modifier: Modifier = Modifier.padding(top = paddingMedium),
    label: String,
    value: String,
    labelStyle: TextStyle = RumbleTypography.body1,
    valueStyle: TextStyle = RumbleTypography.body1Bold
) {
    Row(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = labelStyle
        )
        Text(
            text = value,
            style = valueStyle
        )
    }
}