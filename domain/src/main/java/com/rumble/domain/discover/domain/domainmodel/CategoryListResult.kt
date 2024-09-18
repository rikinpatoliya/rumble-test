package com.rumble.domain.discover.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class CategoryListResult {
    data class Success(val categoryList: List<CategoryEntity>) : CategoryListResult()
    data class Failure(val rumbleError: RumbleError) : CategoryListResult()
}
