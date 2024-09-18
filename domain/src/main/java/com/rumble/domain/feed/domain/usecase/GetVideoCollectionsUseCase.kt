package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionResult
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionsEntityResult
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class GetVideoCollectionsUseCase @Inject constructor(
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    private val feedRepository: FeedRepository,
    private val sessionManager: SessionManager,
) : RumbleUseCase {

    suspend operator fun invoke(): VideoCollectionResult {

        val collections = when (val result = feedRepository.fetchVideoCollections()) {
            is VideoCollectionsEntityResult.Failure -> return VideoCollectionResult.Failure(result.rumbleError)
            is VideoCollectionsEntityResult.Success -> result.videoCollections
        }

        val userId: String = sessionManager.userIdFlow.first()

        val viewCountMap = feedRepository.getVideoCollectionViewCounts(userId = userId)
            .associate { it.collectionId to it.count }

        feedRepository.removeOlderVideoCollectionViews(userId = userId)

        val comparator =
            Comparator { a: VideoCollectionType.VideoCollectionEntity, b: VideoCollectionType.VideoCollectionEntity ->
                val aCount = viewCountMap.getOrDefault(key = a.id, defaultValue = 0)
                    .let { if (it < 5) 0 else it }
                val bCount = viewCountMap.getOrDefault(key = b.id, defaultValue = 0)
                    .let { if (it < 5) 0 else it }

                bCount.compareTo(aCount)
            }

        val result =
            if (sessionManager.isUserSignedIn()) mutableListOf<VideoCollectionType>(VideoCollectionType.MyFeed)
            else mutableListOf()
        result.addAll(collections.sortedWith(comparator))

        return VideoCollectionResult.Success(result)
    }

}