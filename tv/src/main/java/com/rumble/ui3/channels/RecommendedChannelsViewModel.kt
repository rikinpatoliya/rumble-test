package com.rumble.ui3.channels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetPagingFeaturedChannelsUseCase
import com.rumble.network.connection.InternetConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import timber.log.Timber
import javax.inject.Inject

data class RecommendedChannelsUiState(
    val loading: Boolean = false,
    val channelList: Flow<PagingData<CreatorEntity>> = emptyFlow(),
    val focusedChannel: CreatorEntity? = null,
)

sealed class RecommendedChannelsVmEvent {
}

interface RecommendedChannelsHandler {
    val state: StateFlow<RecommendedChannelsUiState>

    fun onFocusedChannel(channel: CreatorEntity?)

    val channels: Flow<PagingData<CreatorEntity>>
    val eventFlow: SharedFlow<RecommendedChannelsVmEvent>
}

private const val TAG = "ChannelsViewModel"

@HiltViewModel
class RecommendedChannelsViewModel @Inject constructor(
    private val getPagingFeaturedChannelsUseCase: GetPagingFeaturedChannelsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), RecommendedChannelsHandler {

    private val _eventFlow = MutableSharedFlow<RecommendedChannelsVmEvent>()
    override val eventFlow: SharedFlow<RecommendedChannelsVmEvent> = _eventFlow

    private val _state = MutableStateFlow(RecommendedChannelsUiState())
    override val state = _state

    override val channels: Flow<PagingData<CreatorEntity>> =
        getPagingFeaturedChannelsUseCase(viewModelScope).cachedIn(viewModelScope)

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        Timber.e(throwable)
    }

    override fun onFocusedChannel(channel: CreatorEntity?) {
        state.value = state.value.copy(focusedChannel = channel)
    }
}