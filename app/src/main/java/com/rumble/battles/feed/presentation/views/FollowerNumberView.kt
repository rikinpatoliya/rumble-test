package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.extension.shortString

@Composable
@Preview
fun FollowerNumberView(
    modifier: Modifier = Modifier,
    followers: Int = 0
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.padding(end = paddingXXXSmall),
            text = followers.shortString(),
            color = MaterialTheme.colors.primaryVariant,
            style = h6
        )

        Text(
            text = stringResource(id = R.string.followers),
            color = MaterialTheme.colors.primaryVariant,
            style = h6
        )
    }
}