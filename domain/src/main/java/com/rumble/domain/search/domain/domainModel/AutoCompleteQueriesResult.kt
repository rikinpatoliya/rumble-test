package com.rumble.domain.search.domain.domainModel

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity

data class AutoCompleteQueriesResult(
    val channelList: List<ChannelDetailsEntity> = emptyList(),
    val categoryList: List<CategoryEntity> = emptyList()
)