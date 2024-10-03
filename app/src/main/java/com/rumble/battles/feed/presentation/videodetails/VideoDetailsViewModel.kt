package com.rumble.battles.feed.presentation.videodetails

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.common.util.DeviceProperties
import com.google.firebase.perf.metrics.Trace
import com.rumble.analytics.CardSize
import com.rumble.analytics.ImaDestroyedEvent
import com.rumble.analytics.LocalsJoinButtonEvent
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.analytics.RantCloseButtonTapEvent
import com.rumble.battles.comments.CommentsHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.landing.RumbleOrientationChangeHandler
import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogRumbleVideoUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoDetailsUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleAdUpNextImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorsResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserCommentAuthorsUseCase
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.usecase.AnnotatedStringUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VoteResult
import com.rumble.domain.feed.domain.usecase.DeleteCommentUseCase
import com.rumble.domain.feed.domain.usecase.GetSensorBasedOrientationChangeEnabledUseCase
import com.rumble.domain.feed.domain.usecase.GetSingleRumbleAddUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoCommentsUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.feed.domain.usecase.LikeCommentUseCase
import com.rumble.domain.feed.domain.usecase.MergeCommentsStateUserCase
import com.rumble.domain.feed.domain.usecase.PostCommentUseCase
import com.rumble.domain.feed.domain.usecase.ReportContentUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentListReplyVisibilityUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentVoteUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListVideosUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageResult
import com.rumble.domain.livechat.domain.domainmodel.PendingMessageInfo
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.domain.usecases.PostLiveChatMessageUseCase
import com.rumble.domain.livechat.domain.usecases.SendRantPurchasedEventUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStartUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStopUseCase
import com.rumble.domain.premium.domain.usecases.ShouldShowPremiumPromoUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.usecase.HasPremiumRestrictionUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.sort.CommentSortOrder
import com.rumble.domain.video.domain.usecases.CreateRumblePlayListUseCase
import com.rumble.domain.video.domain.usecases.InitVideoPlayerSourceUseCase
import com.rumble.domain.video.domain.usecases.RequestEmailVerificationUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.domain.video.domain.usecases.UpdateVideoPlayerSourceUseCase
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.PAGINATION_VIDEO_PAGE_SIZE_PLAYLIST_VIDEO_DETAILS
import com.rumble.utils.extension.getChannelId
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.player.config.RumbleVideoMode
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.views.SettingsBottomSheetHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

interface VideoDetailsHandler : CommentsHandler, SettingsBottomSheetHandler {
    val state: State<VideoDetailsState>
    val playListState: StateFlow<PlayListState?>
    val eventFlow: Flow<VideoDetailsEvent>
    val userNameFlow: Flow<String>
    val userPictureFlow: Flow<String>
    val alertDialogState: State<AlertDialogState>

    fun onFullScreen(fullScreen: Boolean)
    fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int,
    )

    fun onCollapsing(percentage: Float)
    fun onLike()
    fun onDislike()
    fun onLikeRelated(videoEntity: VideoEntity)
    fun onDislikeRelated(videoEntity: VideoEntity)
    fun onShare()
    fun onBack()
    fun onLocals()
    fun onVideoSettings()
    fun reportVideo(videoEntity: VideoEntity, reportType: ReportType)
    fun onDismissDialog()
    fun onOrientationChanged(orientation: Int)
    fun onOpenLiveChat()
    fun onLiveChatHidden()
    fun onCloseLiveChat()
    fun onPostLiveChatMessage(chatId: Long, rantLevel: RantLevel? = null)
    fun onOpenComments()
    fun onCloseComments()
    fun onChangeCommentSortOrder(commentSortOrder: CommentSortOrder)
    fun getSortedCommentsList(commentsList: List<CommentEntity>?): List<CommentEntity>?
    fun onRumbleAdImpression(rumbleAd: RumbleAdEntity)
    fun onVideoPlayerImpression()
    fun onVideoCardImpression(videoEntity: VideoEntity)
    fun onVerifyEmailForLiveChat()
    fun onCloseBuyRant(message: String)
    fun onPipModeEntered()
    fun onVideoClick(feed: Feed)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
    fun onEnableOrientationChangeListener()
    fun onDisableOrientationChangeListener()
    fun onError()
    fun onLoopPlayList(loopPlayList: Boolean)
    fun onShufflePlayList(shufflePlayList: Boolean)
    fun onPlayListVideoClick(videoEntity: VideoEntity, videoNumber: Int)
    fun onPlayListVideoListUpdated(videoList: List<Feed>)
    fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity)
    fun onRantPurchaseSucceeded(rantLevel: RantLevel)
    fun onOpenBuyRantSheet()
    fun onOpenModerationMenu()
    fun onMuteUser()
    fun onDismissMuteMenu()
    fun onSubscribeToPremium()
    fun onReloadContent()
    fun onSignIn()
    fun onLoadContent(
        videoId: Long,
        playListId: String? = null,
        shufflePlayList: Boolean? = null
    )

    fun onUpdateLayoutState(layoutState: CollapsableLayoutState)
    fun onClearVideo()
}

data class PlayListState(
    val playListEntity: PlayListEntity,
    val playListVideosData: Flow<PagingData<Feed>> = emptyFlow(),
    val playListVideoList: List<Feed> = emptyList(),
    val inShuffleMode: Boolean = false,
    val inLoopPlayListMode: Boolean = false,
    val currentVideoNumber: Int = 1,
    //Hiding option for current implementation as requested by Egor&Tim. But living the implementations since it might be added later.
//    val playListOptions: List<PlayListOption> = emptyList(),
)

data class VideoDetailsState(
    val isPlayListPlayBackMode: Boolean = false,
    val videoEntity: VideoEntity? = null,
    val sortedCommentsList: List<CommentEntity>? = null,
    val rumbleAdEntity: RumbleAdEntity? = null,
    val followStatus: FollowStatus? = null,
    val channelDetailsEntity: ChannelDetailsEntity? = null,
    val rumblePlayer: RumblePlayer? = null,
    val isLoading: Boolean = true,
    val screenOrientation: Int = Configuration.ORIENTATION_UNDEFINED,
    val isFullScreen: Boolean = false,
    val uiType: UiType = UiType.EMBEDDED,
    val bottomSheetReason: BottomSheetReason? = null,
    val currentComment: String = "",
    val commentToReply: CommentEntity? = null,
    val commentsDisabled: Boolean = false,
    val commentsSortOrder: CommentSortOrder = CommentSortOrder.POPULAR,
    val inLiveChat: Boolean = false,
    val inComments: Boolean = false,
    val lastBottomSheet: LastBottomSheet = LastBottomSheet.LIVECHAT,
    val watchingNow: Long = 0,
    val userProfile: UserProfileEntity? = null,
    val selectedLiveChatAuthor: CommentAuthorEntity? = null,
    val hasPremiumRestriction: Boolean = false,
    val isLoggedIn: Boolean = false,
    val layoutState: CollapsableLayoutState = CollapsableLayoutState.NONE,
)

sealed class BottomSheetReason {
    object JoinOnLocals : BottomSheetReason()
    data class VideoSettingsDialog(val rumblePlayer: RumblePlayer) : BottomSheetReason()
    data class ReportComment(val commentEntity: CommentEntity) : BottomSheetReason()
    data class ReportVideo(val videoEntity: VideoEntity) : BottomSheetReason()
    object EmailVerificationComment : BottomSheetReason()
    object EmailVerificationLiveChat : BottomSheetReason()
    data class CommentAuthorSwitcher(val channels: List<CommentAuthorEntity>) : BottomSheetReason()
    object BuyRant : BottomSheetReason()
    object ModerationMenu : BottomSheetReason()
}

sealed class VideoDetailsAlertReason : AlertDialogReason {
    data class DiscardReason(val navigate: Boolean) : VideoDetailsAlertReason()
    data class DeleteReason(val commentEntity: CommentEntity) : VideoDetailsAlertReason()
    data class ErrorReason(val errorMessage: String?, val messageToShort: Boolean = false) :
        VideoDetailsAlertReason()

    data class ShowEmailVerificationSent(val email: String) : VideoDetailsAlertReason()
    object ShowYourEmailNotVerifiedYet : VideoDetailsAlertReason()
    data class RestrictedContentReason(val videoEntity: VideoEntity) : VideoDetailsAlertReason()
}

sealed class VideoDetailsEvent {
    data class VideoDetailsError(val errorMessage: String? = null) : VideoDetailsEvent()
    object HideKeyboard : VideoDetailsEvent()
    object ShowKeyboard : VideoDetailsEvent()
    object NavigateBack : VideoDetailsEvent()
    object ShowBottomSheet : VideoDetailsEvent()
    object HideBottomSheet : VideoDetailsEvent()
    object ShowCommentReportedMessage : VideoDetailsEvent()
    object ShowVideoReportedMessage : VideoDetailsEvent()
    object ShowEmailVerificationSuccess : VideoDetailsEvent()
    object OpenLiveChat : VideoDetailsEvent()
    object CloseLiveChat : VideoDetailsEvent()
    object OpenComments : VideoDetailsEvent()
    object CloseComments : VideoDetailsEvent()
    data class InitLiveChat(val videoId: Long) : VideoDetailsEvent()
    data class StartBuyRantFlow(val pendingMessageInfo: PendingMessageInfo) : VideoDetailsEvent()
    object ScrollToTop : VideoDetailsEvent()
    object ShowPremiumPromo : VideoDetailsEvent()
    object OpenMuteMenu : VideoDetailsEvent()
    object CloseMuteMenu : VideoDetailsEvent()
    object OpenPremiumSubscriptionOptions : VideoDetailsEvent()
    data class SetOrientation(val orientation: Int) : VideoDetailsEvent()
    object OpenAuthMenu : VideoDetailsEvent()
}

private const val TAG = "VideoDetailsViewModel"

@HiltViewModel
class VideoDetailsViewModel @Inject constructor(
    private val updateVideoPlayerSourceUseCase: UpdateVideoPlayerSourceUseCase,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val annotatedStringUseCase: AnnotatedStringUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val shareUseCase: ShareUseCase,
    private val getVideoCommentsUseCase: GetVideoCommentsUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val updateCommentListReplyVisibilityUseCase: UpdateCommentListReplyVisibilityUseCase,
    private val mergeCommentsStateUserCase: MergeCommentsStateUserCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val updateCommentVoteUseCase: UpdateCommentVoteUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val postLiveChatMessageUseCase: PostLiveChatMessageUseCase,
    private val getSingleRumbleAddUseCase: GetSingleRumbleAddUseCase,
    private val rumbleAdUpNextImpressionUseCase: RumbleAdUpNextImpressionUseCase,
    private val reportContentUseCase: ReportContentUseCase,
    private val logVideoDetailsUseCase: LogVideoDetailsUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val requestEmailVerificationUseCase: RequestEmailVerificationUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val logRumbleVideoUseCase: LogRumbleVideoUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val getUserCommentAuthorsUseCase: GetUserCommentAuthorsUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val getPlayListUseCase: GetPlayListUseCase,
    private val getPlayListVideosUseCase: GetPlayListVideosUseCase,
    private val createRumblePlayListUseCase: CreateRumblePlayListUseCase,
    private val shouldShowPremiumPromoUseCase: ShouldShowPremiumPromoUseCase,
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase,
    getSensorBasedOrientationChangeEnabledUseCase: GetSensorBasedOrientationChangeEnabledUseCase,
    private val application: Application,
    private val sessionManager: SessionManager,
    private val sendRantPurchasedEventUseCase: SendRantPurchasedEventUseCase,
    private val videoLoadTimeTraceStartUseCase: VideoLoadTimeTraceStartUseCase,
    private val videoLoadTimeTraceStopUseCase: VideoLoadTimeTraceStopUseCase,
    private val deviceType: DeviceType,
) : ViewModel(), VideoDetailsHandler, DefaultLifecycleObserver {
    private var playListId: String = ""
    private var shouldShufflePlayList = false

    private val orientationEventListener: RumbleOrientationChangeHandler

    private var lockPortraitVertical = false
    private var playerImpressionLogged = false
    private var videoLoadTimeTrace: Trace? = null

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        handleError()
    }

    override val state: MutableState<VideoDetailsState> = mutableStateOf(VideoDetailsState())
    override val playListState = MutableStateFlow<PlayListState?>(null)
    private val userIdFlow: Flow<String> = sessionManager.userIdFlow
    private val isPremiumUserFlow: Flow<Boolean> = sessionManager.isPremiumUserFlow
    override val userNameFlow: Flow<String> = sessionManager.userNameFlow
    override val userPictureFlow: Flow<String> = sessionManager.userPictureFlow
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val eventFlow: MutableSharedFlow<VideoDetailsEvent> = MutableSharedFlow()
    override val autoplayFlow: Flow<Boolean> = userPreferenceManager.autoplayFlow

    init {
        orientationEventListener = RumbleOrientationChangeHandler(application) {
            if (getSensorBasedOrientationChangeEnabledUseCase()) {
                onScreenOrientationChanged(it)
            }
        }

        observeLoginState()
    }

    override fun onLoadContent(videoId: Long, playListId: String?, shufflePlayList: Boolean?) {
        if (state.value.videoEntity?.id != videoId) {
            dismissResources()
            this.playListId = playListId ?: ""
            this.shouldShufflePlayList = shufflePlayList ?: false
            state.value = state.value.copy(
                isPlayListPlayBackMode = playListId != null,
                isLoading = true,
            )
            viewModelScope.launch(errorHandler) {
                loadContent(videoId)
            }
        }
        onUpdateLayoutState(CollapsableLayoutState.EXPENDED)
    }

    override fun onUpdateLayoutState(layoutState: CollapsableLayoutState) {
        state.value = state.value.copy(layoutState = layoutState)
        if (layoutState == CollapsableLayoutState.EXPENDED) {
            state.value.rumblePlayer?.rumbleVideoMode = RumbleVideoMode.Normal
            emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED))
        } else {
            state.value.rumblePlayer?.rumbleVideoMode = RumbleVideoMode.Minimized
            emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
        }
    }

    override fun onCleared() {
        runBlocking { sessionManager.allowContentLoadFlow(true) }
        super.onCleared()
    }

    override fun onDismissBottomSheet() {
        state.value = state.value.copy(bottomSheetReason = null)
        emitVmEvent(VideoDetailsEvent.HideBottomSheet)
    }

    override fun onReport() {
        state.value.videoEntity?.let {
            state.value =
                state.value.copy(bottomSheetReason = BottomSheetReason.ReportVideo(it))
            emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
        }
    }

    override fun onAutoplayOn(on: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveAutoplayOn(on)
        }
    }

    override fun onFullScreen(fullScreen: Boolean) {
        if (deviceType == DeviceType.Tablet) {
            val uiType = if (fullScreen) {
                if (state.value.videoEntity?.portraitMode == true) {
                    UiType.FULL_SCREEN_PORTRAIT
                } else {
                    UiType.FULL_SCREEN_LANDSCAPE
                }
            } else {
                UiType.EMBEDDED
            }
            state.value = state.value.copy(
                isFullScreen = fullScreen,
                uiType = uiType
            )
        } else {
            if (fullScreen && isLandscape(state.value.screenOrientation)) return

            if (fullScreen && state.value.videoEntity?.portraitMode == false) {
                emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE))
            } else if (fullScreen && state.value.videoEntity?.portraitMode == true) {
                lockPortraitVertical = true
                state.value =
                    state.value.copy(
                        isFullScreen = true,
                        uiType = UiType.FULL_SCREEN_PORTRAIT,
                    )
            } else if (fullScreen.not() and lockPortraitVertical) {
                lockPortraitVertical = false
                state.value =
                    state.value.copy(
                        isFullScreen = false,
                        uiType = UiType.EMBEDDED
                    )
            } else {
                emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
            }
        }
    }

    override fun onOrientationChanged(orientation: Int) {
        updateUid(orientation)
    }

    override fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    ) {
        annotatedStringUseCase.invoke(annotatedTextWithActions, offset)
    }

    override fun onCollapsing(percentage: Float) {
        if (percentage > 0) {
            state.value = state.value.copy(uiType = UiType.IN_LIST)
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
        } else {
            state.value = state.value.copy(uiType = UiType.EMBEDDED)
        }
    }

    override fun onLike() {
        if (state.value.isLoggedIn.not()) {
            onSignIn()
        } else {
            state.value.videoEntity?.let {
                viewModelScope.launch(errorHandler) {
                    val result = voteVideoUseCase(it, UserVote.LIKE)
                    if (result.success) state.value =
                        state.value.copy(videoEntity = result.updatedFeed)
                }
            }
        }
    }

    override fun onDislike() {
        if (state.value.isLoggedIn.not()) {
            onSignIn()
        } else {
            state.value.videoEntity?.let {
                viewModelScope.launch(errorHandler) {
                    val result = voteVideoUseCase(it, UserVote.DISLIKE)
                    if (result.success) state.value =
                        state.value.copy(videoEntity = result.updatedFeed)
                }
            }
        }
    }

    override fun onLikeRelated(videoEntity: VideoEntity) {
        if (state.value.isLoggedIn.not()) {
            onSignIn()
        } else {
            viewModelScope.launch(errorHandler) {
                val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
                if (result.success) updateRelatedVideo(videoEntity, result)
            }
        }
    }

    override fun onDislikeRelated(videoEntity: VideoEntity) {
        if (state.value.isLoggedIn.not()) {
            onSignIn()
        } else {
            viewModelScope.launch(errorHandler) {
                val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
                if (result.success) updateRelatedVideo(videoEntity, result)
            }
        }
    }

    private fun updateUid(orientation: Int) {
        if (DeviceProperties.isTablet(application.resources)
                .not() && orientation != state.value.screenOrientation
        ) {
            val isLandscape = isLandscape(orientation)
            val uiType = if (isLandscape) {
                UiType.FULL_SCREEN_LANDSCAPE
            } else {
                UiType.EMBEDDED
            }
            state.value = state.value.copy(
                uiType = uiType,
                isFullScreen = isLandscape,
                screenOrientation = orientation
            )
            onDismissDialog()
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
        }
    }

    private fun updateRelatedVideo(
        videoEntity: VideoEntity,
        result: VoteResult
    ) {
        state.value.videoEntity?.let { mainVideo ->
            val videoEntityWithUpdatedRelatedVideoList = mainVideo.copy(
                relatedVideoList = mainVideo.relatedVideoList?.map {
                    if (it.id == videoEntity.id)
                        result.updatedFeed
                    else
                        it
                }
            )
            state.value = state.value.copy(videoEntity = videoEntityWithUpdatedRelatedVideoList)
        }
    }

    override fun onShare() {
        state.value.videoEntity?.let {
            shareUseCase(it.url, it.title)
        }
    }

    override fun onReplies(commentEntity: CommentEntity) {
        state.value.videoEntity?.let { video ->
            video.commentList?.let { comments ->
                val updated = video.copy(commentList = comments.map {
                    updateCommentListReplyVisibilityUseCase(commentEntity.commentId, it)
                })
                state.value = state.value.copy(videoEntity = updated)
            }
        }
    }

    override fun onCommentChanged(comment: String) {
        state.value = state.value.copy(currentComment = comment)
    }

    override fun onSubmitComment() {
        emitVmEvent(VideoDetailsEvent.HideKeyboard)
        viewModelScope.launch(errorHandler) {
            state.value.videoEntity?.let {
                val result = postCommentUseCase(
                    comment = state.value.currentComment,
                    videoId = it.id,
                    commentId = state.value.commentToReply?.commentId,
                )
                if (result.success) {
                    fetchVideoComments()
                    state.value = state.value.copy(currentComment = "", commentToReply = null)
                } else {
                    alertDialogState.value = AlertDialogState(
                        true,
                        VideoDetailsAlertReason.ErrorReason(result.error, result.tooShort)
                    )
                }
            }
        }
    }

    override fun onLiveChatThumbnailTap(channels: List<LiveChatChannelEntity>) {
        viewModelScope.launch(errorHandler) {
            when (val result = getUserCommentAuthorsUseCase()) {
                is CommentAuthorsResult.Failure -> {
                    handleError()
                }

                is CommentAuthorsResult.Success -> {
                    state.value =
                        state.value.copy(
                            bottomSheetReason = BottomSheetReason.CommentAuthorSwitcher(result.authors),
                            selectedLiveChatAuthor = if (state.value.selectedLiveChatAuthor == null) {
                                result.authors.first()
                            } else {
                                state.value.selectedLiveChatAuthor
                            }
                        )
                    emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
                }
            }
        }
    }

    override fun onLiveChatAuthorSelected(commentAuthorEntity: CommentAuthorEntity) {
        state.value = state.value.copy(selectedLiveChatAuthor = commentAuthorEntity)
    }

    override fun onCloseAddComment() {
        if (state.value.currentComment.isEmpty()) {
            state.value = state.value.copy(commentToReply = null)
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
        } else {
            showDiscardDialog(false)
        }
    }

    override fun onBack() {
        if (state.value.currentComment.isNotEmpty()) {
            showDiscardDialog(true)
        } else {
            dismissResources()
            emitVmEvent(VideoDetailsEvent.NavigateBack)
        }
    }

    override fun onClearVideo() {
        dismissResources()
    }

    override fun onDelete(commentEntity: CommentEntity) {
        alertDialogState.value =
            AlertDialogState(true, VideoDetailsAlertReason.DeleteReason(commentEntity))
    }

    override fun onLocals() {
        analyticsEventUseCase(
            LocalsJoinButtonEvent(
                screenId = videoDetailsScreen,
                creatorId = state.value.channelDetailsEntity?.channelId?.getChannelId() ?: 0
            )
        )
        state.value = state.value.copy(bottomSheetReason = BottomSheetReason.JoinOnLocals)
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun onVideoSettings() {
        state.value.rumblePlayer?.let {
            state.value =
                state.value.copy(bottomSheetReason = BottomSheetReason.VideoSettingsDialog(it))
            emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
        }
    }

    override fun onReport(commentEntity: CommentEntity) {
        state.value =
            state.value.copy(bottomSheetReason = BottomSheetReason.ReportComment(commentEntity))
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun report(commentEntity: CommentEntity, reportType: ReportType) {
        viewModelScope.launch(errorHandler) {
            val success = reportContentUseCase(
                contentId = commentEntity.commentId,
                reportType = reportType,
                ReportContentType.COMMENT
            )
            if (success) {
                emitVmEvent(event = VideoDetailsEvent.ShowCommentReportedMessage)
            } else {
                emitVmEvent(event = VideoDetailsEvent.VideoDetailsError())
            }
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
                emitVmEvent(event = VideoDetailsEvent.ShowVideoReportedMessage)
            } else {
                emitVmEvent(event = VideoDetailsEvent.VideoDetailsError())
            }
        }
    }

    override fun onReplyToComment(commentEntity: CommentEntity) {
        if (state.value.isLoggedIn.not()) {
            emitVmEvent(event = VideoDetailsEvent.OpenAuthMenu)
        } else if (state.value.userProfile?.validated == true) {
            state.value = state.value.copy(
                commentToReply = commentEntity.copy(displayReplies = false)
            )
        } else {
            onVerifyEmailForComments()
        }
    }

    override fun onLikeComment(commentEntity: CommentEntity) {
        viewModelScope.launch(errorHandler) {
            val result = likeCommentUseCase(commentEntity)
            if (result.success) {
                state.value.videoEntity?.let { videoEntity ->
                    state.value = state.value.copy(
                        videoEntity = videoEntity.copy(
                            commentList = updateCommentVoteUseCase(
                                result.commentId,
                                result.userVote,
                                videoEntity.commentList ?: emptyList()
                            )
                        )
                    )
                }
            }
        }
    }

    override fun onKeepWriting() {
        onDismissDialog()
        emitVmEvent(VideoDetailsEvent.OpenLiveChat)
    }

    override fun onDiscard(navigate: Boolean) {
        onDismissDialog()
        state.value = state.value.copy(
            currentComment = "",
            commentToReply = null,
            inLiveChat = false
        )
        emitVmEvent(VideoDetailsEvent.HideKeyboard)
        emitVmEvent(VideoDetailsEvent.CloseLiveChat)
        if (navigate) {
            dismissResources()
            emitVmEvent(VideoDetailsEvent.NavigateBack)
        }
    }

    override fun onDeleteAction(commentEntity: CommentEntity) {
        onDismissDialog()
        viewModelScope.launch(errorHandler) {
            val deleteResult = deleteCommentUseCase(commentEntity.commentId)
            if (deleteResult.success) fetchVideoComments()
            else handleError(deleteResult.error)
        }
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun onOpenLiveChat() {
        state.value = state.value.copy(
            inLiveChat = true,
            lastBottomSheet = LastBottomSheet.LIVECHAT,
            currentComment = ""
        )
        emitVmEvent(VideoDetailsEvent.OpenLiveChat)
    }

    override fun onLiveChatHidden() {
        state.value = state.value.copy(
            inLiveChat = false
        )
    }

    override fun onCloseLiveChat() {
        if (state.value.currentComment.isEmpty()) {
            state.value = state.value.copy(inLiveChat = false)
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
            emitVmEvent(VideoDetailsEvent.CloseLiveChat)
        } else {
            showDiscardDialog(false)
        }
    }

    override fun onPostLiveChatMessage(chatId: Long, rantLevel: RantLevel?) {
        viewModelScope.launch(errorHandler) {
            val channelId = state.value.selectedLiveChatAuthor?.channelId

            val result =
                postLiveChatMessageUseCase(
                    chatId = chatId,
                    message = state.value.currentComment,
                    authorChannelId = channelId,
                    rantLevel = rantLevel
                )
            state.value = state.value.copy(currentComment = "")
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
            when (result) {
                is LiveChatMessageResult.Failure -> {
                    alertDialogState.value = AlertDialogState(
                        true,
                        VideoDetailsAlertReason.ErrorReason(
                            result.userErrorMessage
                        )
                    )
                }

                is LiveChatMessageResult.RantMessageSuccess -> {
                    emitVmEvent(
                        VideoDetailsEvent.StartBuyRantFlow(result.pendingMessageInfo)
                    )
                }

                else -> return@launch
            }
        }
    }

    override fun onOpenComments() {
        state.value = state.value.copy(
            inComments = true,
            lastBottomSheet = LastBottomSheet.COMMENTS
        )
        emitVmEvent(VideoDetailsEvent.OpenComments)
    }

    override fun onCloseComments() {
        if (state.value.currentComment.isEmpty()) {
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
            emitVmEvent(VideoDetailsEvent.CloseComments)
        } else {
            showDiscardDialog(false)
        }
    }

    override fun onChangeCommentSortOrder(commentSortOrder: CommentSortOrder) {
        state.value = state.value.copy(commentsSortOrder = commentSortOrder)
    }

    override fun getSortedCommentsList(commentsList: List<CommentEntity>?): List<CommentEntity>? =
        commentsList?.sortedWith(
            compareByDescending {
                if (state.value.commentsSortOrder == CommentSortOrder.POPULAR)
                    it.likeNumber
                else
                    it.date
            }
        )

    override fun onRumbleAdImpression(rumbleAd: RumbleAdEntity) {
        viewModelScope.launch(errorHandler) {
            state.value.channelDetailsEntity?.channelId?.let {
                rumbleAdUpNextImpressionUseCase(rumbleAd, it)
            }
        }
        if (rumbleAd.expirationLocal.isBefore(LocalDateTime.now())) {
            viewModelScope.launch(errorHandler) { getRumbleAd() }
        }
    }

    override fun onVideoPlayerImpression() {
        if (playerImpressionLogged.not()) {
            state.value.videoEntity?.let { videoEntity ->
                playerImpressionLogged = true
                viewModelScope.launch(errorHandler) {
                    logVideoDetailsUseCase(videoDetailsScreen, videoEntity.id.toString())
                    logRumbleVideoUseCase(
                        videoPath = videoEntity.videoLogView.view,
                        screenId = videoDetailsScreen
                    )
                }
            }
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = videoDetailsScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override fun onCloseBuyRant(message: String) {
        analyticsEventUseCase(RantCloseButtonTapEvent)
        state.value = state.value.copy(currentComment = message)
        onDismissBottomSheet()
    }

    override fun onPipModeEntered() {
        state.value = state.value.copy(
            inLiveChat = false,
            bottomSheetReason = null
        )
        onDismissBottomSheet()
    }

    override fun onVideoClick(feed: Feed) {
        (feed as? VideoEntity)?.let { videoEntity ->
            if (videoEntity.ageRestricted) {
                alertDialogState.value = AlertDialogState(
                    true,
                    VideoDetailsAlertReason.RestrictedContentReason(videoEntity)
                )
            } else {
                if (state.value.rumblePlayer?.playerTarget?.value == PlayerTarget.AD) {
                    analyticsEventUseCase(ImaDestroyedEvent, true)
                }
                state.value.rumblePlayer?.pauseAndResetState()
                onWatchVideo(videoEntity)
                state.value = state.value.copy(
                    isPlayListPlayBackMode = false
                )
            }
        }
    }

    override fun onCancelRestricted() {
        onDismissDialog()
        state.value.rumblePlayer?.playVideo()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        onDismissDialog()
        onWatchVideo(videoEntity)
        analyticsEventUseCase(MatureContentWatchEvent)
    }

    override fun onEnableOrientationChangeListener() {
        orientationEventListener.enable()
    }

    override fun onDisableOrientationChangeListener() {
        orientationEventListener.disable()
    }

    override fun onVerifyEmailForLiveChat() {
        state.value = state.value.copy(
            bottomSheetReason = BottomSheetReason.EmailVerificationLiveChat
        )
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun onVerifyEmailForComments() {
        state.value = state.value.copy(
            bottomSheetReason = BottomSheetReason.EmailVerificationComment
        )
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun onRequestVerificationLink() {
        state.value.userProfile?.email?.let {
            viewModelScope.launch(errorHandler) {
                when (requestEmailVerificationUseCase(it)) {
                    is EmptyResult.Failure ->
                        emitVmEvent(VideoDetailsEvent.VideoDetailsError())

                    EmptyResult.Success ->
                        showVerificationEmailSent()
                }
            }
        }
    }

    override fun onCheckVerificationStatus() {
        viewModelScope.launch {
            fetchUserProfile()
            state.value.userProfile?.validated?.let {
                if (it) {
                    emitVmEvent(VideoDetailsEvent.ShowEmailVerificationSuccess)
                } else {
                    showEmailVerificationFailure()
                }
            }
        }
    }

    override fun onError() {
        emitVmEvent(VideoDetailsEvent.VideoDetailsError())
        onDismissBottomSheet()
    }

    private fun onWatchVideo(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            state.value = state.value.copy(videoEntity = videoEntity)
            updateVideoSource(
                videoId = videoEntity.id,
                updatedRelatedVideoList = true,
                autoplay = false
            )
            fetchUserProfile()
            fetchChannelDetails(state.value.videoEntity?.channelId)
            getRumbleAd()
            emitVmEvent(VideoDetailsEvent.ScrollToTop)
        }
    }

    override fun onLoopPlayList(loopPlayList: Boolean) {
        playListState.update {
            it?.copy(
                inLoopPlayListMode = loopPlayList
            )
        }
        state.value.rumblePlayer?.loopPlayList(loopPlayList)
    }

    override fun onShufflePlayList(shufflePlayList: Boolean) {
        playListState.update {
            it?.copy(
                inShuffleMode = shufflePlayList
            )
        }
        state.value.rumblePlayer?.shufflePlayList(shufflePlayList)
    }

    override fun onPlayListVideoClick(
        videoEntity: VideoEntity,
        videoNumber: Int
    ) {
        state.value.rumblePlayer?.onPlayFromPlayList(videoEntity.id)
        updateCurrentPlayListVideoNumber(videoNumber)
    }

    override fun onPlayListVideoListUpdated(videoList: List<Feed>) {
        viewModelScope.launch(errorHandler) {
            if (state.value.isPlayListPlayBackMode &&
                ((playListState.value?.playListVideoList?.size ?: 0) < videoList.size)
            ) {
                playListState.value = playListState.value?.copy(playListVideoList = videoList)
                val playList = createRumblePlayListUseCase(
                    feedList = videoList,
                    publisherId = PublisherId.AndroidApp,
                    shuffle = playListState.value?.inShuffleMode ?: false,
                    loop = playListState.value?.inLoopPlayListMode ?: false
                )
                if (state.value.rumblePlayer == null) {
                    videoList.firstOrNull()?.let {
                        val videoEntity = it as VideoEntity
                        val player = initVideoPlayerSourceUseCase(
                            playList = playList,
                            screenId = videoDetailsScreen,
                            saveLastPosition = saveLastPositionUseCase::invoke,
                            liveVideoReport = { videoId, watchingNow, status ->
                                onLiveVideoReport(videoId, status, watchingNow)
                            },
                            onNextVideo = { videoId, channelId, autoplay ->
                                videoList.forEachIndexed { index, feed ->
                                    if (videoId == (feed as VideoEntity).id) {
                                        updateCurrentPlayListVideoNumber(index + 1)
                                        return@forEachIndexed
                                    }
                                }
                                onNextVideo(videoId, channelId, autoplay)
                            }
                        )
                        if (hasPremiumRestrictionUseCase(videoEntity).not())
                            player.playVideo()
                        state.value = state.value.copy(
                            rumblePlayer = player,
                            commentsDisabled = videoEntity.commentsDisabled,
                            watchingNow = videoEntity.watchingNow
                        )
                    }
                }
                state.value.rumblePlayer?.updatePlayList(
                    playList = playList
                )
            }
        }
    }

    override fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity) {
        state.value = state.value.copy(
            channelDetailsEntity = channelDetailsEntity,
            followStatus = FollowStatus(
                channelId = channelDetailsEntity.channelId,
                followed = channelDetailsEntity.followed,
                isBlocked = channelDetailsEntity.blocked
            )
        )
    }

    private fun onLiveVideoReport(videoId: Long, status: Int?, watchingNow: Long) {
        if (videoId == state.value.videoEntity?.id) {
            if (status != state.value.videoEntity?.livestreamStatus?.value) {
                updateVideoSource(
                    videoId = videoId,
                    updatedRelatedVideoList = true,
                    autoplay = false
                )
            } else {
                state.value = state.value.copy(watchingNow = watchingNow)
            }
        }
    }

    override fun onRantPurchaseSucceeded(rantLevel: RantLevel) {
        onDismissBottomSheet()
        viewModelScope.launch(errorHandler) {
            state.value.channelDetailsEntity?.let {
                sendRantPurchasedEventUseCase(rantLevel.rantPrice, it.channelId)
            }
        }
    }

    override fun onOpenBuyRantSheet() {
        state.value = state.value.copy(bottomSheetReason = BottomSheetReason.BuyRant)
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun onOpenModerationMenu() {
        state.value = state.value.copy(bottomSheetReason = BottomSheetReason.ModerationMenu)
        emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
    }

    override fun onMuteUser() {
        emitVmEvent(VideoDetailsEvent.OpenMuteMenu)
    }

    override fun onDismissMuteMenu() {
        emitVmEvent(VideoDetailsEvent.CloseMuteMenu)
    }

    override fun onSubscribeToPremium() {
        emitVmEvent(VideoDetailsEvent.OpenPremiumSubscriptionOptions)
    }

    override fun onReloadContent() {
        viewModelScope.launch(errorHandler) {
            state.value.videoEntity?.id?.let {
                loadContent(it)
            }
        }
    }

    override fun onSignIn() {
        emitVmEvent(VideoDetailsEvent.OpenAuthMenu)
    }

    private fun handleError(errorMessage: String? = null) {
        emitVmEvent(VideoDetailsEvent.VideoDetailsError(errorMessage))
        state.value = state.value.copy(
            isLoading = false,
            currentComment = "",
            commentToReply = null
        )
    }

    private suspend fun loadContent(videoId: Long) {
        videoLoadTimeTrace = videoLoadTimeTraceStartUseCase(videoId.toString())
        if (state.value.isPlayListPlayBackMode)
            fetchPlayListWithVideos(playListId, shouldShufflePlayList, userIdFlow.first())
        fetchDetails(videoId)
        state.value.videoEntity?.let { videoEntity ->
            if (state.value.isPlayListPlayBackMode.not()) {
                initVideoState(videoEntity)
            }
        }
        fetchChannelDetails(state.value.videoEntity?.channelId)
        getRumbleAd()
        showPremiumPromo()
    }

    private fun showDiscardDialog(navigate: Boolean) {
        alertDialogState.value =
            AlertDialogState(true, VideoDetailsAlertReason.DiscardReason(navigate))
    }

    private fun dismissResources() {
        state.value.rumblePlayer?.stopPlayer()
        state.value = VideoDetailsState(
            rumblePlayer = null,
            videoEntity = null,
            layoutState = CollapsableLayoutState.NONE,
            isLoggedIn = state.value.isLoggedIn,
            userProfile = state.value.userProfile
        )
        orientationEventListener.disable()
    }

    private fun emitVmEvent(event: VideoDetailsEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    private suspend fun initVideoState(videoEntity: VideoEntity) {
        val player = initVideoPlayerSourceUseCase(
            videoId = videoEntity.id,
            saveLastPosition = saveLastPositionUseCase::invoke,
            liveVideoReport = { _, watchingNow, status ->
                if (status != state.value.videoEntity?.livestreamStatus?.value) {
                    updateVideoSource(
                        videoId = videoEntity.id,
                        updatedRelatedVideoList = true,
                        autoplay = false
                    )
                } else {
                    state.value = state.value.copy(watchingNow = watchingNow)
                }
            },
            screenId = videoDetailsScreen,
            onVideoSizeDefined = ::onVideoSizeDefined,
            autoplay = true,
            onNextVideo = ::onNextVideo,
            showAds = sessionManager.isPremiumUserFlow.first().not(),
            sendInitialPlaybackEvent = {
                videoLoadTimeTrace?.let {
                    videoLoadTimeTraceStopUseCase(it)
                }
                videoLoadTimeTrace = null
            }
        )
        if (hasPremiumRestrictionUseCase(videoEntity).not())
            player.playVideo()
        state.value = state.value.copy(
            rumblePlayer = player,
            commentsDisabled = videoEntity.commentsDisabled,
            watchingNow = videoEntity.watchingNow
        )
    }

    private fun updateVideoSource(
        videoId: Long,
        updatedRelatedVideoList: Boolean,
        autoplay: Boolean
    ) {
        viewModelScope.launch(errorHandler) {
            state.value = state.value.copy(isLoading = true)
            videoLoadTimeTrace = videoLoadTimeTraceStartUseCase(videoId.toString())
            state.value.rumblePlayer?.pauseVideo()
            fetchDetails(videoId)
            state.value.rumblePlayer?.let { player ->
                state.value.videoEntity?.let { video ->
                    updateVideoPlayerSourceUseCase(
                        player = player,
                        videoEntity = video,
                        saveLastPosition = saveLastPositionUseCase::invoke,
                        screenId = videoDetailsScreen,
                        autoplay = autoplay,
                        updatedRelatedVideoList = updatedRelatedVideoList
                    )
                    if (hasPremiumRestrictionUseCase(video).not())
                        player.playVideo()
                }
            }
        }
    }

    private fun updateCurrentPlayListVideoNumber(videoNumber: Int) {
        playListState.update {
            it?.copy(
                currentVideoNumber = videoNumber
            )
        }
    }

    private fun onVideoSizeDefined(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            state.value.videoEntity?.let { currentVideo ->
                state.value = state.value.copy(
                    videoEntity = currentVideo.copy(
                        videoWidth = width,
                        videoHeight = height,
                        portraitMode = height > width
                    )
                )
            }
        }
    }

    private fun onNextVideo(videoId: Long, channelId: String, autoplay: Boolean) {
        viewModelScope.launch(errorHandler) {
            updateVideoSource(
                videoId = videoId,
                updatedRelatedVideoList = false,
                autoplay = autoplay
            )
            fetchUserProfile()
            fetchChannelDetails(channelId)
            getRumbleAd()
        }
    }

    private suspend fun fetchPlayListWithVideos(
        playListId: String,
        shouldShufflePlayList: Boolean,
        userId: String
    ) {
        when (val result = getPlayListUseCase(playListId)) {
            is PlayListResult.Failure -> {
                emitVmEvent(VideoDetailsEvent.VideoDetailsError())
            }

            is PlayListResult.Success -> {
                var playListEntity = result.playList
                if (userId == playListEntity.channelId)
                    playListEntity = playListEntity.copy(
                        followStatus = null
                    )
                val playListVideos = getPlayListVideosUseCase(
                    playListEntity.id,
                    PAGINATION_VIDEO_PAGE_SIZE_PLAYLIST_VIDEO_DETAILS
                )
                //Hiding option for current implementation as requested by Egor&Tim. But living the implementations since it might be added later.
//                val playListOptions = getPlayListOptionsUseCase(
//                    playListId = result.playList.id,
//                    userId = userId,
//                    channelId = result.playList.playListChannelEntity?.channelId,
//                    playListOwnerId = result.playList.playListOwnerId,
//                )
                playListState.update {
                    PlayListState(
                        playListEntity = playListEntity,
                        playListVideosData = playListVideos,
                        inShuffleMode = shouldShufflePlayList
//                        playListOptions = playListOptions
                    )
                }
            }
        }
    }

    private suspend fun fetchDetails(videoId: Long) {
        playerImpressionLogged = false
        getVideoDetailsUseCase(videoId)?.let {
            state.value = state.value.copy(
                videoEntity = it,
                isLoading = false,
                commentsDisabled = it.commentsDisabled,
                watchingNow = it.watchingNow
            )
            onVideoPlayerImpression()
            getPremiumRestriction(it)
            initLiveChat(it)
            if (state.value.lastBottomSheet == LastBottomSheet.COMMENTS && state.value.inComments)
                onOpenComments()
        } ?: run {
            state.value = state.value.copy(videoEntity = null)
            handleError()
        }
    }

    private suspend fun getPremiumRestriction(videoEntity: VideoEntity) {
        state.value =
            state.value.copy(hasPremiumRestriction = hasPremiumRestrictionUseCase(videoEntity))
    }

    private suspend fun fetchChannelDetails(channelId: String?) {
        channelId?.let {
            val result = viewModelScope.async { getChannelDataUseCase(it).getOrNull() }
            result.await()?.let { channel ->
                state.value = state.value.copy(
                    channelDetailsEntity = channel,
                    followStatus = FollowStatus(
                        channelId = channel.channelId,
                        followed = channel.followed,
                        isBlocked = channel.blocked
                    )
                )
            }
        }
    }

    private suspend fun fetchUserProfile() {
        // fetch user profile only when user is logged in
        if (state.value.isLoggedIn) {
            val result = getUserProfileUseCase()
            if (result.success) {
                state.value = state.value.copy(
                    userProfile = result.userProfileEntity
                )
            } else {
                handleError()
            }
        }
    }

    private suspend fun showPremiumPromo() {
        if (shouldShowPremiumPromoUseCase())
            emitVmEvent(VideoDetailsEvent.ShowPremiumPromo)
    }

    private suspend fun getRumbleAd() {
        if (sessionManager.isPremiumUserFlow.first().not()) {
            val result = viewModelScope.async {
                getSingleRumbleAddUseCase.invoke(
                    videoEntity = state.value.videoEntity,
                    channelDetailsEntity = state.value.channelDetailsEntity
                )
            }
            result.await()?.let {
                state.value = state.value.copy(rumbleAdEntity = it)
            }
        }
    }

    private suspend fun fetchVideoComments() {
        state.value.videoEntity?.let { video ->
            state.value = state.value.copy(
                videoEntity = video.copy(
                    commentList = mergeCommentsStateUserCase(
                        state.value.commentToReply,
                        video.commentList ?: emptyList(),
                        getVideoCommentsUseCase(video.id)
                    )
                )
            )
        }
    }

    private fun onScreenOrientationChanged(orientation: Int) {
        if (orientation < 0) return
        emitVmEvent(VideoDetailsEvent.SetOrientation(orientation))
    }

    private fun isLandscape(orientation: Int) =
        orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun showVerificationEmailSent() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = VideoDetailsAlertReason.ShowEmailVerificationSent(
                email = state.value.userProfile?.email ?: ""
            )
        )
    }

    private fun showEmailVerificationFailure() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = VideoDetailsAlertReason.ShowYourEmailNotVerifiedYet
        )
    }

    private suspend fun initLiveChat(videoEntity: VideoEntity) {
        if (
            (videoEntity.livestreamStatus == LiveStreamStatus.LIVE || videoEntity.livestreamStatus == LiveStreamStatus.OFFLINE)
            && videoEntity.liveChatDisabled.not()
        ) {
            state.value = state.value.copy(
                inLiveChat = (isPremiumUserFlow.first() || videoEntity.isPremiumExclusiveContent.not()) && state.value.inComments.not(),
                lastBottomSheet = if (state.value.inComments) LastBottomSheet.COMMENTS else LastBottomSheet.LIVECHAT,
                currentComment = ""
            )
            emitVmEvent(VideoDetailsEvent.InitLiveChat(videoEntity.id))
        } else {
            emitVmEvent(VideoDetailsEvent.CloseLiveChat)
        }
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest {
                state.value = state.value.copy(isLoggedIn = it.isNotEmpty())
                if (it.isNotEmpty()) fetchUserProfile()
            }
        }
    }
}