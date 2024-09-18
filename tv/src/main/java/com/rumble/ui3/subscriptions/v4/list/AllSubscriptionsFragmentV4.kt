package com.rumble.ui3.subscriptions.v4.list

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.databinding.V4FrgmentAllSubscriptionsBinding
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.leanback.VerticalGridSupportFragment
import com.rumble.ui3.common.RumbleVerticalGridPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.main.MainViewModel
import com.rumble.ui3.subscriptions.pages.list.AllSubscriptionState
import com.rumble.ui3.subscriptions.v4.SubscriptionsViewModelV4
import com.rumble.util.Constant
import com.rumble.util.Utils
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AllSubscriptionsFragmentV4 : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider {

    companion object {
        private const val NUMBER_OF_COLUMNS = 3
    }

    private lateinit var pagingDataAdapter: PagingDataAdapter<Feed>

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<AllSubscriptionsFragmentV4> =
        object : BrowseSupportFragment.MainFragmentAdapter<AllSubscriptionsFragmentV4>(this) {}

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    private val viewModel: AllSubscriptionsViewModelV4 by activityViewModels()

    private lateinit var headerBinding: V4FrgmentAllSubscriptionsBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val subscriptionsViewModel: SubscriptionsViewModelV4 by activityViewModels()

    private var currentItemSelectedPosition: Int = 0
    
    private var isRefreshClicked: Boolean = false

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<AllSubscriptionsFragmentV4> =
        fragmentAdapter

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                if (item is Feed) {
                    val videoItem = item as VideoEntity
                    if (videoItem.videoSourceList.isEmpty().not()){
                        if (videoItem.ageRestricted){
                            Utils.showMatureContentDialog(requireContext()){
                                Utils.navigateToVideoPlayback(requireContext(), videoItem)
                            }
                        } else {
                            Utils.navigateToVideoPlayback(requireContext(), videoItem)
                        }
                    } else {
                        parentFragmentManager.showAlert(getString(R.string.player_error), true)
                    }
                }
            }

        setOnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
            currentItemSelectedPosition = pagingDataAdapter.snapshot().items.indexOf(item)
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModel.onError(throwable)
        progressBarManager?.hide()
    }

    override fun onStart() {
        super.onStart()
        setupFocusSearchListenerForVerticalGrid()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        installHeaderView(inflater, rootView)
        updateBrowseGridDockMargins(rootView)
        headerBinding.mainViewModel = mainViewModel
        headerBinding.channelRecommendation.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.recommendedChannelsScreenActivity)
        }

        return rootView
    }

    private fun updateBrowseGridDockMargins(rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for browse_grid_dock and move it to bottom
        val gridDock = rootView.findViewById<ViewGroup>(androidx.leanback.R.id.browse_grid_dock)
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, 0, 0, 0)
        gridDock.layoutParams = layoutParams
        gridDock.setPadding(0, resources.getDimensionPixelSize(R.dimen.all_subscription_fragment_top_margin), 0, 0)
    }

    private fun installHeaderView(inflater: LayoutInflater, rootView: View) {
        val gridFrame = rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout
        headerBinding = V4FrgmentAllSubscriptionsBinding.inflate(inflater, gridFrame, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarManager.initialDelay = 0
        progressBarManager.setRootView(view as ViewGroup)

        viewModel.subscriptionsVideoCollectionItemLiveData.observe(viewLifecycleOwner) { pagingData ->
            pagingDataAdapter.submitData(lifecycle, pagingData)
        }

        AllSubscriptionState.lastFocusedView =  AllSubscriptionState.LastFocusedView.NONE
        viewModel.getAllSubscriptionsVideos()

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            pagingDataAdapter.loadStateFlow.collectLatest { loadStates ->
                val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)
                when (loadStates.refresh) {
                    is LoadState.Loading -> {
                        Timber.d("LoadState.Loading")
                        progressBarManager.show()
                        gridDock.visibility = View.GONE
                    }

                    is LoadState.NotLoading -> {
                        Timber.d("LoadState.NotLoading")
                        progressBarManager.hide()
                        gridDock.visibility = View.VISIBLE
                        when {
                            pagingDataAdapter.size() > 0 -> {
                                if (gridDock?.isVisible?.not() == true){
                                    gridDock.isVisible = true
                                }
                                headerBinding.emptyView.isVisible = false
                                isRefreshClicked = false
                            } else -> {
                            headerBinding.emptyView.isVisible = true
                            if (isRefreshClicked){
                                headerBinding.channelRecommendation.isFocusable = false
                                headerBinding.refreshButton.isFocusable = true
                            } else {
                                headerBinding.refreshButton.isFocusable = false
                                headerBinding.channelRecommendation.isFocusable = true
                            }
                            gridDock?.isVisible = false
                            isRefreshClicked = false
                        }
                        }
                    }

                    else -> {}
                }

            }
        }

        headerBinding.refreshButton.setOnClickListener {
            if (requireContext().isNetworkConnected){
                isRefreshClicked = true
                refreshContent()
            } else {
                parentFragmentManager.showAlert(getString(R.string.no_internet), true)
            }
        }

        headerBinding.refreshButton.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                if (headerBinding.emptyView.isVisible){
                    headerBinding.channelRecommendation.isFocusable = true
                    headerBinding.channelRecommendation.requestFocus()
                    AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.CHANNEL_RECOMMENDATION
                }else{
                    AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.DATA_GRID
                }
            }
            return@setOnKeyListener false
        }

        headerBinding.channelRecommendation.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    headerBinding.refreshButton.isFocusable = true
                    headerBinding.refreshButton.requestFocus()
                    AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.REFRESH_BUTTON
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    headerBinding.channelRecommendation.requestFocus()
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    headerBinding.refreshButton.isFocusable = false
                }
            }
            return@setOnKeyListener false
        }
    }

    override fun onResume() {
        super.onResume()
        when (AllSubscriptionState.lastFocusedView) {
            AllSubscriptionState.LastFocusedView.REFRESH_BUTTON -> {
                headerBinding.refreshButton.requestFocus()
            }
            AllSubscriptionState.LastFocusedView.CHANNEL_RECOMMENDATION -> {
                headerBinding.channelRecommendation.requestFocus()
            }

            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.NONE
    }

    private fun refreshContent() {
        pagingDataAdapter.submitData(lifecycle, PagingData.empty())
        AllSubscriptionState.subscriptionsVideosPagingDataMap = null
        viewModel.getAllSubscriptionsVideos()
        subscriptionsViewModel.onRefresh()
    }

    private fun setupFocusSearchListenerForVerticalGrid() {
        val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)

        gridDock.setOnDispatchKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                if (currentItemSelectedPosition <= (NUMBER_OF_COLUMNS - 1)) {
                    headerBinding.refreshButton.requestFocus()
                    AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.REFRESH_BUTTON
                }
            }
            return@setOnDispatchKeyListener false
        }
    }

    private fun setupAdapter() {
        val videoGridPresenter = RumbleVerticalGridPresenter(
            requireContext(),
            0,
            false,
            BaseGridView.WINDOW_ALIGN_HIGH_EDGE.toFloat(),
            Constant.VIEW_ALL_LIVE_OFFSET_PERCENT
        )
        videoGridPresenter.numberOfColumns = NUMBER_OF_COLUMNS
        setGridPresenter(videoGridPresenter)

        pagingDataAdapter = PagingDataAdapter(videoCardPresenter,
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(
                    oldItem: Feed,
                    newItem: Feed
                ): Boolean {
                    return (oldItem as VideoEntity).id == (newItem as VideoEntity).id
                }

                override fun areContentsTheSame(
                    oldItem: Feed,
                    newItem: Feed
                ): Boolean {
                    return (oldItem as VideoEntity) == (newItem as VideoEntity)
                }
            })

        adapter = pagingDataAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
        isRefreshClicked = false

    }
}