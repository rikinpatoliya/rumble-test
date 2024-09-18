package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class DeleteQueryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(recentQuery: RecentQuery) =
        searchRepository.deleteQuery(recentQuery)
}