package com.rumble.battles.search.presentation.searchScreen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.domain.useCases.DeleteAllQueriesUseCase
import com.rumble.domain.search.domain.useCases.DeleteQueryUseCase
import com.rumble.domain.search.domain.useCases.GetAutoCompleteQueriesUseCase
import com.rumble.domain.search.domain.useCases.GetFilteredQueriesUseCase
import com.rumble.domain.search.domain.useCases.GetRecentQueriesUseCase
import com.rumble.domain.search.domain.useCases.SaveQueryUseCase
import com.rumble.domain.search.domain.useCases.UpdateQueryUseCase
import com.rumble.utils.extension.navigationSafeDecode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchHandler: LazyListStateHandler {
    val initialQuery: String
    val state: StateFlow<SearchQueryUIState>
    val navDest: String
    val parentScreen: String
    fun saveQuery(text: String)
    fun updateQuery(recentQuery: RecentQuery)
    fun onQueryChanged(query: String)
    fun onDeleteRecentQuery(recentQuery: RecentQuery)
    fun onDeleteAllRecentQueries()
}

data class SearchQueryUIState(
    val query: String = "",
    val recentQueryList: List<RecentQuery> = emptyList(),
    val autoCompleteChannelsList: List<ChannelDetailsEntity> = emptyList(),
    val autoCompleteCategoriesList: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val saveQueryUseCase: SaveQueryUseCase,
    private val updateQueryUseCase: UpdateQueryUseCase,
    private val deleteQueryUseCase: DeleteQueryUseCase,
    private val deleteAllQueriesUseCase: DeleteAllQueriesUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val getFilteredQueriesUseCase: GetFilteredQueriesUseCase,
    private val getAutoCompleteQueriesUseCase: GetAutoCompleteQueriesUseCase
) : ViewModel(), SearchHandler {

    override val initialQuery: String =
        (stateHandle.get<String>(RumblePath.QUERY.path) ?: "").navigationSafeDecode()

    override val state = MutableStateFlow(SearchQueryUIState(query = initialQuery))

    override val navDest: String =
        stateHandle.get<String>(RumblePath.NAVIGATION.path) ?: "".navigationSafeDecode()

    override val parentScreen: String = stateHandle.get<String>(RumblePath.PARAMETER.path) ?: ""

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    init {
        viewModelScope.launch {
            val recentQueryList = getRecentQueriesUseCase()
            state.update {
                it.copy(
                    recentQueryList = recentQueryList
                )
            }
            if (initialQuery.isNotBlank()) filterQueries(initialQuery)
        }
    }

    override fun saveQuery(text: String) {
        viewModelScope.launch { saveQueryUseCase(text) }
    }

    override fun updateQuery(recentQuery: RecentQuery) {
        viewModelScope.launch { updateQueryUseCase(recentQuery) }
    }

    override fun onQueryChanged(query: String) {
        state.update {
            it.copy(query = query)
        }
        filterQueries(query)
        viewModelScope.launch {
            val result = getAutoCompleteQueriesUseCase(query)
            state.update {
                it.copy(
                    autoCompleteChannelsList = result.channelList,
                    autoCompleteCategoriesList = result.categoryList
                )
            }
        }
    }

    override fun onDeleteRecentQuery(recentQuery: RecentQuery) {
        viewModelScope.launch {
            deleteQueryUseCase(recentQuery)
            filterQueries(state.value.query)
        }
    }

    override fun onDeleteAllRecentQueries() {
        viewModelScope.launch {
            deleteAllQueriesUseCase()
            filterQueries(state.value.query)
        }
    }

    private fun filterQueries(filter: String) {
        viewModelScope.launch {
            val recentQueryList = getFilteredQueriesUseCase(filter)
            state.update {
                it.copy(
                    recentQueryList = recentQueryList
                )
            }
        }
    }
}