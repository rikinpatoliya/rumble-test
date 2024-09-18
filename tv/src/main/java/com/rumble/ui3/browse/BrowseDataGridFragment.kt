package com.rumble.ui3.browse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.databinding.V4CategoryDetailsEmptyViewBinding
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.VerticalGridSupportFragment
import com.rumble.ui3.category.CategoryDetailsActivity
import com.rumble.ui3.channel.details.NoDataException
import com.rumble.ui3.common.RumbleVerticalGridPresenter
import com.rumble.ui3.common.TopLiveCategoriesCardPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.util.Constant
import com.rumble.util.PagingAdapter
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
class BrowseDataGridFragment : VerticalGridSupportFragment {

    private lateinit var displayType: CategoryDisplayType
    private var gridUpClick: (() -> Unit?)? = null

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    constructor() : super()

    constructor(
        displayType: CategoryDisplayType,
        gridUpClick: (() -> Unit)? = null,
    ) : super() {
        this.displayType = displayType
        this.gridUpClick = gridUpClick
    }

    companion object {
        const val NUMBER_OF_COLUMNS = 3
        const val NUMBER_OF_COLUMNS_FOR_CATEGORIES = 6
    }

    /***/
    private lateinit var pagingDataAdapter: PagingAdapter<Feed>
    /***/
    private val viewModel: BrowseViewModel by viewModels()
    /***/
    private lateinit var arrayObjectAdapter  : ArrayObjectAdapter // This is for related categories list items
    /***/
    private lateinit var emptyViewBinding: V4CategoryDetailsEmptyViewBinding
    /***/
    private var currentItemSelectedPosition = 0
    /***/
    private var totalItemCount = 0

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowView, row ->
                if (item is VideoEntity) {
                    if (requireContext().isNetworkConnected){
                        if (item.videoSourceList.isEmpty().not()){
                            if (item.ageRestricted){
                                Utils.showMatureContentDialog(requireContext()){
                                    Utils.navigateToVideoPlayback(requireContext(), item)
                                }
                            } else {
                                Utils.navigateToVideoPlayback(requireContext(), item)
                            }
                        } else {
                            parentFragmentManager.showAlert(getString(R.string.player_error), true)
                        }
                    } else {
                        parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                    }
                }else if (item is CategoryEntity){
                    if (requireContext().isNetworkConnected){
                        val intent = Intent(requireContext(), CategoryDetailsActivity::class.java)
                        intent.putExtra(CategoryDetailsActivity.BUNDLE_KEY_CATEGORY_PATH, item.path)
                        startActivity(intent)
                    } else {
                        parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                    }
                }
            }

        setOnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->

            if (displayType != CategoryDisplayType.CATEGORIES){
                currentItemSelectedPosition = pagingDataAdapter.snapshot().items.indexOf(item)
                totalItemCount = pagingDataAdapter.snapshot().items.size
            } else {
                currentItemSelectedPosition = arrayObjectAdapter.indexOf(item)
                totalItemCount = arrayObjectAdapter.size()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (displayType != CategoryDisplayType.CATEGORIES) {
            setupAdapter()
        } else {
            setupCategoryAdapter()
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModel.onError(throwable)
        progressBarManager?.hide()
        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        installEmptyView(inflater, rootView)
        updateBrowseGridDockMargins(rootView)
        progressBarManager.setRootView(rootView as ViewGroup)
        progressBarManager.initialDelay = 0
        return rootView
    }

    override fun onStart() {
        super.onStart()
        setupFocusSearchListenerForVerticalGrid()
    }

    private fun setupFocusSearchListenerForVerticalGrid() {
        val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)
        gridDock.setOnDispatchKeyListener { v, keyCode, event ->
            val numberOfColumn = if (displayType != CategoryDisplayType.CATEGORIES) NUMBER_OF_COLUMNS else NUMBER_OF_COLUMNS_FOR_CATEGORIES

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                if (currentItemSelectedPosition >= totalItemCount - numberOfColumn && currentItemSelectedPosition < totalItemCount) {
                    val totalFullRowCount = totalItemCount / numberOfColumn
                    val totalItemCountInFullRow = totalFullRowCount * numberOfColumn
                    if (currentItemSelectedPosition < totalItemCountInFullRow && totalItemCountInFullRow != totalItemCount) {
                        setSelectedPosition(totalItemCount - 1)
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                if (currentItemSelectedPosition in 0 until numberOfColumn) {
                    gridUpClick?.invoke()
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                if ((currentItemSelectedPosition + 1) % 3 == 0 || totalItemCount - 1 == currentItemSelectedPosition) {
                    Handler(Looper.getMainLooper()).post {
                        gridDock.requestFocus()
                    }
                }
            }

            return@setOnDispatchKeyListener false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (displayType != CategoryDisplayType.CATEGORIES){
            viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
                viewModel.fetchLiveVideos().collectLatest {
                    pagingDataAdapter.submitData(it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
                pagingDataAdapter.loadStateFlow.collectLatest { loadStates ->
                    when (loadStates.refresh) {
                        is LoadState.Error -> {
                            showLoading(false)
                            if ((loadStates.refresh as LoadState.Error).error is NoDataException) {
                                emptyViewBinding.root.visibility = View.VISIBLE
                            } else {
                                parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
                                emptyViewBinding.root.visibility = View.GONE
                            }
                        }
                        is LoadState.Loading -> {
                            Timber.d("LoadState.Loading")
                            showLoading(true)
                            emptyViewBinding.root.visibility = View.GONE
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
                                }
                                else -> {
                                    when (displayType) {
                                        CategoryDisplayType.LIVE_STREAM -> {
                                            emptyViewBinding.title.text = getString(R.string.category_details_live_empty_title)
                                            emptyViewBinding.description.text = getString(R.string.category_details_live_empty_description)
                                        }
                                        else -> {
                                            emptyViewBinding.title.text = getString(R.string.category_details_videos_empty_title)
                                            emptyViewBinding.description.text = getString(R.string.category_details_videos_empty_description)
                                        }
                                    }
                                    emptyViewBinding.root.isVisible = true
                                    gridDock?.isVisible = false
                                }
                            }
                        }

                        else -> {}
                    }

                }
            }
        } else {
            showLoading(true)
            viewModel.fetchCategoryData()
            val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)
            viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
                viewModel.categoriesLiveData.observe(viewLifecycleOwner) {
                    val gridDock = requireView().findViewById<BrowseFrameLayout>(androidx.leanback.R.id.browse_grid_dock)
                    showLoading(false)
                    if (it.isNullOrEmpty().not()){
                        emptyViewBinding.root.visibility = View.GONE
                        if (gridDock?.isVisible?.not() == true){
                            gridDock.isVisible = true
                        }
                        arrayObjectAdapter.addAll(0, it)
                    } else {
                        gridDock.isVisible = false
                        emptyViewBinding.root.visibility = View.VISIBLE
                    }
                }

                viewModel.errorCategoriesLiveData.observe(viewLifecycleOwner) {
                    showLoading(false)
                    gridDock.isVisible = true
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBarManager.show()
            emptyViewBinding.root.visibility = View.GONE
        } else {
            progressBarManager.hide()
        }
    }

    private fun installEmptyView(inflater: LayoutInflater, rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for BrowseFrameLayout and attach header view to it.
        val gridFrame =
            rootView.findViewById<ViewGroup>(androidx.leanback.R.id.grid_frame) as BrowseFrameLayout
        emptyViewBinding = V4CategoryDetailsEmptyViewBinding.inflate(inflater, gridFrame, true)
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
            0,
            0,
            0
        )
        gridDock.layoutParams = layoutParams
        gridDock.setPadding(resources.getDimensionPixelSize(R.dimen.browse_grid_item_padding_start), resources.getDimensionPixelSize(R.dimen.browse_grid_item_top_layout_padding_expand),0,0)
    }

    fun viewPadding(topPadding: Int){
        val gridDock = requireView().findViewById<ViewGroup>(androidx.leanback.R.id.browse_grid_dock)
        gridDock.setPadding(resources.getDimensionPixelSize(R.dimen.browse_grid_item_padding_start),topPadding,0,0)
        gridDock.requestLayout()
    }

    private fun setupAdapter() {
        val categoryGridPresenter = RumbleVerticalGridPresenter(requireContext(), 0, false,
            BaseGridView.WINDOW_ALIGN_LOW_EDGE.toFloat(), Constant.CATEGORY_DETAILS_OFFSET_PERCENT )
        categoryGridPresenter.numberOfColumns = NUMBER_OF_COLUMNS
        setGridPresenter(categoryGridPresenter)

        pagingDataAdapter = PagingAdapter(videoCardPresenter,
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

    private fun setupCategoryAdapter() {
        val presenter = RumbleVerticalGridPresenter(requireContext(), 0, false,
            BaseGridView.WINDOW_ALIGN_LOW_EDGE.toFloat(), Constant.CATEGORY_DETAILS_OFFSET_PERCENT )
        presenter.shadowEnabled = false
        presenter.numberOfColumns = NUMBER_OF_COLUMNS_FOR_CATEGORIES
        setGridPresenter(presenter)
        val creditsPresenter = TopLiveCategoriesCardPresenter()
        arrayObjectAdapter = ArrayObjectAdapter(creditsPresenter)
        adapter = arrayObjectAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
    }

    fun getCurrentAdapterSize(): Int {
        return if (displayType != CategoryDisplayType.CATEGORIES){
            pagingDataAdapter.size()
        } else {
            arrayObjectAdapter.size()
        }
    }
    fun selectPosition(position: Int){
        setSelectedPosition(position)
    }
}