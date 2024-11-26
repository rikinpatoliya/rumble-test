package com.rumble.battles.common.di

import com.rumble.domain.common.model.datasource.UserRemoteDataSource
import com.rumble.domain.common.model.datasource.UserRemoteDataSourceImpl
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSourceImpl
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.livechat.ErrorResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ResponseBody
import retrofit2.Converter

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Provides
    fun provideVideoRemoteDataSource(
        videoApi: VideoApi,
        errorConverter: Converter<ResponseBody, ErrorResponse>?,
        ): VideoRemoteDataSource =
        VideoRemoteDataSourceImpl(videoApi, errorConverter)

    @Provides
    fun provideUserRemoteDataSource(userApi: UserApi): UserRemoteDataSource =
        UserRemoteDataSourceImpl(userApi)
}