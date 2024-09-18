package com.rumble.videoplayer.domain.usecases

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExternalAudioSourceConnectedUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    @Suppress("DEPRECATION")
    operator fun invoke(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return audioManager.isWiredHeadsetOn || audioManager.isBluetoothScoOn || audioManager.isBluetoothA2dpOn
        } else {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                    || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                ) {
                    return true
                }
            }
        }
        return false
    }
}