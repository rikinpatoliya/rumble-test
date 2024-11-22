package com.rumble.battles.feed.presentation.recommended_channels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.battles.common.presentation.LazyGridStateHandler
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetPagingFeaturedChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RecommendedChannelsHandler: LazyGridStateHandler {
    val channels: Flow<PagingData<CreatorEntity>>
}

@HiltViewModel
class RecommendedChannelsViewModel @Inject constructor(
    getPagingFeaturedChannelsUseCase: GetPagingFeaturedChannelsUseCase,
) : ViewModel(), RecommendedChannelsHandler {

    override val channels: Flow<PagingData<CreatorEntity>> =
        getPagingFeaturedChannelsUseCase(viewModelScope).cachedIn(viewModelScope)

    override var gridState: MutableState<LazyGridState> = mutableStateOf(LazyGridState(0, 0))

}