package com.rumble.domain.discover.domain.domainmodel

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import kotlinx.coroutines.flow.Flow

data class DiscoverPlayerVideoResult(
    val videoList: Flow<PagingData<Feed>>,
    val scrollToIndex: Int = -1,
    val videoEntity: VideoEntity? = null
)