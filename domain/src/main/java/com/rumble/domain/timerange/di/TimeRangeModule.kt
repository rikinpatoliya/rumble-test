package com.rumble.domain.timerange.di

import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.timerange.model.datasource.remote.TimeRangeRemoteDataSource
import com.rumble.domain.timerange.model.datasource.remote.TimeRangeRemoteDataSourceImpl
import com.rumble.domain.timerange.model.repository.TimeRangeRepository
import com.rumble.domain.timerange.model.repository.TimeRangeRepositoryImpl
import com.rumble.network.api.VideoApi
import com.rumble.network.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object TimeRangeModule {

    @Provides
    fun provideTimeRangeRemoteDataSource(
        videoApi: VideoApi
    ): TimeRangeRemoteDataSource =
        TimeRangeRemoteDataSourceImpl(videoApi)

    @Provides
    fun provideTimeRangeRepository(
        database: RumbleDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        timeRangeRemoteDataSource: TimeRangeRemoteDataSource
    ): TimeRangeRepository =
        TimeRangeRepositoryImpl(ioDispatcher, database.timeRangeDao(), timeRangeRemoteDataSource)
}