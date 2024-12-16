package com.rumble.battles.premium.di

import com.rumble.domain.premium.model.datasource.PurchaseRemoteDataSource
import com.rumble.domain.premium.model.datasource.PurchaseRemoteDataSourceImpl
import com.rumble.domain.premium.model.repository.PurchaseRepository
import com.rumble.domain.premium.model.repository.PurchaseRepositoryImpl
import com.rumble.network.api.PurchaseApi
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
        purchaseApi: PurchaseApi
    ): PurchaseRemoteDataSource =
        PurchaseRemoteDataSourceImpl(purchaseApi)

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        purchaseRemoteDataSource: PurchaseRemoteDataSource,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ) : PurchaseRepository =
        PurchaseRepositoryImpl(
            purchaseRemoteDataSource,
            dispatcher
        )
}