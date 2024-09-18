package com.rumble.ui3.home.v4

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.R
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetFeaturedChannelsUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.usecase.GetLiveCategoryListUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetCollectionUseCase
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.ui3.common.TopLiveCategoriesCardPresenter
import com.rumble.ui3.common.TopLiveCategoriesViewAllCardPresenter
import com.rumble.ui3.common.VideoCardPresenter
import com.rumble.ui3.home.ChannelViewAllCardPresenter
import com.rumble.ui3.home.HomeListRowPresenter
import com.rumble.ui3.home.VideoViewAllCardPresenter
import com.rumble.ui3.home.model.ChannelViewAllEntity
import com.rumble.ui3.home.model.TopLiveCategoriesViewAllEntity
import com.rumble.ui3.home.model.VideoViewAllEntity
import com.rumble.ui3.search.ChannelCardPresenter
import com.rumble.ui3.search.SearchItemsPosition
import com.rumble.util.Constant
import com.rumble.util.StringUtils
import com.rumble.util.Utils
import com.rumble.utils.extension.isNetworkRelatedError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject

data class HomeState(
    var lastSelectedItemPosition: Int = 0,
    var lastSelectedRowPosition: Int = 0,
    var lastClickedChannelItemPosition: Int = 0,
    var reloadHomeData: Boolean = false,
    var lastApiCallTime: Date? = null,
    var lastFocusedRowPosition: Int = 0,
)

sealed class HomeEvent {
    object AdapterUpdated : HomeEvent()
    object RemoveRefreshFlagFromBackStack : HomeEvent()
}

interface HomeHandler {
    val state: StateFlow<HomeState>
    val eventFlow: Flow<HomeEvent>

    fun onChannelClicked(position: Int)
    fun onSelectedRowPosition(position: Int)
    fun onSelectedItemPosition(position: Int)
    fun onFocusedRowPosition(position: Int)
    fun onViewResumed()
}

sealed class UiStates {
    object Loading : UiStates()
    object List : UiStates()
    data class Error(val networkError: Boolean) : UiStates()
}

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModelV4 @Inject constructor(
    private val stringUtils: StringUtils,
    private val getFeaturedChannelsUseCase: GetFeaturedChannelsUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val getCollectionUseCase: GetCollectionUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getLiveCategoryListUseCase: GetLiveCategoryListUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
    private val videoCardPresenter: VideoCardPresenter,
) : ViewModel(), HomeHandler {
    companion object {
        private const val CARDS_IN_ROW = 7
    }

    override val state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    override val eventFlow: MutableSharedFlow<HomeEvent> = MutableSharedFlow()

    private val classPresenterSelector = ClassPresenterSelector().apply {
        addClassPresenter(VideoEntity::class.java, videoCardPresenter)
        addClassPresenter(VideoViewAllEntity::class.java, VideoViewAllCardPresenter())
        addClassPresenter(ChannelDetailsEntity::class.java, ChannelCardPresenter())
        addClassPresenter(ChannelViewAllEntity::class.java, ChannelViewAllCardPresenter())
        addClassPresenter(CategoryEntity::class.java, TopLiveCategoriesCardPresenter())
        addClassPresenter(TopLiveCategoriesViewAllEntity::class.java, TopLiveCategoriesViewAllCardPresenter())
    }

    val uiState: MutableLiveData<UiStates> by lazy { MutableLiveData<UiStates>(UiStates.Loading) }
    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()
    val firstVideoCollectionTitle: MutableLiveData<String> by lazy { MutableLiveData<String>("") }
    val channelsArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)

    private val topLiveCategoriesArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)
    private val firstVideoCollectionArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)
    private val secondAndThirdVideoCollectionArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)

    var allChannelDisplayed: List<ChannelDetailsEntity> = listOf()
    private var topLiveCategories: List<CategoryEntity> = listOf()

    private val videoCollectionFirstCollectionPosition = 1
    private val videoCollectionThirdCollectionPosition = 3

    val rowsAdapter: ArrayObjectAdapter =
        ArrayObjectAdapter(
            HomeListRowPresenter(
                lastSelectedItemPosition = state.value.lastSelectedItemPosition,
                zoomFactor = FocusHighlight.ZOOM_FACTOR_NONE,
                useFocusDimmer = false
            )
        )

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        isAdapterUpdating = false
        unhandledErrorUseCase(TAG, throwable)
        uiState.value = UiStates.Error(networkError = throwable.isNetworkRelatedError())
    }
    private var isAdapterUpdating = false

    init {
        observeConnectionState()
    }

    fun fetchContentIfNeeded() {
        if (Utils.shouldRefreshContent(state.value.lastApiCallTime)) {
            updateAdapter()
        }
    }

    fun updateAdapter() {

        if (isAdapterUpdating) {
            return
        }

        isAdapterUpdating = true

        uiState.value = UiStates.Loading

        viewModelScope.launch(errorHandler) {

            // All three API calling
            val collectionData = getCollectionUseCase()
            val topLiveCategories = getLiveCategoryListUseCase(Constant.TOP_LIVE_CATEGORIES_MAX_CARDS)
            allChannelDisplayed = getFeaturedChannelsUseCase().take(CARDS_IN_ROW)

            rowsAdapter.clear()

// First Video collection
            firstVideoCollectionArrayObjectAdapter.clear()
            val firstCollectionItem = collectionData.first()
            firstVideoCollectionArrayObjectAdapter.addAll(0, firstCollectionItem.videos.map { video -> video.getVideoEntity() })
            firstVideoCollectionArrayObjectAdapter.add(VideoViewAllEntity(firstCollectionItem.id, firstCollectionItem.title, index = 0))
            firstVideoCollectionTitle.value = firstCollectionItem.title
            rowsAdapter.add(ListRow(HeaderItem(""), firstVideoCollectionArrayObjectAdapter))

// Top live categories
            if (topLiveCategories is CategoryListResult.Success) {
                topLiveCategoriesArrayObjectAdapter.clear()
                this@HomeViewModelV4.topLiveCategories = topLiveCategories.categoryList
                topLiveCategoriesArrayObjectAdapter.addAll(0,
                    this@HomeViewModelV4.topLiveCategories
                )
                topLiveCategoriesArrayObjectAdapter.add(TopLiveCategoriesViewAllEntity(index = 0))
            }
            if (topLiveCategoriesArrayObjectAdapter.size() > 0) {
                rowsAdapter.add(ListRow(HeaderItem(stringUtils.getString(R.string.home_row_title_top_live_categories)), topLiveCategoriesArrayObjectAdapter))
            }

// Second and Third Video collection
            secondAndThirdVideoCollectionArrayObjectAdapter.clear()
            val secondAndThirdCollectionItem = collectionData.subList(videoCollectionFirstCollectionPosition, videoCollectionThirdCollectionPosition)
            secondAndThirdCollectionItem.let { videoCollections ->
                videoCollections.forEach { collection ->
                    val secondAndThirdRowArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)
                    secondAndThirdRowArrayObjectAdapter.addAll(0, collection.videos.map { video -> video.getVideoEntity() })
                    secondAndThirdRowArrayObjectAdapter.add(VideoViewAllEntity(collection.id, collection.title, index = 0))
                    rowsAdapter.add(ListRow(HeaderItem(collection.title), secondAndThirdRowArrayObjectAdapter))
                }
            }

// Popular channels
            channelsArrayObjectAdapter.clear()
            channelsArrayObjectAdapter.addAll(0, allChannelDisplayed)
            channelsArrayObjectAdapter.add(ChannelViewAllEntity(index = 0))
            rowsAdapter.add(ListRow(HeaderItem(stringUtils.getString(R.string.home_row_title_popular_channels)), channelsArrayObjectAdapter))

// The rest of video collection after 3rd position
            val restOfItems = collectionData.subList(videoCollectionThirdCollectionPosition, collectionData.size)
            restOfItems.let {
                it.forEach { collection ->
                    val allOtherRowArrayObjectAdapter = ArrayObjectAdapter(classPresenterSelector)
                    allOtherRowArrayObjectAdapter.addAll(0, collection.videos.map { video -> video.getVideoEntity() })
                    allOtherRowArrayObjectAdapter.add(VideoViewAllEntity(collection.id, collection.title, index = 0))
                    rowsAdapter.add(ListRow(HeaderItem(collection.title), allOtherRowArrayObjectAdapter))
                }
            }

            eventFlow.tryEmit(HomeEvent.AdapterUpdated)
            state.value = state.value.copy(lastApiCallTime = Date())
            uiState.value = UiStates.List
            isAdapterUpdating = false
        }

    }

    fun refreshChannelData(channelObject: ChannelDetailsEntity): ChannelDetailsEntity? {
        return runBlocking(errorHandler) { getChannelDataUseCase(channelObject.channelId).getOrNull() }
    }

    private fun updateChannelDetails() {
        val allChannelDetailsEntity = allChannelDisplayed

        with(state.value) {
            if (allChannelDetailsEntity.isEmpty().not()) {
                if (SearchItemsPosition.refreshChannelDetailInRowItem
                    && lastClickedChannelItemPosition >= 0
                    && allChannelDetailsEntity.size > lastClickedChannelItemPosition
                ) {
                    val channelDetailsEntity = allChannelDetailsEntity[lastClickedChannelItemPosition]
                    val updatedItem = refreshChannelData(channelDetailsEntity)
                    SearchItemsPosition.refreshChannelDetailInRowItem = false
                    updatedItem?.let {
                        allChannelDisplayed[lastClickedChannelItemPosition].followed =
                            it.followed
                        channelsArrayObjectAdapter.notifyItemRangeChanged(
                            lastClickedChannelItemPosition,
                            allChannelDetailsEntity.size
                        )
                        lastClickedChannelItemPosition = -1
                    }

                    eventFlow.tryEmit(HomeEvent.RemoveRefreshFlagFromBackStack)
                }
            }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch() {
            connectionState.value = internetConnectionUseCase()
            internetConnectionObserver.connectivityFlow.collectLatest {
                connectionState.value = it
                if (it == InternetConnectionState.CONNECTED) {
                    fetchContentIfNeeded()
                }
            }
        }
    }

    override fun onChannelClicked(position: Int) {
        state.value = state.value.copy(lastClickedChannelItemPosition = position)
    }

    override fun onSelectedRowPosition(position: Int) {
        state.value = state.value.copy(lastSelectedRowPosition = position)
    }

    override fun onSelectedItemPosition(position: Int) {
        state.value = state.value.copy(lastSelectedItemPosition = position)
    }

    override fun onFocusedRowPosition(position: Int) {
        state.value = state.value.copy(lastFocusedRowPosition = position)
    }

    override fun onViewResumed() {
        updateChannelDetails()
    }

}