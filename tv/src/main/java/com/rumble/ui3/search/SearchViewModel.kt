package com.rumble.ui3.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.domain.useCases.GetAutoCompleteCategoriesUseCase
import com.rumble.domain.search.domain.useCases.GetFilteredQueriesUseCase
import com.rumble.domain.search.domain.useCases.SaveQueryUseCase
import com.rumble.domain.search.domain.useCases.SearchChannelsUseCase
import com.rumble.domain.search.domain.useCases.SearchVideosUseCase
import com.rumble.utils.RumbleConstants.TV_SEARCH_DEBOUNCE_TIME_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",

    val recentQueries: List<RecentQuery> = emptyList(),

    val categories: List<CategoryEntity> = emptyList(),
    val categoriesLoading: Boolean = false,
    val initialLoadState: Boolean = true,

    val videoResults: Flow<PagingData<VideoEntity>> = emptyFlow(),
    val channelResults: Flow<PagingData<CreatorEntity>> = emptyFlow(),

    val focusedSuggestionIndex: Int = 0,
    val focusedVideo: VideoEntity? = null,
    val focusedChannel: CreatorEntity? = null,

    )

interface SearchHandler {
    val state: StateFlow<SearchUiState>

    fun onQueryChanged(query: String)

    fun onFocusedSuggestion(index: Int)
    fun onFocusVideo(video: VideoEntity?)
    fun onFocusedChannel(channel: CreatorEntity?)

    fun onContentSelected()

}

private const val TAG = "SearchViewModel"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val searchChannelUseCase: SearchChannelsUseCase,
    private val searchVideosUseCase: SearchVideosUseCase,
    private val autoCompleteCategoriesPagedUseCase: GetAutoCompleteCategoriesUseCase,
    private val saveQueryUseCase: SaveQueryUseCase,
    private val getFilteredQueriesUseCase: GetFilteredQueriesUseCase,
) : ViewModel(), SearchHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    override val state = MutableStateFlow(SearchUiState())

    private val debouncer = Debouncer<Unit>(
        delayMillis = TV_SEARCH_DEBOUNCE_TIME_MS,
        coroutineScope = viewModelScope,
        errorHandler = errorHandler
    )

    override fun onQueryChanged(query: String) {
        state.value = state.value.copy(
            query = query
        )

        debouncer.launch(Unit) {
            state.value = state.value.copy(
                categoriesLoading = true
            )
            val query = state.value.query
            val videosFlow = searchVideosUseCase(query)
                .cachedIn(viewModelScope)

            val channelsFlow = searchChannelUseCase(query)
                .cachedIn(viewModelScope)

            state.value = state.value.copy(categories = emptyList())
            val categoriesFlow = autoCompleteCategoriesPagedUseCase(query)
            val recentQueries = getFilteredQueriesUseCase(query)

            state.value = state.value.copy(
                videoResults = videosFlow,
                channelResults = channelsFlow,
                categories = categoriesFlow,
                recentQueries = recentQueries,
                focusedSuggestionIndex = 0,
                focusedVideo = null,
                focusedChannel = null,
                categoriesLoading = false,
                initialLoadState = false
            )
        }
    }

    override fun onFocusedSuggestion(index: Int) {
        state.value = state.value.copy(focusedSuggestionIndex = index)
    }

    override fun onFocusVideo(video: VideoEntity?) {
        state.value = state.value.copy(focusedVideo = video)
    }

    override fun onFocusedChannel(channel: CreatorEntity?) {
        state.value = state.value.copy(focusedChannel = channel)
    }

    override fun onContentSelected() {
        viewModelScope.launch(errorHandler) {
            saveQueryUseCase(state.value.query)
        }
    }
}