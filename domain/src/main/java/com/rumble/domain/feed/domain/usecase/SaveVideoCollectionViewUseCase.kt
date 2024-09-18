package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionView
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class SaveVideoCollectionViewUseCase @Inject constructor(
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    private val feedRepository: FeedRepository,
    private val sessionManager: SessionManager,
) : RumbleUseCase {

    suspend operator fun invoke(collectionType: VideoCollectionType) {
        when (collectionType) {
            VideoCollectionType.MyFeed -> {
                //Do nothing because my feed is always first and has no ID so nothing to save
            }
            is VideoCollectionType.VideoCollectionEntity -> {
                feedRepository.saveVideoCollectionView(
                    videoCollectionView = VideoCollectionView(
                        videoCollectionName = collectionType.id,
                        viewTimestamp = Date(),
                        userId = sessionManager.userIdFlow.first()
                    )
                )
            }
        }

    }

}