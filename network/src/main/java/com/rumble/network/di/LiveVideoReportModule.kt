package com.rumble.network.di

import com.rumble.network.api.LiveVideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object LiveVideoReportModule {

    @Provides
    fun provideLiveVideoApi(@NetworkRetrofit retrofit: Retrofit): LiveVideoApi =
        retrofit.create(LiveVideoApi::class.java)
}