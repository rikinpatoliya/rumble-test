package com.rumble.domain.discover.model

import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.network.dto.discover.Category

fun Category.getCategoryEntity(): CategoryEntity =
    CategoryEntity(
        title = title,
        thumbnail = thumbnail ?: "",
        viewersNumber = viewersNumber,
        description = description,
        path = path
    )