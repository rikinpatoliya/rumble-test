package com.rumble.domain.video.di

import com.rumble.domain.common.model.datasource.UserRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.video.model.datasource.local.LastPositionDao
import com.rumble.domain.video.model.repository.VideoRepository
import com.rumble.domain.video.model.repository.VideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class VideoModule {

    @Provides
    fun provideLastPositionDao(database: RumbleDatabase): LastPositionDao =
        database.lastPositionDao()

    @Provides
    fun provideVideoRepository(
        lastPositionDao: LastPositionDao,
        userRemoteDataSource: UserRemoteDataSource,
        videoRemoteDataSource: VideoRemoteDataSource
    ): VideoRepository = VideoRepositoryImpl(
        lastPositionDao = lastPositionDao,
        dispatcher = Dispatchers.IO,
        userRemoteDataSource = userRemoteDataSource,
        videoRemoteDataSource = videoRemoteDataSource
    )
}