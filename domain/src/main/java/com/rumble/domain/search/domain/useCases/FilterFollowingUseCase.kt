package com.rumble.domain.search.domain.useCases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import javax.inject.Inject

class FilterFollowingUseCase @Inject constructor(
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    operator fun invoke(
        filterQuery: String,
        entities: List<CreatorEntity>
    ): List<CreatorEntity> =
        entities.filter {
            val regex = filterQuery.toRegex(RegexOption.IGNORE_CASE)
            val titleMatch = regex.find(it.channelTitle)
            val nameMatch = regex.find(it.name)
            titleMatch != null || nameMatch != null
        }
}