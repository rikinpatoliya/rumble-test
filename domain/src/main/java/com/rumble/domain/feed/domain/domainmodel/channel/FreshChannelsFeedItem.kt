package com.rumble.domain.feed.domain.domainmodel.channel

import com.rumble.domain.feed.domain.domainmodel.Feed

data class FreshChannelsFeedItem(
    val channels: List<FreshChannel> = emptyList(),
    override val index: Int,
) : Feed