package com.rumble.battles.profile.di

import com.rumble.domain.profile.model.datasource.UserProfileLocalDataSource
import com.rumble.domain.profile.model.datasource.UserProfileLocalDataSourceImpl
import com.rumble.domain.profile.model.datasource.UserProfileRemoteDataSource
import com.rumble.domain.profile.model.datasource.UserProfileRemoteDataSourceImpl
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.domain.profile.model.repository.ProfileRepositoryImpl
import com.rumble.network.api.LoginApi
import com.rumble.network.api.UserApi
import com.rumble.domain.uploadmanager.UploadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideUserProfileRemoteDataSource(
        userApi: UserApi,
        uploadManager: UploadManager
    ): UserProfileRemoteDataSource =
        UserProfileRemoteDataSourceImpl(
            userApi,
            uploadManager,
            Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideUserProfileLocalDataSource(): UserProfileLocalDataSource =
        UserProfileLocalDataSourceImpl()

    @Provides
    @Singleton
    fun provideSettingsRepository(
        loginApi: LoginApi,
        userProfileRemoteDataSource: UserProfileRemoteDataSource,
        userProfileLocalDataSource: UserProfileLocalDataSource
    ): ProfileRepository =
        ProfileRepositoryImpl(
            loginApi,
            userProfileRemoteDataSource,
            userProfileLocalDataSource,
            Dispatchers.IO,
        )
}