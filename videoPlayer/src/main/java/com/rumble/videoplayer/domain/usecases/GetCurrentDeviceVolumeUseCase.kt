package com.rumble.videoplayer.domain.usecases

import android.content.Context
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetCurrentDeviceVolumeUseCase @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    operator fun invoke(): Int {
        val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return (volumeLevel.toFloat() / maxVolumeLevel * 100).toInt()
    }
}