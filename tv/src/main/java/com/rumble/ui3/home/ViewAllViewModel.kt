package com.rumble.ui3.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.usecase.GetFeedByIdUseCase
import com.rumble.domain.feed.domain.usecase.GetLiveFeedUseCase
import com.rumble.network.queryHelpers.VideoCollectionId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val getFeedByIdUseCase : GetFeedByIdUseCase,
    private val getLiveFeedUseCase : GetLiveFeedUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    ): ViewModel() {

    fun getVideoList(id: String) = getFeedByIdUseCase(id).cachedIn(viewModelScope)

    fun getLiveVideoList() = getLiveFeedUseCase().cachedIn(viewModelScope)

    fun handleErrorUseCase(TAG: String, error: Throwable){
        unhandledErrorUseCase(TAG, error)
    }
}