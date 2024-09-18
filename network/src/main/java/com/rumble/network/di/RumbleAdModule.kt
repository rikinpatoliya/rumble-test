package com.rumble.network.di

import com.rumble.network.api.PreRollApi
import com.rumble.network.api.RumbleAdsApi
import com.rumble.network.api.RumbleBannerApi
import com.rumble.network.interceptors.BannerUserAgentInterceptor
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
object RumbleAdModule {

    private const val rumbleAdBaseUrl = "https://a-delivery.rmbl.ws/v1/zones/"
    private const val rumbleBannerUrl = "https://a.ads.rmbl.ws/v1/zones/"

    @Singleton
    @Provides
    @PlainHttpClient
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: BannerUserAgentInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(curlLoggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    @RumbleAdRetrofit
    fun provideRetrofit(
        @PlainHttpClient httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(rumbleAdBaseUrl)
            .build()
    }

    @Provides
    @Singleton
    @RumbleBannerRetrofit
    fun provideBannerRetrofit(
        @PlainHttpClient httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(rumbleBannerUrl)
            .build()
    }

    @Provides
    fun provideAdsApi(@RumbleAdRetrofit retrofit: Retrofit): RumbleAdsApi =
        retrofit.create(RumbleAdsApi::class.java)

    @Provides
    fun providePreRollApi(@NetworkRetrofit retrofit: Retrofit): PreRollApi =
        retrofit.create(PreRollApi::class.java)

    @Provides
    fun provideBannerApi(@RumbleBannerRetrofit retrofit: Retrofit): RumbleBannerApi =
        retrofit.create(RumbleBannerApi::class.java)
}
