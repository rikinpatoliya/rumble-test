package com.rumble.network.di

import com.rumble.network.api.EventApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.harkema.retrofitcurlprinter.RetrofitCurlPrinterInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

    private const val EVENT_BASE_URL = "https://e.rumble.com/"

    @Singleton
    @Provides
    @EventHttpClient
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(curlLoggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    @EventRetrofit
    fun provideRetrofit(
        @EventHttpClient httpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(EVENT_BASE_URL)
        .build()

    @Provides
    @Singleton
    @EventDebugRetrofit
    fun provideDebugRetrofit(
        @EventHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideEventApi(@EventRetrofit retrofit: Retrofit): EventApi =
        retrofit.create(EventApi::class.java)
}