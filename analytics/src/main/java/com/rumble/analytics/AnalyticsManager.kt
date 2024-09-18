package com.rumble.analytics

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.rumble.utils.RumbleConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun sendUnhandledErrorReport(tag: String, throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
        Timber.tag(tag).e(throwable)
    }

    fun sendRumbleErrorReport(
        tag: String,
        requestUrl: String,
        code: Int,
        message: String,
        rawResponse: String,
        httpMethod: String,
        body: String
    ) {
        resetCustomKeys()
        val errorMessage =
            "RumbleError with ${CustomKey.TAG_KEY.key} -> $tag, ${CustomKey.REQUEST_URL_KEY.key} -> $requestUrl, method -> $httpMethod ${CustomKey.CODE_KEY.key} -> $code and ${CustomKey.MESSAGE_KEY.key} -> $message"
        setCustomKeys(tag, requestUrl, code, message, rawResponse, httpMethod, body)
        Firebase.crashlytics.recordException(ServerError(errorMessage))
        Timber.e(tag, errorMessage)
    }

    fun sendMediaErrorReport(mediaErrorData: MediaErrorData) {
        resetCustomKeys()
        setCustomMediaKeys(mediaErrorData)
        val error = MediaError(mediaErrorData.errorMessage)
        Firebase.crashlytics.recordException(error)
        Timber.e(error)
    }

    fun sendAnalyticEvent(event: AnalyticEvent) {
        Firebase.analytics.logEvent(event.eventName, event.firebaseOps)
        AppsFlyerLib.getInstance().logEvent(context, event.eventName, event.appsFlyOps)
        Timber.tag(RumbleConstants.RUMBLE_ANALYTICS_TAG)
            .i("%s : %s", event.eventName, event.firebaseOps)
    }

    private fun setCustomKeys(
        tag: String,
        requestUrl: String,
        code: Int,
        message: String,
        rawResponse: String,
        httpMethod: String,
        body: String
    ) {
        Firebase.crashlytics.setCustomKeys {
            key(CustomKey.TAG_KEY.key, tag)
            key(CustomKey.REQUEST_URL_KEY.key, requestUrl)
            key(CustomKey.CODE_KEY.key, code.toString())
            key(CustomKey.MESSAGE_KEY.key, message)
            key(CustomKey.RAW_RESPONSE_KEY.key, rawResponse)
            key(CustomKey.HTTP_METHOD_KEY.key, httpMethod)
            key(CustomKey.BODY.key, body)
        }
    }

    private fun setCustomMediaKeys(mediaErrorData: MediaErrorData) {
        Firebase.crashlytics.setCustomKeys {
            key(CustomMediaKey.ERROR_MESSAGE.key, mediaErrorData.errorMessage)
            key(CustomMediaKey.VIDEO_ID.key, mediaErrorData.videoId.toString())
            key(CustomMediaKey.VIDEO_URL.key, mediaErrorData.videoUrl)
            key(CustomMediaKey.SCREEN_ID.key, mediaErrorData.screenId)
            key(CustomMediaKey.BACKGROUND_MODE.key, mediaErrorData.backgroundMode)
            key(CustomMediaKey.PLAYBACK_TIME.key, mediaErrorData.playbackTime.toString())
            key(CustomMediaKey.PLAYBACK_SPEED.key, mediaErrorData.playbackSpeed.toString())
            key(CustomMediaKey.VOLUME.key, mediaErrorData.volume.toString())
            key(CustomMediaKey.QUALITY.key, mediaErrorData.quality)
            key(CustomMediaKey.BITRATE.key, mediaErrorData.bitrate)
            key(CustomMediaKey.TARGET.key, mediaErrorData.target)
        }
    }

    private fun resetCustomKeys() {
        Firebase.crashlytics.setCustomKeys {
            CustomKey.values().forEach {
                key(it.key, "")
            }
            CustomMediaKey.values().forEach {
                key(it.key, "")
            }
        }
    }
}