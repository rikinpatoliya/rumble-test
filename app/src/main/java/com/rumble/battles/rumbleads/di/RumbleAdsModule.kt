package com.rumble.battles.rumbleads.di

import com.rumble.domain.rumbleads.model.datasource.RumbleAdsRemoteDataSource
import com.rumble.domain.rumbleads.model.datasource.RumbleAdsRemoteDataSourceImpl
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepositoryImpl
import com.rumble.network.api.PreRollApi
import com.rumble.network.api.RumbleAdsApi
import com.rumble.network.api.RumbleBannerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RumbleAdsModule {

    @Provides
    fun provideRumbleAdsRemoteDataSource(
        rumbleBannerApi: RumbleBannerApi,
        rumbleAdsApi: RumbleAdsApi,
        preRollApi: PreRollApi
    ): RumbleAdsRemoteDataSource =
        RumbleAdsRemoteDataSourceImpl(rumbleBannerApi, rumbleAdsApi, preRollApi)

    @Provides
    @Singleton
    fun provideRumbleAdRepository(remoteDataSource: RumbleAdsRemoteDataSource): RumbleAdRepository =
        RumbleAdRepositoryImpl(
            remoteDataSource = remoteDataSource,
            dispatcher = Dispatchers.IO
        )
}