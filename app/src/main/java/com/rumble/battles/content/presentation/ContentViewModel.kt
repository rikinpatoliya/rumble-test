package com.rumble.battles.content.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.rumble.analytics.FollowTapEvent
import com.rumble.analytics.PremiumPromoCloseEvent
import com.rumble.analytics.PremiumPromoGetButtonTapEvent
import com.rumble.analytics.PremiumPromoViewEvent
import com.rumble.analytics.UnfollowCancelEvent
import com.rumble.analytics.UnfollowConfirmedEvent
import com.rumble.analytics.UnfollowTapEvent
import com.rumble.battles.BuildConfig
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.landing.AppUpdateHandler
import com.rumble.battles.landing.AppUpdateState
import com.rumble.battles.landing.RumbleActivityAlertReason
import com.rumble.battles.library.presentation.playlist.AddToPlayListHandler
import com.rumble.battles.library.presentation.playlist.AddToPlayListState
import com.rumble.battles.library.presentation.playlist.EditPlayListHandler
import com.rumble.battles.library.presentation.playlist.EditPlayListScreenUIState
import com.rumble.battles.library.presentation.playlist.PlayListAction
import com.rumble.battles.library.presentation.playlist.PlayListOptionsHandler
import com.rumble.battles.library.presentation.playlist.PlayListSettingsBottomSheetDialog
import com.rumble.battles.library.presentation.playlist.UpdatePlaylist
import com.rumble.battles.onboarding.presentation.OnboardingHandler
import com.rumble.battles.premium.presentation.PremiumSubscriptionHandler
import com.rumble.battles.subscriptions.presentation.NotificationsHandler
import com.rumble.battles.subscriptions.presentation.SortFollowingHandler
import com.rumble.battles.subscriptions.presentation.SubscriptionHandler
import com.rumble.battles.videos.presentation.VideoOptionsHandler
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.billing.model.PurchaseHandler
import com.rumble.domain.billing.model.PurchaseResult
import com.rumble.domain.billing.model.RumblePurchaseUpdateListener
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.camera.domain.usecases.GetUploadNotificationVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoEntityUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserUploadChannelsUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.common.domain.domainmodel.AddToPlaylistResult
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.domainmodel.RemoveFromPlaylistResult
import com.rumble.domain.common.domain.usecase.AddToPlaylistUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.common.domain.usecase.OpenPlayStoreUseCase
import com.rumble.domain.common.domain.usecase.RemoveFromPlaylistUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntityWithOptions
import com.rumble.domain.feed.domain.domainmodel.video.PlayListUserEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.landing.usecases.ShouldForceNewAppVersionUseCase
import com.rumble.domain.landing.usecases.ShouldSuggestNewAppVersionUseCase
import com.rumble.domain.library.domain.model.ClearWatchHistoryResult
import com.rumble.domain.library.domain.model.DeletePlayListResult
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.domain.library.domain.model.UpdatePlayListResult
import com.rumble.domain.library.domain.usecase.AddPlayListUseCase
import com.rumble.domain.library.domain.usecase.CanSaveVideoToPlayListUseCase
import com.rumble.domain.library.domain.usecase.ClearWatchHistoryUseCase
import com.rumble.domain.library.domain.usecase.DeletePlayListUseCase
import com.rumble.domain.library.domain.usecase.EditPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetLibraryPlayListsPagedUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListContainVideoUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.notifications.model.NotificationDataManager
import com.rumble.domain.onboarding.domain.domainmodel.DoNotShow
import com.rumble.domain.onboarding.domain.domainmodel.None
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingPopupType
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingViewState
import com.rumble.domain.onboarding.domain.domainmodel.ShowOnboarding
import com.rumble.domain.onboarding.domain.domainmodel.ShowOnboardingPopups
import com.rumble.domain.onboarding.domain.usecase.FeedOnboardingViewUseCase
import com.rumble.domain.onboarding.domain.usecase.SaveFeedOnboardingUseCase
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionParams
import com.rumble.domain.premium.domain.domainmodel.SubscriptionResult
import com.rumble.domain.premium.domain.usecases.BuildPremiumSubscriptionParamsUseCase
import com.rumble.domain.premium.domain.usecases.FetchPremiumSubscriptionListUseCase
import com.rumble.domain.premium.domain.usecases.FetchUserInfoUseCase
import com.rumble.domain.premium.domain.usecases.PostSubscriptionProofUseCase
import com.rumble.domain.premium.domain.usecases.SendPremiumPurchasedEventUseCase
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.sort.NotificationFrequency
import com.rumble.domain.sort.SortFollowingType
import com.rumble.domain.video.domain.usecases.GetVideoOptionsUseCase
import com.rumble.domain.video.model.VideoOption
import com.rumble.network.queryHelpers.PlayListType
import com.rumble.network.queryHelpers.SubscriptionSource
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ContentHandler : VideoOptionsHandler, AddToPlayListHandler, EditPlayListHandler,
    PlayListOptionsHandler, SubscriptionHandler, NotificationsHandler, PremiumSubscriptionHandler,
    PurchaseHandler, SortFollowingHandler, OnboardingHandler, AppUpdateHandler {
    val userNameFlow: Flow<String>
    val userPictureFlow: Flow<String>
    val bottomSheetUiState: StateFlow<BottomSheetUIState>
    val eventFlow: Flow<ContentScreenVmEvent>
    val uploadsToNotifyAbout: Flow<List<UploadVideoEntity>>
    val videoDetailsState: State<VideoDetailsState>
    val userUIState: StateFlow<UserUIState>

    fun updateColorMode(colorMode: ColorMode)
    fun updateBottomSheetUiState(data: BottomSheetContent)
    fun notifyUserAboutUploads(list: List<UploadVideoEntity>)
    fun onDeepLinkNavigated()
    fun onContentResumed()
    fun isPremiumUser(): Boolean
    fun onOpenAuthMenu()
    fun onError(errorMessage: String?, withPadding: Boolean = true)
    fun onShowSnackBar(messageId: Int, titleId: Int? = null, withPadding: Boolean = true)
    fun onShowSnackBar(message: String, title: String? = null, withPadding: Boolean = true)
    fun onOpenVideoDetails(videoId: Long, playListId: String? = null, shuffle: Boolean? = null)
    fun onCloseVideoDetails()
    fun onNavigateHome()
    fun onNavigateHomeAfterSignedOut()
    fun scrollToTop(index: Int)
}

data class VideoDetailsState(
    val visible: Boolean = false,
    val collapsed: Boolean = false,
    val isTablet: Boolean = false,
)

data class BottomSheetUIState(val data: BottomSheetContent)
data class UserUIState(
    val userId: String = "",
    val userName: String = "",
    val userPicture: String = "",
    val isPremiumUser: Boolean = false,
    val isLoggedIn: Boolean = false
)

sealed class BottomSheetContent {
    data object HideBottomSheet : BottomSheetContent()
    data object ChangeAppearance : BottomSheetContent()
    data class UserUploadChannelSwitcher(val channels: List<UserUploadChannelEntity>) :
        BottomSheetContent()

    data class ChannelNotificationsSheet(val channelDetailsEntity: ChannelDetailsEntity) :
        BottomSheetContent()

    data class MoreUploadOptionsSheet(
        val title: String? = null,
        val subtitle: String? = null,
        val bottomSheetItems: List<BottomSheetItem>
    ) : BottomSheetContent()

    data class MoreVideoOptionsSheet(
        val videoEntity: VideoEntity,
        val videoOptions: List<VideoOption>
    ) : BottomSheetContent()

    data class AddToPlayList(val videoEntityId: Long) : BottomSheetContent()
    data class CreateNewPlayList(val videoEntityId: Long) : BottomSheetContent()

    data class MorePlayListOptionsSheet(
        val playListEntityWithOptions: PlayListEntityWithOptions,
    ) : BottomSheetContent()

    data object PlayListSettingsSheet : BottomSheetContent()

    data object PremiumPromo : BottomSheetContent()
    data object PremiumOptions : BottomSheetContent()
    data object PremiumSubscription : BottomSheetContent()
    data class SortFollowingSheet(val sortFollowingType: SortFollowingType) : BottomSheetContent()
    data object AuthMenu : BottomSheetContent()
}

sealed class ContentScreenVmEvent {
    data object HideBottomSheetEvent : ContentScreenVmEvent()
    data object ShowBottomSheetEvent : ContentScreenVmEvent()
    data object HideAlertDialogEvent : ContentScreenVmEvent()
    data class ShowAlertDialogEvent(
        val reason: RumbleActivityAlertReason
    ) : ContentScreenVmEvent()

    data class UserUploadNotification(
        val uploadTitle: String,
        val success: Boolean = false,
        val message: String? = null
    ) : ContentScreenVmEvent()

    data object NavigateHome : ContentScreenVmEvent()
    data object NavigateHomeAfterSignOut : ContentScreenVmEvent()
    data object ScrollToTop : ContentScreenVmEvent()
    data class NavigateToChannelDetails(val channelId: String) : ContentScreenVmEvent()
    data class Error(val errorMessage: String? = null, val withPadding: Boolean = false) : ContentScreenVmEvent()
    data class ShowSnackBarMessage(val messageId: Int, val titleId: Int? = null, val withPadding: Boolean = false) : ContentScreenVmEvent()
    data class ShowSnackBarMessageString(val message: String, val title: String? = null, val withPadding: Boolean = true) : ContentScreenVmEvent()
    data class ChannelSubscriptionUpdated(val channelDetailsEntity: ChannelDetailsEntity) :
        ContentScreenVmEvent()

    data class ChannelNotificationsUpdated(val channelDetailsEntity: ChannelDetailsEntity) :
        ContentScreenVmEvent()

    data object VideoAlreadyInPlayList : ContentScreenVmEvent()
    data class VideoAddedToPlayList(val withPadding: Boolean) : ContentScreenVmEvent()
    data class VideoRemovedFromPlayList(val withPadding: Boolean) : ContentScreenVmEvent()
    data object PlayListDeleted : ContentScreenVmEvent()
    data class PlayListUpdated(val playListEntity: PlayListEntity) : ContentScreenVmEvent()
    data class PlayListCreated(val playListEntity: PlayListEntity) : ContentScreenVmEvent()
    data object WatchHistoryCleared : ContentScreenVmEvent()
    data class StartPremiumPurchase(
        val billingClient: BillingClient,
        val billingParams: BillingFlowParams
    ) : ContentScreenVmEvent()

    data class ExpendVideoDetails(
        val videoId: Long,
        val playListId: String?,
        val shuffle: Boolean?
    ) : ContentScreenVmEvent()

    data class SortFollowingTypeUpdated(val sortFollowingType: SortFollowingType) :
        ContentScreenVmEvent()
}

private const val TAG = "ContentViewModel"

@HiltViewModel
class ContentViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val feedOnboardingViewUseCase: FeedOnboardingViewUseCase,
    private val updateUploadVideoEntityUseCase: UpdateUploadVideoEntityUseCase,
    private val saveFeedOnboardingUseCase: SaveFeedOnboardingUseCase,
    private val getVideoOptionsUseCase: GetVideoOptionsUseCase,
    private val shareUseCase: ShareUseCase,
    private val getPlayListUseCase: GetPlayListUseCase,
    private val getLibraryPlayListsPagedUseCase: GetLibraryPlayListsPagedUseCase,
    private val getPlayListContainVideoUseCase: GetPlayListContainVideoUseCase,
    private val canSaveVideoToPlayListUseCase: CanSaveVideoToPlayListUseCase,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val addToPlaylistUseCase: AddToPlaylistUseCase,
    private val editPlayListUseCase: EditPlayListUseCase,
    private val addPlayListUseCase: AddPlayListUseCase,
    private val getUserUploadChannelsUseCase: GetUserUploadChannelsUseCase,
    private val deletePlayListUseCase: DeletePlayListUseCase,
    private val clearWatchHistoryUseCase: ClearWatchHistoryUseCase,
    private val updateChannelSubscriptionUseCase: UpdateChannelSubscriptionUseCase,
    private val updateNotificationsUseCase: UpdateChannelNotificationsUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    getUploadNotificationVideoUseCase: GetUploadNotificationVideoUseCase,
    private val fetchPremiumSubscriptionListUseCase: FetchPremiumSubscriptionListUseCase,
    private val buildPremiumSubscriptionParamsUseCase: BuildPremiumSubscriptionParamsUseCase,
    private val billingClient: BillingClient,
    purchaseUpdateListener: RumblePurchaseUpdateListener,
    private val postSubscriptionProofUseCase: PostSubscriptionProofUseCase,
    private val sendPremiumPurchasedEventUseCase: SendPremiumPurchasedEventUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val notificationDataManager: NotificationDataManager,
    private val developModeUseCase: IsDevelopModeUseCase,
    private val shouldSuggestNewAppVersionUseCase: ShouldSuggestNewAppVersionUseCase,
    private val shouldForceNewAppVersionUseCase: ShouldForceNewAppVersionUseCase,
    private val openPlayStoreUseCase: OpenPlayStoreUseCase,
    private val deviceType: DeviceType,
) : ViewModel(), ContentHandler {
    override val userNameFlow: Flow<String> = sessionManager.userNameFlow
    override val userPictureFlow: Flow<String> = sessionManager.userPictureFlow
    override val bottomSheetUiState =
        MutableStateFlow(BottomSheetUIState(BottomSheetContent.HideBottomSheet))
    override val eventFlow: MutableSharedFlow<ContentScreenVmEvent> = MutableSharedFlow()
    override val uploadsToNotifyAbout: Flow<List<UploadVideoEntity>> =
        getUploadNotificationVideoUseCase()
    override val userUIState = MutableStateFlow(UserUIState())
    override var subscriptionList: List<PremiumSubscriptionData> = emptyList()
    override val onboardingViewState: MutableStateFlow<OnboardingViewState> = MutableStateFlow(None)
    override val popupsListIndex = MutableStateFlow(0)
    override val discoverIconLocationState = MutableStateFlow(Offset.Zero)
    override val searchIconLocationState = MutableStateFlow(Offset.Zero)
    override val followingIconLocationState = MutableStateFlow(Offset.Zero)
    override val libraryIconLocationState = MutableStateFlow(Offset.Zero)
    override val appUpdateState: MutableStateFlow<AppUpdateState> =
        MutableStateFlow(AppUpdateState())
    override val videoDetailsState: MutableState<VideoDetailsState> = mutableStateOf(
        VideoDetailsState(
            isTablet = deviceType == DeviceType.Tablet
        )
    )
    override val addToPlayListState = MutableStateFlow(AddToPlayListState())
    override val updatedPlaylist: MutableStateFlow<UpdatePlaylist?> = MutableStateFlow(null)
    override val editPlayListState = MutableStateFlow(EditPlayListScreenUIState())
    override val playListSettingsState =
        MutableStateFlow<PlayListSettingsBottomSheetDialog>(PlayListSettingsBottomSheetDialog.DefaultPopupState)

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    private var purchaseInProgress: Boolean = false
    private var currentSubscriptionParams: PremiumSubscriptionParams = PremiumSubscriptionParams()

    init {
        loadDeepLink()
        observeUser()
        purchaseUpdateListener.subscribeToPurchaseUpdate(this)
        viewModelScope.launch(errorHandler) {
            subscriptionList = fetchPremiumSubscriptionListUseCase() ?: emptyList()
        }
        viewModelScope.launch {
            sessionManager.videoDetailsCollapsedFlow.distinctUntilChanged().collectLatest {
                videoDetailsState.value = videoDetailsState.value.copy(
                    collapsed = it
                )
            }
        }
    }

    private fun checkAppUpdates() {
        val appUpdateSuggested = shouldSuggestNewAppVersionUseCase(BuildConfig.VERSION_NAME)
        val forceUpdateRequired = shouldForceNewAppVersionUseCase(BuildConfig.VERSION_NAME)
        appUpdateState.update {
            it.copy(
                appUpdateSuggested = appUpdateSuggested,
                forceUpdateRequired = forceUpdateRequired
            )
        }
    }

    override fun onSuggestedUpdateDismissed() {
        appUpdateState.update {
            it.copy(
                appUpdateSuggested = false,
            )
        }
        saveNewVersionDisplayTimeStamp()
    }

    override fun onGoToStore() {
        appUpdateState.update {
            it.copy(
                appUpdateSuggested = false,
            )
        }
        saveNewVersionDisplayTimeStamp()
        openPlayStoreUseCase()
    }

    private fun saveNewVersionDisplayTimeStamp() {
        viewModelScope.launch {
            userPreferenceManager.saveNewVersionDisplayTimeStamp(System.currentTimeMillis())
        }
    }

    private fun observeUser() {
        viewModelScope.launch(errorHandler) {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest {
                userUIState.update { userState ->
                    userState.copy(
                        isLoggedIn = it.isNotEmpty()
                    )
                }
                if (it.isNotEmpty()) {
                    initAdditionalInfoForLoggedInUser()
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            sessionManager.userIdFlow.distinctUntilChanged().collect { id ->
                userUIState.update {
                    it.copy(userId = id)
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            sessionManager.userNameFlow.distinctUntilChanged().collect { name ->
                userUIState.update {
                    it.copy(userName = name)
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            sessionManager.userPictureFlow.distinctUntilChanged().collect { picture ->
                userUIState.update {
                    it.copy(userPicture = picture)
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            sessionManager.isPremiumUserFlow.distinctUntilChanged().collect { isPremium ->
                userUIState.update {
                    it.copy(isPremiumUser = isPremium)
                }
            }
        }
    }

    private suspend fun initAdditionalInfoForLoggedInUser() {
        fetchWatchLaterPlaylist()
        getAvailablePlayLists()
        fetchUserUploadChannels()
        observeNotificationData()
    }

    override fun onContentResumed() {
        viewModelScope.launch(errorHandler) {
            fetchUserInfoUseCase()
            checkAppUpdates()
        }
    }

    override fun isPremiumUser() = userUIState.value.isPremiumUser

    override fun updateColorMode(colorMode: ColorMode) {
        viewModelScope.launch(errorHandler) {
            userPreferenceManager.saveColorMode(colorMode)
        }
    }

    override fun updateBottomSheetUiState(data: BottomSheetContent) {
        viewModelScope.launch(errorHandler) {
            bottomSheetUiState.update {
                it.copy(data = data)
            }
            if (data is BottomSheetContent.HideBottomSheet)
                emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
            else
                emitVmEvent(ContentScreenVmEvent.ShowBottomSheetEvent)
        }
    }

    override fun notifyUserAboutUploads(list: List<UploadVideoEntity>) {
        viewModelScope.launch(errorHandler) {
            list.forEach { uploadVideoEntity ->
                emitVmEvent(
                    ContentScreenVmEvent.UserUploadNotification(
                        uploadTitle = uploadVideoEntity.title,
                        success = uploadVideoEntity.status == UploadStatus.UPLOADING_SUCCEEDED,
                        message = uploadVideoEntity.statusNotificationMessage,
                    )
                )
                updateUploadVideoEntityUseCase(
                    uploadVideoEntity.copy(
                        userWasNotifiedAboutStatus = true
                    )
                )
            }
        }
    }

    override fun onDeepLinkNavigated() {
        viewModelScope.launch { notificationDataManager.saveDeepLinkChannelId("") }
    }

    override fun onMoreVideoOptionsClicked(videoEntity: VideoEntity, playListId: String) {
        viewModelScope.launch(errorHandler) {
            updateBottomSheetUiState(
                BottomSheetContent.MoreVideoOptionsSheet(
                    videoEntity = videoEntity,
                    getVideoOptionsUseCase(
                        videoEntityId = videoEntity.id,
                        playListId = playListId
                    )
                )
            )
        }
    }

    override fun onSaveToPlayList(videoId: Long) {
        if (userUIState.value.isLoggedIn) {
            viewModelScope.launch(errorHandler) {
                getAvailablePlayLists(videoId)
                updateBottomSheetUiState(
                    BottomSheetContent.AddToPlayList(videoId)
                )
            }
        } else {
            onOpenAuthMenu()
        }
    }

    override fun onSaveToWatchLater(videoId: Long) {
        if (userUIState.value.isLoggedIn) {
            addVideoToPlayList(PlayListType.WATCH_LATER.toString(), videoId)
        } else {
            onOpenAuthMenu()
        }
    }

    override fun onRemoveFromPlayList(playListId: String, videoId: Long) {
        removeVideoFromPlayList(playListId, videoId)
    }

    override fun onShare(videUrl: String) {
        shareUseCase(videUrl)
    }

    // region AddToPlayListHandler
    private fun getAvailablePlayLists(videoId: Long? = null) {
        val availablePlayLists = getLibraryPlayListsPagedUseCase(videoId)
        addToPlayListState.value = addToPlayListState.value.copy(
            availablePlayLists = availablePlayLists
        )
    }

    private suspend fun fetchWatchLaterPlaylist() {
        when (val result = getPlayListUseCase(PlayListType.WATCH_LATER.toString())) {
            is PlayListResult.Failure -> emitVmEvent(ContentScreenVmEvent.Error())
            is PlayListResult.Success -> addToPlayListState.value =
                addToPlayListState.value.copy(watchLaterPlayList = result.playList)
        }
    }

    override fun onCreateNewPlayList(videoId: Long) {
        updateBottomSheetUiState(
            BottomSheetContent.CreateNewPlayList(videoId)
        )
        editPlayListState.update {
            it.copy(
                editPlayListEntity = PlayListEntity(
                    playListOwnerId = userUIState.value.userId,
                    playListUserEntity = PlayListUserEntity(
                        id = userUIState.value.userId,
                        username = userUIState.value.userName,
                        thumbnail = userUIState.value.userPicture
                    )
                )
            )
        }
    }

    override fun getIsVideoInPlayList(playListEntity: PlayListEntity, videoId: Long): Boolean =
        getPlayListContainVideoUseCase(playListEntity, videoId)

    override fun onToggleVideoInPlayList(inPlayList: Boolean, playListId: String, videoId: Long) {
        if (inPlayList)
            removeVideoFromPlayList(playListId, videoId, true)
        else
            addVideoToPlayList(playListId, videoId, true)
    }

    override fun canSaveToPlayList(entity: PlayListEntity): Boolean =
        canSaveVideoToPlayListUseCase(
            entity,
            userUIState.value.userId,
            editPlayListState.value.userUploadChannels
        )

    private fun removeVideoFromPlayList(
        playListId: String,
        videoId: Long,
        withPadding: Boolean = false
    ) {
        viewModelScope.launch(errorHandler) {
            when (removeFromPlaylistUseCase(playListId, videoId)) {
                is RemoveFromPlaylistResult.Failure ->
                    emitVmEvent(ContentScreenVmEvent.Error())

                RemoveFromPlaylistResult.Success, RemoveFromPlaylistResult.FailureVideoNotInPlaylist -> {
                    updatedPlaylist.tryEmit(
                        UpdatePlaylist.VideoRemovedFromPlaylist(
                            playListId,
                            videoId
                        )
                    )
                    emitVmEvent(ContentScreenVmEvent.VideoRemovedFromPlayList(withPadding = withPadding))
                }
            }
            if (playListId == PlayListType.WATCH_LATER.toString()) {
                fetchWatchLaterPlaylist()
            }
        }
    }

    private fun addVideoToPlayList(
        playListId: String,
        videoId: Long,
        withPadding: Boolean = false
    ) {
        viewModelScope.launch(errorHandler) {
            when (val result = addToPlaylistUseCase(playListId, videoId)) {
                is AddToPlaylistResult.Failure ->
                    emitVmEvent(ContentScreenVmEvent.Error())

                is AddToPlaylistResult.Success -> {
                    updatedPlaylist.tryEmit(
                        UpdatePlaylist.VideoAddedToPlaylist(
                            playlistId = playListId,
                            playlistVideoEntity = result.playlistVideoEntity
                        )
                    )
                    emitVmEvent(ContentScreenVmEvent.VideoAddedToPlayList(withPadding = withPadding))
                }

                AddToPlaylistResult.FailureAlreadyInPlaylist ->
                    emitVmEvent(ContentScreenVmEvent.VideoAlreadyInPlayList)
            }
            if (playListId == PlayListType.WATCH_LATER.toString()) {
                fetchWatchLaterPlaylist()
            }
        }
    }
    // endregion

    // region EditPlayListHandler
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
        emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
    }

    override fun onOpenChannelSelectionBottomSheet() {
        editPlayListState.value.editPlayListEntity?.let {
            playListSettingsState.value =
                PlayListSettingsBottomSheetDialog.PlayListChannelSelection(
                    playListEntity = it,
                    userUploadChannels = editPlayListState.value.userUploadChannels
                )
        }
    }

    override fun onOpenPlayListVisibilitySelectionBottomSheet() {
        editPlayListState.value.editPlayListEntity?.let {
            playListSettingsState.value =
                PlayListSettingsBottomSheetDialog.PlayListVisibilitySelection(it)
        }
    }

    override fun onSavePlayListSettings(playListAction: PlayListAction, videoId: Long?) {
        if (isValidBeforeSave()) {
            viewModelScope.launch(errorHandler) {
                if (playListAction == PlayListAction.Edit) {
                    editPlayListState.value.editPlayListEntity?.let { entity ->
                        when (val result = editPlayListUseCase(entity)) {
                            is UpdatePlayListResult.Failure -> {
                                emitVmEvent(ContentScreenVmEvent.Error())
                            }

                            is UpdatePlayListResult.Success -> {
                                editPlayListState.update {
                                    it.copy(
                                        editPlayListEntity = null
                                    )
                                }
                                emitVmEvent(ContentScreenVmEvent.PlayListUpdated(result.playListEntity))
                            }

                            else -> {}
                        }
                    }
                } else if (playListAction == PlayListAction.Create) {
                    editPlayListState.value.editPlayListEntity?.let { entity ->
                        when (val result = addPlayListUseCase(entity)) {
                            is UpdatePlayListResult.Failure -> {
                                emitVmEvent(ContentScreenVmEvent.Error())
                            }

                            is UpdatePlayListResult.Success -> {
                                editPlayListState.update {
                                    it.copy(
                                        editPlayListEntity = null
                                    )
                                }
                                videoId?.let {
                                    addVideoToPlayList(result.playListEntity.id, it)
                                }
                                emitVmEvent(ContentScreenVmEvent.PlayListCreated(result.playListEntity))
                            }

                            else -> {}
                        }
                    }
                }
                emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
            }
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
    // endregion

    // region PlayListOptionsHandler
    override fun onMorePlayListOptions(playListEntityWithOptions: PlayListEntityWithOptions) {
        updateBottomSheetUiState(
            BottomSheetContent.MorePlayListOptionsSheet(
                playListEntityWithOptions
            )
        )
    }

    override fun onConfirmDeleteWatchHistory() {
        emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
        emitVmEvent(
            ContentScreenVmEvent.ShowAlertDialogEvent(
                RumbleActivityAlertReason.DeleteWatchHistoryConfirmationReason
            )
        )
    }

    override fun onDeleteWatchHistory() {
        emitVmEvent(ContentScreenVmEvent.HideAlertDialogEvent)
        viewModelScope.launch(errorHandler) {
            when (clearWatchHistoryUseCase()) {
                is ClearWatchHistoryResult.Failure -> {
                    emitVmEvent(ContentScreenVmEvent.ShowSnackBarMessage(messageId = R.string.general_error_message))
                }

                is ClearWatchHistoryResult.Success -> {
                    emitVmEvent(ContentScreenVmEvent.WatchHistoryCleared)
                }
            }
        }
    }

    override fun onDeletePlayList(playListId: String) {
        emitVmEvent(ContentScreenVmEvent.HideAlertDialogEvent)
        viewModelScope.launch(errorHandler) {
            when (deletePlayListUseCase(playListId)) {
                is DeletePlayListResult.Failure -> {
                    emitVmEvent(ContentScreenVmEvent.ShowSnackBarMessage(messageId = R.string.general_error_message))
                }

                is DeletePlayListResult.Success -> {
                    emitVmEvent(ContentScreenVmEvent.PlayListDeleted)
                }
            }
        }
    }

    override fun onPlayListSettings(playListEntity: PlayListEntity) {
        editPlayListState.update {
            it.copy(
                editPlayListEntity = playListEntity.copy()
            )
        }
        updateBottomSheetUiState(BottomSheetContent.PlayListSettingsSheet)
    }

    override fun onConfirmDeletePlayList(playListId: String) {
        emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
        emitVmEvent(
            ContentScreenVmEvent.ShowAlertDialogEvent(
                RumbleActivityAlertReason.DeletePlayListConfirmationReason(
                    playListId
                )
            )
        )
    }
    // endregion

    // region SubscriptionHandler
    override fun onUpdateSubscription(
        channel: ChannelDetailsEntity?,
        action: UpdateChannelSubscriptionAction
    ) {
        if (userUIState.value.isLoggedIn) {
            channel?.let {
                if (action == UpdateChannelSubscriptionAction.UNSUBSCRIBE) {
                    analyticsEventUseCase(UnfollowTapEvent)
                    emitVmEvent(
                        ContentScreenVmEvent.ShowAlertDialogEvent(
                            RumbleActivityAlertReason.UnfollowConfirmationReason(
                                channel
                            )
                        )
                    )
                } else {
                    analyticsEventUseCase(FollowTapEvent)
                    updateSubscription(channel = channel, action = action)
                }
            }
        } else {
            onOpenAuthMenu()
        }
    }

    override fun onUnfollow(channel: ChannelDetailsEntity) {
        analyticsEventUseCase(UnfollowConfirmedEvent)
        emitVmEvent(ContentScreenVmEvent.HideAlertDialogEvent)
        updateSubscription(channel = channel, action = UpdateChannelSubscriptionAction.UNSUBSCRIBE)
    }

    override fun onCancelUnfollow() {
        analyticsEventUseCase(UnfollowCancelEvent)
        emitVmEvent(ContentScreenVmEvent.HideAlertDialogEvent)
    }

    override fun onUpdateSubscriptionStatus(channel: ChannelDetailsEntity) {
        emitVmEvent(ContentScreenVmEvent.ChannelSubscriptionUpdated(channel))
    }

    override fun onUpdateEmailFrequency(
        channelDetailsEntity: ChannelDetailsEntity,
        notificationFrequency: NotificationFrequency
    ) {
        updateNotifications(
            channelDetailsEntity = channelDetailsEntity,
            notificationFrequency = notificationFrequency
        )
    }

    override fun onEnablePushForLivestreams(
        channelDetailsEntity: ChannelDetailsEntity,
        enable: Boolean
    ) {
        updateNotifications(
            channelDetailsEntity = channelDetailsEntity,
            pushNotificationsEnabled = enable
        )
    }

    override fun onEnableEmailNotifications(
        channelDetailsEntity: ChannelDetailsEntity,
        enable: Boolean
    ) {
        updateNotifications(
            channelDetailsEntity = channelDetailsEntity,
            emailNotificationsEnabled = enable
        )
    }

    override fun onSortFollowingSelected(sortFollowingType: SortFollowingType) {
        emitVmEvent(ContentScreenVmEvent.SortFollowingTypeUpdated(sortFollowingType))
    }
    // endregion

    // region OnboardingHandler
    override fun onSkipAll(popupsList: List<OnboardingPopupType>) {
        viewModelScope.launch {
            updateOnboardingState(popupsList)
        }
    }

    override fun onNext(
        popup: OnboardingPopupType,
        index: Int,
        popupsList: List<OnboardingPopupType>
    ) {
        viewModelScope.launch {
            saveFeedOnboardingUseCase(popup.onboardingType)
            if (index == popupsList.lastIndex) {
                updateOnboardingState(emptyList())
            } else {
                popupsListIndex.value = index + 1
            }
        }
    }

    override fun onBack(index: Int) {
        if (index != 0) popupsListIndex.value = index - 1
    }

    override fun onSearchIconMeasured(center: Offset) {
        searchIconLocationState.value = center
    }

    override fun onDiscoverIconMeasured(center: Offset) {
        discoverIconLocationState.value = center
    }

    override fun onFollowingIconMeasured(center: Offset) {
        followingIconLocationState.value = center
    }

    override fun onLibraryIconMeasured(center: Offset) {
        libraryIconLocationState.value = center
    }

    private suspend fun updateOnboardingState(popupsList: List<OnboardingPopupType>) {
        when (onboardingViewState.value) {
            ShowOnboarding -> saveFeedOnboardingUseCase(OnboardingType.FeedScreen)
            is ShowOnboardingPopups -> {
                if (popupsList.isNotEmpty()) {
                    saveFeedOnboardingUseCase(popupsList.map {
                        when (it) {
                            OnboardingPopupType.SearchRumble -> OnboardingType.SearchRumble
                            OnboardingPopupType.DiscoverContent -> OnboardingType.DiscoverContent
                            OnboardingPopupType.FollowingChannels -> OnboardingType.FollowingChannels
                            OnboardingPopupType.YourLibrary -> OnboardingType.YourLibrary
                        }
                    })
                }
            }

            None, DoNotShow -> {}
        }
        onboardingViewState.value = DoNotShow
    }
    // endregion

    override fun onShowPremiumPromo(videoId: Long?, source: SubscriptionSource?) {
        currentSubscriptionParams = currentSubscriptionParams.copy(videoId = videoId, source = source)
        analyticsEventUseCase(PremiumPromoViewEvent)
        updateBottomSheetUiState(BottomSheetContent.PremiumPromo)
    }

    override fun onClosePremiumPromo() {
        analyticsEventUseCase(PremiumPromoCloseEvent)
        viewModelScope.launch {
            userPreferenceManager.saveLastPremiumPromoTimeStamp(System.currentTimeMillis())
        }
    }

    override fun onGetPremium() {
        analyticsEventUseCase(PremiumPromoGetButtonTapEvent)
        onShowSubscriptionOptions(currentSubscriptionParams.videoId, currentSubscriptionParams.source)
    }

    override fun onShowSubscriptionOptions(videoId: Long?, source: SubscriptionSource?) {
        if (userUIState.value.isLoggedIn) {
            if (subscriptionList.isNotEmpty() or developModeUseCase()) {
                currentSubscriptionParams = currentSubscriptionParams.copy(
                    videoId = videoId,
                    source = source
                )
                updateBottomSheetUiState(BottomSheetContent.PremiumSubscription)
            } else {
                emitVmEvent(
                    ContentScreenVmEvent.ShowAlertDialogEvent(RumbleActivityAlertReason.SubscriptionNotAvailable)
                )
            }
        } else {
            onOpenAuthMenu()
        }
    }

    override fun onPurchaseFinished(result: PurchaseResult) {
        if (purchaseInProgress) {
            purchaseInProgress = false
            if (result is PurchaseResult.Success) {
                viewModelScope.launch(errorHandler) {
                    currentSubscriptionParams.subscriptionData?.let {
                        sendPremiumPurchasedEventUseCase(
                            it.type,
                            currentSubscriptionParams.videoId,
                            currentSubscriptionParams.source,
                        )
                    }
                    when (val proofResult = postSubscriptionProofUseCase(
                        result.purchaseToken,
                        currentSubscriptionParams.videoId,
                        currentSubscriptionParams.source
                    )) {
                        is SubscriptionResult.Success -> {
                            sessionManager.saveIsPremiumUser(true)
                            emitVmEvent(
                                ContentScreenVmEvent.ShowAlertDialogEvent(
                                    RumbleActivityAlertReason.PremiumPurchaseMade
                                )
                            )
                        }

                        is SubscriptionResult.PurchaseFailure -> {
                            emitVmEvent(ContentScreenVmEvent.Error(errorMessage = proofResult.errorMessage))
                        }

                        is SubscriptionResult.Failure -> {
                            emitVmEvent(ContentScreenVmEvent.Error())
                        }
                    }
                    currentSubscriptionParams = PremiumSubscriptionParams()
                }
            } else if (result is PurchaseResult.Failure) {
                emitVmEvent(ContentScreenVmEvent.Error())
                currentSubscriptionParams = PremiumSubscriptionParams()
                unhandledErrorUseCase(TAG, Error(result.errorMessage))
            }
        }
    }

    override fun onSubscribe(premiumSubscriptionData: PremiumSubscriptionData) {
        viewModelScope.launch(errorHandler) {
            currentSubscriptionParams =
                currentSubscriptionParams.copy(subscriptionData = premiumSubscriptionData)
            purchaseInProgress = true
            emitVmEvent(ContentScreenVmEvent.HideBottomSheetEvent)
            emitVmEvent(
                ContentScreenVmEvent.StartPremiumPurchase(
                    billingClient = billingClient,
                    billingParams = buildPremiumSubscriptionParamsUseCase(
                        productDetails = premiumSubscriptionData.productDetails,
                        offerToken = premiumSubscriptionData.offerToken
                    )
                )
            )
        }
    }

    override fun onOpenAuthMenu() {
        updateBottomSheetUiState(BottomSheetContent.AuthMenu)
    }

    override fun onError(errorMessage: String?, withPadding: Boolean) {
        emitVmEvent(ContentScreenVmEvent.Error(errorMessage, withPadding))
    }

    override fun onShowSnackBar(messageId: Int, titleId: Int?, withPadding: Boolean) {
        emitVmEvent(
            ContentScreenVmEvent.ShowSnackBarMessage(
                messageId, titleId, withPadding
            )
        )
    }

    override fun onShowSnackBar(message: String, title: String?, withPadding: Boolean) {
        emitVmEvent(
            ContentScreenVmEvent.ShowSnackBarMessageString(
                message, title, withPadding
            )
        )
    }

    override fun onOpenVideoDetails(videoId: Long, playListId: String?, shuffle: Boolean?) {
        viewModelScope.launch {
            sessionManager.saveVideoDetailsState(true)
            emitVmEvent(ContentScreenVmEvent.ExpendVideoDetails(videoId, playListId, shuffle))
            videoDetailsState.value = videoDetailsState.value.copy(visible = true)
        }
    }

    override fun onCloseVideoDetails() {
        viewModelScope.launch {
            sessionManager.saveVideoDetailsState(false)
            videoDetailsState.value = videoDetailsState.value.copy(visible = false)
        }
    }

    override fun onNavigateHome() {
        emitVmEvent(ContentScreenVmEvent.NavigateHome)
    }

    override fun onNavigateHomeAfterSignedOut() {
        emitVmEvent(ContentScreenVmEvent.NavigateHomeAfterSignOut)
    }

    override fun scrollToTop(index: Int) {
        emitVmEvent(ContentScreenVmEvent.ScrollToTop)
    }

    private fun emitVmEvent(event: ContentScreenVmEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    private fun updateSubscription(
        channel: ChannelDetailsEntity,
        action: UpdateChannelSubscriptionAction
    ) {
        viewModelScope.launch(errorHandler) {
            updateChannelSubscriptionUseCase(
                channelDetailsEntity = channel,
                action = action,
            )
                .onSuccess { channelDetailsEntity ->
                    emitVmEvent(ContentScreenVmEvent.ChannelSubscriptionUpdated(channelDetailsEntity))
                }
                .onFailure { throwable ->
                    unhandledErrorUseCase(TAG, throwable)
                }
        }
    }

    private fun updateNotifications(
        channelDetailsEntity: ChannelDetailsEntity,
        pushNotificationsEnabled: Boolean? = null,
        emailNotificationsEnabled: Boolean? = null,
        notificationFrequency: NotificationFrequency? = null
    ) {
        viewModelScope.launch(errorHandler) {
            updateNotificationsUseCase(
                channelDetailsEntity = channelDetailsEntity,
                data = UpdateChannelNotificationsData(
                    pushNotificationsEnabled = pushNotificationsEnabled,
                    emailNotificationsEnabled = emailNotificationsEnabled,
                    notificationFrequency = notificationFrequency,
                )
            )
                .onSuccess { channelEntity ->
                    emitVmEvent(ContentScreenVmEvent.ChannelNotificationsUpdated(channelEntity))
                }
                .onFailure { throwable ->
                    unhandledErrorUseCase(TAG, throwable)
                }
        }
    }

    private fun loadDeepLink() {
        viewModelScope.launch {
            notificationDataManager.deepLinkChannelId.distinctUntilChanged().collect {
                if (it.isNotEmpty()) {
                    emitVmEvent(ContentScreenVmEvent.NavigateToChannelDetails(it))
                    onboardingViewState.value = DoNotShow
                } else if (onboardingViewState.value != DoNotShow) {
                    onboardingViewState.value =
                        feedOnboardingViewUseCase()
                }
            }
        }
    }

    private suspend fun fetchUserUploadChannels() {
        when (val result = getUserUploadChannelsUseCase()) {
            is UserUploadChannelsResult.UserUploadChannelsError -> {
                delay(RumbleConstants.RETRY_DELAY_USER_UPLOAD_CHANNELS)
                fetchUserUploadChannels()
            }

            is UserUploadChannelsResult.UserUploadChannelsSuccess -> {
                editPlayListState.update {
                    it.copy(
                        userUploadChannels = result.userUploadChannels
                    )
                }
            }
        }
    }

    private suspend fun observeNotificationData() {
        notificationDataManager.showPremiumMenu.distinctUntilChanged().collect {
            if (it) {
                if (sessionManager.isPremiumUserFlow.first()) {
                    updateBottomSheetUiState(BottomSheetContent.PremiumOptions)
                } else {
                    onShowSubscriptionOptions(videoId = null, source = SubscriptionSource.PushNotification)
                }
                notificationDataManager.setShowPremiumMenu(false)
            }
        }
    }
}