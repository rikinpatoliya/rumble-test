package com.rumble.network.di

import javax.inject.Qualifier

@Qualifier
annotation class NetworkHttpClient

@Qualifier
annotation class NetworkRetrofit

@Qualifier
annotation class PlainHttpClient

@Qualifier
annotation class LoginHttpClient

@Qualifier
annotation class LoginRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppRequestName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppVersion

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VersionCode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OsVersion

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LiveRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RumbleAdRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LiveStreamPingBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LiveStreamPingRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Publisher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RumbleBannerRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EventHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EventRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EventDebugRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BundleId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppStoreUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LiveChatEventHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LiveChatEventRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugLogRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppFlyerId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugHttpClient

