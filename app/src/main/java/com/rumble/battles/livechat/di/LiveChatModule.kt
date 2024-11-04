package com.rumble.battles.livechat.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.rumble.domain.billing.model.RumblePurchaseUpdateListener
import com.rumble.domain.livechat.model.datasource.LiveChatRemoteDataSource
import com.rumble.domain.livechat.model.datasource.LiveChatRemoteDataSourceImpl
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import com.rumble.domain.livechat.model.repository.LiveChatRepositoryImpl
import com.rumble.domain.performance.domain.usecase.CreateLiveStreamMetricUseCase
import com.rumble.network.api.EmoteApi
import com.rumble.network.api.LiveChatApi
import com.rumble.network.api.LiveChatEventsApi
import com.rumble.network.di.AppName
import com.rumble.network.di.AppRequestName
import com.rumble.network.di.AppVersion
import com.rumble.network.di.IoDispatcher
import com.rumble.network.di.OsVersion
import com.rumble.network.di.VersionCode
import com.rumble.network.dto.livechat.LiveChatErrorResponse
import com.rumble.network.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LiveChatModule {

    @Provides
    @Singleton
    fun provideUpdateListener(): RumblePurchaseUpdateListener = RumblePurchaseUpdateListener()

    @Provides
    @Singleton
    fun provideBillingClient(
        @ApplicationContext appContext: Context,
        updatedListener: RumblePurchaseUpdateListener
    ): BillingClient = BillingClient.newBuilder(appContext)
        .setListener(updatedListener)
        .enablePendingPurchases()
        .build()

    @Provides
    fun provideLiveChatRemoteSource(
        sessionManager: SessionManager,
        liveChatApi: LiveChatApi?,
        emoteApi: EmoteApi,
        liveChatEventsApi: LiveChatEventsApi,
        @AppName appName: String,
        @VersionCode versionCode: Int,
        @AppRequestName packageName: String,
        @AppVersion appVersion: String,
        @OsVersion osVersion: String,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        createLiveStreamMetricUseCase: CreateLiveStreamMetricUseCase,
    ): LiveChatRemoteDataSource {
        val chatEndpoint = runBlocking { sessionManager.chatEndPointFlow.first() }
        val cookies = runBlocking { sessionManager.cookiesFlow.first() }
        return LiveChatRemoteDataSourceImpl(
            chatEndpoint = chatEndpoint,
            cookies = cookies,
            liveChatApi = liveChatApi,
            emoteApi = emoteApi,
            liveChatEventsApi = liveChatEventsApi,
            appName = appName,
            versionCode = versionCode,
            packageName = packageName,
            appVersion = appVersion,
            osVersion = osVersion,
            dispatcher = dispatcher,
            createLiveStreamMetricUseCase = createLiveStreamMetricUseCase,
        )
    }

    @Provides
    fun provideLiveChatRepository(
        liveChatRemoteDataSource: LiveChatRemoteDataSource,
        errorConverter: Converter<ResponseBody, LiveChatErrorResponse>?,
        baseUrl: String,
        sessionManager: SessionManager,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): LiveChatRepository {
        val userId = runBlocking { sessionManager.userIdFlow.first() }
        return LiveChatRepositoryImpl(liveChatRemoteDataSource, errorConverter, baseUrl, userId, dispatcher)
    }
}