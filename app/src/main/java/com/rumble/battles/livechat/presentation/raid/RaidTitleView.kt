package com.rumble.battles.livechat.presentation.raid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.RaidEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h5Medium
import com.rumble.theme.imageXXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall

@Composable
fun RaidTitleView(
    modifier: Modifier = Modifier,
    raidEntity: RaidEntity,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall)
    ) {
        ProfileImageComponent(
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = raidEntity.currentChannelName,
            userPicture = raidEntity.currentChannelAvatar ?: ""
        )

        Icon(
            modifier = Modifier.size(imageXXXSmall),
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = "",
            tint = RumbleCustomTheme.colors.primaryVariant
        )

        ProfileImageComponent(
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
            userName = raidEntity.targetChannelName,
            userPicture = raidEntity.targetChannelAvatar ?: ""
        )

        Column(modifier = Modifier.padding(start = paddingXSmall)) {
            Text(
                text = raidEntity.currentChannelName + " " + stringResource(R.string.is_raiding),
                color = RumbleCustomTheme.colors.primary,
                style = h5Medium
            )

            Text(
                text = raidEntity.targetChannelName,
                color = RumbleCustomTheme.colors.primary,
                style = h4
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    val raidEntity = RaidEntity(
        currentChannelName = "Current channel",
        targetUrl = "",
        targetChannelName = "Target channel",
        targetVideoTitle = "Video Title",
    )

    RumbleTheme {
        RaidTitleView(raidEntity = raidEntity)
    }
}