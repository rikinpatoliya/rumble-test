package com.rumble.di

import android.os.Build
import com.rumble.battles.BuildConfig
import com.rumble.network.di.AppFlyerId
import com.rumble.network.di.AppName
import com.rumble.network.di.AppRequestName
import com.rumble.network.di.AppStoreUrl
import com.rumble.network.di.AppVersion
import com.rumble.network.di.BundleId
import com.rumble.network.di.OsVersion
import com.rumble.network.di.Publisher
import com.rumble.network.di.VersionCode
import com.rumble.network.queryHelpers.PublisherId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ATvSingletonModule {

    @AppRequestName
    @Singleton
    @Provides
    fun provideAppPackageName(): String = BuildConfig.REQUEST_PARAM_APP_NAME

    @AppVersion
    @Singleton
    @Provides
    fun provideAppVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"

    @VersionCode
    @Singleton
    @Provides
    fun provideAppVersionCode() = BuildConfig.VERSION_CODE

    @AppName
    @Singleton
    @Provides
    fun provideAppName() = "Rumble-AndroidTV"

    @OsVersion
    @Provides
    @Singleton
    fun provideOsVersion() =
        "Android ".plus(Build.VERSION.RELEASE).plus(" ").plus(Build.VERSION.SDK_INT)

    @Publisher
    @Provides
    @Singleton
    fun providePublisherId() = PublisherId.AndroidTv

    @BundleId
    @Provides
    @Singleton
    fun provideBundleId() = BuildConfig.METADATA_BUNDLE_ID

    @AppStoreUrl
    @Provides
    @Singleton
    fun provideAppStoreUrl() = "https://play.google.com/store/apps/details?id=com.rumble.battles"

    @AppFlyerId
    @Singleton
    @Provides
    fun provideAppFlyerId(): String = BuildConfig.APPS_FLYER_API_ID
}