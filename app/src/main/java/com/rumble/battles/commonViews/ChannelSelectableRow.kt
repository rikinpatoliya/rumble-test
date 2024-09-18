package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.*

@Composable
fun ChannelSelectableRow(
    modifier: Modifier = Modifier,
    channelId: String,
    channelTitle: String,
    thumbnail: String,
    selected: Boolean = false,
    onSelectChannel: (channelId: String) -> Unit
) {
    Surface(
        modifier = modifier
            .wrapContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(radiusMedium),
        elevation = elevation
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusMedium))
                .clickable { onSelectChannel(channelId) },
        ) {
            Row(
                modifier = Modifier
                    .padding(paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImageComponent(
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                    userName = channelTitle,
                    userPicture = thumbnail
                )

                Text(
                    text = channelTitle,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = paddingXSmall),
                    color = MaterialTheme.colors.primary,
                    style = RumbleTypography.h4
                )

                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = stringResource(id = R.string.selected_channel),
                        tint = rumbleGreen
                    )
                }
            }
        }
    }
}