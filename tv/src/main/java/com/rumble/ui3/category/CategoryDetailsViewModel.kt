package com.rumble.ui3.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryResult
import com.rumble.domain.discover.domain.usecase.GetCategoryUseCase
import com.rumble.domain.discover.domain.usecase.GetCategoryVideoListUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CategoryDetailsViewModel"

interface CategoryDetailsHandler {
    val vmEvents: Flow<CategoryDetailsVmEvent>
}

sealed class CategoryDetailsVmEvent {
    object RefreshData : CategoryDetailsVmEvent()
}

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getCategoryVideoListUseCase: GetCategoryVideoListUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
) : ViewModel(), CategoryDetailsHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    private val _vmEvents = Channel<CategoryDetailsVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<CategoryDetailsVmEvent> = _vmEvents.receiveAsFlow()

    private val _relatedCategoriesLiveData = MutableLiveData<Pair<CategoryEntity, List<CategoryEntity>>>()
    val relatedCategoriesLiveData: LiveData<Pair<CategoryEntity, List<CategoryEntity>>> = _relatedCategoriesLiveData

    private val _errorRelatedCategoriesLiveData = MutableLiveData<CategoryResult.Failure>()
    val errorRelatedCategoriesLiveData: LiveData<CategoryResult.Failure> = _errorRelatedCategoriesLiveData

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    init {
        observeConnectionState()
    }

    /***/
    fun fetchCategoryVideos(
        category: CategoryEntity,
        displayType: CategoryDisplayType,
        showLiveCategoryList: Boolean,
    ) =
        getCategoryVideoListUseCase(category, displayType, showLiveCategoryList, emptyList()).cachedIn(
            viewModelScope
        )

    fun fetchCategoryData(categoryPath: String){
        viewModelScope.launch(errorHandler) {
            val result = getCategoryUseCase(categoryPath)
            if (result is CategoryResult.Success) {
                _relatedCategoriesLiveData.value = Pair(result.category, result.subcategoryList)
            } else {
                _errorRelatedCategoriesLiveData.value = (result as CategoryResult.Failure)
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
                    _vmEvents.trySend(CategoryDetailsVmEvent.RefreshData)
                }
                connectionState.value = it
            }
        }
    }
}