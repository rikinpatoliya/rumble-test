package com.rumble.domain.search.domain.domainModel

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

data class SearchResult(
    val channelList: List<ChannelDetailsEntity> = emptyList(),
    val videoList: List<VideoEntity> = emptyList()
)