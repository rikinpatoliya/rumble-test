package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.utils.extension.shortString

@Composable
fun FollowingContentView(
    modifier: Modifier = Modifier,
    likes: Int = 0,
    followers: Int = 0,
    following: Int = 0
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.likes),
                color = MaterialTheme.colors.primaryVariant,
                style = RumbleTypography.h6
            )
            Text(
                text = likes.shortString(),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h4
            )
        }


        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp), color = MaterialTheme.colors.secondaryVariant
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.followers),
                color = MaterialTheme.colors.primaryVariant,
                style = RumbleTypography.h6
            )
            Text(
                text = followers.shortString(),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h4
            )
        }


        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp), color = MaterialTheme.colors.secondaryVariant
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.following),
                color = MaterialTheme.colors.primaryVariant,
                style = RumbleTypography.h6
            )
            Text(
                text = following.shortString(),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h4
            )
        }
    }
}

@Composable
@Preview
fun PreviewFollowingContentView() {

    RumbleTheme {
        FollowingContentView(
            modifier = Modifier.fillMaxWidth(),
            likes = 100,
            followers = 1222,
            following = 204
        )
    }
}