package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.model.repository.SearchRepository
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import javax.inject.Inject

class SearchCombineUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        sortType: SortType? = null,
        dateType: FilterType? = null,
        durationType: DurationType? = null
    ) = searchRepository.searchCombined(query, sortType, dateType, durationType)
}