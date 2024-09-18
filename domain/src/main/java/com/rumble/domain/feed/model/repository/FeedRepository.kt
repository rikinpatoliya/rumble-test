package com.rumble.domain.feed.model.repository

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionView
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionViewCount
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
import com.rumble.domain.feed.model.datasource.local.RoomChannelView
import com.rumble.network.dto.collection.VideoCollection
import com.rumble.network.queryHelpers.Options
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    fun fetchLiveFeedList(shortList: Boolean = false, pageSize: Int): Flow<PagingData<Feed>>
    suspend fun getLiveFeed(offset: Int, loadSize: Int): Result<List<VideoEntity>>
    fun fetchSubscriptionFeedList(pageSize: Int): Flow<PagingData<Feed>>
    fun fetchFeedList(videoCollectionType: VideoCollectionType, pageSize: Int): Flow<PagingData<Feed>>
    fun fetchFeedList(id: String, pageSize: Int): Flow<PagingData<Feed>>
    suspend fun getFeedList(id: String, offset: Int, loadSize: Int): Result<List<VideoEntity>>
    suspend fun getCollectionList(): Result<List<VideoCollection>>
    suspend fun voteVideo(videoEntityId: Long, userVote: UserVote): VoteResponseResult
    suspend fun fetchVideoDetails(videoId: Long, options: List<Options>? = null): VideoDetailsResult
    suspend fun fetchVideoDetails(
        videoUrl: String,
        options: List<Options>? = null,
    ): VideoDetailsResult

    suspend fun reportWatchingLiveStream(videoId: Long, viewerId: String): Result<WatchingNowEntity>
    suspend fun reportVideoPageView(path: String): Boolean
    suspend fun fetchFreshChannelList(): ChannelListResult
    suspend fun fetchChannelView(channelId: String): RoomChannelView?
    suspend fun postComment(userComment: UserComment): CommentResult
    suspend fun deleteComment(commentId: Long): CommentResult
    suspend fun likeComment(commentId: Long, userVote: UserVote): CommentVoteResult
    suspend fun fetchVideoCollections(): VideoCollectionsEntityResult
    suspend fun saveVideoCollectionView(videoCollectionView: VideoCollectionView)
    suspend fun getVideoCollectionViewCounts(userId: String): List<VideoCollectionViewCount>
    suspend fun removeOlderVideoCollectionViews(userId: String)
    suspend fun fetchLiveVideoPlaylist(url: String): List<VideoSource>
}