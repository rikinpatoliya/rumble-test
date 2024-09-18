package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class SaveQueryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(text: String) {
        if (text.isNotBlank()) searchRepository.saveQuery(RecentQuery(query = text.trim()))
    }
}