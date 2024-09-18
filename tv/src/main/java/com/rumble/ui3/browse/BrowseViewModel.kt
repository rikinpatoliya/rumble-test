package com.rumble.ui3.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.usecase.GetLiveCategoryListUseCase
import com.rumble.domain.feed.domain.usecase.GetLiveFeedListUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BrowseViewModel"

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getLiveCategoryListUseCase: GetLiveCategoryListUseCase,
    private val getLiveFeedListUseCase: GetLiveFeedListUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val internetConnectionUseCase: InternetConnectionUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    private val _relatedCategoriesLiveData = MutableLiveData<List<CategoryEntity>>()
    val categoriesLiveData: LiveData<List<CategoryEntity>> = _relatedCategoriesLiveData

    private val _errorRelatedCategoriesLiveData = MutableLiveData<CategoryListResult.Failure>()
    val errorCategoriesLiveData: LiveData<CategoryListResult.Failure> =
        _errorRelatedCategoriesLiveData

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    init {
        observeConnectionState()
    }

    /***/
    fun fetchLiveVideos() = getLiveFeedListUseCase().cachedIn(viewModelScope)

    fun fetchCategoryData() {
        viewModelScope.launch(errorHandler) {
            val result = getLiveCategoryListUseCase()
            if (result is CategoryListResult.Success) {
                _relatedCategoriesLiveData.value = result.categoryList
            } else {
                _errorRelatedCategoriesLiveData.value = (result as CategoryListResult.Failure)
            }
        }
    }

    /***/
    fun onError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
    }

    private fun observeConnectionState() {
        viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()

            internetConnectionObserver.connectivityFlow.collectLatest {
                if (connectionState.value == InternetConnectionState.LOST
                    && it == InternetConnectionState.CONNECTED
                ) {
                    fetchCategoryData()
                }
                connectionState.value = it
            }
        }
    }
}