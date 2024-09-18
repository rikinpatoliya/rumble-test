package com.rumble.domain.feed.model.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.channels.model.datasource.VideoPagingSource
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.database.getRoomVideoCollectionView
import com.rumble.domain.database.getVideoCollectionViewCount
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionView
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionViewCount
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannel
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannelsFeedItem
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionsEntityResult
import com.rumble.domain.feed.domain.domainmodel.comments.CommentResult
import com.rumble.domain.feed.domain.domainmodel.comments.CommentVoteResult
import com.rumble.domain.feed.domain.domainmodel.comments.UserComment
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoDetailsResult
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoSource
import com.rumble.domain.feed.domain.domainmodel.video.VoteResponseResult
import com.rumble.domain.feed.domain.domainmodel.video.WatchingNowEntity
import com.rumble.domain.feed.model.PlaylistMapper
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.domain.feed.model.datasource.local.HomeCategoryViewDao
import com.rumble.domain.feed.model.datasource.local.RoomChannelView
import com.rumble.domain.feed.model.datasource.remote.CommentRemoteDataSource
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPagingSource
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPlaylistDataSource
import com.rumble.domain.feed.model.datasource.remote.SubscriptionVideoPagingSource
import com.rumble.domain.feed.model.datasource.remote.VideoCollectionPagingSource
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.feed.model.getWatchingNowEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.collection.VideoCollection
import com.rumble.network.dto.livevideo.LiveReportBody
import com.rumble.network.dto.livevideo.LiveReportBodyData
import com.rumble.network.queryHelpers.Options
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.ZoneOffset

class FeedRepositoryImpl(
    private val videoApi: VideoApi,
    private val channelRemoteDataSource: ChannelRemoteDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val channelViewDao: ChannelViewDao,
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val homeCategoryViewDao: HomeCategoryViewDao,
    private val liveVideoPlaylistDataSource: LiveVideoPlaylistDataSource,
) : FeedRepository {

    override fun fetchLiveFeedList(shortList: Boolean, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                LiveVideoPagingSource(
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                    shortList = shortList
                )
            }).flow
    }

    override suspend fun getLiveFeed(offset: Int, loadSize: Int): Result<List<VideoEntity>> {
        val response = withContext(dispatcher) {
            videoApi.fetchLiveVideoList(offset, loadSize, front = 1)
        }
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.items.map { it.getVideoEntity() })
        else
            Result.failure(IllegalStateException("getLiveFeed failed"))
    }

    override fun fetchSubscriptionFeedList(pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                SubscriptionVideoPagingSource(
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override fun fetchFeedList(videoCollectionType: VideoCollectionType, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                VideoCollectionPagingSource(
                    videoCollectionType = videoCollectionType,
                    videoApi = videoApi,
                    dispatcher = dispatcher
                )
            }).flow.map { pagingData ->
            pagingData.map { feed ->
                if (feed is FreshChannelsFeedItem) {
                    feed.copy(
                        channels = feed.channels.map { it.copy(freshContent = isFresh(it)) }
                    )
                } else feed
            }
        }
    }

    override fun fetchFeedList(id: String, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                VideoPagingSource(
                    id = id,
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                    Sort.DATE
                )
            }).flow
    }

    override suspend fun getFeedList(
        id: String,
        offset: Int,
        loadSize: Int
    ): Result<List<VideoEntity>> {
        val response = withContext(dispatcher) {
            videoApi.fetchVideoCollection(id, Sort.DATE, offset, loadSize)
        }
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.items.map { it.getVideoEntity() })
        else
            Result.failure(IllegalStateException("getFeedList failed"))
    }

    override suspend fun getCollectionList(): Result<List<VideoCollection>> {
        val response = withContext(dispatcher) {
            videoApi.fetchCollectionList()
        }
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.collections)
        else
            Result.failure(IllegalStateException("getCollectionList failed"))
    }

    override suspend fun voteVideo(videoEntityId: Long, userVote: UserVote): VoteResponseResult =
        videoRemoteDataSource.voteVideo(videoEntityId, userVote)

    override suspend fun fetchVideoDetails(
        videoId: Long,
        options: List<Options>?
    ): VideoDetailsResult =
        videoRemoteDataSource.fetchVideoDetails(videoId, options)

    override suspend fun fetchVideoDetails(
        videoUrl: String,
        options: List<Options>?
    ): VideoDetailsResult =
        videoRemoteDataSource.fetchVideoDetails(videoUrl, options)

    override suspend fun reportWatchingLiveStream(
        videoId: Long,
        viewerId: String
    ): Result<WatchingNowEntity> {
        val response = withContext(dispatcher) {
            videoApi.reportWatchingLiveStream(
                LiveReportBody(
                    data = LiveReportBodyData(
                        videoId,
                        viewerId
                    )
                )
            )
        }
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.getWatchingNowEntity())
        else
            Result.failure(IllegalStateException("reportWatchingLiveStream failed"))
    }

    override suspend fun reportVideoPageView(path: String): Boolean {
        val response = withContext(dispatcher) { videoApi.reportVideoPageView(path) }
        return response.isSuccessful
    }

    override suspend fun fetchFreshChannelList(): ChannelListResult {
        return channelRemoteDataSource.fetchFreshChannels()
    }

    override suspend fun fetchChannelView(channelId: String): RoomChannelView? =
        channelViewDao.getByChannelId(channelId = channelId)

    override suspend fun postComment(userComment: UserComment): CommentResult {
        val result = commentRemoteDataSource.postComment(userComment)
        val success = result.body()?.data?.success ?: false
        val error = result.body()?.data?.error
        return CommentResult(
            success = result.isSuccessful and (error == null) and success,
            commentId = result.body()?.data?.commentId,
            tooShort = false,
            error = error
        )
    }

    override suspend fun deleteComment(commentId: Long): CommentResult {
        val result = commentRemoteDataSource.deleteComment(commentId)
        val success = result.body()?.data?.success ?: false
        val error = result.body()?.data?.error
        return CommentResult(
            success = result.isSuccessful and (error == null) and success,
            commentId = result.body()?.data?.commentId,
            tooShort = false,
            error = error
        )
    }

    override suspend fun likeComment(commentId: Long, userVote: UserVote): CommentVoteResult {
        val result = commentRemoteDataSource.likeComment(commentId, userVote)
        return CommentVoteResult(
            success = result.isSuccessful,
            commentId = result.body()?.data?.commentId ?: 0,
            userVote = UserVote.getByVote(result.body()?.data?.vote ?: 0)
        )
    }

    override suspend fun fetchVideoCollections(): VideoCollectionsEntityResult {
        return withContext(dispatcher) { videoRemoteDataSource.fetchVideoCollections() }
    }

    override suspend fun saveVideoCollectionView(videoCollectionView: VideoCollectionView) {
        homeCategoryViewDao.save(roomVideoCollectionView = videoCollectionView.getRoomVideoCollectionView())
    }

    override suspend fun getVideoCollectionViewCounts(userId: String): List<VideoCollectionViewCount> =
        homeCategoryViewDao.getRecentViewCounts(userId = userId)
            .map { it.getVideoCollectionViewCount() }

    override suspend fun removeOlderVideoCollectionViews(userId: String) {
        homeCategoryViewDao.deleteOlderViews(userId = userId)
    }

    override suspend fun fetchLiveVideoPlaylist(url: String): List<VideoSource> {
        val response = liveVideoPlaylistDataSource.fetchLiveVideoPlayList(url)
        return if (response.isSuccessful && response.body() != null) {
            PlaylistMapper.getVideoSourceList(response.body()?.string() ?: "")
        } else {
            emptyList()
        }
    }

    private suspend fun isFresh(channel: FreshChannel): Boolean {
        val uploadTimestamp =
            channel.channelDetailsEntity.latestVideo?.uploadDate?.toEpochSecond(ZoneOffset.UTC) ?: 0
        val view = fetchChannelView(channel.channelDetailsEntity.channelId)
        return (view == null || view.time < uploadTimestamp)
    }
}