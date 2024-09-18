package com.rumble.battles.onboarding.di

import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.onboarding.model.datasource.local.OnboardingViewDao
import com.rumble.domain.onboarding.model.repsitory.OnboardingViewRepository
import com.rumble.domain.onboarding.model.repsitory.OnboardingViewRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {
    @Provides
    fun provideOnboardingViewDao(database: RumbleDatabase): OnboardingViewDao =
        database.onboardingViewDao()

    @Provides
    fun provideOnboardingViewRepository(onboardingViewDao: OnboardingViewDao): OnboardingViewRepository =
        OnboardingViewRepositoryImpl(onboardingViewDao)


}