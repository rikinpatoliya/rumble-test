package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.dto.collection.VideoCollection
import javax.inject.Inject

class GetCollectionUseCase @Inject constructor(
    private val feedRepository: FeedRepository
){

    suspend operator fun invoke(): List<VideoCollection> {
        return feedRepository.getCollectionList().getOrDefault(emptyList())
    }
}