package com.rumble.ui3.channel.details.v4

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed class ChannelDetailsSharedVmEvent {
    object RestoreLastFocusState : ChannelDetailsSharedVmEvent()
}

interface ChannelDetailsSharedHandler {
    val vmEvents: Flow<ChannelDetailsSharedVmEvent>
    val uiStateMap: StateFlow<Map<String, ChannelDetailsUIState>>
    val channelVideosMap: StateFlow<Map<String, Flow<PagingData<Feed>>>>

    fun onEmptyCache()
    fun onUpdateUiState(uiState: ChannelDetailsUIState, pagingDataFlow: Flow<PagingData<Feed>>)
}

@HiltViewModel
class ChannelDetailsSharedViewModel
@Inject constructor() : ViewModel(), ChannelDetailsSharedHandler {
    override val uiStateMap: MutableStateFlow<Map<String, ChannelDetailsUIState>> = MutableStateFlow(mapOf())
    override val channelVideosMap: MutableStateFlow<Map<String, Flow<PagingData<Feed>>>> = MutableStateFlow(mapOf())

    private val _vmEvents = Channel<ChannelDetailsSharedVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChannelDetailsSharedVmEvent> = _vmEvents.receiveAsFlow()

    override fun onEmptyCache() {
        uiStateMap.value = hashMapOf()
    }

    override fun onUpdateUiState(uiState: ChannelDetailsUIState, pagingDataFlow: Flow<PagingData<Feed>>) {
        uiStateMap.value = uiStateMap.value.toMutableMap().apply {
            put(uiState.channelId, uiState)
        }
        channelVideosMap.value = channelVideosMap.value.toMutableMap().apply {
            put(uiState.channelId, pagingDataFlow)
        }
    }
}