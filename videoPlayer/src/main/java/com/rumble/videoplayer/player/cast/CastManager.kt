package com.rumble.videoplayer.player.cast

import android.content.Context
import android.net.Uri
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.StreamStatus

internal class CastManager(context: Context, private val rumblePlayer: RumblePlayer) {

    private var mediaRouter: MediaRouter
    private var castContext: CastContext?
    private var castSession: CastSession?
    private var sessionManagerListener: SessionManagerListener<CastSession>? = null
    private var appConnected: Boolean

    init {
        mediaRouter = MediaRouter.getInstance(context)
        castContext = CastContext.getSharedInstance()
        castSession = castContext?.sessionManager?.currentCastSession
        appConnected = castSession?.isConnected == true
        setupSessionListener()
    }

    fun startListen() {
        sessionManagerListener?.let {
            castContext?.sessionManager?.addSessionManagerListener(it, CastSession::class.java)
        }
        if (appConnected and (castSession?.isConnected == false)) {
            appConnected = false
            resumeLocal()
        }
    }

    fun stopListen() {
        sessionManagerListener?.let {
            castContext?.sessionManager?.removeSessionManagerListener(it, CastSession::class.java)
        }
    }

    private fun setupSessionListener() {
        sessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionEnded(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                onApplicationConnected(session)
            }

            override fun onSessionResumeFailed(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionStarted(session: CastSession, sessionId: String) {
                onApplicationConnected(session)
            }

            override fun onSessionStartFailed(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionStarting(session: CastSession) {}
            override fun onSessionEnding(session: CastSession) {}
            override fun onSessionResuming(session: CastSession, sessionId: String) {}
            override fun onSessionSuspended(session: CastSession, reason: Int) {}

            private fun onApplicationConnected(session: CastSession) {
                appConnected = true
                startRemote(session)
            }

            private fun onApplicationDisconnected() {
                appConnected = false
                resumeLocal()
            }
        }
    }

    private fun startRemote(session: CastSession) {
        rumblePlayer.pauseVideo()
        castSession = session
        rumblePlayer.setPlayerTarget(PlayerTarget.REMOTE)
        rumblePlayer.remoteMediaClient = castSession?.remoteMediaClient
        loadRemoteMedia()
    }

    private fun resumeLocal() {
        rumblePlayer.setPlayerTarget(PlayerTarget.LOCAL)
        rumblePlayer.remoteMediaClient = null
        rumblePlayer.playVideo()
        rumblePlayer.seekTo(rumblePlayer.castLastPosition)
        rumblePlayer.castLastPosition = 0
    }

    private fun loadRemoteMedia() {
        castSession?.let {
            it.remoteMediaClient?.let { remoteMediaClient ->
                remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
                    override fun onStatusUpdated() {
                        rumblePlayer.castLastPosition = remoteMediaClient.approximateStreamPosition
                        rumblePlayer.onRemoteTargetPlaying(remoteMediaClient.isPlaying)
                    }
                })
                remoteMediaClient.load(
                    MediaLoadRequestData.Builder()
                        .setMediaInfo(buildMediaInfo())
                        .setAutoplay(true)
                        .setCurrentTime(rumblePlayer.currentPosition.value.toLong())
                        .build()
                )
            }
        }
    }

    private fun buildMediaInfo(): MediaInfo? {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        movieMetadata.putString(MediaMetadata.KEY_TITLE, rumblePlayer.videoTitle)
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, rumblePlayer.videoDescription)
        movieMetadata.addImage(WebImage(Uri.parse(rumblePlayer.videoThumbnailUri)))
        return rumblePlayer.videoUrl?.let {
            MediaInfo.Builder(it)
                .setStreamType(if (rumblePlayer.streamStatus == StreamStatus.LiveStream) MediaInfo.STREAM_TYPE_LIVE else MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .build()
        }
    }
}