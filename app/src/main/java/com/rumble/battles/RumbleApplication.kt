package com.rumble.battles

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.media3.common.util.UnstableApi
import androidx.work.Configuration
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.appsflyer.AppsFlyerConsent
import com.appsflyer.AppsFlyerLib
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.onesignal.OneSignal
import com.rumble.analytics.REPORT_DELAY
import com.rumble.analytics.REPORT_DELAY_KYE
import com.rumble.analytics.VIDEO_VISIBILITY_PERCENTAGE
import com.rumble.analytics.VIDEO_VISIBILITY_PERCENTAGE_KEY
import com.rumble.battles.deeplinks.RumbleDeepLinkListener
import com.rumble.battles.notifications.pushnotifications.RumbleNotificationOpenedHandler
import com.rumble.domain.logging.domain.FileLoggingTree
import com.rumble.network.NetworkRumbleConstants.FETCH_CONFIG_INTERVAL_MINUTES
import com.rumble.utils.RumbleConstants.LOGIN_PROMPT_PERIOD
import com.rumble.utils.RumbleConstants.LOGIN_PROMPT_PERIOD_KEY
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@UnstableApi
@HiltAndroidApp
class RumbleApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var rumbleNotificationOpenedHandler: RumbleNotificationOpenedHandler

    @Inject
    lateinit var rumbleDeepLinkListener: RumbleDeepLinkListener

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        initAppsFlyer()
        initOneSignal()
        initFirebaseConfig()
        initLogging()
        setupCoil()
    }

    private fun initAppsFlyer() {
        val appsFlyerLib = AppsFlyerLib.getInstance()
        appsFlyerLib.subscribeForDeepLink(rumbleDeepLinkListener)
        appsFlyerLib.init(BuildConfig.APPS_FLYER_API_ID, null, this)
        val hasConsentForDataUsage = false
        val hasConsentForAdsPersonalization = false
        appsFlyerLib.setConsentData(AppsFlyerConsent.forGDPRUser(hasConsentForDataUsage, hasConsentForAdsPersonalization))
        appsFlyerLib.start(this)
    }

    private fun initLogging() {
        if (BuildConfig.BUILD_TYPE == "qa" || BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(FileLoggingTree(this))
        }
    }

    private fun initOneSignal() {
        if (BuildConfig.DEBUG)
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(BuildConfig.ONE_SIGNAL_APP_ID)
        OneSignal.setNotificationOpenedHandler(rumbleNotificationOpenedHandler)
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
    }

    private fun initFirebaseConfig() {
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds =
                TimeUnit.MINUTES.toSeconds(FETCH_CONFIG_INTERVAL_MINUTES)
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf(
                VIDEO_VISIBILITY_PERCENTAGE_KEY to VIDEO_VISIBILITY_PERCENTAGE,
                REPORT_DELAY_KYE to REPORT_DELAY,
                LOGIN_PROMPT_PERIOD_KEY to LOGIN_PROMPT_PERIOD
            )
        )
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
    }

    private fun setupCoil() {
        val imageLoader = ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}