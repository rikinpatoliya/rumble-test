package com.rumble.battles.premium.di

import com.rumble.domain.premium.model.datasource.SubscriptionRemoteDataSource
import com.rumble.domain.premium.model.datasource.SubscriptionRemoteDataSourceImpl
import com.rumble.domain.premium.model.repository.SubscriptionRepository
import com.rumble.domain.premium.model.repository.SubscriptionRepositoryImpl
import com.rumble.network.api.SubscriptionApi
import com.rumble.network.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PremiumModule {
    @Provides
    @Singleton
    fun provideSubscriptionRemoteDataSource(
        subscriptionApi: SubscriptionApi
    ): SubscriptionRemoteDataSource =
        SubscriptionRemoteDataSourceImpl(subscriptionApi)

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        subscriptionRemoteDataSource: SubscriptionRemoteDataSource,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ) : SubscriptionRepository =
        SubscriptionRepositoryImpl(
            subscriptionRemoteDataSource,
            dispatcher
        )
}