package com.rumble.battles.subscriptions.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.rumble.battles.commonViews.ChannelRow
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.theme.*

@Composable
fun SubscriptionView(
    modifier: Modifier,
    channelDetailsEntity: ChannelDetailsEntity,
    onChannelClick: (channelId: String) -> Unit
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
                .clickable { onChannelClick(channelDetailsEntity.channelId) },
        ) {
            ChannelRow(
                channelTitle = channelDetailsEntity.channelTitle,
                thumbnail = channelDetailsEntity.thumbnail,
                followers = channelDetailsEntity.followers,
                verifiedBadge = channelDetailsEntity.verifiedBadge
            )
        }
    }
}