package com.rumble.network.di

import com.rumble.network.api.DebugLogsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugLogsModule {

    @Provides
    @Singleton
    @DebugLogRetrofit
    fun provideRetrofit(
        @PlainHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideDebugLogsApi(@DebugLogRetrofit retrofit: Retrofit) =
        retrofit.create(DebugLogsApi::class.java)
}