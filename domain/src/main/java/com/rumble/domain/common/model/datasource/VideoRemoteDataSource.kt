package com.rumble.domain.common.model.datasource

import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionsEntityResult
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoDetailsResult
import com.rumble.domain.feed.domain.domainmodel.video.VoteResponseResult
import com.rumble.network.dto.video.RelatedVideoResponse
import com.rumble.network.queryHelpers.BattlesType
import com.rumble.network.queryHelpers.LiveVideoFront
import com.rumble.network.queryHelpers.Options
import com.rumble.network.queryHelpers.Sort
import com.rumble.network.queryHelpers.VideoCollectionId
import retrofit2.Response

interface VideoRemoteDataSource {
    suspend fun voteVideo(videoEntityId: Long, userVote: UserVote): VoteResponseResult

    suspend fun fetchVideoCollections(): VideoCollectionsEntityResult

    suspend fun fetchBattlesVideos(
        battlesType: BattlesType? = null,
        offset: Int? = null,
        limit: Int? = null,
    ): VideoListResult

    suspend fun fetchLiveVideos(front: LiveVideoFront): VideoListResult

    suspend fun fetchVideoCollection(
        videoCollectionId: VideoCollectionId,
        sort: Sort,
        offset: Int? = null,
        limit: Int? = null,
    ): VideoListResult

    suspend fun fetchVideoDetails(
        videoId: Long,
        options: List<Options>?
    ): VideoDetailsResult

    suspend fun fetchVideoDetails(
        videoUrl: String,
        options: List<Options>?
    ): VideoDetailsResult

    suspend fun fetchRelatedVideoList(videoId: Long): Response<RelatedVideoResponse>
}