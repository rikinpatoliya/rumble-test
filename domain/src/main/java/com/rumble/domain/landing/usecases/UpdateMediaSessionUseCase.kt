package com.rumble.domain.landing.usecases

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlaybackSpeed
import javax.inject.Inject

class UpdateMediaSessionUseCase @Inject constructor() {
    private val actionPlayPause =
        PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE

    operator fun  invoke(session: MediaSessionCompat, player: RumblePlayer?, addPlayActions: Boolean = true) {
        player?.let {
            updateSessionState(session, it.isPlaying(), addPlayActions)
            session.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPause() {
                    it.pauseVideo()
                    updateSessionState(session, false, addPlayActions)
                }

                override fun onPlay() {
                    if (it.videoFinished) it.replay()
                    else it.playVideo()
                    updateSessionState(session, true, addPlayActions)
                }
            })
            it.onVideoFinished = {
                updateSessionState(session, false, addPlayActions)
            }
        }
    }

    private fun updateSessionState(session: MediaSessionCompat, isPlaying: Boolean, addPlayActions: Boolean) {
        val state =
            if (isPlaying) PlaybackStateCompat.STATE_PLAYING
            else PlaybackStateCompat.STATE_PAUSED
        val builder = PlaybackStateCompat.Builder()
            .setState(state, 0, PlaybackSpeed.NORMAL.value)
        if (addPlayActions) builder.setActions(actionPlayPause)
        session.setPlaybackState(builder.build())
    }
}