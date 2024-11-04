package com.rumble.network.di

import com.rumble.network.api.LoginApi
import com.rumble.network.interceptors.ApiVersionInterceptor
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
object LoginModule {

    @Singleton
    @Provides
    @LoginHttpClient
    fun provideOkHttpClient(
        urlEncodedInterceptor: UrlEncodedInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        queryInterceptor: QueryInterceptor,
        apiVersionInterceptor: ApiVersionInterceptor,
        responseInterceptor: ResponseInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        curlLoggingInterceptor: RetrofitCurlPrinterInterceptor,
        performanceInterceptor: PerformanceInterceptor,
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
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
    @LoginRetrofit
    fun provideRetrofit(
        @LoginHttpClient httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    fun provideLoginApi(@LoginRetrofit retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)
}