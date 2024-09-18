package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryResult
import com.rumble.domain.discover.model.repository.DiscoverRepository
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(categoryPath: String) =
        when (val result = discoverRepository.getCategory(categoryPath)) {
            is CategoryResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }

            is CategoryResult.Success -> result
        }
}