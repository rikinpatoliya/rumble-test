package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.rumbleGreen
import com.rumble.theme.settingItemRowHeight

@Composable
fun ToggleRowView(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    checked: Boolean,
    enabled: Boolean = true,
    addSeparator: Boolean = false,
    onCheckedChange: (enable: Boolean) -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .height(settingItemRowHeight)
                .alpha(if (enabled) 1F else 0.5F)
                .padding(
                    start = paddingMedium,
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(0.8F),
                style = textStyle
            )
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange(it) },
                modifier = Modifier.weight(0.2F),
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = rumbleGreen,
                    checkedTrackColor = rumbleGreen,
                    uncheckedThumbColor = MaterialTheme.colors.primaryVariant,
                    uncheckedTrackColor = MaterialTheme.colors.primaryVariant,
                )
            )
        }
        if (addSeparator) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}

@Preview
@Composable
fun ToggleRowViewPreview() {
    ToggleRowView(
        text = stringResource(id = R.string.push_for_livestreams),
        textStyle = RumbleTypography.body1Bold,
        enabled = false,
        checked = true,
        onCheckedChange = {}
    )
}