package com.rumble.battles.discover.presentation.discoverscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.rumble.battles.comments.CommentsHandler
import com.rumble.battles.comments.CommentsUIState
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.domainmodel.discoverPlayerScreen
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.discover.domain.usecase.GetDiscoverPlayerVideoListUseCase
import com.rumble.domain.discover.model.DiscoverPlayerVideoListSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.DeleteCommentUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoCommentsUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.feed.domain.usecase.LikeCommentUseCase
import com.rumble.domain.feed.domain.usecase.MergeCommentsStateUserCase
import com.rumble.domain.feed.domain.usecase.PostCommentUseCase
import com.rumble.domain.feed.domain.usecase.ReportContentUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentListReplyVisibilityUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentVoteUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.settings.domain.usecase.HasPremiumRestrictionUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoPlayerSourceUseCase
import com.rumble.domain.video.domain.usecases.RequestEmailVerificationUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.presentation.views.SettingsBottomSheetHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DiscoverPlayerHandler : CommentsHandler, SettingsBottomSheetHandler {
    val uiState: StateFlow<DiscoverPlayerUIState>
    val userNameFlow: Flow<String>
    val userPictureFlow: Flow<String>
    val vmEvents: Flow<DiscoverPlayerVmEvent>
    val currentPlayerState: State<RumblePlayer?>
    val updatedEntity: MutableStateFlow<VideoEntity?>
    val followedVideoEntity: MutableStateFlow<VideoEntity?>
    val popupState: StateFlow<DiscoverPlayerDialog>
    val alertDialogState: State<AlertDialogState>

    fun onFollowChannel(channelId: String)
    fun onShareVideoClicked()
    fun onVideoDetailsClicked()
    fun updateCurrentVideo(index: Int, videoEntity: VideoEntity)
    fun onPauseCurrentPlayer()
    fun stopPlayer()
    fun resumePlayer()
    fun onCommentsClicked(videoEntity: VideoEntity)
    fun onVideoSettingsClicked()
    fun onDismissDialog()
    fun reportVideo(videoEntity: VideoEntity, reportType: ReportType)
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun handleLoadState(loadStates: LoadStates)
}

sealed class DiscoverPlayerAlertReason : AlertDialogReason {
    data class ShowEmailVerificationSent(val email: String) : DiscoverPlayerAlertReason()
    data class DeleteReason(val commentEntity: CommentEntity) : DiscoverPlayerAlertReason()
    data class ErrorReason(val errorMessage: String?, val messageToShort: Boolean = false) :
        DiscoverPlayerAlertReason()
    object ShowYourEmailNotVerifiedYet : DiscoverPlayerAlertReason()
    object DiscardReason : DiscoverPlayerAlertReason()
}

sealed class DiscoverPlayerVmEvent {
    data class DiscoverPlayerError(val errorMessage: String? = null) : DiscoverPlayerVmEvent()
    object ShowBottomSheetPopup : DiscoverPlayerVmEvent()
    object DismissBottomSheetPopup : DiscoverPlayerVmEvent()
    object ShowVideoReportedMessage : DiscoverPlayerVmEvent()
    object ShowCommentReportedMessage : DiscoverPlayerVmEvent()
    object ShowEmailVerificationSuccess : DiscoverPlayerVmEvent()
    object HideKeyboard : DiscoverPlayerVmEvent()
    object ShowKeyboard : DiscoverPlayerVmEvent()
    data class OpenVideoDetails(val videoEntity: VideoEntity) : DiscoverPlayerVmEvent()
    data class ScrollCommentToIndex(val index: Int) : DiscoverPlayerVmEvent()
}

data class DiscoverPlayerUIState(
    val videoList: Flow<PagingData<Feed>> = emptyFlow(),
    val scrollToIndex: Int = -1,
    val currentVideoEntity: VideoEntity? = null,
    val currentIndex: Int = -1,
    val loading: Boolean = false,
    val commentsDisabled: Boolean = false,
)

sealed class DiscoverPlayerDialog {
    data class VideoSettingsDialog(val rumblePlayer: RumblePlayer) : DiscoverPlayerDialog()
    data class OpenReportVideoPopup(val videoEntity: VideoEntity) : DiscoverPlayerDialog()
    object DefaultPopupState : DiscoverPlayerDialog()
    object OpenCommentsPopup : DiscoverPlayerDialog()
    object OpenEmailVerificationComment : DiscoverPlayerDialog()
    data class OpenReportCommentPopup(val commentEntity: CommentEntity) : DiscoverPlayerDialog()
}

private const val TAG = "DiscoverPlayerViewModel"
private const val SCROLL_TO_VIDEO_DELAY = 200L

@HiltViewModel
class DiscoverPlayerViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val updateChannelSubscriptionUseCase: UpdateChannelSubscriptionUseCase,
    private val updateCommentListReplyVisibilityUseCase: UpdateCommentListReplyVisibilityUseCase,
    private val requestEmailVerificationUseCase: RequestEmailVerificationUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val mergeCommentsStateUserCase: MergeCommentsStateUserCase,
    private val getVideoCommentsUseCase: GetVideoCommentsUseCase,
    private val updateCommentVoteUseCase: UpdateCommentVoteUseCase,
    private val reportContentUseCase: ReportContentUseCase,
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val shareUseCase: ShareUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val getDiscoverPlayerVideoListUseCase: GetDiscoverPlayerVideoListUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase,
    sessionManager: SessionManager,
) : ViewModel(), DiscoverPlayerHandler {

    private val _vmEvents = Channel<DiscoverPlayerVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<DiscoverPlayerVmEvent> = _vmEvents.receiveAsFlow()
    override val currentPlayerState: MutableState<RumblePlayer?> = mutableStateOf(null)
    override val updatedEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)
    override val followedVideoEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)
    override val popupState =
        MutableStateFlow<DiscoverPlayerDialog>(DiscoverPlayerDialog.DefaultPopupState)
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val uiState = MutableStateFlow(DiscoverPlayerUIState())
    override val userNameFlow: Flow<String> = sessionManager.userNameFlow
    override val userPictureFlow: Flow<String> = sessionManager.userPictureFlow
    override val commentsUIState = MutableStateFlow(CommentsUIState())
    override val autoplayFlow: Flow<Boolean> = userPreferenceManager.autoplayFlow

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleFailure(throwable)
    }

    init {
        fetchVideoListData(
            source = DiscoverPlayerVideoListSource.valueOf(
                stateHandle.get<String>(RumblePath.VIDEO_CATEGORY.path) ?: ""
            ),
            channelId = stateHandle.get<String>(RumblePath.CHANNEL.path) ?: ""
        )
        viewModelScope.launch(errorHandler) {
            fetchUserProfile()
        }
    }

    override fun onDismissBottomSheet() {
        popupState.value = DiscoverPlayerDialog.DefaultPopupState
        emitVmEvent(DiscoverPlayerVmEvent.DismissBottomSheetPopup)
    }

    override fun onReport() {
        uiState.value.currentVideoEntity?.let {
            popupState.value = DiscoverPlayerDialog.OpenReportVideoPopup(it)
            emitVmEvent(DiscoverPlayerVmEvent.ShowBottomSheetPopup)
        }
    }

    override fun onAutoplayOn(on: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveAutoplayOn(on)
        }
    }

    override fun reportVideo(videoEntity: VideoEntity, reportType: ReportType) {
        viewModelScope.launch(errorHandler) {
            val success = reportContentUseCase(
                contentId = videoEntity.id,
                reportType = reportType,
                ReportContentType.VIDEO
            )
            if (success) {
                emitVmEvent(event = DiscoverPlayerVmEvent.ShowVideoReportedMessage)
            } else {
                emitVmEvent(event = DiscoverPlayerVmEvent.DiscoverPlayerError())
            }
        }
    }

    override fun onLike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
            if (result.success) updatedEntity.value = result.updatedFeed
        }
    }

    override fun onDislike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
            if (result.success) updatedEntity.value = result.updatedFeed
        }
    }

    override fun handleLoadState(loadStates: LoadStates) {
        arrayOf(
            loadStates.append,
            loadStates.prepend,
            loadStates.refresh
        ).filterIsInstance(LoadState.Error::class.java).firstOrNull()?.let { errorState ->
            unhandledErrorUseCase(TAG, errorState.error)
        }
    }

    override fun onFollowChannel(channelId: String) {
        viewModelScope.launch(errorHandler) {
            getChannelDataUseCase(channelId).getOrNull()?.let { channelDetailsEntity ->
                updateChannelSubscriptionUseCase(
                    channelDetailsEntity = channelDetailsEntity,
                    action = UpdateChannelSubscriptionAction.SUBSCRIBE
                )
                    .onSuccess { channelDetailEntity ->
                        uiState.value.currentVideoEntity?.let {
                            followedVideoEntity.value = it.copy(
                                channelFollowed = channelDetailEntity.followed
                            )
                        }
                    }
                    .onFailure { throwable ->
                        unhandledErrorUseCase(TAG, throwable)
                        emitVmEvent(DiscoverPlayerVmEvent.DiscoverPlayerError())
                    }
            }
        }
    }

    override fun onShareVideoClicked() {
        uiState.value.currentVideoEntity?.let { videoEntity ->
            shareUseCase(videoEntity.url, videoEntity.title)
        }
    }

    override fun onVideoDetailsClicked() {
        uiState.value.currentVideoEntity?.let { videoEntity ->
            emitVmEvent(DiscoverPlayerVmEvent.OpenVideoDetails(videoEntity))
        }
    }

    override fun updateCurrentVideo(index: Int, videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            currentPlayerState.value?.stopPlayer()
            currentPlayerState.value = initVideoPlayerSourceUseCase(
                videoId = videoEntity.id,
                loopWhenFinished = true,
                restrictBackground = true,
                useLowQuality = true,
                screenId = discoverPlayerScreen,
                saveLastPosition = { lastPosition, videoId ->
                    saveLastPositionUseCase(lastPosition, videoId)
                }
            )
            currentPlayerState.value?.playVideo()
            fetchDetails(videoEntity.id)
            uiState.update {
                it.copy(
                    currentIndex = index,
                    scrollToIndex = -1
                )
            }
        }
    }

    private suspend fun fetchDetails(videoId: Long) {
        val result = viewModelScope.async { getVideoDetailsUseCase(videoId) }
        result.await()?.let { videoEntity ->
            updatedEntity.value = videoEntity
            uiState.update {
                it.copy(
                    currentVideoEntity = videoEntity,
                    loading = false,
                    commentsDisabled = it.commentsDisabled
                )
            }
            commentsUIState.update {
                it.copy(
                    commentList = videoEntity.commentList,
                    commentNumber = videoEntity.commentNumber,
                    commentsDisabled = videoEntity.commentsDisabled
                )
            }
        } ?: run {
            handleError()
        }
    }

    private suspend fun fetchUserProfile() {
        val result = getUserProfileUseCase()
        if (result.success) {
            commentsUIState.update {
                it.copy(
                    userProfile = result.userProfileEntity
                )
            }
        } else {
            handleError()
        }
    }

    private fun fetchVideoListData(source: DiscoverPlayerVideoListSource, channelId: String) {
        viewModelScope.launch(errorHandler) {
            val result = getDiscoverPlayerVideoListUseCase(source, channelId)
            uiState.update {
                it.copy(
                    videoList = result.videoList,
                    scrollToIndex = result.scrollToIndex,
                    currentVideoEntity = result.videoEntity,
                    currentIndex = result.scrollToIndex
                )
            }
        }
    }

    override fun onPauseCurrentPlayer() {
        currentPlayerState.value?.pauseVideo()
    }

    override fun stopPlayer() {
        currentPlayerState.value?.stopPlayer()
    }
    override fun resumePlayer() {
        viewModelScope.launch(errorHandler) {
            uiState.value.currentVideoEntity?.let {
                currentPlayerState.value?.let { player ->
                    player.seekTo(getLastPositionUseCase(it.id))
                    player.playVideo()
                }
            }
        }
    }

    override fun onCommentsClicked(videoEntity: VideoEntity) {
        viewModelScope.launch {
            commentsUIState.update {
                it.copy(
                    hasPremiumRestriction = hasPremiumRestrictionUseCase(videoEntity)
                )
            }
            popupState.value = DiscoverPlayerDialog.OpenCommentsPopup
            emitVmEvent(DiscoverPlayerVmEvent.ShowBottomSheetPopup)
        }
    }

    override fun onVideoSettingsClicked() {
        currentPlayerState.value?.let {
            popupState.value = DiscoverPlayerDialog.VideoSettingsDialog(it)
            emitVmEvent(DiscoverPlayerVmEvent.ShowBottomSheetPopup)
        }
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun onCloseAddComment() {
        if (commentsUIState.value.currentComment.isEmpty()) {
            commentsUIState.update {
                it.copy(
                    commentToReply = null
                )
            }
            emitVmEvent(DiscoverPlayerVmEvent.HideKeyboard)
        } else {
            alertDialogState.value =
                AlertDialogState(true, DiscoverPlayerAlertReason.DiscardReason)
        }
    }

    override fun onReplies(commentEntity: CommentEntity) {
        commentsUIState.update {
            it.copy(
                commentList = it.commentList?.map { commentEntity ->
                    updateCommentListReplyVisibilityUseCase(commentEntity.commentId, commentEntity)
                }
            )
        }
    }

    override fun onDelete(commentEntity: CommentEntity) {
        alertDialogState.value =
            AlertDialogState(true, DiscoverPlayerAlertReason.DeleteReason(commentEntity))
    }

    override fun onReport(commentEntity: CommentEntity) {
        popupState.value = DiscoverPlayerDialog.OpenReportCommentPopup(commentEntity)
        emitVmEvent(DiscoverPlayerVmEvent.ShowBottomSheetPopup)
    }

    override fun report(commentEntity: CommentEntity, reportType: ReportType) {
        viewModelScope.launch(errorHandler) {
            val success = reportContentUseCase(
                contentId = commentEntity.commentId,
                reportType = reportType,
                ReportContentType.COMMENT
            )
            if (success) {
                emitVmEvent(event = DiscoverPlayerVmEvent.ShowCommentReportedMessage)
            } else {
                emitVmEvent(event = DiscoverPlayerVmEvent.DiscoverPlayerError())
            }
        }
    }

    override fun onReplyToComment(commentEntity: CommentEntity) {
        if (commentsUIState.value.userProfile?.validated == true) {
            commentsUIState.value = commentsUIState.value.copy(
                commentToReply = commentEntity.copy(displayReplies = false)
            )
        } else {
            onVerifyEmailForComments()
        }
    }

    override fun onDeleteAction(commentEntity: CommentEntity) {
        alertDialogState.value = AlertDialogState()
        viewModelScope.launch(errorHandler) {
            val deleteResult = deleteCommentUseCase(commentEntity.commentId)
            if (deleteResult.success) fetchVideoComments()
            else handleError(deleteResult.error)
        }
    }

    override fun onLikeComment(commentEntity: CommentEntity) {
        viewModelScope.launch(errorHandler) {
            val result = likeCommentUseCase(commentEntity)
            if (result.success) {
                commentsUIState.update {
                    it.copy(
                        commentList = updateCommentVoteUseCase(
                            result.commentId,
                            result.userVote,
                            it.commentList ?: emptyList()
                        )
                    )
                }
            }
        }
    }

    override fun onCommentChanged(comment: String) {
        commentsUIState.update {
            it.copy(
                currentComment = comment
            )
        }
    }

    override fun onSubmitComment() {
        emitVmEvent(DiscoverPlayerVmEvent.HideKeyboard)
        viewModelScope.launch(errorHandler) {
            uiState.value.currentVideoEntity?.let { videoEntity ->
                val result = postCommentUseCase(
                    commentsUIState.value.currentComment,
                    videoEntity.id,
                    commentsUIState.value.commentToReply?.commentId
                )
                if (result.success) {
                    fetchVideoComments()
                    commentsUIState.update {
                        it.copy(
                            currentComment = "",
                            commentToReply = null
                        )
                    }
                    scrollToComment(result.commentId)
                } else {
                    alertDialogState.value = AlertDialogState(
                        true,
                        DiscoverPlayerAlertReason.ErrorReason(result.error, result.tooShort)
                    )
                }
            }
        }
    }

    override fun onLiveChatAuthorSelected(commentAuthorEntity: CommentAuthorEntity) {}
    override fun onLiveChatThumbnailTap(channels: List<LiveChatChannelEntity>) {}

    private fun scrollToComment(commentId: Long?) {
        commentId?.let {
            val comments = commentsUIState.value.commentList
            comments?.find { it.commentId == commentId }?.let { comment ->
                val index = comments.indexOf(comment)
                if (index > -1) emitVmEvent(DiscoverPlayerVmEvent.ScrollCommentToIndex(index))
            }
        }
    }

    private suspend fun fetchVideoComments() {
        uiState.value.currentVideoEntity?.let { video ->
            commentsUIState.update {
                it.copy(
                    commentList = mergeCommentsStateUserCase(
                        commentsUIState.value.commentToReply,
                        video.commentList ?: emptyList(),
                        getVideoCommentsUseCase(video.id)
                    )
                )
            }
        }
    }

    override fun onKeepWriting() {
        alertDialogState.value = AlertDialogState()
        emitVmEvent(DiscoverPlayerVmEvent.ShowKeyboard)
    }

    override fun onDiscard(navigate: Boolean) {
        alertDialogState.value = AlertDialogState()
        commentsUIState.update {
            it.copy(
                currentComment = "",
                commentToReply = null,
            )
        }
        emitVmEvent(DiscoverPlayerVmEvent.HideKeyboard)
    }

    override fun onVerifyEmailForComments() {
        popupState.value = DiscoverPlayerDialog.OpenEmailVerificationComment
        emitVmEvent(DiscoverPlayerVmEvent.ShowBottomSheetPopup)
    }

    override fun onRequestVerificationLink() {
        commentsUIState.value.userProfile?.email?.let {
            viewModelScope.launch(errorHandler) {
                when (requestEmailVerificationUseCase(it)) {
                    is EmptyResult.Failure -> handleError()
                    is EmptyResult.Success -> showVerificationEmailSent()
                }
            }
        }
    }

    private fun showVerificationEmailSent() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = DiscoverPlayerAlertReason.ShowEmailVerificationSent(
                email = commentsUIState.value.userProfile?.email ?: ""
            )
        )
    }

    override fun onCheckVerificationStatus() {
        viewModelScope.launch {
            fetchUserProfile()
            commentsUIState.value.userProfile?.validated?.let {
                if (it) {
                    emitVmEvent(DiscoverPlayerVmEvent.ShowEmailVerificationSuccess)
                } else {
                    alertDialogState.value = AlertDialogState(
                        show = true,
                        alertDialogReason = DiscoverPlayerAlertReason.ShowYourEmailNotVerifiedYet
                    )
                }
            }
        }
    }

    private fun emitVmEvent(event: DiscoverPlayerVmEvent) {
        _vmEvents.trySend(event)
    }

    private fun handleFailure(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
    }

    private fun handleError(errorMessage: String? = null) {
        emitVmEvent(DiscoverPlayerVmEvent.DiscoverPlayerError(errorMessage))
        uiState.value = uiState.value.copy(
            loading = false,
        )
        commentsUIState.value = commentsUIState.value.copy(
            currentComment = "",
            commentToReply = null
        )
    }
}