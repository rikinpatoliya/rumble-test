package com.rumble.ui3.channel.details.v4

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.databinding.V3ChannelDetailsEmptyViewBinding
import com.rumble.databinding.V3ChannelDetailsHeaderBinding
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.leanback.VerticalGridSupportFragment
import com.rumble.network.queryHelpers.Sort
import com.rumble.network.session.SessionManager
import com.rumble.ui3.channel.details.NoDataException
import com.rumble.ui3.channel.details.more.BlockStateListener
import com.rumble.ui3.channel.details.more.GuidedStepActivity
import com.rumble.ui3.common.RumbleVerticalGridPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.search.SearchItemsPosition
import com.rumble.util.Constant
import com.rumble.util.Constant.CHANNEL_DETAILS_OFFSET_PERCENT
import com.rumble.util.Constant.CHANNEL_DETAILS_OFFSET_PERCENT_COLLAPSED
import com.rumble.util.PagingAdapter
import com.rumble.util.Utils
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class ChannelDetailsFragmentV4() : VerticalGridSupportFragment(),
    View.OnClickListener,
    BrowseSupportFragment.MainFragmentAdapterProvider, BlockStateListener, View.OnFocusChangeListener {

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    companion object {
        private const val NUMBER_OF_COLUMNS = 3
        private const val BUNDLE_KEY_CHANNEL = "channel"
        private const val BUNDLE_KEY_SHOW_LOGO = "show_logo"
        private const val BUNDLE_FROM_ACTIVITY = "from_activity"
        private const val BUNDLE_IS_CACHING_SUPPORTED = "is_caching_supported"
        var showLogo: Boolean = true
        var fromActivity: Boolean = false
        var caching: Boolean = false

        fun getInstance(channel: CreatorEntity?, showLogo: Boolean = true, fromActivity: Boolean = false, isCachingSupported: Boolean = false): ChannelDetailsFragmentV4 {
            val fragment = ChannelDetailsFragmentV4()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_KEY_CHANNEL, channel)
            bundle.putBoolean(BUNDLE_KEY_SHOW_LOGO, showLogo)
            bundle.putBoolean(BUNDLE_FROM_ACTIVITY, fromActivity)
            bundle.putBoolean(BUNDLE_IS_CACHING_SUPPORTED, isCachingSupported)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: ChannelDetailsHandler
            by viewModels<ChannelDetailsViewModelV4>()

    private val channelDetailsSharedViewModel: ChannelDetailsSharedHandler
            by activityViewModels<ChannelDetailsSharedViewModel>()

    private lateinit var pagingDataAdapter: PagingAdapter<Feed>
    private lateinit var headerBinding: V3ChannelDetailsHeaderBinding
    private lateinit var emptyViewBinding: V3ChannelDetailsEmptyViewBinding
    private lateinit var rootView: View

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<ChannelDetailsFragmentV4> =
        object : BrowseSupportFragment.MainFragmentAdapter<ChannelDetailsFragmentV4>(this) {}

    private var currentItemSelectedPosition: Int = 0
    private var totalItemCount: Int = 0

    private var rightFocusedView: View? = null
    private var lastSelectedView: View? = null

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                if (item is Feed) {
                    viewModel.onVideoItemClicked(item as VideoEntity)
                    lastSelectedView = null
                }
            }

        setOnItemViewSelectedListener { _, item, _, row ->
            currentItemSelectedPosition = pagingDataAdapter.snapshot().items.indexOf(item)
            viewModel.onVideoItemFocusChanged(currentItemSelectedPosition)
            totalItemCount = pagingDataAdapter.snapshot().items.size

            adjustHeaderSize(currentItemSelectedPosition < NUMBER_OF_COLUMNS)
        }
    }

    private fun adjustHeaderSize(expanded: Boolean) {
        val largeMargin = resources.getDimensionPixelSize(R.dimen.channel_details_header_row_alignment_offset)
        val smallMargin =
            resources.getDimensionPixelSize(R.dimen.channel_details_header_row_alignment_offset_collapsed)
        
        mGridViewHolder.gridView.windowAlignmentOffsetPercent = if (expanded) {
            CHANNEL_DETAILS_OFFSET_PERCENT
        } else {
            CHANNEL_DETAILS_OFFSET_PERCENT_COLLAPSED
        }

        val currentMargin = if (expanded) {
            smallMargin
        } else {
            largeMargin
        }
        val targetMargin = if (expanded) {
            largeMargin
        } else {
            smallMargin
        }

        val layoutParams = headerBinding.detailsHeaderContainer.layoutParams

        if (layoutParams is ViewGroup.MarginLayoutParams && layoutParams.topMargin != targetMargin) {

            val animator = ValueAnimator.ofInt(currentMargin, targetMargin)

            animator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Int
                layoutParams.topMargin = animatedValue
                headerBinding.detailsHeaderContainer.requestLayout()
            }

            animator.start()
        }

        adjustGridDockTopMargin(expanded)
    }

    private fun adjustGridDockTopMargin(expandedHeader: Boolean) {
        val gridDock = rootView.findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)

        val largeMargin = resources.getDimensionPixelSize(R.dimen.channel_details_video_row_alignment_offset)
        val smallMargin =
            resources.getDimensionPixelSize(R.dimen.channel_details_video_row_alignment_offset_collapsed_header)

        val currentMargin = if (expandedHeader) {
            smallMargin
        } else {
            largeMargin
        }
        val targetMargin = if (expandedHeader) {
            largeMargin
        } else {
            smallMargin
        }

        val layoutParams = gridDock.layoutParams

        if (layoutParams is ViewGroup.MarginLayoutParams && layoutParams.topMargin != targetMargin) {

            val animator = ValueAnimator.ofInt(currentMargin, targetMargin)

            animator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Int

                layoutParams.topMargin = animatedValue
                gridDock.requestLayout()
            }

            animator.start()
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModel.onError(throwable)
        progressBarManager?.hide()
        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChannelDetailsFragmentV4Args.fromBundle(requireArguments()).channel.let {
            viewModel.channelObject = it
            viewModel.channelId = it.channelId
        }

        showLogo = ChannelDetailsFragmentV4Args.fromBundle(requireArguments()).showLogo
        fromActivity = ChannelDetailsFragmentV4Args.fromBundle(requireArguments()).fromActivity
        caching = ChannelDetailsFragmentV4Args.fromBundle(requireArguments()).isCachingSupported
        setupAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        rootView = super.onCreateView(inflater, container, savedInstanceState)
        installHeaderView(inflater, rootView)
        installEmptyView(inflater, rootView)
        updateBrowseGridDockMargins(rootView)
        headerBinding.radioGroup.isVisible = false

        viewModel.onLoadInitialData(
            channelDetailsSharedViewModel.uiStateMap.value[viewModel.channelId],
            channelDetailsSharedViewModel.channelVideosMap.value[viewModel.channelId]
        )

        progressBarManager.setRootView(rootView as ViewGroup)
        progressBarManager.initialDelay = 0

        return rootView
    }

    override fun onStart() {
        super.onStart()
        setupFocusSearchListener()
        setupFocusSearchListenerForVerticalGrid()
    }

    private fun collectPagingFlow(flow: Flow<PagingData<Feed>>) {
        lifecycleScope.launch {
            flow.collectLatest { pagingData ->
                pagingDataAdapter.submitData(pagingData)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showLogo) headerBinding.topHeaderImage.visibility =
            View.VISIBLE else headerBinding.topHeaderImage.visibility = View.GONE

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataState.collect {
                collectPagingFlow(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            pagingDataAdapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Error -> {
                        Timber.d("LoadState.Error")

                        showLoading(false)

                        if ((loadStates.refresh as LoadState.Error).error is NoDataException) {
                            emptyViewBinding.root.visibility = View.VISIBLE
                            headerBinding.detailsHeaderContainer.requestFocus()
                        } else {
                            parentFragmentManager.showAlert(
                                getString(R.string.error_fragment_message),
                                true
                            )
                            emptyViewBinding.root.visibility = View.GONE
                        }
                        headerBinding.radioGroup.isVisible = false
                    }
                    is LoadState.Loading -> {
                        Timber.d("LoadState.Loading")
                        showLoading(true)
                    }
                    is LoadState.NotLoading -> {
                        Timber.d("LoadState.NotLoading")
                        showLoading(false)
                        val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)
                        when {
                            pagingDataAdapter.size() > 0 -> {
                                if (gridDock?.isVisible?.not() == true){
                                    gridDock.isVisible = true
                                }
                                emptyViewBinding.root.isVisible = false
                                headerBinding.radioGroup.isVisible = true
                            }

                            else -> {
                                pagingDataAdapter.submitData(lifecycle, PagingData.empty())
                                emptyViewBinding.root.isVisible = true
                                headerBinding.radioGroup.isVisible = false
                                gridDock?.isVisible = false
                            }
                        }

                        if (fromActivity) {
                            if (pagingDataAdapter.size() > 0) {
                                if (viewModel.uiState.value.lastSelectedView == FocusViews.GRID) {
                                    gridDock?.requestFocus()
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        viewModel.channelDetails.observe(viewLifecycleOwner) { _ ->
            headerBinding.moreButton.isVisible = true
            headerBinding.subscribeButton.isVisible = true
            headerBinding.item = viewModel.channelObject
        }

        headerBinding.subscribeButton.onFocusChangeListener = this
        headerBinding.moreButton.onFocusChangeListener = this
        headerBinding.radioButtonRecent.onFocusChangeListener = this
        headerBinding.radioButtonViewed.onFocusChangeListener = this

        headerBinding.radioButtonViewed.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                if (pagingDataAdapter.size() <= 0) {
                    Handler(Looper.getMainLooper()).post {
                        headerBinding.radioButtonViewed.requestFocus()
                    }
                }
            }
            return@setOnKeyListener false
        }

        observeVmEvents()

    }

    private fun observeVmEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.vmEvents.collect { event ->
                    when (event) {
                        ChannelDetailsVmEvent.RefreshVideosIfEmpty -> {
                            if (pagingDataAdapter.size() <= 0) {
                                viewModel.onLoadChannelVideos()
                            }
                        }

                        is ChannelDetailsVmEvent.NavigateToVideoPlayer -> {
                            if (event.videoEntity.ageRestricted) {
                                Utils.showMatureContentDialog(requireContext()) {
                                    Utils.navigateToVideoPlayback(
                                        requireContext(),
                                        event.videoEntity,
                                        viewModel.channelObject?.channelId ?: ""
                                    )
                                }
                            } else {
                                Utils.navigateToVideoPlayback(
                                    requireContext(),
                                    event.videoEntity,
                                    viewModel.channelObject?.channelId ?: ""
                                )
                            }
                        }

                        ChannelDetailsVmEvent.ShowVideoPlayerError -> {
                            parentFragmentManager.showAlert(getString(R.string.player_error), true)
                        }

                        ChannelDetailsVmEvent.ShowChannelReported -> {
                            parentFragmentManager.showAlert(getString(R.string.the_channel_has_been_reported), false)
                        }
                    }
                }
            }

            channelDetailsSharedViewModel.vmEvents.collect { event ->
                when (event) {
                    ChannelDetailsSharedVmEvent.RestoreLastFocusState -> lastFocusFromSubscriptionListScreen()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.subscribe_button -> {
                if (requireContext().isNetworkConnected.not()) {
                    parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                    return
                }
                val isCookieExist = runBlocking(errorHandler) {
                    sessionManager.cookiesFlow.first().isNotEmpty()
                }

                if (isCookieExist) {
                    viewModel.onUpdateSubscription()
                    if (fromActivity.not()){
                        Navigation.findNavController(requireView()).previousBackStackEntry?.savedStateHandle?.set(Constant.TAG_REFRESH, true)
                    }else{
                        SearchItemsPosition.refreshChannelDetailInRowItem = true
                    }

                } else {
                    parentFragmentManager.showAlert(getString(R.string.channel_details_user_not_logged_in), false)
                }
            }
            R.id.radio_button_recent -> {
                if (requireContext().isNetworkConnected.not()) {
                    parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                    sortButtonsClickable(true)
                    return
                }
                pagingDataAdapter.submitData(lifecycle, PagingData.empty())
                headerBinding.radioButtonRecent.isClickable = false
                headerBinding.radioButtonViewed.isClickable = true
                viewModel.onSortChanged(Sort.DATE)
            }
            R.id.radio_button_viewed -> {
                if (requireContext().isNetworkConnected.not()) {
                    parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                    sortButtonsClickable(true)
                    return
                }
                pagingDataAdapter.submitData(lifecycle, PagingData.empty())
                headerBinding.radioButtonRecent.isClickable = true
                headerBinding.radioButtonViewed.isClickable = false
                viewModel.onSortChanged(Sort.VIEWS)
            }
            R.id.more_button -> {
                GuidedStepActivity.launchActivity(requireActivity(), viewModel.channelObject, this)
            }
        }
    }

    private fun sortButtonsClickable(clickable: Boolean) {
        headerBinding.radioButtonRecent.isClickable = clickable
        headerBinding.radioButtonViewed.isClickable = clickable
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<ChannelDetailsFragmentV4> =
        fragmentAdapter

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBarManager.show()
            emptyViewBinding.root.visibility = View.GONE
        } else {
            progressBarManager.hide()
            headerBinding.detailsHeaderContainer.visibility = View.VISIBLE
        }
    }

    private fun setupFocusSearchListener() {
        val browseFrameLayout = requireView().findViewById<BrowseFrameLayout>(R.id.grid_frame)
        browseFrameLayout.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->

                if (headerBinding.detailsHeaderContainer.hasFocus() && (direction == View.FOCUS_DOWN)) {
                    if (pagingDataAdapter.size()<=0){
                        headerBinding.detailsHeaderContainer
                    }else{
                        lastSelectedView = null
                        viewModel.onFocusChanged(FocusViews.GRID)
                        browseFrameLayout
                    }
                } else if (headerBinding.subscribeButton.hasFocus()) {
                    if (direction == View.FOCUS_LEFT || direction == View.FOCUS_UP) {
                        null
                    } else if (direction == View.FOCUS_RIGHT) {
                        if (headerBinding.radioGroup.isVisible.not() && headerBinding.moreButton.hasFocus()){
                            headerBinding.moreButton
                        }else{
                            headerBinding.radioButtonRecent
                        }
                    }
                    null
                } else if (headerBinding.radioButtonRecent.hasFocus()) {
                    if (direction == View.FOCUS_LEFT || direction == View.FOCUS_UP) {
                        headerBinding.subscribeButton
                    } else if (direction == View.FOCUS_RIGHT) {
                        headerBinding.radioButtonViewed
                    }
                    null
                } else if (headerBinding.radioButtonViewed.hasFocus()) {
                    if (direction == View.FOCUS_LEFT || direction == View.FOCUS_UP) {
                        headerBinding.radioButtonRecent
                    } else if (direction == View.FOCUS_RIGHT) {
                        if (headerBinding.radioButtonViewed.hasFocus()){
                            Handler(Looper.getMainLooper()).post {
                                headerBinding.radioButtonViewed.requestFocus()
                            }
                        }else{
                            null
                        }
                    }
                    null
                } else if (focused !== headerBinding.root && direction == View.FOCUS_UP) {
                    if (headerBinding.moreButton.hasFocus()){
                        headerBinding.moreButton
                    }else{
                        headerBinding.subscribeButton
                    }
                } else if (headerBinding.moreButton.hasFocus() && direction == View.FOCUS_RIGHT) {
                    if (headerBinding.radioGroup.isVisible.not()){
                        headerBinding.moreButton
                    }else{
                        headerBinding.radioButtonRecent
                    }
                } else {
                    null
                }
            }
    }

    private fun setupFocusSearchListenerForVerticalGrid() {
        val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)

        gridDock.setOnDispatchKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                if (currentItemSelectedPosition >= totalItemCount - NUMBER_OF_COLUMNS && currentItemSelectedPosition < totalItemCount) {
                    val totalFullRowCount = totalItemCount / NUMBER_OF_COLUMNS
                    val totalItemCountInFullRow = totalFullRowCount * NUMBER_OF_COLUMNS
                    if (currentItemSelectedPosition < totalItemCountInFullRow && totalItemCountInFullRow != totalItemCount) {
                        setSelectedPosition(totalItemCount - 1)
                    }
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                when (currentItemSelectedPosition) {
                    0, 1 -> {
                        headerBinding.subscribeButton.requestFocus()
                    }

                    (NUMBER_OF_COLUMNS - 1) -> {
                        rightFocusedView?.requestFocus()
                    }
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                if ((currentItemSelectedPosition + 1).mod(3) == 0 || (totalItemCount - 1) == currentItemSelectedPosition) {
                    Handler(Looper.getMainLooper()).post {
                        gridDock.requestFocus()
                        lastSelectedView = null
                        viewModel.onFocusChanged(FocusViews.GRID)
                    }
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN && showLogo) {
                if ((currentItemSelectedPosition + 1).mod(1) == 0) {
                    Handler(Looper.getMainLooper()).post {
                        gridDock.requestFocus()
                        lastSelectedView = null
                        viewModel.onFocusChanged(FocusViews.GRID)
                    }
                }
            }

            return@setOnDispatchKeyListener false
        }
    }

    fun lastFocusFromSubscriptionListScreen() {
        when (viewModel.uiState.value.currentFocusedViews) {
            FocusViews.NONE -> {}
            FocusViews.FOLLOW -> {
                Handler(Looper.getMainLooper()).post {
                    headerBinding.subscribeButton.requestFocus()
                }
            }

            FocusViews.MORE_BUTTON -> {
                Handler(Looper.getMainLooper()).post {
                    headerBinding.moreButton.requestFocus()
                }
            }

            FocusViews.MOST_RECENT -> {
                Handler(Looper.getMainLooper()).post {
                    if (headerBinding.radioGroup.isVisible) {
                        headerBinding.radioButtonRecent.requestFocus()
                    }
                }
            }

            FocusViews.MOST_VIEWED -> {
                Handler(Looper.getMainLooper()).post {
                    if (headerBinding.radioGroup.isVisible) {
                        headerBinding.radioButtonViewed.requestFocus()
                    }
                }
            }

            FocusViews.GRID -> {
                setSelectedPosition(viewModel.uiState.value.lastSelectedDetailsItemPosition)
            }
        }
        viewModel.onLastFocusRestored()
    }

    private fun installHeaderView(inflater: LayoutInflater, rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for BrowseFrameLayout and attach header view to it.
        val gridFrame =
            rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout
        headerBinding = V3ChannelDetailsHeaderBinding.inflate(inflater, gridFrame, true)

        headerBinding.item = viewModel.channelObject
        headerBinding.actionClickHandler = this
        rightFocusedView = headerBinding.radioButtonRecent
    }

    private fun updateBrowseGridDockMargins(rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for browse_grid_dock and move it to bottom
        val gridDock = rootView.findViewById<ViewGroup>(androidx.leanback.R.id.browse_grid_dock)

        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(
            0,
            resources.getDimensionPixelSize(R.dimen.channel_details_video_row_alignment_offset),
            0,
            0
        )
        gridDock.layoutParams = layoutParams
    }

    private fun installEmptyView(inflater: LayoutInflater, rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for BrowseFrameLayout and attach header view to it.
        val gridFrame =
            rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout
        emptyViewBinding = V3ChannelDetailsEmptyViewBinding.inflate(inflater, gridFrame, true)
    }

    private fun setupAdapter() {
        val videoGridPresenter = RumbleVerticalGridPresenter(
            context = requireContext(),
            focusZoomFactor = 0,
            useFocusDimmer = false,
            windowAlignment = BaseGridView.WINDOW_ALIGN_HIGH_EDGE.toFloat(),
            windowAlignmentOffsetPercent = Constant.CHANNEL_DETAILS_OFFSET_PERCENT
        )
        videoGridPresenter.numberOfColumns = NUMBER_OF_COLUMNS
        setGridPresenter(videoGridPresenter)

        pagingDataAdapter = PagingAdapter(
            videoCardPresenter,
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

    override fun updateChannelState(channelDetailsEntity: CreatorEntity) {
        viewModel.channelObject = channelDetailsEntity
        headerBinding.item = channelDetailsEntity
        headerBinding.detailsHeaderContainer.requestFocus()

        if (fromActivity.not()){
            Navigation.findNavController(requireView()).previousBackStackEntry?.savedStateHandle?.set(Constant.TAG_REFRESH, true)
        }else{
            SearchItemsPosition.refreshChannelDetailInRowItem = true
        }
    }

    override fun channelReported() {
        viewModel.onChannelReported()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
    }

    override fun onPause() {
        super.onPause()
        channelDetailsSharedViewModel.onUpdateUiState(
            uiState = viewModel.uiState.value,
            pagingDataFlow = viewModel.pagingDataState.value
        )
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.subscribe_button -> {
                if (hasFocus) {
                    lastSelectedView = v
                    viewModel.onFocusChanged(FocusViews.FOLLOW)
                    viewModel.onVideoItemFocusChanged(-1)
                }
            }

            R.id.more_button -> {
                if (hasFocus) {
                    lastSelectedView = v
                    viewModel.onFocusChanged(FocusViews.MORE_BUTTON)
                    viewModel.onVideoItemFocusChanged(-1)
                }
            }
            R.id.radio_button_recent -> {
                if (hasFocus) {
                    rightFocusedView = v
                    lastSelectedView = v
                    viewModel.onFocusChanged(FocusViews.MOST_RECENT)
                    viewModel.onVideoItemFocusChanged(-1)
                }
            }
            R.id.radio_button_viewed -> {
                if (hasFocus) {
                    rightFocusedView = v
                    lastSelectedView = v
                    viewModel.onFocusChanged(FocusViews.MOST_VIEWED)
                    viewModel.onVideoItemFocusChanged(-1)
                }
            }
        }
    }
}