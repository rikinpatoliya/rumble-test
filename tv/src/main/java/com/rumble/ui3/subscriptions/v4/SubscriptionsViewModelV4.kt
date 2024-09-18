package com.rumble.ui3.subscriptions.v4

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.usecase.FetchFollowedChannelsUseV2Case
import com.rumble.domain.channels.channeldetails.domain.usecase.SortFollowingChannelsUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.sort.SortFollowingType
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.ui3.subscriptions.SubscriptionsFragmentStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SubscriptionsViewModelV4"

interface SubscriptionsHandler {
    fun onSortSelected(it: SortFollowingType)

    val vmEvents: Flow<SubscriptionsVmEvent>
}

sealed class SubscriptionsVmEvent {
    object RefreshSubscriptions : SubscriptionsVmEvent()
}

@HiltViewModel
class SubscriptionsViewModelV4 @Inject constructor(
    private val sessionManager: SessionManager,
    private val fetchFollowedChannelsUseCase: FetchFollowedChannelsUseV2Case,
    private val sortFollowingChannelsUseCase: SortFollowingChannelsUseCase,
    private val rumbleErrorUseCase: RumbleErrorUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : ViewModel(), SubscriptionsHandler {

    private val _vmEvents = Channel<SubscriptionsVmEvent>(capacity = Channel.CONFLATED)

    override val vmEvents: Flow<SubscriptionsVmEvent> = _vmEvents.receiveAsFlow()

    val followedChannelCountFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _uiState =
        MutableStateFlow<SubscriptionsFragmentStates>(SubscriptionsFragmentStates.Loading)
    val uiState: StateFlow<SubscriptionsFragmentStates> = _uiState

    private val _sortType = MutableStateFlow(SortFollowingType.DEFAULT)
    val sortType: StateFlow<SortFollowingType> = _sortType

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    init {
        observeConnectionState()
    }

    fun onRefresh() {
        _vmEvents.trySend(SubscriptionsVmEvent.RefreshSubscriptions)
        getUiState()
    }

    fun getUiState() {
        viewModelScope.launch(errorHandler) {
            fetchUserProfile()

            _uiState.value = SubscriptionsFragmentStates.Loading
            sessionManager.cookiesFlow
                .distinctUntilChanged()
                .collectLatest { cookies ->

                    if (cookies.isEmpty()) {
                        _uiState.value = SubscriptionsFragmentStates.NotLoggedIn
                    } else {
                        when (val result = fetchFollowedChannelsUseCase()) {
                            is ChannelListResult.Failure -> {
                                _uiState.value = SubscriptionsFragmentStates.Error
                                rumbleErrorUseCase(result.rumbleError)
                            }

                            is ChannelListResult.Success -> {
                                if (result.channelList.isNotEmpty()) {
                                    val sortedChannels =
                                        sortFollowingChannelsUseCase(_sortType.value, result.channelList)
                                    _uiState.value =
                                        SubscriptionsFragmentStates.SubscriptionsList(
                                            sortedList = sortedChannels,
                                            originalList = result.channelList
                                        )
                                } else {
                                    _uiState.value = SubscriptionsFragmentStates.NoSubscriptions
                                }
                            }
                        }
                    }
                }
        }
    }

    private suspend fun fetchUserProfile() {
        val result = getUserProfileUseCase()
        if (result.success) {
            followedChannelCountFlow.value = result.userProfileEntity?.followedChannelCount ?: 0
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()

            internetConnectionObserver.connectivityFlow.collectLatest {
                if (connectionState.value == InternetConnectionState.LOST
                    && it == InternetConnectionState.CONNECTED
                    && _uiState.value == SubscriptionsFragmentStates.Error
                ) {
                    _vmEvents.trySend(SubscriptionsVmEvent.RefreshSubscriptions)
                    getUiState()
                }
                connectionState.value = it
            }
        }
    }

    override fun onSortSelected(sortType: SortFollowingType) {
        _sortType.value = sortType
        (_uiState.value as SubscriptionsFragmentStates.SubscriptionsList)?.let { it ->
            val sortedList = sortFollowingChannelsUseCase(
                sortType,
                it.originalList
            )
            _uiState.value = it.copy(sortedList = sortedList)
        }
    }

    fun onError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
    }
}