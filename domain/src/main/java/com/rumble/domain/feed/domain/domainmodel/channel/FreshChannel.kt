package com.rumble.domain.feed.domain.domainmodel.channel

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity

data class FreshChannel(
    val channelDetailsEntity: ChannelDetailsEntity,
    val freshContent: Boolean = false
)