package com.rumble.battles.common.di

import com.rumble.domain.common.model.datasource.UserRemoteDataSource
import com.rumble.domain.common.model.datasource.UserRemoteDataSourceImpl
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSourceImpl
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Provides
    fun provideVideoRemoteDataSource(videoApi: VideoApi): VideoRemoteDataSource =
        VideoRemoteDataSourceImpl(videoApi)

    @Provides
    fun provideUserRemoteDataSource(userApi: UserApi): UserRemoteDataSource =
        UserRemoteDataSourceImpl(userApi)
}