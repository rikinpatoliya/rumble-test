package com.rumble.battles.library.presentation.playlist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListOption
import com.rumble.domain.library.domain.usecase.GetLibraryPlayListsPagedUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListOptionsUseCase
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PlayListsHandler: LazyListStateHandler {
    val playListsFlow: Flow<PagingData<PlayListEntity>>
    val eventFlow: Flow<PlayListsScreenVmEvent>

    fun getPlayListOptions(playListEntity: PlayListEntity): List<PlayListOption>
    fun handleLoadState(loadStates: LoadStates)
}

sealed class PlayListsScreenVmEvent {
    data class Error(val errorMessage: String? = null) : PlayListsScreenVmEvent()
}

private const val TAG = "PlayListsViewModel"

@HiltViewModel
class PlayListsViewModel @Inject constructor(
    getLibraryPlayListsPagedUseCase: GetLibraryPlayListsPagedUseCase,
    private val getPlayListOptionsUseCase: GetPlayListOptionsUseCase,
    private val sessionManager: SessionManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), PlayListsHandler {
    override val playListsFlow: Flow<PagingData<PlayListEntity>> =
        getLibraryPlayListsPagedUseCase().cachedIn(viewModelScope)
    override val eventFlow: MutableSharedFlow<PlayListsScreenVmEvent> = MutableSharedFlow()

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    private val errorHandler = CoroutineExceptionHandler { context, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    private var userId: String = ""

    init {
        viewModelScope.launch(errorHandler) {
            userId = sessionManager.userIdFlow.first()
        }
    }

    override fun getPlayListOptions(playListEntity: PlayListEntity): List<PlayListOption> =
        getPlayListOptionsUseCase(
            playListEntity = playListEntity,
            userId = userId,
        )

    override fun handleLoadState(loadStates: LoadStates) {
        arrayOf(
            loadStates.append,
            loadStates.prepend,
            loadStates.refresh
        ).filterIsInstance(LoadState.Error::class.java).firstOrNull()?.let { errorState ->
            unhandledErrorUseCase(TAG, errorState.error)
            emitVmEvent(PlayListsScreenVmEvent.Error())
        }
    }

    private fun emitVmEvent(event: PlayListsScreenVmEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}