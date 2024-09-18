package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.domain.domainModel.AutoCompleteQueriesResult
import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class GetAutoCompleteQueriesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(query: String): AutoCompleteQueriesResult =
        searchRepository.getAutoCompleteQueries(query)

}