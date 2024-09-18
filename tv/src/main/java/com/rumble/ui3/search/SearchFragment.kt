package com.rumble.ui3.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.player.VideoPlaybackActivityDirections
import com.rumble.theme.RumbleTvTheme
import com.rumble.ui3.category.CategoryDetailsActivityDirections
import com.rumble.ui3.channel.details.v4.ChannelDetailsActivityV4Directions
import com.rumble.util.Utils
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    val viewModel by viewModels<SearchViewModel>()

    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        composeView = view.findViewById(R.id.compose_view)

        composeView
            .setContent {
                composeView.isFocusable = true
                composeView.isFocusableInTouchMode = true
                val focusRequester = remember { FocusRequester() }

                composeView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    Timber.d("onFocusChangeListener hasFocus$hasFocus")

                    if (hasFocus) {
                        focusRequester.requestFocus()
                        composeView.isFocusable = false
                        composeView.isFocusableInTouchMode = false
                    }
                }

                RumbleTvTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        focusRequester = focusRequester,
                        onNavigateToCategory = this::onNavigateToCategory,
                        onNavigateToVideo = this::onNavigateToVideo,
                        onNavigateToChannel = this::onNavigateToChannel
                    )
                }
            }

        return view
    }

    private fun onNavigateToCategory(categoryEntity: CategoryEntity) {
        Navigation.findNavController(requireView())
            .navigate(CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(categoryEntity.path))
    }

    private fun onNavigateToVideo(videoEntity: VideoEntity) {
        if (videoEntity.videoSourceList.isEmpty().not()) {
            if (videoEntity.ageRestricted) {
                Utils.showMatureContentDialog(requireContext()) {
                    Navigation.findNavController(requireView())
                        .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(videoEntity))
                }
            } else {
                Navigation.findNavController(requireView())
                    .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(videoEntity))
            }
        } else {
            parentFragmentManager.showAlert(getString(R.string.player_error), true)
        }
    }

    private fun onNavigateToChannel(channelDetailsEntity: ChannelDetailsEntity) {
        Navigation.findNavController(requireView()).navigate(
            ChannelDetailsActivityV4Directions.actionGlobalChannelDetailsActivityV4(channelDetailsEntity, true)
        )
    }

    /**
     * This is a bit of a work around. In some cases inside of a ComposeView when the
     * ComposeView is focusable and the Composable manipulates focus the ComposeView
     * will also lose focus and then gain focus again over and over, causing an endless
     * loop and eventually stack overflow crash. If we just leave the ComposeView as not
     * focusable then we end up with issue when the Composable inside it needs to gain
     * focus needing 2 interactions from the user.
     */
    fun setComposeViewFocusable() {
        composeView.isFocusable = true
        composeView.isFocusableInTouchMode = true
    }
}