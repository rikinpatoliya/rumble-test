package com.rumble.battles.common.di

import android.content.Context
import android.os.Build
import com.google.android.gms.common.util.DeviceProperties
import com.rumble.battles.BuildConfig
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.network.di.AppFlyerId
import com.rumble.network.di.AppId
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MobileSingletonModule {

    @AppId
    @Singleton
    @Provides
    fun provideAppId(): String = BuildConfig.APPLICATION_ID

    @AppRequestName
    @Singleton
    @Provides
    fun provideAppRequestName(): String = BuildConfig.REQUEST_PARAM_APP_NAME

    @AppVersion
    @Singleton
    @Provides
    fun provideAppVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"

    @VersionCode
    @Singleton
    @Provides
    fun provideAppVersionCode(): Int = BuildConfig.VERSION_CODE

    @AppName
    @Singleton
    @Provides
    fun provideAppName() = "Rumble-Android"

    @OsVersion
    @Provides
    @Singleton
    fun provideOsVersion() =
        "Android ".plus(Build.VERSION.RELEASE).plus(" ").plus(Build.VERSION.SDK_INT)

    @Provides
    @Singleton
    fun provideDeviceType(@ApplicationContext context: Context): DeviceType {
        return if (DeviceProperties.isTablet(context.resources)) {
            DeviceType.Tablet
        } else {
            DeviceType.Phone
        }
    }

    @Publisher
    @Provides
    @Singleton
    fun providePublisherId() = PublisherId.AndroidApp

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