package com.rumble.battles.discover.di

import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.discover.model.datasource.CategoryDataSource
import com.rumble.domain.discover.model.datasource.CategoryDataSourceImpl
import com.rumble.domain.discover.model.repository.DiscoverRepository
import com.rumble.domain.discover.model.repository.DiscoverRepositoryImpl
import com.rumble.network.api.DiscoverApi
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiscoverModule {

    @Provides
    @Singleton
    fun provideDiscoverRepository(
        channelRemoteDataSource: ChannelRemoteDataSource,
        videoRemoteDataSource: VideoRemoteDataSource,
        categoryDataSource: CategoryDataSource,
        discoverApi: DiscoverApi,
        videoApi: VideoApi,
    ): DiscoverRepository =
        DiscoverRepositoryImpl(
            channelRemoteDataSource = channelRemoteDataSource,
            videoRemoteDataSource = videoRemoteDataSource,
            categoryDataSource = categoryDataSource,
            dispatcher = Dispatchers.IO,
            discoverApi = discoverApi,
            videoApi = videoApi,
        )

    @Provides
    fun provideCategoryDataSource(discoverApi: DiscoverApi): CategoryDataSource =
        CategoryDataSourceImpl(discoverApi)
}