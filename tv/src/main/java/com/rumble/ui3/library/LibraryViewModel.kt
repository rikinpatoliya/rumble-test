package com.rumble.ui3.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.model.LibraryCollection
import com.rumble.domain.library.domain.model.LibraryCollectionType
import com.rumble.domain.library.domain.usecase.GetLibraryCollectionVideosUseCase
import com.rumble.domain.library.domain.usecase.GetLibraryPlayListsPagedUseCase
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LibraryUiState(
    val loggedIn: Boolean = true,
    val selectedList: ListSelectionType = ListSelectionType.Library(LibraryCollection.WatchHistory),
    val focusedVideo: VideoEntity? = null,
    val playLists: Flow<PagingData<PlayListEntity>> = emptyFlow(),
    val videoList: Flow<PagingData<Feed>> = emptyFlow(),
    val fetchingLoggedInState: Boolean
)

sealed class ListSelectionType {
    data class Library(val libraryCollection: LibraryCollection) : ListSelectionType()
    data class PlayList(val playListEntity: PlayListEntity) : ListSelectionType()
}

sealed class LibraryVmEvent {
}

interface LibraryHandler {
    val state: StateFlow<LibraryUiState>
    fun onFocusedPlayList(selection: ListSelectionType)
    fun onFocusVideo(video: VideoEntity?)
    val eventFlow: MutableSharedFlow<LibraryVmEvent>
}

private const val TAG = "LibraryViewModel"

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getLibraryCollectionVideosUseCase: GetLibraryCollectionVideosUseCase,
    private val getLibraryPlayListsPagedUseCase: GetLibraryPlayListsPagedUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), LibraryHandler {

    override val state = MutableStateFlow(LibraryUiState(fetchingLoggedInState = true))
    override val eventFlow: MutableSharedFlow<LibraryVmEvent> = MutableSharedFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        initLoggedInState()
        loadPlaylist()
        loadSelectedPlaylistVideos()
    }

    private fun initLoggedInState() {
        viewModelScope.launch(errorHandler) {
            sessionManager.cookiesFlow.collectLatest { cookies ->
                state.value = state.value.copy(
                    loggedIn = cookies.isEmpty().not(),
                    fetchingLoggedInState = false
                )
            }
        }
    }

    override fun onFocusedPlayList(selection: ListSelectionType) {
        if (state.value.selectedList != selection) {
            state.value = state.value.copy(selectedList = selection, focusedVideo = null)
            loadSelectedPlaylistVideos()
        }
    }

    override fun onFocusVideo(video: VideoEntity?) {
        state.value = state.value.copy(focusedVideo = video)
    }

    private fun loadSelectedPlaylistVideos() {
        viewModelScope.launch(errorHandler) {
            val type = when (val it = state.value.selectedList) {
                is ListSelectionType.Library -> it.libraryCollection.type
                is ListSelectionType.PlayList -> LibraryCollectionType.PlayList(it.playListEntity)
            }

            val pagingData = getLibraryCollectionVideosUseCase(type).cachedIn(viewModelScope)
            state.value = state.value.copy(videoList = pagingData)
        }
    }

    private fun loadPlaylist() {
        viewModelScope.launch(errorHandler) {
            val pagingData = getLibraryPlayListsPagedUseCase()
            state.value = state.value.copy(playLists = pagingData)
        }
    }


}