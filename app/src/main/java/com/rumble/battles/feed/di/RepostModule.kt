package com.rumble.battles.feed.di

import com.rumble.domain.repost.model.datasource.remote.RepostRemoteDataSource
import com.rumble.domain.repost.model.datasource.remote.RepostRemoteDataSourceImpl
import com.rumble.domain.repost.model.repository.RepostRepository
import com.rumble.domain.repost.model.repository.RepostRepositoryImpl
import com.rumble.network.api.RepostApi
import com.rumble.network.di.IoDispatcher
import com.rumble.network.dto.livechat.ErrorResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepostModule {

    @Provides
    @Singleton
    fun provideRepostRemoteDataSource(
        repostApi: RepostApi,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): RepostRemoteDataSource =
        RepostRemoteDataSourceImpl(
            repostApi = repostApi,
            dispatcher = dispatcher,
        )

    @Provides
    @Singleton
    fun provideRepostRepository(
        repostRemoteDataSource: RepostRemoteDataSource,
        errorConverter: Converter<ResponseBody, ErrorResponse>?,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): RepostRepository = RepostRepositoryImpl(
        remoteDataSource = repostRemoteDataSource,
        errorConverter = errorConverter,
        dispatcher = dispatcher
    )
}