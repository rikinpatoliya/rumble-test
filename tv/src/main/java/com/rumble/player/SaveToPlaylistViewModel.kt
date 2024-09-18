package com.rumble.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.domainmodel.AddToPlaylistResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.domainmodel.RemoveFromPlaylistResult
import com.rumble.domain.common.domain.usecase.AddToPlaylistUseCase
import com.rumble.domain.common.domain.usecase.RemoveFromPlaylistUseCase
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlaylistVideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.usecase.GetLibraryPlayListsPagedUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.network.queryHelpers.PlayListType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaveToPlaylistUiState(
    val visible: Boolean = false,
    val playlists: Flow<PagingData<PlayListEntity>> = emptyFlow(),
    val focusedPlaylistEntity: PlayListEntity? = null,
    val videoEntity: VideoEntity? = null,
    val watchLaterPlaylistEntity: PlayListEntity? = null,
)

sealed class SaveToPlaylistVmEvent {
    object ErrorVideoAlreadyAdded : SaveToPlaylistVmEvent()
    object Error : SaveToPlaylistVmEvent()
}

sealed class UpdatePlaylist {
    data class VideoAddedToPlaylist(
        val playlistId: String,
        val playlistVideoEntity: PlaylistVideoEntity,
    ) : UpdatePlaylist()

    data class VideoRemovedFromPlaylist(
        val playlistId: String,
        val videoId: Long,
    ) : UpdatePlaylist()
}

interface SaveToPlaylistHandler {
    val state: StateFlow<SaveToPlaylistUiState>

    fun onShowSaveToPlaylist(videoEntity: VideoEntity?)
    fun onSaveToPlaylist(playlistId: String)
    fun onRemoveFromPlaylist(playlistId: String)
    fun onDismiss()

    val eventFlow: Flow<SaveToPlaylistVmEvent>
    val updatedPlaylist: StateFlow<UpdatePlaylist?>
}

private const val TAG = "SaveToPlaylistViewModel"

@HiltViewModel
class SaveToPlaylistViewModel @Inject constructor(
    private val getLibraryPlayListsPagedUseCase: GetLibraryPlayListsPagedUseCase,
    private val getPlayListUseCase: GetPlayListUseCase,
    private val addToPlaylistUseCase: AddToPlaylistUseCase,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), SaveToPlaylistHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    override val eventFlow: MutableSharedFlow<SaveToPlaylistVmEvent> = MutableSharedFlow()
    override val updatedPlaylist: MutableStateFlow<UpdatePlaylist?> = MutableStateFlow(null)

    override val state = MutableStateFlow(SaveToPlaylistUiState())

    init {
        viewModelScope.launch(errorHandler) {

        }
    }

    override fun onShowSaveToPlaylist(videoEntity: VideoEntity?) {
        viewModelScope.launch(errorHandler) {
            fetchWatchLaterPlaylist()

            val playlists = getLibraryPlayListsPagedUseCase()
            state.value = state.value.copy(videoEntity = videoEntity, visible = true, playlists = playlists)
        }
    }

    private suspend fun fetchWatchLaterPlaylist() {
        when (val result = getPlayListUseCase(PlayListType.WATCH_LATER.toString())) {
            is PlayListResult.Failure -> eventFlow.tryEmit(SaveToPlaylistVmEvent.Error)
            is PlayListResult.Success -> state.value = state.value.copy(watchLaterPlaylistEntity = result.playList)
        }
    }

    override fun onSaveToPlaylist(playlistId: String) {
        viewModelScope.launch(errorHandler) {
            state.value.videoEntity?.id?.let { videoId ->
                val result = addToPlaylistUseCase(playlistId, videoId)
                when (result) {
                    is AddToPlaylistResult.Failure ->
                        eventFlow.tryEmit(SaveToPlaylistVmEvent.ErrorVideoAlreadyAdded)

                    is AddToPlaylistResult.Success ->
                        updatedPlaylist.tryEmit(
                            UpdatePlaylist.VideoAddedToPlaylist(
                                playlistId = playlistId,
                                playlistVideoEntity = result.playlistVideoEntity
                            )
                        )

                    AddToPlaylistResult.FailureAlreadyInPlaylist ->
                        eventFlow.tryEmit(SaveToPlaylistVmEvent.ErrorVideoAlreadyAdded)
                }
            }

            if (playlistId == PlayListType.WATCH_LATER.toString()) {
                fetchWatchLaterPlaylist()
            }
        }
    }

    override fun onRemoveFromPlaylist(playlistId: String) {
        viewModelScope.launch(errorHandler) {
            state.value.videoEntity?.id?.let { videoId ->
                val result = removeFromPlaylistUseCase(playlistId, videoId)
                when (result) {
                    is RemoveFromPlaylistResult.Failure -> eventFlow.tryEmit(SaveToPlaylistVmEvent.Error)

                    RemoveFromPlaylistResult.Success, RemoveFromPlaylistResult.FailureVideoNotInPlaylist ->
                        updatedPlaylist.tryEmit(
                            UpdatePlaylist.VideoRemovedFromPlaylist(
                                playlistId,
                                videoId
                            )
                        )
                }
            }
            
            if (playlistId == PlayListType.WATCH_LATER.toString()) {
                fetchWatchLaterPlaylist()
            }
        }
    }

    override fun onDismiss() {
        state.value = state.value.copy(visible = false)
    }

}