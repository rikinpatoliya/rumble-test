package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.discover.model.repository.DiscoverRepository
import javax.inject.Inject

class GetCategoryLiveVideoListUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
)  {
    operator fun invoke() = discoverRepository.fetchCategoryLiveVideoList(pageSize = getVideoPageSizeUseCase())
}