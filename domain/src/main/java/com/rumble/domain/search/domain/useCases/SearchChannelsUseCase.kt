package com.rumble.domain.search.domain.useCases

import com.rumble.domain.search.model.repository.SearchRepository
import javax.inject.Inject

class SearchChannelsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(query: String) = searchRepository.searchChannels(query)
}