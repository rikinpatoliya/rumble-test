package com.rumble.ui3.home.v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.databinding.V4ViewAllHeaderBinding
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.queryHelpers.VideoCollectionId
import com.rumble.ui3.common.RumbleVerticalGridPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.common.v4.RumbleVerticalGridSupportFragmentV4
import com.rumble.ui3.home.ViewAllViewModel
import com.rumble.util.Constant
import com.rumble.util.Utils
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "ViewAllFragmentV4"

@AndroidEntryPoint
class ViewAllFragmentV4 : RumbleVerticalGridSupportFragmentV4(), View.OnClickListener {

    companion object {
        private const val NUMBER_OF_COLUMNS = 3
        private const val BUNDLE_KEY_FEED_ID = "feed_id"
        private const val BUNDLE_KEY_FEED_TITLE = "feed_title"

        fun getInstance(feedId: String, feedTitle: String): ViewAllFragmentV4 {
            val fragment = ViewAllFragmentV4()
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_FEED_ID, feedId)
            bundle.putString(BUNDLE_KEY_FEED_TITLE, feedTitle)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    private lateinit var pagingDataAdapter: PagingDataAdapter<Feed>
    private val viewModel: ViewAllViewModel by viewModels()

    private lateinit var headerBinding: V4ViewAllHeaderBinding

    override val numberOfColumns: Int
        get() = NUMBER_OF_COLUMNS

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
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModel.handleErrorUseCase(TAG, throwable)
        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarManager.initialDelay = 0
        progressBarManager.setRootView(view as ViewGroup)

        arguments?.let {
            val feedId = ViewAllFragmentV4Args.fromBundle(it).feedId
            viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
                if (feedId != VideoCollectionId.Live.value) {
                    viewModel.getVideoList(feedId).collectLatest { pagingData ->
                        pagingDataAdapter.submitData(pagingData)
                    }
                } else {
                    viewModel.getLiveVideoList().collectLatest { pagingData ->
                        pagingDataAdapter.submitData(pagingData)
                    }
                }
            }
            headerBinding.categoryTitle = ViewAllFragmentV4Args.fromBundle(it).feedTitle
        }

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

                    else -> {}
                }

            }
        }
    }

    override fun getPresenter() = videoCardPresenter

    private fun setupAdapter() {
        val videoGridPresenter = RumbleVerticalGridPresenter(
            requireContext(),
            0,
            false,
            BaseGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED,
            Constant.VIEW_ALL_LIVE_OFFSET_PERCENT
        )
        videoGridPresenter.numberOfColumns = numberOfColumns
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
            resources.getDimensionPixelSize(R.dimen.view_all_top_padding),
            0,
            0
        )
        gridDock.layoutParams = layoutParams
    }

    private fun installHeaderView(inflater: LayoutInflater, rootView: View) {
        val gridFrame =
            rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout
        headerBinding = V4ViewAllHeaderBinding.inflate(inflater, gridFrame, true)
        headerBinding.actionClickHandler = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
    }
}