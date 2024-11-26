package com.rumble.battles.feed.presentation.videodetails

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
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
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorsResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserCommentAuthorsUseCase
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentVoteResult
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
import com.rumble.domain.feed.domain.usecase.StartPremiumPreviewCountdownUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentListReplyVisibilityUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentVoteUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListVideosUseCase
import com.rumble.domain.livechat.domain.domainmodel.ChatMode
import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageResult
import com.rumble.domain.livechat.domain.domainmodel.LiveGateEntity
import com.rumble.domain.livechat.domain.domainmodel.PendingMessageInfo
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.domain.usecases.CalculateLiveGateCountdownValueUseCase
import com.rumble.domain.livechat.domain.usecases.PostLiveChatMessageUseCase
import com.rumble.domain.livechat.domain.usecases.SendRantPurchasedEventUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceHasPreRollUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTracePlayedPreRollUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStartUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStopUseCase
import com.rumble.domain.premium.domain.usecases.ShouldShowPremiumPromoUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.usecase.DefineLiveChatModeUseCase
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
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.PAGINATION_VIDEO_PAGE_SIZE_PLAYLIST_VIDEO_DETAILS
import com.rumble.utils.extension.getChannelId
import com.rumble.utils.extension.insertTextAtPosition
import com.rumble.utils.extension.removeCharacterAtPosition
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.player.config.RumbleVideoMode
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.views.SettingsBottomSheetHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
import java.time.LocalDateTime
import javax.inject.Inject

interface VideoDetailsHandler : CommentsHandler, SettingsBottomSheetHandler {
    val state: State<VideoDetailsState>
    val playListState: StateFlow<PlayListState?>
    val emoteState: State<EmoteState>
    val eventFlow: Flow<VideoDetailsEvent>
    val userNameFlow: Flow<String>
    val userPictureFlow: Flow<String>
    val alertDialogState: State<AlertDialogState>

    fun onFullScreen(fullScreen: Boolean)
    fun onCollapsing(percentage: Float)
    fun onLike()
    fun onDislike()
    fun onLikeRelated(videoEntity: VideoEntity)
    fun onDislikeRelated(videoEntity: VideoEntity)
    fun onShare()
    fun onCloseVideoDetails()
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
    fun updateChannelDetailsEntity(channelDetailsEntity: CreatorEntity)
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
    fun onEnforceLiveGatePremiumRestriction(liveGateEntity: LiveGateEntity? = null)
    fun onLiveGateEvent(liveGateEntity: LiveGateEntity)
    fun getVideoAspectRatio(): Int
    fun onLoadNewVideo(videoUrl: String)
    fun onShowEmoteSelector()
    fun onSwitchToKeyboard()
    fun onEmoteSelected(emoteEntity: EmoteEntity)
    fun onDeleteSymbol()
    fun onBackPressed()
    fun onDismissEmoteRequest()
    fun onFollowChannel()
    fun onOpenPremiumPromo()
    fun onClosePremiumPromo()
    fun onRepostDeleted(repostId: Long)
    fun onRepostedByCurrentUser()
    fun onRantPopupShown()
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
    val channelDetailsEntity: CreatorEntity? = null,
    val rumblePlayer: RumblePlayer? = null,
    val isLoading: Boolean = true,
    val screenOrientation: Int = Configuration.ORIENTATION_UNDEFINED,
    val isFullScreen: Boolean = false,
    val isCollapsingMiniPlayerInProgress: Boolean = false,
    val uiType: UiType = UiType.EMBEDDED,
    val bottomSheetReason: BottomSheetReason? = null,
    val currentComment: String = "",
    val currentCursorPosition: Int = 0,
    val commentToReply: CommentEntity? = null,
    val commentsDisabled: Boolean = false,
    val commentsSortOrder: CommentSortOrder = CommentSortOrder.POPULAR,
    val inLiveChat: Boolean = false,
    val inComments: Boolean = false,
    val lastBottomSheet: LastBottomSheet = LastBottomSheet.NONE,
    val watchingNow: Long = 0,
    val userProfile: UserProfileEntity? = null,
    val selectedLiveChatAuthor: CommentAuthorEntity? = null,
    val hasPremiumRestriction: Boolean = false,
    val hasLiveGateRestriction: Boolean = false,
    val screenOrientationLocked: Boolean = false,
    val isLoggedIn: Boolean = false,
    val layoutState: CollapsableLayoutState = CollapsableLayoutState.None,
    val displayPremiumOnlyContent: Boolean = false,
    val chatMode: ChatMode = ChatMode.Free,
    val repostedByUser: Boolean = false,
    val showJoinButton: Boolean = false,
)

data class EmoteState(
    val showEmoteSelector: Boolean = false,
    val requestFollow: Boolean = false,
    val requestSubscribe: Boolean = false,
    val requestedEmote: EmoteEntity? = null,
    val channelName: String = "",
)

sealed class BottomSheetReason {
    data object JoinOnLocals : BottomSheetReason()
    data class VideoSettingsDialog(val rumblePlayer: RumblePlayer) : BottomSheetReason()
    data class ReportComment(val commentEntity: CommentEntity) : BottomSheetReason()
    data class ReportVideo(val videoEntity: VideoEntity) : BottomSheetReason()
    data object EmailVerificationComment : BottomSheetReason()
    data object EmailVerificationLiveChat : BottomSheetReason()
    data class CommentAuthorSwitcher(val channels: List<CommentAuthorEntity>) : BottomSheetReason()
    data object BuyRant : BottomSheetReason()
    data object ModerationMenu : BottomSheetReason()
}

sealed class VideoDetailsAlertReason : AlertDialogReason {
    data class DiscardReason(val navigate: Boolean) : VideoDetailsAlertReason()
    data class DeleteReason(val commentEntity: CommentEntity) : VideoDetailsAlertReason()
    data class ErrorReason(val errorMessage: String?, val messageToShort: Boolean = false) :
        VideoDetailsAlertReason()

    data class ShowEmailVerificationSent(val email: String) : VideoDetailsAlertReason()
    data object ShowYourEmailNotVerifiedYet : VideoDetailsAlertReason()
    data class RestrictedContentReason(val videoEntity: VideoEntity) : VideoDetailsAlertReason()
}

sealed class VideoDetailsEvent {
    data class VideoDetailsError(val errorMessage: String? = null) : VideoDetailsEvent()
    data object HideKeyboard : VideoDetailsEvent()
    data object ShowKeyboard : VideoDetailsEvent()
    data object CloseVideoDetails : VideoDetailsEvent()
    data object ShowBottomSheet : VideoDetailsEvent()
    data object HideBottomSheet : VideoDetailsEvent()
    data object ShowCommentReportedMessage : VideoDetailsEvent()
    data object ShowVideoReportedMessage : VideoDetailsEvent()
    data object ShowEmailVerificationSuccess : VideoDetailsEvent()
    data object OpenLiveChat : VideoDetailsEvent()
    data object CloseLiveChat : VideoDetailsEvent()
    data object OpenComments : VideoDetailsEvent()
    data object CloseComments : VideoDetailsEvent()
    data object OpenPremiumPromo : VideoDetailsEvent()
    data object ClosePremiumPromo : VideoDetailsEvent()
    data class InitLiveChat(val videoEntity: VideoEntity) : VideoDetailsEvent()
    data class StartBuyRantFlow(val pendingMessageInfo: PendingMessageInfo) : VideoDetailsEvent()
    data object ScrollToTop : VideoDetailsEvent()
    data object ShowPremiumPromo : VideoDetailsEvent()
    data object OpenMuteMenu : VideoDetailsEvent()
    data object CloseMuteMenu : VideoDetailsEvent()
    data object OpenPremiumSubscriptionOptions : VideoDetailsEvent()
    data class SetOrientation(val orientation: Int) : VideoDetailsEvent()
    data object OpenAuthMenu : VideoDetailsEvent()
    data object RequestMessageFocus : VideoDetailsEvent()
    data class StartFollowChannel(
        val channelDetailsEntity: CreatorEntity?,
        val action: UpdateChannelSubscriptionAction
    ) : VideoDetailsEvent()

    data class EmoteUsed(val emote: EmoteEntity) : VideoDetailsEvent()
    data object VideoModeMinimized : VideoDetailsEvent()
}

private const val TAG = "VideoDetailsViewModel"

@HiltViewModel
class VideoDetailsViewModel @Inject constructor(
    private val updateVideoPlayerSourceUseCase: UpdateVideoPlayerSourceUseCase,
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
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
    private val videoLoadTimeTraceHasPreRollUseCase: VideoLoadTimeTraceHasPreRollUseCase,
    private val videoLoadTimeTracePlayedPreRollUseCase: VideoLoadTimeTracePlayedPreRollUseCase,
    private val deviceType: DeviceType,
    private val calculateLiveGateCountdownValueUseCase: CalculateLiveGateCountdownValueUseCase,
    private val startPremiumPreviewCountdownUseCase: StartPremiumPreviewCountdownUseCase,
    private val defineLiveChatModeUseCase: DefineLiveChatModeUseCase,
) : ViewModel(), VideoDetailsHandler, DefaultLifecycleObserver {
    private var playListId: String = ""
    private var shouldShufflePlayList = false

    private val orientationEventListener: RumbleOrientationChangeHandler

    private var lockPortraitVertical = false
    private var playerImpressionLogged = false
    private var videoLoadTimeTrace: Trace? = null
    private var videoStarted: Boolean = false
    private var preRollAdStarted: Boolean = false

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        handleError()
    }

    override val state: MutableState<VideoDetailsState> = mutableStateOf(VideoDetailsState())
    override val playListState = MutableStateFlow<PlayListState?>(null)
    override val emoteState: MutableState<EmoteState> = mutableStateOf(EmoteState())
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
            if (state.value.screenOrientationLocked && getSensorBasedOrientationChangeEnabledUseCase()) {
                onScreenOrientationChanged(it)
            }
        }

        observeLoginState()
    }

    override fun onShowEmoteSelector() {
        if (emoteState.value.showEmoteSelector) {
            onSwitchToKeyboard()
        } else {
            emoteState.value = emoteState.value.copy(showEmoteSelector = true)
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
        }
    }

    override fun onSwitchToKeyboard() {
        emoteState.value = emoteState.value.copy(showEmoteSelector = false)
        emitVmEvent(VideoDetailsEvent.RequestMessageFocus)
    }

    override fun onEmoteSelected(emoteEntity: EmoteEntity) {
        if (emoteEntity.locked) {
            if (state.value.channelDetailsEntity?.followed != true) {
                emoteState.value = emoteState.value.copy(
                    requestFollow = true,
                    requestedEmote = emoteEntity.copy(locked = false),
                    channelName = state.value.channelDetailsEntity?.name ?: ""
                )
            } else {
                emoteState.value = emoteState.value.copy(
                    requestSubscribe = true,
                    requestedEmote = emoteEntity.copy(locked = false),
                    channelName = state.value.channelDetailsEntity?.name ?: ""
                )
            }
        } else {
            val emoteText = " :${emoteEntity.name}: "
            val updated = state.value.currentComment
                .insertTextAtPosition(emoteText, state.value.currentCursorPosition)
            state.value = state.value.copy(
                currentComment = updated,
                currentCursorPosition = state.value.currentCursorPosition + emoteText.length
            )
            emitVmEvent(VideoDetailsEvent.EmoteUsed(emoteEntity))
        }
    }

    override fun onDeleteSymbol() {
        if (state.value.currentComment.isNotEmpty()) {
            val deletePosition = state.value.currentCursorPosition - 1
            val updated = state.value.currentComment.removeCharacterAtPosition(deletePosition)
            state.value = state.value.copy(
                currentComment = updated,
                currentCursorPosition = deletePosition
            )
        }
    }

    override fun onBackPressed() {
        if (emoteState.value.showEmoteSelector) {
            emoteState.value = EmoteState()
        } else {
            onUpdateLayoutState(CollapsableLayoutState.Collapsed)
        }
    }

    override fun onDismissEmoteRequest() {
        emoteState.value = emoteState.value.copy(
            requestFollow = false,
            requestSubscribe = false,
            requestedEmote = null,
        )
    }

    override fun onFollowChannel() {
        emitVmEvent(
            VideoDetailsEvent.StartFollowChannel(
                channelDetailsEntity = state.value.channelDetailsEntity,
                action = UpdateChannelSubscriptionAction.SUBSCRIBE,
            )
        )
        emoteState.value = emoteState.value.copy(
            requestFollow = false,
        )
    }

    override fun onLoadNewVideo(videoUrl: String) {
        viewModelScope.launch(errorHandler) {
            getVideoDetailsUseCase(videoUrl)?.let {
                state.value = state.value.copy(videoEntity = getVideoDetailsUseCase(it.id))
                updateVideoSource(videoId = it.id, updatedRelatedVideoList = true, autoplay = true)
            }
        }
    }

    override fun onRepostDeleted(repostId: Long) {
        state.value.videoEntity?.let { video ->
            if (video.userRepost?.id == repostId) {
                viewModelScope.launch(errorHandler) {
                    fetchDetails(video.id)
                }
            }
        }
    }

    override fun onRepostedByCurrentUser() {
        viewModelScope.launch(errorHandler) {
            state.value.videoEntity?.let { video ->
                viewModelScope.launch(errorHandler) {
                    fetchDetails(video.id)
                }
            }
        }
    }

    override fun onRantPopupShown() {
        emitVmEvent(VideoDetailsEvent.HideKeyboard)
        emoteState.value = EmoteState()
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
        onUpdateLayoutState(CollapsableLayoutState.Expended())
    }

    override fun onUpdateLayoutState(layoutState: CollapsableLayoutState) {
        state.value = state.value.copy(
            layoutState = layoutState,
        )
        emoteState.value =
            emoteState.value.copy(showEmoteSelector = emoteState.value.showEmoteSelector && layoutState != CollapsableLayoutState.Collapsed)
        if (layoutState == CollapsableLayoutState.Expended()) {
            state.value.rumblePlayer?.setRumbleVideoMode(RumbleVideoMode.Normal)
            emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED))
        } else {
            viewModelScope.launch { sessionManager.allowContentLoadFlow(true) }
            state.value.rumblePlayer?.setRumbleVideoMode(RumbleVideoMode.Minimized)
            emitVmEvent(VideoDetailsEvent.VideoModeMinimized)
            if (deviceType != DeviceType.Tablet) {
                state.value = state.value.copy(screenOrientationLocked = false)
                emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
            }
            if (state.value.hasPremiumRestriction || state.value.hasLiveGateRestriction) onCloseVideoDetails()
        }
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
                state.value =
                    state.value.copy(
                        screenOrientationLocked = true,
                    )
                emoteState.value = EmoteState()
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
                state.value =
                    state.value.copy(
                        screenOrientationLocked = true
                    )
                emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
            }
        }
    }

    override fun onOrientationChanged(orientation: Int) {
        updateUid(orientation)
    }

    override fun onCollapsing(percentage: Float) {
        state.value = state.value.copy(isCollapsingMiniPlayerInProgress = percentage in 0.01..0.99)
        if (percentage > 0) {
            state.value = state.value.copy(uiType = UiType.IN_LIST)
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
            viewModelScope.launch { sessionManager.saveVideoDetailsCollapsed(true) }
        } else {
            state.value = state.value.copy(uiType = UiType.EMBEDDED)
            viewModelScope.launch { sessionManager.saveVideoDetailsCollapsed(false) }
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
                    else emitVmEvent(VideoDetailsEvent.VideoDetailsError(result.errorMessage))
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
                    else emitVmEvent(VideoDetailsEvent.VideoDetailsError(result.errorMessage))
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
                else emitVmEvent(VideoDetailsEvent.VideoDetailsError(result.errorMessage))
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
                else emitVmEvent(VideoDetailsEvent.VideoDetailsError(result.errorMessage))
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
            if (isLandscape) {
                emoteState.value = EmoteState()
            }
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

    override fun onCommentChanged(comment: String, position: Int) {
        state.value = state.value.copy(
            currentComment = comment,
            currentCursorPosition = position,
        )
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

    override fun onCloseVideoDetails() {
        if (state.value.currentComment.isNotEmpty()) {
            showDiscardDialog(true)
        } else {
            dismissResources()
            emitVmEvent(VideoDetailsEvent.CloseVideoDetails)
        }
    }

    override fun onClearVideo() {
        if (state.value.currentComment.isNotEmpty()) {
            showDiscardDialog(true)
        } else {
            dismissResources()
            emitVmEvent(VideoDetailsEvent.CloseVideoDetails)
        }
    }

    override fun onEnforceLiveGatePremiumRestriction(liveGateEntity: LiveGateEntity?) {
        state.value.videoEntity?.let {
            viewModelScope.launch(errorHandler + Dispatchers.Main) {
                if (hasPremiumRestrictionUseCase(it)) {
                    state.value.rumblePlayer?.pauseVideo()
                    state.value = state.value.copy(
                        videoEntity = state.value.videoEntity?.copy(hasLiveGate = true),
                        hasLiveGateRestriction = true,
                        chatMode = defineLiveChatModeUseCase(
                            it,
                            liveGateEntity?.chatMode ?: state.value.chatMode
                        ),
                    )
                    onFullScreen(false)
                }
            }
        }
    }

    override fun onLiveGateEvent(liveGateEntity: LiveGateEntity) {
        viewModelScope.launch {
            state.value.videoEntity?.copy(hasLiveGate = true)?.let { videoEntity ->
                state.value = state.value.copy(
                    videoEntity = videoEntity,
                    chatMode = defineLiveChatModeUseCase(videoEntity, liveGateEntity.chatMode),
                )
                state.value.videoEntity?.let {
                    val countdown = calculateLiveGateCountdownValueUseCase(
                        videoEntity,
                        liveGateEntity.videoTimeCode,
                        liveGateEntity.countDownValue
                    )
                    state.value.rumblePlayer?.startPremiumCountDown(countdown.toLong())
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun getVideoAspectRatio(): Int =
        if (state.value.rumblePlayer?.playerTarget?.value == PlayerTarget.AD
            || state.value.videoEntity?.portraitMode == true
        )
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        else
            AspectRatioFrameLayout.RESIZE_MODE_FILL

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
        if (state.value.channelDetailsEntity?.localsCommunityEntity?.showPremiumFlow == true) {
            emitVmEvent(VideoDetailsEvent.OpenPremiumSubscriptionOptions)
        } else {
            state.value = state.value.copy(bottomSheetReason = BottomSheetReason.JoinOnLocals)
            emitVmEvent(VideoDetailsEvent.ShowBottomSheet)
        }
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
            when(val result = likeCommentUseCase(commentEntity)) {
                is CommentVoteResult.Success -> {
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

                is CommentVoteResult.Failure -> {
                    emitVmEvent(VideoDetailsEvent.VideoDetailsError(result.errorMessage))
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
            inLiveChat = false,
            currentCursorPosition = 0,
        )
        emoteState.value = emoteState.value.copy(
            showEmoteSelector = false,
            requestSubscribe = false,
            requestFollow = false,
        )
        emitVmEvent(VideoDetailsEvent.HideKeyboard)
        emitVmEvent(VideoDetailsEvent.CloseLiveChat)
        if (navigate) {
            dismissResources()
            emitVmEvent(VideoDetailsEvent.CloseVideoDetails)
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
            currentComment = "",
            currentCursorPosition = 0,
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
            state.value = state.value.copy(
                inLiveChat = false,
            )
            emoteState.value = emoteState.value.copy(
                showEmoteSelector = false,
                requestSubscribe = false,
                requestFollow = false,
            )
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
            state.value = state.value.copy(
                currentComment = "",
                currentCursorPosition = 0,
            )
            emoteState.value = emoteState.value.copy(
                showEmoteSelector = false,
                requestSubscribe = false,
                requestFollow = false,
            )
            emitVmEvent(VideoDetailsEvent.HideKeyboard)
            if (rantLevel != null) onDismissBottomSheet()
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
                    sessionManager.saveDisablePip(true)
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
            state.value = state.value.copy(inComments = false)
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
                state.value = state.value.copy(displayPremiumOnlyContent = false)
                onWatchVideo(videoEntity)
                state.value = state.value.copy(
                    isPlayListPlayBackMode = false,
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
                            liveVideoReport = { videoId, result ->
                                onLiveVideoReport(videoId, result.statusCode, result.watchingNow)
                            },
                            onNextVideo = { videoId, channelId, autoplay ->
                                videoList.forEachIndexed { index, feed ->
                                    if (videoId == (feed as VideoEntity).id) {
                                        updateCurrentPlayListVideoNumber(index + 1)
                                        return@forEachIndexed
                                    }
                                }
                                onNextVideo(videoId, channelId, autoplay)
                            },
                            onPremiumCountdownFinished = {
                                onEnforceLiveGatePremiumRestriction()
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

    override fun updateChannelDetailsEntity(channelDetailsEntity: CreatorEntity) {
        viewModelScope.launch {
            val isPremium = isPremiumUserFlow.first()
            updateChannelDetails(channelDetailsEntity, isPremium)
        }
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

    override fun onOpenPremiumPromo() {
        viewModelScope.launch {
            if (state.value.inLiveChat) {
                state.value = state.value.copy(inLiveChat = false)
                emitVmEvent(VideoDetailsEvent.HideKeyboard)
                emitVmEvent(VideoDetailsEvent.CloseLiveChat)
                delay(RumbleConstants.LIVE_CHAT_ANIMATION_DURATION.toLong())
            } else if (state.value.inComments) {
                state.value = state.value.copy(inComments = false)
                emitVmEvent(VideoDetailsEvent.HideKeyboard)
                emitVmEvent(VideoDetailsEvent.CloseComments)
                delay(RumbleConstants.LIVE_CHAT_ANIMATION_DURATION.toLong())
            }

            state.value = state.value.copy(
                inComments = false,
                inLiveChat = false,
                lastBottomSheet = LastBottomSheet.PREMIUM_PROMO
            )
            emitVmEvent(VideoDetailsEvent.OpenPremiumPromo)
        }
    }

    override fun onClosePremiumPromo() {
        state.value = state.value.copy(
            lastBottomSheet = LastBottomSheet.NONE
        )
        emitVmEvent(VideoDetailsEvent.ClosePremiumPromo)
    }

    private fun handleError(errorMessage: String? = null) {
        emitVmEvent(VideoDetailsEvent.VideoDetailsError(errorMessage))
        state.value = state.value.copy(
            isLoading = false,
            currentComment = "",
            commentToReply = null,
            currentCursorPosition = 0,
        )
    }

    private suspend fun loadContent(videoId: Long) {
        startVideoLoadTimeTrace(videoId)
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

    private fun startVideoLoadTimeTrace(videoId: Long) {
        videoStarted = false
        preRollAdStarted = false
        videoLoadTimeTrace = videoLoadTimeTraceStartUseCase(videoId.toString())
        videoLoadTimeTrace?.let {
            videoLoadTimeTraceHasPreRollUseCase(it, false)
        }
    }

    private fun stopVideoLoadTimeTrace() {
        videoLoadTimeTrace?.let {
            videoLoadTimeTraceStopUseCase(it)
        }
        videoLoadTimeTrace = null
    }

    private fun stopVideoLoadTimeTracePlayedPreRoll() {
        videoLoadTimeTrace?.let {
            videoLoadTimeTracePlayedPreRollUseCase(it)
        }
        videoLoadTimeTrace = null
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
            layoutState = CollapsableLayoutState.None,
            isLoggedIn = state.value.isLoggedIn,
            userProfile = state.value.userProfile,
            inLiveChat = false,
            inComments = false,
            displayPremiumOnlyContent = false,
            hasPremiumRestriction = false,
            hasLiveGateRestriction = false,
        )
        orientationEventListener.disable()
        viewModelScope.launch { sessionManager.saveVideoDetailsCollapsed(true) }
    }

    private fun emitVmEvent(event: VideoDetailsEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    private suspend fun initVideoState(videoEntity: VideoEntity) {
        val player = initVideoPlayerSourceUseCase(
            videoId = videoEntity.id,
            saveLastPosition = saveLastPositionUseCase::invoke,
            liveVideoReport = { videoId, result ->
                onLiveVideoReport(videoId, result.statusCode, result.watchingNow)
            },
            screenId = videoDetailsScreen,
            onVideoSizeDefined = ::onVideoSizeDefined,
            autoplay = videoEntity.hasLiveGate.not() || hasPremiumRestrictionUseCase(videoEntity).not(),
            onNextVideo = ::onNextVideo,
            showAds = sessionManager.isPremiumUserFlow.first().not(),
            sendInitialPlaybackEvent = {
                videoStarted = true
                if (!preRollAdStarted) {
                    stopVideoLoadTimeTrace()
                }
            },
            onPremiumCountdownFinished = {
                onEnforceLiveGatePremiumRestriction()
            },
            onVideoReady = { duration, _ ->
                checkLiveGateRestrictions(videoEntity, duration)
            },
            preRollAdLoadingEvent = {
                videoLoadTimeTrace?.let {
                    videoLoadTimeTraceHasPreRollUseCase(it, true)
                }
            },
            preRollAdStartedEvent = {
                preRollAdStarted = true
                if (!videoStarted) {
                    stopVideoLoadTimeTracePlayedPreRoll()
                }
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
            startVideoLoadTimeTrace(videoId)
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
                        updatedRelatedVideoList = updatedRelatedVideoList,
                        onPremiumCountdownFinished = {
                            onEnforceLiveGatePremiumRestriction()
                        },
                        onVideoReady = { duration, _ ->
                            state.value.videoEntity?.let { videoEntity ->
                                checkLiveGateRestrictions(videoEntity, duration)
                            }
                        }
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
                watchingNow = it.watchingNow,
                hasPremiumRestriction = hasPremiumRestrictionUseCase(it) && (it.hasLiveGate.not() || it.livestreamStatus == LiveStreamStatus.LIVE),
                chatMode = defineLiveChatModeUseCase(
                    it,
                    it.liveGateEntity?.chatMode ?: ChatMode.Free
                ),
                repostedByUser = it.userRepost?.video?.id == videoId
            )
            onVideoPlayerImpression()
            initLiveChat(it)
            if (state.value.lastBottomSheet == LastBottomSheet.COMMENTS && state.value.inComments)
                onOpenComments()
        } ?: run {
            state.value = state.value.copy(videoEntity = null)
            handleError()
        }
    }

    private fun checkLiveGateRestrictions(videoEntity: VideoEntity, actualDuration: Long) {
        viewModelScope.launch(errorHandler) {
            if (hasPremiumRestrictionUseCase(videoEntity)) {
                if (videoEntity.hasLiveGate && videoEntity.livestreamStatus != LiveStreamStatus.LIVE) {
                    state.value = state.value.copy(
                        displayPremiumOnlyContent = true,
                        chatMode = defineLiveChatModeUseCase(
                            videoEntity,
                            videoEntity.liveGateEntity?.chatMode ?: ChatMode.Free
                        )
                    )
                    state.value.rumblePlayer?.playVideo()
                    videoEntity.liveGateEntity?.let {
                        state.value.rumblePlayer?.let { rumblePlayer ->
                            startPremiumPreviewCountdownUseCase(rumblePlayer, actualDuration)
                        }
                    }
                } else if (videoEntity.hasLiveGate) {
                    state.value = state.value.copy(
                        hasLiveGateRestriction = true,
                        chatMode = defineLiveChatModeUseCase(
                            videoEntity,
                            videoEntity.liveGateEntity?.chatMode ?: ChatMode.Free
                        )
                    )
                }
            } else {
                state.value = state.value.copy(
                    hasLiveGateRestriction = false,
                    displayPremiumOnlyContent = false
                )
            }
        }
    }

    private suspend fun fetchChannelDetails(channelId: String?) {
        channelId?.let {
            val result = viewModelScope.async { getChannelDataUseCase(it).getOrNull() }
            val isPremium = isPremiumUserFlow.first()
            result.await()?.let { channel ->
                updateChannelDetails(channel, isPremium)
            }
        }
    }

    private fun updateChannelDetails(
        channelDetailsEntity: CreatorEntity,
        isPremium: Boolean
    ) {
        val showPremiumFlow = channelDetailsEntity.localsCommunityEntity?.showPremiumFlow ?: false
        state.value = state.value.copy(
            channelDetailsEntity = channelDetailsEntity,
            followStatus = FollowStatus(
                channelId = channelDetailsEntity.channelId,
                followed = channelDetailsEntity.followed,
                isBlocked = channelDetailsEntity.blocked
            ),
            showJoinButton = (showPremiumFlow && isPremium.not()) || showPremiumFlow.not()
        )
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
        val currentScreenOrientation = state.value.screenOrientation
        if (orientation < 0 ||
            (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && isLandscape(
                currentScreenOrientation
            )) ||
            (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && isPortrait(
                currentScreenOrientation
            ))
        ) {
            return
        }
        state.value = state.value.copy(screenOrientationLocked = false)
        emitVmEvent(VideoDetailsEvent.SetOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED))
    }

    private fun isLandscape(orientation: Int) = orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun isPortrait(orientation: Int) = orientation == Configuration.ORIENTATION_PORTRAIT

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
                currentComment = "",
                currentCursorPosition = 0,
            )
            emitVmEvent(VideoDetailsEvent.InitLiveChat(videoEntity))
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