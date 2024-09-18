package com.rumble.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeviceUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) {

    companion object {
        private const val AMAZON_SYSTEM_FEATURE = "amazon.hardware.fire_tv"
        private const val DEVICE_TYPE_AMAZON = "fireTv"
        private const val DEVICE_TYPE_ANDROID = "androidTv"
    }

    /**
     * Check is device has Amazon Fire TV system feature
     */
    fun isAmazonFireTvDevice(): Boolean {
        return packageManager.hasSystemFeature(AMAZON_SYSTEM_FEATURE)
    }

    /**
     * Returns if device is fireTv or androidTv
     */
    fun getDeviceType(): String {
        return if (isAmazonFireTvDevice())
            DEVICE_TYPE_AMAZON
        else
            DEVICE_TYPE_ANDROID
    }

    /**
     * Returns ANDROID_ID for device
     */
    fun getAndroidId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * Returns the current device model
     */
    fun getModel(): String {
        return Build.MODEL
    }

}


