package com.rumble.battles.videolist.di

import com.rumble.domain.videolist.model.datasource.VideoListRemoteDataSource
import com.rumble.domain.videolist.model.datasource.VideoListRemoteDataSourceImpl
import com.rumble.domain.videolist.model.repository.VideoListRepository
import com.rumble.domain.videolist.model.repository.VideoListRepositoryImpl
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VideoListModule {
    @Provides
    @Singleton
    fun provideVideoListRemoteDataSource(
        videoApi: VideoApi,
    ): VideoListRemoteDataSource =
        VideoListRemoteDataSourceImpl(
            videoApi = videoApi,
            dispatcher = Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideVideoCollectionRepository(
        videoListRemoteDataSource: VideoListRemoteDataSource,
    ): VideoListRepository =
        VideoListRepositoryImpl(
            videoListRemoteDataSource = videoListRemoteDataSource
        )
}