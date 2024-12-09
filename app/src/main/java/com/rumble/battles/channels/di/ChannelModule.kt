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
import com.rumble.network.dto.livechat.ErrorResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import retrofit2.Converter
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
        errorConverter: Converter<ResponseBody, ErrorResponse>?
    ): ChannelRemoteDataSource =
        ChannelRemoteDataSourceImpl(
            channelApi,
            videoApi,
            userApi,
            Dispatchers.IO,
            errorConverter,
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