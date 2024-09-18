package com.rumble.battles.channels.di

import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSourceImpl
import com.rumble.domain.channels.model.datasource.local.ChannelFollowDao
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.channels.model.repository.ChannelRepositoryImpl
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.network.api.ChannelApi
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChannelModule {

    @Provides
    @Singleton
    fun provideChannelRemoteDataSource(
        channelApi: ChannelApi,
        videoApi: VideoApi,
        userApi: UserApi,
    ): ChannelRemoteDataSource =
        ChannelRemoteDataSourceImpl(
            channelApi,
            videoApi,
            userApi,
            Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideChannelRepository(
        channelRemoteDataSource: ChannelRemoteDataSource,
        channelViewDao: ChannelViewDao,
        channelFollowDao: ChannelFollowDao,
    ): ChannelRepository =
        ChannelRepositoryImpl(
            channelRemoteDataSource = channelRemoteDataSource,
            channelViewDao = channelViewDao,
            channelFollowDao = channelFollowDao,
            dispatcher = Dispatchers.IO,
        )
}