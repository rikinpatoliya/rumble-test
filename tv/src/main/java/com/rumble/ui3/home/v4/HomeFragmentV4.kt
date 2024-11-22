package com.rumble.ui3.home.v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.rumble.MainActivityNew
import com.rumble.R
import com.rumble.databinding.V4FragmentHomeBinding
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.RowsSupportFragment
import com.rumble.network.connection.InternetConnectionState
import com.rumble.player.VideoPlaybackActivityDirections
import com.rumble.ui3.category.CategoryDetailsActivityDirections
import com.rumble.ui3.channel.details.v4.ChannelDetailsActivityV4Directions
import com.rumble.ui3.home.model.ChannelViewAllEntity
import com.rumble.ui3.home.model.TopLiveCategoriesViewAllEntity
import com.rumble.ui3.home.model.VideoViewAllEntity
import com.rumble.ui3.main.MainViewModel
import com.rumble.util.Constant
import com.rumble.util.Utils
import com.rumble.util.gone
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import com.rumble.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragmentV4 : RowsSupportFragment() {

    private val viewModel: HomeViewModelV4 by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val progressBarManager by lazy {
        ProgressBarManager().apply {
            this.initialDelay = 0
        }
    }

    private var _binding: V4FragmentHomeBinding? = null

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, row ->
                val rowPos = viewModel.rowsAdapter.indexOf(row)
                val listRow = row as ListRow
                val currentRowAdapter = listRow.adapter as ArrayObjectAdapter
                val selectedIndex = currentRowAdapter.indexOf(item)
                viewModel.onSelectedRowPosition(rowPos)
                viewModel.onSelectedItemPosition(selectedIndex)

                when (item) {
                    is VideoEntity -> {
                        if (item.videoSourceList.isEmpty().not()) {
                            if (item.ageRestricted) {
                                Utils.showMatureContentDialog(requireContext()) {
                                    Navigation.findNavController(requireView())
                                        .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(item))
                                }
                            } else {
                                Navigation.findNavController(requireView())
                                    .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(item))
                            }
                        } else {
                            parentFragmentManager.showAlert(getString(R.string.player_error), true)
                        }
                    }

                    is CreatorEntity -> {
                        viewModel.onChannelClicked(selectedPosition)
                        Navigation.findNavController(requireView()).navigate(
                            ChannelDetailsActivityV4Directions.actionGlobalChannelDetailsActivityV4(
                                item,
                                true
                            )
                        )
                    }

                    is VideoViewAllEntity -> {
                        Navigation.findNavController(requireView()).navigate(
                            ViewAllActivityV4Directions.actionGlobalViewAllActivity(
                                item.feed_id,
                                item.feed_title
                            )
                        )
                    }

                    is ChannelViewAllEntity -> {
                        Navigation.findNavController(requireView()).navigate(R.id.recommendedChannelsScreenActivity)
                    }

                    is CategoryEntity -> {
                        if (requireContext().isNetworkConnected){
                            Navigation.findNavController(requireView()).navigate(CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(item.path))
                        } else {
                            parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                        }
                    }

                    is TopLiveCategoriesViewAllEntity -> {
                        mainViewModel.goToBrowse()
                    }
                }
            }

        onItemViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
            viewModel.onSelectedRowPosition(0)
            viewModel.onSelectedItemPosition(0)
            val rowPos = viewModel.rowsAdapter.indexOf(row)
            viewModel.onFocusedRowPosition(rowPos)

            if (rowPos == 0) {
                headerTitleVisibility(true)
            } else {
                headerTitleVisibility(false)
            }

            try {
                val activity = (activity as MainActivityNew)
                if (rowPos > 0) {
                    activity.showLogo(false)
                } else {
                    activity.showLogo(true)
                }
            }catch (e: Exception){
                Timber.e("Error: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchContentIfNeeded()
        adapter = viewModel.rowsAdapter

        if (viewModel.rowsAdapter.size() == 0 || viewModel.uiState.value is UiStates.Error) {
            viewModel.updateAdapter()
        }
    }

    private fun headerTitleVisibility(visible: Boolean){
        if (visible){
            binding.title.visible(true)
            binding.homeHeaderRefreshButton.visible(true)
        } else {
            binding.title.gone(true)
            binding.homeHeaderRefreshButton.gone(true)
        }
    }

    private fun setFocusToRowPos(bottomRowPos: Int) {
        setSelectedPosition(bottomRowPos, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = V4FragmentHomeBinding.inflate(inflater, container, false)
        progressBarManager.setRootView(binding.homeFragmentFrameLayoutMain as ViewGroup)

        // set the view model as the lifecycle owner of the binding.
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiStates.List -> {
                    progressBarManager.hide()
                    headerTitleVisibility(true)
                }
                UiStates.Loading -> {
                    progressBarManager.show()
                }
                is UiStates.Error -> {
                    progressBarManager.hide()
                    if (viewModel.connectionState.value == InternetConnectionState.CONNECTED || state.networkError.not()) {
                        parentFragmentManager.showAlert(
                            requireContext().getString(R.string.error_fragment_message),
                            true
                        )
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.eventFlow.collect {
                        when (it) {
                            HomeEvent.AdapterUpdated -> {
                                setFocusToRowPos(viewModel.state.value.lastSelectedRowPosition)
                            }
                            HomeEvent.RemoveRefreshFlagFromBackStack -> {
                                Navigation.findNavController(requireView()).currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                                    Constant.TAG_REFRESH
                                )
                            }
                        }
                    }
                }
            }
        }

        viewModel.firstVideoCollectionTitle.observe(viewLifecycleOwner){
            binding.title.text = it
        }

        binding.homeHeaderRefreshButton.setOnClickListener {
            viewModel.updateAdapter()
        }
    }

    override fun onResume() {
        viewModel.onViewResumed()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressBarManager.setRootView(null)
    }

    override fun findGridViewFromRoot(view: View?): VerticalGridView = binding.containerList
}