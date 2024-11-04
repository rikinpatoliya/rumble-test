package com.rumble.network.di

import com.rumble.battles.network.BuildConfig
import com.rumble.network.api.CameraApi
import com.rumble.network.api.ChannelApi
import com.rumble.network.api.DiscoverApi
import com.rumble.network.api.ReportApi
import com.rumble.network.api.SearchApi
import com.rumble.network.api.SubscriptionApi
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import com.rumble.network.interceptors.AcceptJsonHeadersInterceptor
import com.rumble.network.interceptors.ApiVersionInterceptor
import com.rumble.network.interceptors.HeadersInterceptor
import com.rumble.network.interceptors.PerformanceInterceptor
import com.rumble.network.interceptors.QueryInterceptor
import com.rumble.network.interceptors.ResponseInterceptor
import com.rumble.network.interceptors.UserAgentInterceptor
import com.rumble.network.subdomain.SyncRumbleSubdomainUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.harkema.retrofitcurlprinter.Logger
import io.harkema.retrofitcurlprinter.RetrofitCurlPrinterInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SCHEMA = "https://"

    @Provides
    fun provideBaseUrl(
        syncRumbleSubdomainUseCase: SyncRumbleSubdomainUseCase,
    ): String {
        val subdomain = runBlocking { syncRumbleSubdomainUseCase() }
        val prefix =
            if (subdomain.isNotEmpty()) subdomain.plus(
                "."
            ) else ""
        return "$SCHEMA$prefix${BuildConfig.BASE_URL}"
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun provideCurlLoggingInterceptor(): RetrofitCurlPrinterInterceptor =
        RetrofitCurlPrinterInterceptor(object : Logger {
            override fun log(message: String) {
                Timber.tag("CurlLogging").i(message)
            }
        })

    @Singleton
    @Provides
    @NetworkHttpClient
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: HeadersInterceptor,
        acceptJsonHeadersInterceptor: AcceptJsonHeadersInterceptor,
        queryInterceptor: QueryInterceptor,
        apiVersionInterceptor: ApiVersionInterceptor,
        responseInterceptor: ResponseInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
        performanceInterceptor: PerformanceInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .addInterceptor(acceptJsonHeadersInterceptor)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(queryInterceptor)
            .addInterceptor(apiVersionInterceptor)
            .addInterceptor(responseInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(curlLoggingInterceptor)
            .addInterceptor(performanceInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    @NetworkRetrofit
    fun provideRetrofit(
        @NetworkHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    fun provideUserApi(@NetworkRetrofit retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    fun provideVideoApi(@NetworkRetrofit retrofit: Retrofit): VideoApi =
        retrofit.create(VideoApi::class.java)

    @Provides
    fun provideChannelApi(@NetworkRetrofit retrofit: Retrofit): ChannelApi =
        retrofit.create(ChannelApi::class.java)

    @Provides
    fun provideSearchApi(@NetworkRetrofit retrofit: Retrofit): SearchApi =
        retrofit.create(SearchApi::class.java)

    @Provides
    fun provideReportApi(@NetworkRetrofit retrofit: Retrofit): ReportApi =
        retrofit.create(ReportApi::class.java)

    @Provides
    fun provideDiscoverApi(@NetworkRetrofit retrofit: Retrofit): DiscoverApi =
        retrofit.create(DiscoverApi::class.java)

    @Provides
    fun provideCameraApi(@NetworkRetrofit retrofit: Retrofit): CameraApi =
        retrofit.create(CameraApi::class.java)

    @Provides
    fun provideSubscriptionApi(@NetworkRetrofit retrofit: Retrofit): SubscriptionApi =
        retrofit.create(SubscriptionApi::class.java)
}