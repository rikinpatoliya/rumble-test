package com.rumble.domain.events.di

import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.events.model.datasource.EventRemoteDataSource
import com.rumble.domain.events.model.datasource.EventRemoteDataSourceImpl
import com.rumble.domain.events.model.repository.EventRepository
import com.rumble.domain.events.model.repository.EventRepositoryImpl
import com.rumble.network.api.DebugLogsApi
import com.rumble.network.api.EventApi
import com.rumble.network.di.AppRequestName
import com.rumble.network.di.AppVersion
import com.rumble.network.di.IoDispatcher
import com.rumble.network.di.OsVersion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object EventsModule {
    @Provides
    fun provideEventRemoteDataSource(
        eventApi: EventApi,
        debugLogsApi: DebugLogsApi,
    ): EventRemoteDataSource =
        EventRemoteDataSourceImpl(eventApi, debugLogsApi)

    @Provides
    fun provideEventRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        eventRemoteDataSource: EventRemoteDataSource,
        @AppRequestName appRequestName: String,
        @AppVersion appVersion: String,
        @OsVersion osVersion: String,
        database: RumbleDatabase,
    ): EventRepository =
        EventRepositoryImpl(
            dispatcher = ioDispatcher,
            eventRemoteDataSource = eventRemoteDataSource,
            appRequestName = appRequestName,
            appVersion = appVersion,
            osVersion = osVersion,
            watchProgressDao = database.watchProgressDao()
        )
}