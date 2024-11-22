package com.rumble.ui3.search.v4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.rumble.MainActivityNew
import com.rumble.R
import com.rumble.databinding.V4FragmentSearchNewBinding
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.leanback.RowsSupportFragment
import com.rumble.player.VideoPlaybackActivityDirections
import com.rumble.ui3.channel.details.v4.ChannelDetailsActivityV4Directions
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.search.ChannelCardPresenter
import com.rumble.ui3.search.LastFocusView
import com.rumble.ui3.search.SearchItemsPosition
import com.rumble.ui3.search.SearchItemsPosition.combinedSelectedItemPosition
import com.rumble.ui3.search.SearchItemsPosition.lastClickedChannelItemPosition
import com.rumble.ui3.search.SearchItemsPosition.lastClickedChannelRowPosition
import com.rumble.ui3.search.SearchItemsPosition.lastFocusView
import com.rumble.ui3.search.SearchItemsPosition.selectedChannelItemPosition
import com.rumble.ui3.search.SearchItemsPosition.selectedChannelRowPosition
import com.rumble.ui3.search.SearchItemsPosition.selectedRowPosition
import com.rumble.ui3.search.SearchItemsPosition.selectedVideoItemPosition
import com.rumble.util.Constant
import com.rumble.util.PagingAdapter
import com.rumble.util.Utils
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import com.rumble.utils.DeviceUtil
import com.rumble.utils.RumbleConstants.SEARCH_UPDATE_DELAY_MS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragmentV4 : RowsSupportFragment(){

    @Inject
    lateinit var inputMethodManager: InputMethodManager

    @Inject
    lateinit var deviceUtil: DeviceUtil
    
    private val viewModel: SearchViewModelV4 by viewModels()
    private val progressBarManager by lazy {
        ProgressBarManager().apply {
            this.initialDelay = 0
        }
    }

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    private var _binding: V4FragmentSearchNewBinding? = null

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    private var isKeyboardShown = false
    private var lastCursorPosition = 0

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        viewModel.doSearch(lifecycle)
    }

    init {
        onItemViewSelectedListener =
            OnItemViewSelectedListener { _, item, _, row ->
                if (item != null && row != null) {
                    val listRow = row as ListRow
                    selectedRowPosition = viewModel.rowsAdapter.indexOf(row) ?: -1
                    if (item is VideoEntity) {
                        val currentRowAdapter = listRow.adapter as PagingAdapter<VideoEntity>
                        val selectedIndex = currentRowAdapter.snapshot().indexOf(item)
                        selectedVideoItemPosition = selectedIndex
                        combinedSelectedItemPosition = selectedIndex
                    }

                    if (item is CreatorEntity) {
                        selectedChannelRowPosition = 1

                        val currentRowAdapter = listRow.adapter as PagingAdapter<CreatorEntity>
                        val selectedIndex = currentRowAdapter.snapshot().indexOf(item)
                        selectedChannelItemPosition = selectedIndex
                        combinedSelectedItemPosition = selectedIndex
                    }
                }
            }

        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, row ->
                val rowPos = viewModel.rowsAdapter.indexOf(row) ?: 0
                val listRow = row as ListRow
                lastClickedChannelRowPosition = rowPos

                if (item is VideoEntity) {
                    if (item.videoSourceList.isEmpty().not()){
                        val currentRowAdapter = listRow.adapter as PagingAdapter<VideoEntity>
                        val selectedIndex = currentRowAdapter.snapshot().indexOf(item)
                        lastClickedChannelItemPosition = selectedIndex

                        if (item.ageRestricted){
                            Utils.showMatureContentDialog(requireContext()){
                                Navigation.findNavController(requireView()).navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(item))
                            }
                        } else {
                            Navigation.findNavController(requireView()).navigate(VideoPlaybackActivityDirections.actionGlobalPlaybackActivity(item))
                        }
                    } else {
                        parentFragmentManager.showAlert(getString(R.string.player_error), true)
                    }
                } else if (item is CreatorEntity) {

                    val currentRowAdapter = listRow.adapter as PagingAdapter<CreatorEntity>
                    val selectedIndex = currentRowAdapter.snapshot().indexOf(item)
                    lastClickedChannelItemPosition = selectedIndex

                    Navigation.findNavController(requireView()).navigate(
                        ChannelDetailsActivityV4Directions.actionGlobalChannelDetailsActivityV4(item, true))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {

        val channelCardPresenter = ChannelCardPresenter()
        viewModel.channelPagingDataAdapter = PagingAdapter(channelCardPresenter,
            object : DiffUtil.ItemCallback<CreatorEntity>() {
                override fun areItemsTheSame(
                    oldItem: CreatorEntity,
                    newItem: CreatorEntity
                ): Boolean {
                    return oldItem.channelId == newItem.channelId
                }

                override fun areContentsTheSame(
                    oldItem: CreatorEntity,
                    newItem: CreatorEntity
                ): Boolean {
                    return oldItem == newItem
                }
            })

        viewModel.videoPagingDataAdapter = PagingAdapter(videoCardPresenter,
            object : DiffUtil.ItemCallback<VideoEntity>() {
                override fun areItemsTheSame(
                    oldItem: VideoEntity,
                    newItem: VideoEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: VideoEntity,
                    newItem: VideoEntity
                ): Boolean {
                    return oldItem == newItem
                }
            })

        adapter = viewModel.rowsAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = V4FragmentSearchNewBinding.inflate(inflater, container, false)

        progressBarManager.setRootView(binding.root as ViewGroup)
        progressBarManager.setProgressBarView(binding.loadingView)

        // set the view model as the lifecycle owner of the binding.
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.search.setOnClickListener {
            if (!isKeyboardShown) {
                showNativeKeyboard()
                isKeyboardShown = true
            }
        }



        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(searchRunnable)
                handler.postDelayed(searchRunnable, SEARCH_UPDATE_DELAY_MS)
            }
        })

        binding.search.setOnLongClickListener {
            showNativeKeyboard()
            true
        }

        binding.search.setOnKeyboardDismissListener {
            if (isKeyboardShown) {
                hideNativeKeyboard()
                isKeyboardShown = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.search.requestFocus()
                    if (deviceUtil.isAmazonFireTvDevice()) {
                        binding.search.setSelection(binding.search.text.length)
                    }
                }, Constant.REQUEST_FOCUS_DELAY)
            }
        }

        binding.search.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                lastFocusView = LastFocusView.SearchView
            }
        }

        binding.search.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                if (binding.search.hasFocus() && viewModel.rowsAdapter.size() <= 0){
                    binding.search.requestFocus()
                    return@setOnKeyListener true
                }else if (binding.search.hasFocus() && viewModel.rowsAdapter.size() > 0){
                    val videoRowItemAdapter = viewModel.rowsAdapter.get(0) as ListRow
                    val channelRowItemAdapter = viewModel.rowsAdapter.get(1) as ListRow
                    if (videoRowItemAdapter.adapter.size() <= 0 && channelRowItemAdapter.adapter.size() <= 0){
                        Handler(Looper.getMainLooper()).post {
                            binding.search.requestFocus()
                        }
                    }else if (videoRowItemAdapter.adapter.size() <= 0){
                        Handler(Looper.getMainLooper()).post {
                            binding.containerList.requestFocus()
                            setFocusToRowPos(1,selectedChannelItemPosition)
                        }
                    }else{
                        Handler(Looper.getMainLooper()).post {
                            binding.containerList.requestFocus()
                            setFocusToRowPos(0,selectedVideoItemPosition)
                        }
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN && binding.search.hasFocus()){
                Handler(Looper.getMainLooper()).post {
                    binding.search.setSelection(binding.search.text.length)
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN && binding.search.hasFocus()){
                if (isKeyboardShown.not()){
                    val cursorPosition = binding.search.selectionStart
                    if (cursorPosition <= 0){
                        val activity = (activity as MainActivityNew)
                        activity.showLeftMenu()
                    }
                }
            }
            false
        }

        binding.containerList.setOnKeyInterceptListener {
            if (it.action == KeyEvent.ACTION_DOWN){
                if (it.keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    if (viewModel.rowsAdapter.size() > 0){
                        if (selectedRowPosition == 0){
                            binding.search.isFocusable = true
                            binding.search.requestFocus()
                            showNativeKeyboard()
                            return@setOnKeyInterceptListener true
                        }else if (selectedRowPosition == 1){
                            val channelRowItemAdapter = viewModel.rowsAdapter.get(0) as ListRow
                            if (channelRowItemAdapter.adapter.size() <= 0){
                                binding.search.isFocusable = true
                                binding.search.requestFocus()
                                showNativeKeyboard()

                                return@setOnKeyInterceptListener true
                            }
                        }
                    }
                }else if (it.keyCode == KeyEvent.KEYCODE_DPAD_DOWN && selectedRowPosition == 0){
                    if (viewModel.rowsAdapter.size() > 0){
                        val channelRowItemAdapter = viewModel.rowsAdapter.get(1) as ListRow
                        channelRowItemAdapter.let {
                            Handler(Looper.getMainLooper()).post {
                                if (it.adapter.size() <= 0) {
                                    binding.search.isFocusable = false
                                    setFocusToRowPos(0, selectedVideoItemPosition)
                                }
                            }
                        }
                    }
                } else if (it.keyCode == KeyEvent.KEYCODE_DPAD_LEFT && combinedSelectedItemPosition <= 0) {
                    val activity = (activity as MainActivityNew)
                    activity.showLeftMenu()
                }
            }
            false
        }

        binding.search.setOnEditorActionListener { _, action, _ ->

            var handled = true
            if ((EditorInfo.IME_ACTION_SEARCH == action || EditorInfo.IME_NULL == action)) {
                if (binding.search.text.count() > 2){
                    hideNativeKeyboard()
                }

                if (requireContext().isNetworkConnected){
                    isKeyboardShown = false
                    viewModel.doSearch(lifecycle)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.search.setSelection(binding.search.text.length)
                    },Constant.SEARCH_CURSOR_SELECTION_DELAY)
                }else{
                    parentFragmentManager.showAlert(getString(R.string.no_internet), true)
                }
            } else if (EditorInfo.IME_ACTION_NONE == action) {
                hideNativeKeyboard()
            } else if (EditorInfo.IME_ACTION_PREVIOUS == action) {
                hideNativeKeyboard()
                isKeyboardShown = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.search.requestFocus()
                    if (deviceUtil.isAmazonFireTvDevice()){
                        binding.search.setSelection(binding.search.text.length)
                    }
                }, Constant.REQUEST_FOCUS_DELAY)
            } else if (EditorInfo.IME_ACTION_GO == action) {
                hideNativeKeyboard()
            } else {
                handled = false
            }
            handled
        }

        binding.search.privateImeOptions = "escapeNorth,voiceDismiss"

        showNativeKeyboard()

        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch() {
            combine(viewModel.videoPagingDataAdapter.loadStateFlow, viewModel.channelPagingDataAdapter.loadStateFlow){ videoLoadState, channelLoadState ->
                Pair(videoLoadState, channelLoadState)
            }.collectLatest { (videoLoadState, channelLoadState) ->

                if (videoLoadState.refresh is LoadState.NotLoading &&
                    channelLoadState.refresh is LoadState.NotLoading
                ) {
                    progressBarManager.hide()
                    if (SearchItemsPosition.hasCreatedRows.not()){
                        viewModel.createVideoSearchRow()
                        viewModel.createChannelsSearchRow()
                        SearchItemsPosition.hasCreatedRows = true
                    }
                }else if (videoLoadState.refresh is LoadState.Loading &&
                    channelLoadState.refresh is LoadState.Loading){
                    progressBarManager.show()
                }else if (videoLoadState.refresh is LoadState.Error &&
                    channelLoadState.refresh is LoadState.Error){
                    viewModel.rowsAdapter.clear()
                    SearchItemsPosition.hasCreatedRows = false
                    progressBarManager.hide()
                    parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
                }
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchViewModelV4.SearchFragmentStates.List -> progressBarManager.hide()
                SearchViewModelV4.SearchFragmentStates.Loading -> progressBarManager.show()
                SearchViewModelV4.SearchFragmentStates.Error -> {
                    progressBarManager.hide()
                    parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
                }
            }
        }
    }

    override fun onResume() {
        updateChannelDetails()
        super.onResume()
    }

    private fun updateChannelDetails() {
        val allChannelDetailsEntity = viewModel.channelPagingDataAdapter.snapshot().items
        if (allChannelDetailsEntity.isEmpty().not()) {
            if (SearchItemsPosition.refreshChannelDetailInRowItem && lastClickedChannelItemPosition >= 0 && allChannelDetailsEntity.size > lastClickedChannelItemPosition) {
                val channelDetailsEntity = allChannelDetailsEntity[lastClickedChannelItemPosition]
                val updatedItem = viewModel.refreshChannelData(channelDetailsEntity)
                SearchItemsPosition.refreshChannelDetailInRowItem = false
                updatedItem?.let {
                    viewModel.channelPagingDataAdapter.snapshot().items[lastClickedChannelItemPosition].followed =
                        it.followed
                    viewModel.channelPagingDataAdapter.notifyItemRangeChanged(
                        lastClickedChannelItemPosition,
                        viewModel.channelPagingDataAdapter.size()
                    )
                }
            }
        }
    }

    fun requestFocusToLastPosition(){
        isKeyboardShown = false
        if (viewModel.rowsAdapter.size() == 0){
            binding.search.requestFocus()
            binding.search.setSelection(binding.search.text.length)
        }else if (lastFocusView == LastFocusView.SearchView){
            binding.search.requestFocus()
            binding.search.setSelection(binding.search.text.length)
        } else{
            if (selectedRowPosition >= 0 ){
                if (selectedRowPosition == 0){
                    if (selectedVideoItemPosition >= 0){
                        Handler(Looper.getMainLooper()).post{
                            binding.containerList.requestFocus()
                            hideNativeKeyboard()
                            setFocusToRowPos(selectedRowPosition, selectedVideoItemPosition)
                        }
                    }
                }else {
                    if (selectedChannelItemPosition >= 0){
                        Handler(Looper.getMainLooper()).post{
                            binding.containerList.requestFocus()
                            hideNativeKeyboard()
                            setFocusToRowPos(selectedChannelRowPosition, selectedChannelItemPosition)
                        }
                    }
                }
            }
        }
    }

    override fun findGridViewFromRoot(view: View?): VerticalGridView {
        return binding.containerList
    }

    private fun hideNativeKeyboard() {
        isKeyboardShown = false
        inputMethodManager.hideSoftInputFromWindow(binding.search.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    private fun showNativeKeyboard() {
        lastCursorPosition = binding.search.selectionStart
        Handler(Looper.getMainLooper()).post {
            binding.search.requestFocusFromTouch()
            binding.search.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    binding.search.width.toFloat(),
                    binding.search.height.toFloat(),
                    0
                )
            )
            binding.search.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    binding.search.width.toFloat(),
                    binding.search.height.toFloat(),
                    0
                )
            )

            if (deviceUtil.isAmazonFireTvDevice().not()){
                val viewTreeObserver = binding.search.viewTreeObserver
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.search.post {
                            if (lastCursorPosition > binding.search.text.length){
                                lastCursorPosition = binding.search.text.length
                            }
                            binding.search.setSelection(lastCursorPosition)
                        }
                    }
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isKeyboardShown = false
    }

    private fun setFocusToRowPos(bottomRowPos: Int, itemPos: Int) {
        val rowViewHolderTask = ListRowPresenter.SelectItemViewHolderTask(itemPos)
        rowViewHolderTask.isSmoothScroll = false
        setSelectedPosition(bottomRowPos, false, rowViewHolderTask)
        lastFocusView = LastFocusView.DataView
    }
}