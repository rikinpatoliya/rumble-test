package com.rumble.domain.feed.domain.usecase

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class GetSensorBasedOrientationChangeEnabledUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(): Boolean {
        val isEnabled = Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION, 0
        )
        Timber.d("GetAutoOrientationChangeStatusUseCase returned: $isEnabled")
        return isEnabled == 1
    }
}