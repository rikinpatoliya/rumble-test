package com.rumble.battles.library.di

import com.rumble.domain.library.model.datasource.PlayListRemoteDataSource
import com.rumble.domain.library.model.datasource.PlayListRemoteDataSourceImpl
import com.rumble.domain.library.model.repository.PlayListRepository
import com.rumble.domain.library.model.repository.PlayListRepositoryImpl
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryModule {

    @Provides
    @Singleton
    fun providePlayListRemoteDataSource(
        videoApi: VideoApi
    ): PlayListRemoteDataSource =
        PlayListRemoteDataSourceImpl(
            videoApi,
            Dispatchers.IO
        )

    @Provides
    @Singleton
    fun providePlayListRepository(
        playlistRemoteDataSource: PlayListRemoteDataSource
    ): PlayListRepository =
        PlayListRepositoryImpl(
            playlistRemoteDataSource
        )
}