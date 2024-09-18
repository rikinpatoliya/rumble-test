package com.rumble.battles.search.presentation.channelsSearch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.search.domain.useCases.SearchChannelsUseCase
import com.rumble.utils.extension.navigationSafeDecode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ChannelSearchHandler {
    val query: String
    val channelList: Flow<PagingData<ChannelDetailsEntity>>
}

@HiltViewModel
class ChannelSearchViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    searchChannelsUseCase: SearchChannelsUseCase
) : ViewModel(), ChannelSearchHandler {

    override val query: String =
        (stateHandle.get<String>(RumblePath.QUERY.path) ?: "").navigationSafeDecode()

    override val channelList: Flow<PagingData<ChannelDetailsEntity>> =
        searchChannelsUseCase(query).cachedIn(viewModelScope)
}