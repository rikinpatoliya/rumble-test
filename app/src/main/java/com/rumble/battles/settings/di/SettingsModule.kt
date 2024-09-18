package com.rumble.battles.settings.di

import android.content.Context
import com.rumble.battles.BuildConfig
import com.rumble.domain.settings.model.datasource.SettingsLocalDataSource
import com.rumble.domain.settings.model.datasource.SettingsLocalDataSourceImpl
import com.rumble.domain.settings.model.datasource.SettingsRemoteDataSource
import com.rumble.domain.settings.model.datasource.SettingsRemoteDataSourceImpl
import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.model.repository.SettingsRepositoryImpl
import com.rumble.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsRemoteDataSource(userApi: UserApi): SettingsRemoteDataSource =
        SettingsRemoteDataSourceImpl(
            userApi
        )

    @Provides
    @Singleton
    fun provideSettingsLocalDataSource(@ApplicationContext context: Context): SettingsLocalDataSource =
        SettingsLocalDataSourceImpl(context, BuildConfig.OPEN_SOURCE_LICENCES_FILENAME)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsRemoteDataSource: SettingsRemoteDataSource,
        settingsLocalDataSource: SettingsLocalDataSource
    ): SettingsRepository =
        SettingsRepositoryImpl(
            settingsRemoteDataSource,
            settingsLocalDataSource,
            Dispatchers.IO,
        )
}