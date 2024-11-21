package com.rumble.domain.common.model.datasource

import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionsEntityResult
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoDetailsResult
import com.rumble.domain.feed.domain.domainmodel.video.VoteResponseResult
import com.rumble.domain.feed.model.getVideoCollectionEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.NetworkRumbleConstants.USER_HAS_ALREADY_VOTED_ON_THIS_CONTENT_ERROR_CODE
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.video.RelatedVideoResponse
import com.rumble.network.dto.video.Video
import com.rumble.network.dto.video.VideoVoteBody
import com.rumble.network.dto.video.VideoVoteData
import com.rumble.network.queryHelpers.BattlesType
import com.rumble.network.queryHelpers.LiveVideoFront
import com.rumble.network.queryHelpers.Options
import com.rumble.network.queryHelpers.Sort
import com.rumble.network.queryHelpers.VideoCollectionId
import retrofit2.Response

private const val TAG = "VideoRemoteDataSourceImpl"

class VideoRemoteDataSourceImpl(
    private val videoApi: VideoApi
) : VideoRemoteDataSource {

    override suspend fun fetchVideoCollections(): VideoCollectionsEntityResult {
        val response = videoApi.fetchCollectionListWithoutVideos()
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            VideoCollectionsEntityResult.Success(
                videoCollections = body.data.collections.map { it.getVideoCollectionEntity() }
            )
        } else {
            VideoCollectionsEntityResult.Failure(
                rumbleError = RumbleError(TAG, response = response.raw())
            )
        }
    }

    override suspend fun fetchBattlesVideos(
        battlesType: BattlesType?,
        offset: Int?,
        limit: Int?,
    ): VideoListResult {
        val response = videoApi.fetchBattlesVideos(battlesType = battlesType)

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoListResult.Success(
                videoList = body.data.items.filterIsInstance<Video>().map { it.getVideoEntity() }
            )
        } else {
            VideoListResult.Failure(
                rumbleError = RumbleError(TAG, response = response.raw())
            )
        }
    }

    override suspend fun fetchLiveVideos(front: LiveVideoFront): VideoListResult {
        val response = videoApi.fetchLiveVideos(front = front.value)

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoListResult.Success(
                videoList = body.data.items.filterIsInstance<Video>().map { it.getVideoEntity() }
            )
        } else {
            VideoListResult.Failure(
                rumbleError = RumbleError(TAG, response = response.raw())
            )
        }
    }

    override suspend fun fetchVideoCollection(
        videoCollectionId: VideoCollectionId,
        sort: Sort,
        offset: Int?,
        limit: Int?
    ): VideoListResult {
        val response = videoApi.fetchVideoCollection(
            id = videoCollectionId.value,
            sortType = sort,
            offset = offset,
            limit = limit
        )

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            val collection = body.data.items.filterIsInstance<Video>().map { it.getVideoEntity() }
            if (collection.isEmpty()) {
                VideoListResult.Failure(
                    rumbleError = RumbleError(TAG, response = response.raw())
                )
            }
            else {
                VideoListResult.Success(videoList = collection)
            }
        } else {
            VideoListResult.Failure(
                rumbleError = RumbleError(TAG, response = response.raw())
            )
        }
    }

    override suspend fun fetchVideoDetails(
        videoId: Long,
        options: List<Options>?
    ): VideoDetailsResult {
        val response = videoApi.fetchVideoDetails(id = videoId, options = options?.joinToString(separator = ","))
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoDetailsResult.VideoDetailsSuccess(body.data.getVideoEntity())
        } else {
            VideoDetailsResult.VideoDetailsError(RumbleError(TAG, response.raw()))
        }
    }

    override suspend fun fetchVideoDetails(
        videoUrl: String,
        options: List<Options>?
    ): VideoDetailsResult {
        val response = videoApi.fetchVideoDetails(url = videoUrl, options = options?.joinToString(separator = ","))
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoDetailsResult.VideoDetailsSuccess(body.data.getVideoEntity())
        } else {
            VideoDetailsResult.VideoDetailsError(RumbleError(TAG, response.raw()))
        }
    }

    override suspend fun voteVideo(videoEntityId: Long, userVote: UserVote): VoteResponseResult {
        val vote = VideoVoteBody(
            data = VideoVoteData(
                videoId = videoEntityId,
                vote = userVote.value
            )
        )
        val response = videoApi.likeVideo(vote)
        val success = response.isSuccessful || response.code() == USER_HAS_ALREADY_VOTED_ON_THIS_CONTENT_ERROR_CODE
        return VoteResponseResult(
            success = success,
            rumbleError = if (success.not()) RumbleError(tag = TAG, response = response.raw()) else null
        )
    }

    override suspend fun fetchRelatedVideoList(videoId: Long): Response<RelatedVideoResponse> =
        videoApi.fetchRelatedVideoList(videoId)
}