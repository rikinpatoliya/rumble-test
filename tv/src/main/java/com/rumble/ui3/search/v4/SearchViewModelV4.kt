package com.rumble.ui3.search.v4

import android.text.TextUtils
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FetchChannelDataResult
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.search.domain.useCases.SearchChannelsUseCase
import com.rumble.domain.search.domain.useCases.SearchVideosUseCase
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.search.ChannelCardPresenter
import com.rumble.ui3.search.SearchItemsPosition
import com.rumble.ui3.search.SearchListRowPresenter
import com.rumble.util.PagingAdapter
import com.rumble.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

private val TAG = "SearchViewModelV4"

@HiltViewModel
class SearchViewModelV4 @Inject constructor(
    private val stringUtils: StringUtils,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val searchChannelUseCase: SearchChannelsUseCase,
    private val searchVideosUseCase: SearchVideosUseCase
) : ViewModel() {

    @Inject
    lateinit var videoCardPresenter: VideoCardPresenter

    private val classPresenterSelector = ClassPresenterSelector().apply {
        addClassPresenter(VideoEntity::class.java, videoCardPresenter)
        addClassPresenter(CreatorEntity::class.java, ChannelCardPresenter())
    }

    private val channelsArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)

    var searchQuery: String = ""
        set(value) {
            if (TextUtils.equals(value, field)) {
                return
            }
            Timber.d("searchQuery: [$value]")
            field = value
        }
    var lastExecutedQuery: String = ""
    /***/
    val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(SearchListRowPresenter())
    /***/
    val uiState: MutableLiveData<SearchFragmentStates> by lazy {
        MutableLiveData<SearchFragmentStates>()
    }

    lateinit var channelPagingDataAdapter: PagingAdapter<CreatorEntity>
    lateinit var videoPagingDataAdapter: PagingAdapter<VideoEntity>

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.value = SearchFragmentStates.Error
    }

    sealed class SearchFragmentStates {
        object Loading : SearchFragmentStates()
        object List : SearchFragmentStates()
        object Error : SearchFragmentStates()
    }


    fun doSearch(lifecycle: Lifecycle) {
        Timber.d("doSearch($searchQuery)")

        if (lastExecutedQuery == searchQuery){
            return
        }

        if (searchQuery.isNotEmpty() && searchQuery.count() > 2) {

            SearchItemsPosition.hasCreatedRows = false
            lastExecutedQuery = searchQuery
            val searchString = searchQuery
            rowsAdapter.clear()
            uiState.value = SearchFragmentStates.Loading

            viewModelScope.launch(errorHandler) {
                val videosFlow = searchVideosUseCase(searchQuery)
                    .cachedIn(viewModelScope)

                val channelsFlow = searchChannelUseCase(searchQuery)
                    .cachedIn(viewModelScope)

                videosFlow.combine(channelsFlow){  videosResult, channelsResult ->
                    Pair(videosResult, channelsResult)
                }.collectLatest { (videosResult, channelsResult) ->
                    videoPagingDataAdapter.submitData(lifecycle, videosResult)
                    channelPagingDataAdapter.submitData(lifecycle, channelsResult)
                }
            }

            SearchItemsPosition.selectedVideoItemPosition = 0
            SearchItemsPosition.selectedChannelItemPosition = 0

        }else{
            rowsAdapter.clear()
            lastExecutedQuery = searchQuery
        }
    }

    fun createChannelsSearchRow() {
        channelsArrayObjectAdapter.clear()
        val rowTitle = if (channelPagingDataAdapter.size() == 0) {
            stringUtils.getSearchEmptyChannelRowTitle(searchQuery)
        } else {
            stringUtils.getSearchChannelRowTitle(searchQuery)
        }
        rowsAdapter.add(ListRow(HeaderItem(rowTitle), channelPagingDataAdapter))
        uiState.value = SearchFragmentStates.List
    }

    fun createVideoSearchRow() {
        val rowTitle = if (videoPagingDataAdapter.size() == 0) {
            stringUtils.getSearchEmptyVideoRowTitle(searchQuery)
        } else {
            stringUtils.getSearchVideoRowTitle(searchQuery)
        }
        rowsAdapter.add(ListRow(HeaderItem(rowTitle), videoPagingDataAdapter))
        uiState.value = SearchFragmentStates.List
    }

    fun refreshChannelData(channelObject: CreatorEntity): CreatorEntity? {
        return runBlocking(errorHandler) {
            when(val result = getChannelDataUseCase(channelObject.channelId)) {
                is FetchChannelDataResult.Success -> result.channelData
                is FetchChannelDataResult.Failure -> null
            }
        }
    }
}