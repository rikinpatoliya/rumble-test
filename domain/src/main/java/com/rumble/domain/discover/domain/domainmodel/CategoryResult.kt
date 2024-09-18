package com.rumble.domain.discover.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class CategoryResult {
    data class Success(
        val category: CategoryEntity,
        val subcategoryList: List<CategoryEntity>
    ): CategoryResult()

    data class Failure(val rumbleError: RumbleError): CategoryResult()
}
