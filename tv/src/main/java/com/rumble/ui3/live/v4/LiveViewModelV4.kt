package com.rumble.ui3.live.v4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.usecase.GetLiveFeedListUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.ui3.live.LiveStates
import com.rumble.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LiveViewModel"

@HiltViewModel
class LiveViewModelV4 @Inject constructor(
    private val getLiveFeedListUseCase: GetLiveFeedListUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val internetConnectionUseCase: InternetConnectionUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
): ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    private val _liveVideoCollectionLiveData = MutableLiveData<PagingData<Feed>>()
    val liveVideoCollectionLiveData: LiveData<PagingData<Feed>> = _liveVideoCollectionLiveData

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    init {
        observeConnectionState()
    }

    fun onRefresh() {
        LiveStates.reloadLiveData = true
        LiveStates.liveVideosPagingDataMap = null
        getLiveVideos()
        LiveStates.reloadLiveData = false
    }

    fun getLiveVideos() {
        if (LiveStates.liveVideosPagingDataMap != null) {
            _liveVideoCollectionLiveData.value = LiveStates.liveVideosPagingDataMap
        } else {
            viewModelScope.launch {
                getLiveFeedListUseCase().cachedIn(viewModelScope).collectLatest { pagingData ->
                    _liveVideoCollectionLiveData.value = pagingData
                    LiveStates.liveVideosPagingDataMap = pagingData
                    clearDataAfterDelay()
                }
            }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()
            internetConnectionObserver.connectivityFlow.collectLatest {
                if (connectionState.value == InternetConnectionState.LOST && it == InternetConnectionState.CONNECTED) {
                    LiveStates.reloadLiveData = true
                    LiveStates.liveVideosPagingDataMap = null
                    getLiveVideos()
                }
                connectionState.value = it
            }
        }
    }

    private suspend fun clearDataAfterDelay() {
        delay(Constant.REFRESH_CONTENT_DURATION)
        LiveStates.liveVideosPagingDataMap = null
    }

    fun onError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
    }
}