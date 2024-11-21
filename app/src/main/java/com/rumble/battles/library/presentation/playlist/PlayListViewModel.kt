package com.rumble.battles.library.presentation.playlist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.common.presentation.RestrictedVideoHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.domainmodel.videoListScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserUploadChannelsUseCase
import com.rumble.domain.common.domain.domainmodel.FollowPlayListResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.model.PlayListOption
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.domain.library.domain.model.UpdatePlayListResult
import com.rumble.domain.library.domain.usecase.EditPlayListUseCase
import com.rumble.domain.library.domain.usecase.FollowPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListOptionsUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListVideosUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.toChannelIdString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PlayListHandler : RestrictedVideoHandler, EditPlayListHandler, LazyListStateHandler {
    val state: StateFlow<PlayListScreenUIState>
    val playListVideosFlow: Flow<PagingData<Feed>>
    val eventFlow: Flow<PlayListScreenVmEvent>
    val alertDialogState: State<AlertDialogState>

    fun onRefreshPlayList()
    fun onPlayAllClick(feed: Feed?)
    fun onShuffleClick(feed: Feed?)
    fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize)
    fun handleLoadState(loadStates: LoadStates)
    fun onShare()
    fun onFollowPlayList(following: Boolean)
    fun onUpdateSubscription(action: UpdateChannelSubscriptionAction)
    fun updateFollowStatus(channelDetailsEntity: ChannelDetailsEntity)
    fun onPlayListUpdated(playListEntity: PlayListEntity)
}

sealed class PlayListScreenVmEvent {
    data class FollowPLayList(val following: Boolean) : PlayListScreenVmEvent()
    data class Error(val errorMessage: String? = null) : PlayListScreenVmEvent()
    data object WatchHistoryCleared : PlayListScreenVmEvent()
    data class PlayVideo(val videoEntity: VideoEntity) : PlayListScreenVmEvent()
    data class UpdateChannelSubscription(
        val channelDetailsEntity: ChannelDetailsEntity,
        val action: UpdateChannelSubscriptionAction
    ) : PlayListScreenVmEvent()
}

sealed class PlayListScreenAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : PlayListScreenAlertReason()
}

data class PlayListScreenUIState(
    val userId: String,
    val playListEntity: PlayListEntity,
    val followStatus: FollowStatus? = null,
    val playListOptions: List<PlayListOption> = emptyList(),
)

private const val TAG = "PlayListsViewModel"

@HiltViewModel
class PlayListViewModel @Inject constructor(
    savedState: SavedStateHandle,
    getPlayListVideosUseCase: GetPlayListVideosUseCase,
    getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
    private val sessionManager: SessionManager,
    private val getPlayListUseCase: GetPlayListUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val followPlayListUseCase: FollowPlayListUseCase,
    private val getPlayListOptionsUseCase: GetPlayListOptionsUseCase,
    private val editPlayListUseCase: EditPlayListUseCase,
    private val shareUseCase: ShareUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val getUserUploadChannelsUseCase: GetUserUploadChannelsUseCase,
) : ViewModel(), PlayListHandler {

    private val playListId = savedState.get<String>(RumblePath.PLAYLIST.path) ?: ""
    override val state = MutableStateFlow(
        PlayListScreenUIState(
            userId = "",
            playListEntity = PlayListEntity()
        )
    )
    override val editPlayListState = MutableStateFlow(EditPlayListScreenUIState())
    override val playListVideosFlow: Flow<PagingData<Feed>> =
        getPlayListVideosUseCase(playListId, getVideoPageSizeUseCase()).cachedIn(viewModelScope)
    override val eventFlow: MutableSharedFlow<PlayListScreenVmEvent> = MutableSharedFlow()
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val playListSettingsState =
        MutableStateFlow<PlayListSettingsBottomSheetDialog>(PlayListSettingsBottomSheetDialog.DefaultPopupState)

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override fun onRefreshPlayList() {
        viewModelScope.launch(errorHandler) {
            refreshPlayList()
        }
    }

    override fun onPlayAllClick(feed: Feed?) {
        if (feed != null && feed is VideoEntity) {
            onVideoClick(feed)
        }
    }

    override fun onShuffleClick(feed: Feed?) {
        if (feed != null && feed is VideoEntity) {
            onVideoClick(feed)
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = videoListScreen,
                index = videoEntity.index,
                cardSize = cardSize
            )
        }
    }

    override fun handleLoadState(loadStates: LoadStates) {
        arrayOf(
            loadStates.append,
            loadStates.prepend,
            loadStates.refresh
        ).filterIsInstance(LoadState.Error::class.java).firstOrNull()?.let { errorState ->
            unhandledErrorUseCase(TAG, errorState.error)
            emitVmEvent(PlayListScreenVmEvent.Error())
        }
    }

    override fun onShare() {
        shareUseCase(state.value.playListEntity.url)
    }

    override fun onFollowPlayList(following: Boolean) {
        viewModelScope.launch(errorHandler) {
            when (followPlayListUseCase(state.value.playListEntity.id, following)) {
                FollowPlayListResult.Success -> {
                    emitVmEvent(PlayListScreenVmEvent.FollowPLayList(following.not()))
                    state.update {
                        it.copy(
                            playListEntity = state.value.playListEntity.copy(
                                isFollowing = following.not()
                            )
                        )
                    }
                }

                is FollowPlayListResult.Failure -> emitVmEvent(PlayListScreenVmEvent.Error())
            }
        }
    }

    override fun onUpdateSubscription(action: UpdateChannelSubscriptionAction) {
        viewModelScope.launch(errorHandler) {
            getChannelDataUseCase(state.value.playListEntity.channelId).getOrNull()
                ?.let { channel ->
                    emitVmEvent(PlayListScreenVmEvent.UpdateChannelSubscription(channel, action))
                }
        }
    }

    override fun updateFollowStatus(channelDetailsEntity: ChannelDetailsEntity) {
        val newFollowStatus = FollowStatus(
            channelId = channelDetailsEntity.channelId,
            followed = channelDetailsEntity.followed,
            isBlocked = channelDetailsEntity.blocked
        )
        state.value = state.value.copy(
            playListEntity = state.value.playListEntity.copy(
                followStatus = newFollowStatus
            ),
            followStatus = newFollowStatus
        )
    }

    override fun onPlayListUpdated(playListEntity: PlayListEntity) {
        state.value = state.value.copy(
            playListEntity = state.value.playListEntity.copy(
                title = playListEntity.title,
                description = playListEntity.description,
                playListOwnerId = playListEntity.playListOwnerId,
                channelName = playListEntity.channelName,
                username = playListEntity.username,
                visibility = playListEntity.visibility
            ),
        )
    }

    override fun onTitleChanged(value: String) {
        editPlayListState.value.editPlayListEntity?.let { playListEntity ->
            editPlayListState.update {
                it.copy(
                    editPlayListEntity = playListEntity.copy(
                        title = value
                    ),
                    titleError = value.count() > RumbleConstants.MAX_CHARACTERS_PLAYLIST_TITLE
                )
            }
        }
    }

    override fun onDescriptionChanged(value: String) {
        editPlayListState.value.editPlayListEntity?.let { playListEntity ->
            editPlayListState.update {
                it.copy(
                    editPlayListEntity = playListEntity.copy(
                        description = value
                    ),
                    descriptionError = value.count() > RumbleConstants.MAX_CHARACTERS_PLAYLIST_DESCRIPTION
                )
            }
        }
    }

    override fun onCancelPlayListSettings() {
        editPlayListState.update {
            it.copy(
                editPlayListEntity = null
            )
        }
    }

    private fun isValidBeforeSave(): Boolean {
        var isValid = true
        editPlayListState.value.editPlayListEntity?.let { playListEntity ->
            if (playListEntity.title.isEmpty()) {
                editPlayListState.update {
                    it.copy(
                        titleError = true
                    )
                }
                isValid = false
            } else if (editPlayListState.value.titleError ||
                editPlayListState.value.descriptionError
            ) {
                isValid = false
            }
        }
        return isValid
    }

    override fun onSavePlayListSettings(playListAction: PlayListAction, videoId: Long?) {
        if (isValidBeforeSave()) {
            viewModelScope.launch(errorHandler) {
                if (playListAction == PlayListAction.Edit) {
                    editPlayListState.value.editPlayListEntity?.let { entity ->
                        when (val result = editPlayListUseCase(entity)) {
                            is UpdatePlayListResult.Failure -> {
                                emitVmEvent(PlayListScreenVmEvent.Error())
                            }

                            is UpdatePlayListResult.Success -> {
                                state.update {
                                    it.copy(
                                        playListEntity = result.playListEntity,
                                    )
                                }
                                editPlayListState.update {
                                    it.copy(
                                        editPlayListEntity = null
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onOpenChannelSelectionBottomSheet() {
        editPlayListState.value.editPlayListEntity?.let {
            playListSettingsState.value =
                PlayListSettingsBottomSheetDialog.PlayListChannelSelection(
                    playListEntity = it,
                )
        }
    }

    override fun onOpenPlayListVisibilitySelectionBottomSheet() {
        editPlayListState.value.editPlayListEntity?.let {
            playListSettingsState.value =
                PlayListSettingsBottomSheetDialog.PlayListVisibilitySelection(it)
        }
    }

    override fun onPlayListVisibilityChanged(playListVisibility: PlayListVisibility) {
        editPlayListState.value.editPlayListEntity?.let { entity ->
            editPlayListState.update { playListScreenState ->
                playListScreenState.copy(
                    editPlayListEntity = entity.copy(
                        visibility = playListVisibility
                    )
                )
            }
        }
        playListSettingsState.value = PlayListSettingsBottomSheetDialog.DefaultPopupState
    }

    override fun onPlayListOwnerChanged(ownerId: String) {
        editPlayListState.value.editPlayListEntity?.let { playListEntity ->
            editPlayListState.update {
                it.copy(
                    editPlayListEntity = playListEntity.copy(playListOwnerId = ownerId)
                )
            }
        }
        playListSettingsState.value = PlayListSettingsBottomSheetDialog.DefaultPopupState
    }

    override fun onVideoClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                PlayListScreenAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            emitVmEvent(PlayListScreenVmEvent.PlayVideo(videoEntity))
        }
    }

    override fun onCancelRestricted() {
        dismissAlertDialog()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        dismissAlertDialog()
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(PlayListScreenVmEvent.PlayVideo(videoEntity))
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    private fun dismissAlertDialog() {
        alertDialogState.value = AlertDialogState()
    }

    init {
        viewModelScope.launch(errorHandler) {
            val userId = sessionManager.userIdFlow.first()
            state.update {
                it.copy(
                    userId = userId,
                )
            }
            refreshPlayList()
            fetchUserUploadChannels()
        }
    }

    private suspend fun refreshPlayList() {
        when (val result = getPlayListUseCase(playListId)) {
            is PlayListResult.Failure -> {
                emitVmEvent(PlayListScreenVmEvent.Error())
            }

            is PlayListResult.Success -> {
                var playListEntity = result.playList
                if (state.value.userId == playListEntity.channelId)
                    playListEntity = playListEntity.copy(
                        followStatus = null
                    )
                val playListOptions = getPlayListOptionsUseCase(
                    playListEntity = result.playList,
                    userId = state.value.userId,
                )
                state.update {
                    it.copy(
                        playListEntity = playListEntity,
                        playListOptions = playListOptions
                    )
                }
            }
        }
    }

    private fun fetchUserUploadChannels() {
        viewModelScope.launch(errorHandler) {
            when (val result = getUserUploadChannelsUseCase()) {
                is UserUploadChannelsResult.UserUploadChannelsError -> {
                    delay(RumbleConstants.RETRY_DELAY_USER_UPLOAD_CHANNELS)
                    fetchUserUploadChannels()
                }

                is UserUploadChannelsResult.UserUploadChannelsSuccess -> {
                    result.userUploadChannels.forEach { userUploadChannelEntity ->
                        if (userUploadChannelEntity.channelId.toChannelIdString() == state.value.playListEntity.channelId) {
                            state.update { playListScreenState ->
                                playListScreenState.copy(
                                    playListEntity = playListScreenState.playListEntity.copy(
                                        followStatus = null
                                    ),
                                )
                            }
                        }
                    }
                    state.update {
                        it.copy(
                            followStatus = state.value.playListEntity.followStatus
                        )
                    }
                }
            }
        }
    }

    private fun emitVmEvent(event: PlayListScreenVmEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}