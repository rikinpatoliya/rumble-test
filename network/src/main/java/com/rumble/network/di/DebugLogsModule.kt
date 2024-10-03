package com.rumble.network.di

import com.rumble.network.api.DebugLogsApi
import com.rumble.network.interceptors.BannerUserAgentInterceptor
import com.rumble.network.interceptors.DebugEventInterceptor
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
object DebugLogsModule {

    @Singleton
    @Provides
    @DebugHttpClient
    fun provideDebugOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: BannerUserAgentInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
        debugEventInterceptor: DebugEventInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(curlLoggingInterceptor)
            .addInterceptor(debugEventInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    @DebugLogRetrofit
    fun provideRetrofit(
        @DebugHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    @Singleton
    fun provideDebugLogsApi(@DebugLogRetrofit retrofit: Retrofit): DebugLogsApi =
        retrofit.create(DebugLogsApi::class.java)
}