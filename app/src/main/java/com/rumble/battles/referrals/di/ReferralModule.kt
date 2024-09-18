package com.rumble.battles.referrals.di

import com.rumble.domain.referrals.model.ReferralsRepository
import com.rumble.domain.referrals.model.ReferralsRepositoryImpl
import com.rumble.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReferralModule {
    @Provides
    @Singleton
    fun provideReferralsRepository(
        userApi: UserApi
    ): ReferralsRepository = ReferralsRepositoryImpl(userApi = userApi)
}