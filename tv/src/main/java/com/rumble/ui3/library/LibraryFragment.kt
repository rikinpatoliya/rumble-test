package com.rumble.ui3.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.player.VideoPlaybackActivityDirections
import com.rumble.theme.RumbleTvTheme
import com.rumble.ui3.main.MainViewModel
import com.rumble.util.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val viewModel: LibraryHandler by viewModels<LibraryViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView
            .setContent {
                composeView.isFocusable = true
                composeView.isFocusableInTouchMode = true
                val focusRequester = remember { FocusRequester() }

                composeView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) focusRequester.requestFocus()
                }

                RumbleTvTheme {
                    LibraryScreen(
                        viewModel,
                        focusRequester = focusRequester,
                        onNavigateToVideoPlayer = { navigateToVideoPlayer(it) },
                        onNavigateToLogin = { navigateToLogin() },
                        onNavigateToPlayAll = { title, videoList, shuffle ->
                            navigateToPlayAll(title, videoList, shuffle)
                        }
                    )
                }
            }

        return view
    }

    private fun navigateToVideoPlayer(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            Utils.showMatureContentDialog(requireContext()) {
                Navigation.findNavController(requireView())
                    .navigate(
                        VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(
                            videoEntity
                        )
                    )
            }
        } else {
            Navigation.findNavController(requireView())
                .navigate(
                    VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(
                        videoEntity
                    )
                )
        }
    }

    private fun navigateToPlayAll(title: String, videoList: List<Feed>, shuffle: Boolean = false) {
        Navigation.findNavController(requireView())
            .navigate(
                VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(
                    playListTitle = title,
                    videoList = videoList.filterIsInstance<VideoEntity>().toTypedArray(),
                    shuffle = shuffle
                )
            )
    }

    private fun navigateToLogin() {
        mainViewModel.goToLoginNew()
    }
}