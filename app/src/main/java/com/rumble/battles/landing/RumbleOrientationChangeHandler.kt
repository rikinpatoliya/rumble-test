package com.rumble.battles.landing

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import com.google.android.gms.common.util.DeviceProperties

class RumbleOrientationChangeHandler(private val context: Context, val onChanged: (Int) -> Unit) {

    private val orientationChangeListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            val newOrientation = getScreenOrientationByAngle(orientation)
            if (newOrientation != currentOrientation) {
                currentOrientation = newOrientation
                onChanged(newOrientation)
            }
        }
    }

    private var currentOrientation = OrientationEventListener.ORIENTATION_UNKNOWN

    private fun getScreenOrientationByAngle(orientationAngle: Int): Int {
        return when (orientationAngle) {
            in 330..360, in 0..30 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            in 60..120 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            in 150..210 -> {
                if (DeviceProperties.isTablet(context.resources))
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                else currentOrientation
            }
            in 240..300 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> -1
        }
    }

    fun enable() =  orientationChangeListener.enable()

    fun disable() = orientationChangeListener.disable()
}