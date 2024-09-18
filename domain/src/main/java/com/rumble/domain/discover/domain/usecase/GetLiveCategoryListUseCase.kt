package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.model.repository.DiscoverRepository
import javax.inject.Inject

class GetLiveCategoryListUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(limit: Int? = null) =
        when (val result = discoverRepository.getCategoryList(limit = limit)) {
            is CategoryListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }

            is CategoryListResult.Success -> result
        }
}