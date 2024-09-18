package com.rumble.domain.common.model

import androidx.paging.PagingConfig
import com.rumble.utils.RumbleConstants.PAGINATION_PAGE_SIZE

fun getRumblePagingConfig(
    pageSize: Int = PAGINATION_PAGE_SIZE,
    enablePlaceholders: Boolean = false,
): PagingConfig {
    return PagingConfig(
        pageSize = pageSize,
        initialLoadSize = pageSize,
        prefetchDistance = pageSize / 2,
        enablePlaceholders = enablePlaceholders
    )
}