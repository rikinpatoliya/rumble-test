package com.rumble.domain.search.domain.useCases

import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.search.model.repository.SearchRepository
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import javax.inject.Inject

class SearchVideosUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(
        query: String,
        sortType: SortType? = null,
        dateType: FilterType? = null,
        durationType: DurationType? = null,
    ) = searchRepository.searchVideos(
        query = query,
        sort = sortType,
        filter = dateType,
        duration = durationType,
        pageSize = getVideoPageSizeUseCase()
    )
}