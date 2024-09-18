package com.rumble.domain.feed.domain.domainmodel.channel

import com.rumble.domain.feed.domain.domainmodel.Feed

data class FeaturedChannelsFeedItem(
    override val index: Int,
) : Feed
