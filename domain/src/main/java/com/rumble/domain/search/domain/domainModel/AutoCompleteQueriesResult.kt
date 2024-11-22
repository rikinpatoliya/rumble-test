package com.rumble.domain.search.domain.domainModel

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity

data class AutoCompleteQueriesResult(
    val channelList: List<CreatorEntity> = emptyList(),
    val categoryList: List<CategoryEntity> = emptyList()
)