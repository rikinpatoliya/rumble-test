package com.rumble.battles.earnings.di

import com.rumble.domain.earnings.datasource.EarningsRemoteDataSource
import com.rumble.domain.earnings.datasource.EarningsRemoteDataSourceImpl
import com.rumble.domain.earnings.repository.EarningsRepository
import com.rumble.domain.earnings.repository.EarningsRepositoryImpl
import com.rumble.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EarningsModule {
    @Provides
    @Singleton
    fun provideEarningsRemoteDataSource(userApi: UserApi): EarningsRemoteDataSource =
        EarningsRemoteDataSourceImpl(
            userApi
        )

    @Provides
    @Singleton
    fun provideEarningsRepository(earningsRemoteDataSource: EarningsRemoteDataSource): EarningsRepository =
        EarningsRepositoryImpl(
            earningsRemoteDataSource = earningsRemoteDataSource
        )
}