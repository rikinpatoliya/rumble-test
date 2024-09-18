package com.rumble.domain.discover.domain.domainmodel

import com.rumble.domain.feed.domain.domainmodel.Feed

data class CategoryListEntity(
    val categoryList: List<CategoryEntity>,
    override val index: Int,
) : Feed