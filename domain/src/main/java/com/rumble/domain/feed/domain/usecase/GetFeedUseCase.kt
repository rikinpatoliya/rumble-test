package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.queryHelpers.VideoCollectionId
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(
        id: VideoCollectionId,
        offset: Int,
        loadSize: Int
    ): List<VideoEntity> {
        return feedRepository.getFeedList(id.value, offset, loadSize).getOrDefault(emptyList())
    }
}