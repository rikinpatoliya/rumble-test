package com.rumble.ui3.subscriptions.v4.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.usecase.GetSubscriptionsFeedListUseCase
import com.rumble.ui3.subscriptions.pages.list.AllSubscriptionState
import com.rumble.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AllSubscriptionsViewModelV4"

@HiltViewModel
class AllSubscriptionsViewModelV4 @Inject constructor(
    private val getSubscriptionsFeedListUseCase: GetSubscriptionsFeedListUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
): ViewModel() {

    private val _subscriptionsVideoCollectionItemLiveData = MutableLiveData<PagingData<Feed>>()
    val subscriptionsVideoCollectionItemLiveData: LiveData<PagingData<Feed>> = _subscriptionsVideoCollectionItemLiveData

    fun getAllSubscriptionsVideos(){
        if (AllSubscriptionState.subscriptionsVideosPagingDataMap != null){
            _subscriptionsVideoCollectionItemLiveData.value =
                AllSubscriptionState.subscriptionsVideosPagingDataMap
        }else{
            viewModelScope.launch {
                getSubscriptionsFeedListUseCase().cachedIn(viewModelScope).collectLatest { pagingData ->
                    AllSubscriptionState.subscriptionsVideosPagingDataMap = pagingData
                    _subscriptionsVideoCollectionItemLiveData.value = pagingData
                    clearDataAfterDelay()
                }
            }
        }
    }

    private suspend fun clearDataAfterDelay() {
        delay(Constant.REFRESH_CONTENT_DURATION)
        AllSubscriptionState.subscriptionsVideosPagingDataMap = null
    }

    fun onError(throwable: Throwable){
        unhandledErrorUseCase(TAG, throwable)
    }
}