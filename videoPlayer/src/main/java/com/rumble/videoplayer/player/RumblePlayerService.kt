package com.rumble.videoplayer.player

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.internal.notification.NotificationData
import com.rumble.videoplayer.player.internal.notification.RumbleNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
@UnstableApi
class RumblePlayerService : Service(), AudioManager.OnAudioFocusChangeListener {

    @Inject
    internal lateinit var notificationManager: RumbleNotificationManager

    private val playerBinder = PlayerBinder()
    private var currentPlayer: RumblePlayer? = null
    private var mediaSession: MediaSession? = null
    private var resumeAfterPause = false
    private var onSaveLastPosition: ((Long, Long) -> Unit)? = null
    private var videoId: Long = 0
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null


    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onBind(intent: Intent?): IBinder = playerBinder

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopNotification()
    }

    private fun stopNotification() {
        onSaveLastPosition?.invoke(
            currentPlayer?.getPlayerInstance()?.currentPosition ?: 0,
            videoId
        )
        stopCurrentSession()
        stopForeground()
        stopSelf()
        notificationManager.stopNotification()
    }

    private fun startForegroundWithNotification(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                RumbleNotificationManager.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            )
        } else {
            startForeground(RumbleNotificationManager.NOTIFICATION_ID, notification)
        }
    }

    private fun stopForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    inner class PlayerBinder : Binder() {
        fun setPlayer(
            player: RumblePlayer,
            enableSeekBar: Boolean,
            playInBackground: Boolean,
            id: Long,
            saveLastPosition: ((Long, Long) -> Unit)?
        ) {
            if (player != currentPlayer) {
                stopCurrentSession()
                videoId = id
                onSaveLastPosition = saveLastPosition
                currentPlayer = player
                if (playInBackground) startAsForegroundService(
                    player.getPlayerInstance(),
                    enableSeekBar
                )
            }
        }

        fun requestAudioFocus() {
            this@RumblePlayerService.requestAudioFocus()
        }

        fun abandonAudioFocus() {
            this@RumblePlayerService.abandonAudioFocus()
        }

        fun stopPlay() {
            stopCurrentSession()
            stopForeground()
            notificationManager.stopNotification()
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        currentPlayer?.let {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> resumeIfNeeded()

                else -> pauseIfNeeded()
            }
        }
    }


    private fun requestAudioFocus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(this).build()
            audioFocusRequest?.let {
                audioManager.requestAudioFocus(it)
            }

        } else {
            audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(
                    it
                )
            }
        } else {
            audioManager.abandonAudioFocus(this)
        }
    }

    private fun startAsForegroundService(player: ExoPlayer, enableSeekBar: Boolean) {
        val forwardingPlayer =
            object : ForwardingPlayer(player) {

                override fun play() {
                    if (currentPlayer?.playerTarget?.value == PlayerTarget.LOCAL)
                        currentPlayer?.playVideo()
                }

                override fun pause() {
                    if (currentPlayer?.playerTarget?.value == PlayerTarget.LOCAL)
                        currentPlayer?.pauseVideo()
                }

                override fun seekTo(positionMs: Long) {
                    if (currentPlayer?.playerTarget?.value == PlayerTarget.LOCAL)
                        currentPlayer?.seekTo(positionMs)
                }

                override fun setPlayWhenReady(playWhenReady: Boolean) {
                    if (currentPlayer?.playerTarget?.value == PlayerTarget.LOCAL)
                        currentPlayer?.playVideo()
                }

                override fun getAvailableCommands(): Player.Commands {
                    return super.getAvailableCommands()
                        .buildUpon()
                        .removeIf(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM, enableSeekBar.not())
                        .removeAll(
                            COMMAND_SEEK_TO_PREVIOUS,
                            COMMAND_SEEK_TO_NEXT,
                        )
                        .build()
                }
            }
        mediaSession =
            MediaSession.Builder(this@RumblePlayerService, forwardingPlayer).build()
        mediaSession?.let { mediaSession ->
            val notificationData = NotificationData(mediaSession, forwardingPlayer, enableSeekBar)
            startForegroundWithNotification(
                notificationManager.getNotification(notificationData)
            )
        }
    }

    private fun stopCurrentSession() {
        resumeAfterPause = false
        currentPlayer?.getPlayerInstance()?.stop()
        currentPlayer?.getAdsPlayerInstance()?.stop()
        mediaSession?.release()
    }

    private fun pauseIfNeeded() {
        if (currentPlayer?.getPlayerInstance()?.isPlaying == true) {
            resumeAfterPause = true
            currentPlayer?.pauseVideo()
        }
    }

    private fun resumeIfNeeded() {
        if (resumeAfterPause) {
            currentPlayer?.playVideo()
            resumeAfterPause = false
        }
    }
}