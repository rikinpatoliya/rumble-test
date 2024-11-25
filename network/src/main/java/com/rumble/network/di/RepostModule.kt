package com.rumble.network.di

import com.rumble.network.api.RepostApi
import com.rumble.network.interceptors.ApiVersionInterceptor
import com.rumble.network.interceptors.HeadersInterceptor
import com.rumble.network.interceptors.PerformanceInterceptor
import com.rumble.network.interceptors.QueryInterceptor
import com.rumble.network.interceptors.ResponseInterceptor
import com.rumble.network.interceptors.UrlEncodedInterceptor
import com.rumble.network.interceptors.UserAgentInterceptor
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
object RepostModule {

    @Singleton
    @Provides
    @RepostHttpClient
    fun provideRepostOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: HeadersInterceptor,
        queryInterceptor: QueryInterceptor,
        urlEncodedInterceptor: UrlEncodedInterceptor,
        apiVersionInterceptor: ApiVersionInterceptor,
        responseInterceptor: ResponseInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
        performanceInterceptor: PerformanceInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .addInterceptor(urlEncodedInterceptor)
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
    @RepostRetrofit
    fun provideRepostRetrofit(
        @RepostHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    fun provideRepostApi(@RepostRetrofit retrofit: Retrofit): RepostApi =
        retrofit.create(RepostApi::class.java)
}