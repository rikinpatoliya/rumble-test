package com.rumble.player

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.R
import com.rumble.network.connection.InternetConnectionState
import com.rumble.theme.RumbleTheme
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.ui3.library.VideoCard
import com.rumble.ui3.main.InternetConnectionLostDialogFragment
import com.rumble.util.Constant.FINISH_ACTION
import com.rumble.util.Utils
import com.rumble.util.showAlert
import com.rumble.videoplayer.presentation.RumbleVideoView
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.controlViews.PremiumNoteView
import com.rumble.videoplayer.presentation.internal.controlViews.PremiumTag
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
open class VideoPlaybackActivity : FragmentActivity() {

    private val viewModel: VideoPlaybackViewModel by viewModels()
    private val saveToPlaylistViewModel: SaveToPlaylistViewModel by viewModels()

    private val broadcastReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == FINISH_ACTION) {
                this@VideoPlaybackActivity.finish()
            }
        }
    }

    private lateinit var dialogInternet: InternetConnectionLostDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()
        intent.extras?.let { bundle ->
            val args = VideoPlaybackActivityArgs.fromBundle(bundle)
            args.video?.let {
                viewModel.initPlayerWithVideo(it, args.fromChannel)
            }
            args.videoList?.let {
                viewModel.initPlayerWithVideoList(args.playListTitle, it.toList(), args.shuffle)
            }
        }
        setContent {
            RumbleTheme {
                VideoPlayerView()
            }
        }

        dialogInternet = supportFragmentManager
            .findFragmentByTag(InternetConnectionLostDialogFragment::class.java.simpleName)
                as? InternetConnectionLostDialogFragment ?: InternetConnectionLostDialogFragment()

        viewModel.connectionState.observe(this) {
            when {
                it == InternetConnectionState.LOST && dialogInternet.isVisible.not() -> {
                    dialogInternet.show(
                        supportFragmentManager,
                        InternetConnectionLostDialogFragment::class.java.simpleName
                    )
                }

                it == InternetConnectionState.CONNECTED && dialogInternet.isAdded -> {
                    dialogInternet.dismiss()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }

    private fun registerReceiver() {
        val filter = IntentFilter(FINISH_ACTION)
        ContextCompat.registerReceiver(this, broadcastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    @Composable
    private fun VideoPlayerView() {
        val state by remember { viewModel.videoPlayerState }
        val context = LocalContext.current

        val saveToPlaylistState by saveToPlaylistViewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect {
                when (it) {
                    is VideoPlayerEvent.VideoReported ->
                        supportFragmentManager.showAlert(context.getString(R.string.video_has_been_reported), false)

                    is VideoPlayerEvent.Error ->
                        supportFragmentManager.showAlert(context.getString(R.string.error_fragment_message), true)

                    is VideoPlayerEvent.LoginToLike ->
                        supportFragmentManager.showAlert(
                            message = it.errorMessage ?: context.getString(R.string.login_to_like),
                            showIcon = true
                        )

                    is VideoPlayerEvent.LoginToDislike ->
                        supportFragmentManager.showAlert(
                            message = it.errorMessage ?: context.getString(R.string.login_to_dislike),
                            showIcon = true
                        )

                    is VideoPlayerEvent.ClosePlayer -> this@VideoPlaybackActivity.finish()

                    is VideoPlayerEvent.OpenChannelDetails -> {
                        Utils.navigateToChannelDetails(this@VideoPlaybackActivity, it.channelDetailsEntity)
                    }

                    VideoPlayerEvent.AddToPlaylist -> {
                        saveToPlaylistViewModel.onShowSaveToPlaylist(state.videoEntity)
                    }

                    VideoPlayerEvent.LoginToAddToPlaylist ->
                        supportFragmentManager.showAlert(context.getString(R.string.login_to_add_to_playlist), true)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(enforcedDarkmo)
        ) {
            state.rumblePlayer?.let {
                val videoEntity = state.videoEntity
                if (videoEntity != null && state.showPayWall) {
                    PremiumPayWall(
                        modifier = Modifier
                            .fillMaxSize(),
                        videoEntity = videoEntity,
                        onChannelDetails = viewModel::onChannelDetails
                    )
                } else {
                    RumbleVideoView(
                        modifier = Modifier
                            .fillMaxSize(),
                        rumblePlayer = it,
                        uiType = UiType.TV,
                        liveChatDisabled = true,
                        userVote = state.currentVote,
                        onReport = viewModel::onReport,
                        onLike = viewModel::onLikeVideo,
                        onDislike = viewModel::onDislikeVideo,
                        onAddToPlaylist = viewModel::onAddToPlaylist,
                        onChannelDetails = viewModel::onChannelDetails,
                        playListVideoCardComposable = { video, isPlaying, onFocused, onSelection ->
                            VideoCard(
                                video = video,
                                playing = isPlaying,
                                onFocused = onFocused,
                                onClick = { onSelection() }
                            )
                        }
                    )

                    if (saveToPlaylistState.visible) {
                        SaveToPlaylistDialog(
                            viewModel = saveToPlaylistViewModel,
                            onShowAlertDialog = { message -> supportFragmentManager.showAlert(message, true) }
                        )
                    }
                }
            }
        }
    }
}