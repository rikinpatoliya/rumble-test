package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.WatchingNowEntity
import com.rumble.domain.feed.model.repository.FeedRepository
import javax.inject.Inject


class ReportWatchingLiveStreamUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(videoEntity: VideoEntity, viewerId: String): WatchingNowEntity? {
        return feedRepository.reportWatchingLiveStream(videoEntity.id, viewerId).getOrNull()
    }
}