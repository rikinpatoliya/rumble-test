package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class GetFilteredQueriesUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(filter: String): List<RecentQuery> {
        val result =  if (filter.isNotBlank()) searchRepository.filterQueries(filter)
        else searchRepository.getAllQueries()
        return result.sortedWith { q1, q2 ->
            if (q1.query.indexOf(filter) < q2.query.indexOf(filter)) -1
            else if (q1.query.indexOf(filter) > q2.query.indexOf(filter)) 1
            else 0
        }
    }
}