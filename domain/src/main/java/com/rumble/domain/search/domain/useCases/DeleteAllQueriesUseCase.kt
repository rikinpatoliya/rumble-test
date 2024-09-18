package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class DeleteAllQueriesUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke() = searchRepository.deleteAllQueries()
}