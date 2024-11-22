package com.rumble.domain.feed.domain.domainmodel.channel

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity

data class FreshChannel(
    val channelDetailsEntity: CreatorEntity,
    val freshContent: Boolean = false
)