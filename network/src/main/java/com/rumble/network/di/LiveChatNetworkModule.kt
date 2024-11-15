package com.rumble.network.di

import android.webkit.URLUtil
import com.rumble.network.api.EmoteApi
import com.rumble.network.api.LiveChatApi
import com.rumble.network.dto.livechat.LiveChatErrorResponse
import com.rumble.network.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object LiveChatNetworkModule {

    @Provides
    @LiveRetrofit
    fun provideRetrofit(
        @NetworkHttpClient httpClient: OkHttpClient,
        sessionManager: SessionManager
    ): Retrofit? {
        val chatEndpoint = "${runBlocking { sessionManager.chatEndPointFlow.first() }}/"
        return if (URLUtil.isValidUrl(chatEndpoint)) {
            Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(chatEndpoint)
                .build()
        } else null
    }

    @Provides
    fun provideLiveChatApi(@LiveRetrofit retrofit: Retrofit?): LiveChatApi? =
        retrofit?.create(LiveChatApi::class.java)

    @Provides
    fun provideErrorBodyConverter(@LiveRetrofit retrofit: Retrofit?): Converter<ResponseBody, LiveChatErrorResponse>? =
        retrofit?.responseBodyConverter(LiveChatErrorResponse::class.java, emptyArray())

    @Provides
    fun provideEmoteApi(@NetworkRetrofit retrofit: Retrofit): EmoteApi =
        retrofit.create(EmoteApi::class.java)
}