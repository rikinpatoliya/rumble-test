package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.sort.SortFilter
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall

@Composable
fun CheckMarkItem(
    sortFilter: SortFilter,
    selected: Boolean,
    addSeparator: Boolean,
    enabled: Boolean = true,
    hasAlpha: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (hasAlpha) 0.5F else 1F)
    ) {
        Row(
            modifier = Modifier.clickable(enabled = enabled) { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = paddingMedium, top = paddingSmall, bottom = paddingSmall),
                text = stringResource(id = sortFilter.nameId),
                style = if (selected) RumbleTypography.body1Bold else RumbleTypography.body1
            )
            if (selected) {
                Image(
                    modifier = Modifier.padding(end = paddingMedium),
                    painter = painterResource(id = R.drawable.ic_check), contentDescription = ""
                )
            }
        }
        if (addSeparator) {
            Divider(
                modifier = Modifier.fillMaxWidth().padding(start = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}

@Composable
fun CheckMarkItem(
    title: String,
    selected: Boolean,
    addSeparator: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingMedium)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(end = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = paddingSmall, bottom = paddingSmall),
                text = title,
                style = if (selected) RumbleTypography.body1Bold else RumbleTypography.body1
            )
            if (selected) {
                Image(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
            }
        }
        if (addSeparator) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}

@Preview
@Composable
fun CheckMarkItemSelectedPreview() {
    CheckMarkItem(
        title = "CheckMarkItem",
        selected = true,
        addSeparator = true,
        onClick = {}
    )
}

@Preview
@Composable
fun CheckMarkItemPreview() {
    CheckMarkItem(
        title = "CheckMarkItem",
        selected = false,
        addSeparator = true,
        onClick = {}
    )
}