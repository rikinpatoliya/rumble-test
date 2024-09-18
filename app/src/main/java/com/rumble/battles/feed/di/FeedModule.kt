package com.rumble.battles.feed.di

import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.channels.model.datasource.local.ChannelFollowDao
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.domain.feed.model.datasource.local.HomeCategoryViewDao
import com.rumble.domain.feed.model.datasource.remote.CommentRemoteDataSource
import com.rumble.domain.feed.model.datasource.remote.CommentRemoteDataSourceImpl
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPlaylistDataSource
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPlaylistDataSourceImpl
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.domain.feed.model.repository.FeedRepositoryImpl
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Provides
    fun provideHomeCategoryViewTimeDao(database: RumbleDatabase): HomeCategoryViewDao =
        database.homeCategoryViewTimeDao()

    @Provides
    fun provideCommentsRemoteDataSource(videoApi: VideoApi): CommentRemoteDataSource =
        CommentRemoteDataSourceImpl(videoApi)

    @Provides
    fun provideChannelViewDao(database: RumbleDatabase): ChannelViewDao =
        database.channelViewDao()

    @Provides
    fun provideChannelFollowDao(database: RumbleDatabase): ChannelFollowDao =
        database.channelFollowDao()

    @Provides
    fun provideLiveVidePlaylistDataSource(videoApi: VideoApi): LiveVideoPlaylistDataSource =
        LiveVideoPlaylistDataSourceImpl(videoApi)

    @Provides
    @Singleton
    fun provideFeedRepository(
        videoApi: VideoApi,
        channelRemoteDataSource: ChannelRemoteDataSource,
        channelViewDao: ChannelViewDao,
        commentRemoteDataSource: CommentRemoteDataSource,
        videoRemoteDataSource: VideoRemoteDataSource,
        homeCategoryViewDao: HomeCategoryViewDao,
        liveVideoPlaylistDataSource: LiveVideoPlaylistDataSource,
    ): FeedRepository =
        FeedRepositoryImpl(
            videoApi = videoApi,
            channelRemoteDataSource = channelRemoteDataSource,
            dispatcher = Dispatchers.IO,
            channelViewDao = channelViewDao,
            commentRemoteDataSource = commentRemoteDataSource,
            videoRemoteDataSource = videoRemoteDataSource,
            homeCategoryViewDao = homeCategoryViewDao,
            liveVideoPlaylistDataSource = liveVideoPlaylistDataSource,
        )
}