package com.rumble.battles.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.ChannelRow
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXMedium

@Composable
fun ChannelSwitchBottomSheet(
    channelEntityList: List<UserUploadChannelEntity>,
    onChannelClick: (id: String) -> Unit,
    onHideBottomSheet: () -> Unit
) {
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(RumbleCustomTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(paddingMedium))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.channels),
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .align(Alignment.CenterStart),
                    color = RumbleCustomTheme.colors.primary,
                    style = RumbleTypography.h1
                )
                IconButton(
                    onClick = onHideBottomSheet,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close),
                        tint = RumbleCustomTheme.colors.primary
                    )
                }
            }
            channelEntityList.forEachIndexed { index, entity ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(radiusMedium))
                        .clickable {
                            onHideBottomSheet()
                            onChannelClick(entity.id)
                        },
                ) {
                    ChannelRow(
                        channelTitle = entity.title,
                        thumbnail = entity.thumbnail ?: "",
                        followers = entity.followers,
                        verifiedBadge = false
                    )
                }
                if (channelEntityList.lastIndex != index)
                    Divider(
                        color = RumbleCustomTheme.colors.backgroundHighlight
                    )
            }
            Spacer(modifier = Modifier.height(paddingLarge))
        }
    }
}