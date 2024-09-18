package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.settingItemRowHeight

@Composable
fun RowItemWithArrowView(
    modifier: Modifier = Modifier,
    text: String,
    label: String = "",
    addSeparator: Boolean = false,
) {
    Column {
        Row(
            modifier = modifier
                .height(settingItemRowHeight)
                .padding(
                    start = paddingMedium,
                    end = paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = RumbleTypography.body1
            )
            Spacer(
                Modifier
                    .weight(1f)
            )
            Text(
                text = label,
                style = RumbleTypography.body1
            )
            Spacer(
                Modifier
                    .width(paddingXXXSmall)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = ""
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