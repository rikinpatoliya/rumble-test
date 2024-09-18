package com.rumble.ui3.live.v4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.databinding.V4FrgmentLiveBinding
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.VerticalGridSupportFragment
import com.rumble.network.connection.InternetConnectionState
import com.rumble.player.VideoPlaybackActivityDirections
import com.rumble.ui3.common.RumbleVerticalGridPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.live.LiveStates
import com.rumble.util.Constant
import com.rumble.util.PagingAdapter
import com.rumble.util.Utils
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class LiveFragmentV4 : VerticalGridSupportFragment() {

    companion object {
        private const val NUMBER_OF_COLUMNS = 3
    }

    private lateinit var pagingDataAdapter: PagingAdapter<Feed>
    private val viewModel: LiveViewModelV4 by activityViewModels()

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    private lateinit var headerBinding: V4FrgmentLiveBinding

    private var currentItemSelectedPosition: Int = 0
    private var totalItemCount: Int = 0

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModel.onError(throwable)
        progressBarManager?.hide()
        LiveStates.reloadLiveData = true
        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
    }

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                if (item is Feed) {
                    val videoItem = item as VideoEntity
                    if (videoItem.videoSourceList.isEmpty().not()) {
                        if (videoItem.ageRestricted) {
                            Utils.showMatureContentDialog(requireContext()) {
                                Navigation.findNavController(requireView())
                                    .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(videoItem))
                            }
                        } else {
                            Navigation.findNavController(requireView())
                                .navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(videoItem))
                        }
                    } else {
                        parentFragmentManager.showAlert(getString(R.string.player_error), true)
                    }

                }
            }

        setOnItemViewSelectedListener { _, item, _, row ->
            currentItemSelectedPosition = pagingDataAdapter.snapshot().items.indexOf(item)
            totalItemCount = pagingDataAdapter.snapshot().items.size
        }
    }

    private fun installHeaderView(inflater: LayoutInflater, rootView: View) {
        val gridFrame =
            rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout

        headerBinding = V4FrgmentLiveBinding.inflate(inflater, gridFrame, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
        Handler(Looper.getMainLooper()).post {
            if (LiveStates.reloadLiveData) {
                LiveStates.liveVideosPagingDataMap = null
                viewModel.getLiveVideos()
                LiveStates.reloadLiveData = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        installHeaderView(inflater, rootView)
        updateBrowseGridDockMargins(rootView)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        setupFocusSearchListener()
    }

    private fun setupFocusSearchListener() {
        val browseFrameLayout = requireView().findViewById<BrowseFrameLayout>(R.id.grid_frame)
        browseFrameLayout.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->

                if (headerBinding.refreshButton.hasFocus() && (direction == View.FOCUS_DOWN)) {
                    if (pagingDataAdapter.size() <= 0) {
                        headerBinding.refreshButton
                    } else {
                        browseFrameLayout
                    }
                } else {
                    null
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarManager.initialDelay = 0
        progressBarManager.setRootView(view as ViewGroup)

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            progressBarManager.show()
            delay(Constant.LIVE_CHANNEL_LOADING_DELAY)
            viewModel.liveVideoCollectionLiveData.observe(viewLifecycleOwner){ pagingData ->
                pagingDataAdapter.submitData(lifecycle, pagingData)
            }
        }

        viewModel.getLiveVideos()

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            pagingDataAdapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> {
                        Timber.d("LoadState.Loading")
                        progressBarManager.show()
                    }
                    is LoadState.NotLoading -> {
                        Timber.d("LoadState.NotLoading")
                        progressBarManager.hide()
                    }

                    is LoadState.Error -> {
                        progressBarManager.hide()
                        LiveStates.reloadLiveData = true
                        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            viewModel.connectionState.observe(viewLifecycleOwner) {
                headerBinding.refreshButton.visibility =
                    if (it == InternetConnectionState.CONNECTED) View.VISIBLE else View.GONE
            }
        }

        headerBinding.refreshButton.setOnClickListener {
            viewModel.onRefresh()
        }
    }

    private fun updateBrowseGridDockMargins(rootView:View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for browse_grid_dock and move it to bottom
        val gridDock = rootView.findViewById<ViewGroup>(androidx.leanback.R.id.browse_grid_dock)
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(
            resources.getDimensionPixelSize(R.dimen.liveV4_fragment_left_margin),
            resources.getDimensionPixelSize(R.dimen.liveV4_fragment_top_margin),
            0, 0)
        gridDock.layoutParams = layoutParams
        gridDock.setPadding(0, 1, 0, 0)
    }

    private fun setupAdapter() {
        val videoGridPresenter = RumbleVerticalGridPresenter(requireContext(),0, false, BaseGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED, Constant.VIEW_ALL_LIVE_OFFSET_PERCENT)
        videoGridPresenter.numberOfColumns = NUMBER_OF_COLUMNS
        setGridPresenter(videoGridPresenter)

        pagingDataAdapter = PagingAdapter(videoCardPresenter,
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return (oldItem as VideoEntity).id == (newItem as VideoEntity).id
                }

                override fun areContentsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return (oldItem as VideoEntity) == (newItem as VideoEntity)
                }
            })

        adapter = pagingDataAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
    }
}