package com.rumble.battles.subscriptions.presentation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.usecase.FetchFollowedChannelsUseV2Case
import com.rumble.domain.search.domain.useCases.FilterFollowingUseCase
import com.rumble.domain.sort.SortFollowingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


interface SubscriptionsScreenHandler: LazyListStateHandler {
    val state: StateFlow<SubscriptionsScreenUIState>
    fun onRefresh()
    fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity)
    fun updateSortFollowing(sortFollowingType: SortFollowingType)
    fun onQueryChanged(query: String)
    fun onUpdateSearchVisible()
}

data class SubscriptionsScreenUIState(
    val loading: Boolean = false,
    val searchVisible: Boolean = false,
    val error: Boolean = false,
    val followedChannels: List<ChannelDetailsEntity> = emptyList(),
    val sortFollowingType: SortFollowingType = SortFollowingType.DEFAULT,
    val query: String = "",
)


@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val fetchFollowedChannelsUseV2Case: FetchFollowedChannelsUseV2Case,
    private val filterFollowingUseCase: FilterFollowingUseCase
) : ViewModel(), SubscriptionsScreenHandler {

    override val state = MutableStateFlow(SubscriptionsScreenUIState())

    private var originalFollowedChannels: List<ChannelDetailsEntity> = emptyList()

    init {
        loadFollowingChannels()
    }

    override fun onRefresh() {
        loadFollowingChannels()
    }

    override fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity) {
        state.update {
            it.copy(
                followedChannels = state.value.followedChannels.map { entity ->
                    if (channelDetailsEntity.channelId == entity.channelId)
                        channelDetailsEntity
                    else
                        entity
                }
            )
        }
    }

    override fun updateSortFollowing(sortFollowingType: SortFollowingType) {
        state.update { uiState ->
            val searchVisible = if (uiState.searchVisible) originalFollowedChannels.isNotEmpty() else false
            val query = if (searchVisible) uiState.query else ""
            uiState.copy(
                followedChannels = sortFollowing(
                    filterFollowingUseCase(query, originalFollowedChannels),
                    sortFollowingType
                ),
                sortFollowingType = sortFollowingType,
                searchVisible = searchVisible,
                query = if (searchVisible) uiState.query else ""
            )
        }
    }

    override fun onQueryChanged(query: String) {
        val filteredFollowing = filterFollowingUseCase(query, originalFollowedChannels)
        state.update {
            it.copy(
                query = query,
                followedChannels = sortFollowing(filteredFollowing, state.value.sortFollowingType),
            )
        }
    }

    override fun onUpdateSearchVisible() {
        state.update {
            it.copy(searchVisible = true)
        }
    }

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override fun updateListState(newState: LazyListState) {
        listState.value = newState
    }

    private fun loadFollowingChannels() {
        viewModelScope.launch {
            state.update {
                it.copy(loading = true)
            }
            val result = fetchFollowedChannelsUseV2Case()
            if (result is ChannelListResult.Success) {
                originalFollowedChannels = result.channelList
                updateSortFollowing(state.value.sortFollowingType)
            } else {
                state.update {
                    it.copy(
                        error = true
                    )
                }
            }
            state.update {
                it.copy(
                    loading = false,
                )
            }
        }
    }

    private fun sortFollowing(
        followingList: List<ChannelDetailsEntity>,
        sortFollowingType: SortFollowingType
    ): List<ChannelDetailsEntity> =
        when (sortFollowingType) {
            SortFollowingType.DEFAULT -> followingList
            SortFollowingType.NAME_A_Z -> followingList.sortedBy { it.channelTitle.lowercase() }
            SortFollowingType.NAME_Z_A -> followingList.sortedByDescending { it.channelTitle.lowercase() }
            SortFollowingType.FOLLOWERS_HIGHEST -> followingList.sortedByDescending { it.followers }
            SortFollowingType.FOLLOWERS_LOWEST -> followingList.sortedBy { it.followers }
        }
}