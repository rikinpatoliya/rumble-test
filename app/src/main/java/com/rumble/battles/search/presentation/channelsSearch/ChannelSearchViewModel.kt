package com.rumble.battles.search.presentation.channelsSearch

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.search.domain.useCases.SearchChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ChannelSearchHandler: LazyListStateHandler {
    val query: String
    val channelList: Flow<PagingData<CreatorEntity>>
}

@HiltViewModel
class ChannelSearchViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    searchChannelsUseCase: SearchChannelsUseCase
) : ViewModel(), ChannelSearchHandler {

    override val query: String =
        (stateHandle.get<String>(RumblePath.QUERY.path) ?: "")

    override val channelList: Flow<PagingData<CreatorEntity>> =
        searchChannelsUseCase(query).cachedIn(viewModelScope)

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))
}