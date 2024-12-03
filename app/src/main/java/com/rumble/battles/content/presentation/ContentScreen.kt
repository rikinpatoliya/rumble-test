package com.rumble.battles.content.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.onesignal.OneSignal
import com.rumble.battles.R
import com.rumble.battles.bottomSheets.BottomSheetOverNavBarContent
import com.rumble.battles.camera.presentation.CameraGalleryScreen
import com.rumble.battles.camera.presentation.CameraModeScreen
import com.rumble.battles.camera.presentation.CameraUploadLicenceSelectionScreen
import com.rumble.battles.camera.presentation.CameraUploadScheduleSelectionScreen
import com.rumble.battles.camera.presentation.CameraUploadStepOneScreen
import com.rumble.battles.camera.presentation.CameraUploadStepTwoScreen
import com.rumble.battles.camera.presentation.CameraUploadVisibilitySelectionScreen
import com.rumble.battles.camera.presentation.CameraViewModel
import com.rumble.battles.camera.presentation.UploadCategorySelectionScreen
import com.rumble.battles.camera.presentation.UploadChannelSelectionScreen
import com.rumble.battles.camera.presentation.VideoPreviewScreen
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsScreen
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsViewModel
import com.rumble.battles.commonViews.DefaultSystemBarIconsColor
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DeletePlayListConfirmationAlertDialog
import com.rumble.battles.commonViews.dialogs.DeleteWatchHistoryConfirmationAlertDialog
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.PremiumPurchaseConfirmationDialog
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.dialogs.UnfollowConfirmationAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.discover.presentation.categories.BrowseCategoriesScreen
import com.rumble.battles.discover.presentation.categories.CategoryScreen
import com.rumble.battles.discover.presentation.categories.CategoryViewModel
import com.rumble.battles.discover.presentation.discoverscreen.DiscoverPlayerScreen
import com.rumble.battles.discover.presentation.discoverscreen.DiscoverPlayerViewModel
import com.rumble.battles.discover.presentation.discoverscreen.DiscoverScreen
import com.rumble.battles.discover.presentation.discoverscreen.DiscoverViewModel
import com.rumble.battles.earnings.presentation.EarningsScreen
import com.rumble.battles.earnings.presentation.EarningsViewModel
import com.rumble.battles.feed.presentation.feedlist.HomeScreen
import com.rumble.battles.feed.presentation.feedlist.HomeViewModel
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelScreen
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelsViewModel
import com.rumble.battles.feed.presentation.videodetails.CollapsableLayoutState
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsScreen
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsViewModel
import com.rumble.battles.landing.AppUpdateAvailableAlertDialog
import com.rumble.battles.landing.ForceUpdateView
import com.rumble.battles.landing.RumbleActivityAlertReason
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.landing.RumbleEvent
import com.rumble.battles.library.presentation.library.LibraryScreen
import com.rumble.battles.library.presentation.library.LibraryViewModel
import com.rumble.battles.library.presentation.playlist.PlayListTypeRefresh
import com.rumble.battles.library.presentation.playlist.PlayListViewModel
import com.rumble.battles.library.presentation.playlist.PlayListsViewModel
import com.rumble.battles.library.presentation.views.PLAY_LIST_ENTITY
import com.rumble.battles.library.presentation.views.PLAY_LIST_TYPE_REFRESH
import com.rumble.battles.library.presentation.views.PlayListScreen
import com.rumble.battles.library.presentation.views.PlayListsScreen
import com.rumble.battles.livechat.presentation.LiveChatViewModel
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthViewModel
import com.rumble.battles.navigation.BottomNavigationBar
import com.rumble.battles.navigation.LandingScreens
import com.rumble.battles.navigation.NAV_ITEM_INDEX_ACCOUNT
import com.rumble.battles.navigation.NAV_ITEM_INDEX_CAMERA
import com.rumble.battles.navigation.NAV_ITEM_INDEX_DISCOVER
import com.rumble.battles.navigation.NAV_ITEM_INDEX_HOME
import com.rumble.battles.navigation.NAV_ITEM_INDEX_LIBRARY
import com.rumble.battles.navigation.NavItems
import com.rumble.battles.navigation.RumblePath
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.battles.notifications.presentation.NotificationSettingsScreen
import com.rumble.battles.notifications.presentation.NotificationSettingsViewModel
import com.rumble.battles.onboarding.presentation.OnboardingPopupsOverlay
import com.rumble.battles.profile.presentation.EditProfileScreen
import com.rumble.battles.profile.presentation.EditProfileViewModel
import com.rumble.battles.profile.presentation.NEW_IMAGE_URI_KEY
import com.rumble.battles.profile.presentation.ProfileNotificationsScreen
import com.rumble.battles.profile.presentation.ProfileNotificationsViewModel
import com.rumble.battles.profile.presentation.ProfileScreen
import com.rumble.battles.profile.presentation.ProfileViewModel
import com.rumble.battles.referrals.presentation.ReferralsScreen
import com.rumble.battles.referrals.presentation.ReferralsViewModel
import com.rumble.battles.search.presentation.channelsSearch.ChannelSearchScreen
import com.rumble.battles.search.presentation.channelsSearch.ChannelSearchViewModel
import com.rumble.battles.search.presentation.combinedSearch.CombineSearchResultScreen
import com.rumble.battles.search.presentation.combinedSearch.CombineSearchResultViewModel
import com.rumble.battles.search.presentation.searchScreen.SearchScreen
import com.rumble.battles.search.presentation.searchScreen.SearchViewModel
import com.rumble.battles.search.presentation.videosSearch.VideosSearchScreen
import com.rumble.battles.search.presentation.videosSearch.VideosSearchViewModel
import com.rumble.battles.settings.presentation.ChangeEmailScreen
import com.rumble.battles.settings.presentation.ChangeEmailViewModel
import com.rumble.battles.settings.presentation.ChangePasswordScreen
import com.rumble.battles.settings.presentation.ChangePasswordViewModel
import com.rumble.battles.settings.presentation.ChangeSubdomainScreen
import com.rumble.battles.settings.presentation.ChangeSubdomainViewModel
import com.rumble.battles.settings.presentation.CloseAccountScreen
import com.rumble.battles.settings.presentation.CloseAccountViewModel
import com.rumble.battles.settings.presentation.CreditsScreen
import com.rumble.battles.settings.presentation.CreditsScreenViewModel
import com.rumble.battles.settings.presentation.DebugAdSettingsScreen
import com.rumble.battles.settings.presentation.DebugAdSettingsViewModel
import com.rumble.battles.settings.presentation.SettingsScreen
import com.rumble.battles.settings.presentation.SettingsViewModel
import com.rumble.battles.settings.presentation.UploadQualityScreen
import com.rumble.battles.subscriptions.presentation.SubscriptionsScreen
import com.rumble.battles.subscriptions.presentation.SubscriptionsViewModel
import com.rumble.battles.videolist.presentation.VideoListScreen
import com.rumble.battles.videolist.presentation.VideoListViewModel
import com.rumble.battles.videos.presentation.MyVideosScreen
import com.rumble.battles.videos.presentation.MyVideosViewModel
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.feed.domain.domainmodel.ads.AdEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.onboarding.domain.domainmodel.ShowOnboardingPopups
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.paddingGiant
import com.rumble.theme.paddingNone
import com.rumble.utils.RumbleConstants.NAV_BAR_ANIMATION_DURATION
import com.rumble.utils.extension.isAtTopOfNavStack
import com.rumble.utils.extension.navigationSafeEncode
import com.rumble.utils.replaceUrlParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ContentScreen(
    activityHandler: RumbleActivityHandler,
    contentHandler: ContentHandler,
    authHandler: AuthHandler,
    parentController: NavController
) {
    val homeNavController = rememberNavController()
    val discoverNavController = rememberNavController()
    val cameraNavController = rememberNavController()
    val libraryNavController = rememberNavController()
    val profileNavController = rememberNavController()
    val bottomSheetUiState by contentHandler.bottomSheetUiState.collectAsStateWithLifecycle()
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()
    val onboardingViewState =
        contentHandler.onboardingViewState.collectAsStateWithLifecycle()
    val appUpdateState =
        contentHandler.appUpdateState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarHostStateWithPadding = remember { SnackbarHostState() }
    var snackBarHostPadding by remember { mutableStateOf(paddingGiant) }
    val context = LocalContext.current
    val alertDialogState by activityHandler.alertDialogState
    val videoDetailsState by contentHandler.videoDetailsState
    val userUIState by contentHandler.userUIState.collectAsStateWithLifecycle()
    val videoDetailsViewModel: VideoDetailsViewModel = hiltViewModel()
    val liveChatViewModel: LiveChatViewModel = hiltViewModel()

    val tabScreens = NavItems.items.map {
        it.root.rootName
    }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(NAV_ITEM_INDEX_HOME) }
    var navigateToMyVideos by remember { mutableStateOf(false) }
    val navControllers = remember {
        tabScreens.map {
            when (it) {
                RumbleScreens.Feeds.rootName -> homeNavController
                RumbleScreens.Discover.rootName -> discoverNavController
                RumbleScreens.CameraGalleryScreen.rootName -> cameraNavController
                RumbleScreens.Library.rootName -> libraryNavController
                RumbleScreens.Profile.rootName -> profileNavController
                else -> homeNavController
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            contentHandler.onContentResumed()
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(bottomSheetState.isVisible) {
        hideBottomSheet(coroutineScope, bottomSheetState, contentHandler)
    }

    LaunchedEffect(activityHandler.eventFlow) {
        activityHandler.eventFlow.collectLatest {
            when (it) {
                is RumbleEvent.NavigateToVideoDetailsFromNotification -> {
                    contentHandler.onOpenVideoDetails(it.videoEntity.id)
                }

                is RumbleEvent.UnexpectedError -> {
                    snackBarHostState.showRumbleSnackbar(
                        context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is RumbleEvent.NavigateToMyVideos -> {
                    navControllers[selectedTabIndex].popBackStack(
                        navControllers[selectedTabIndex].graph.startDestinationId,
                        inclusive = false
                    )
                    navigateToMyVideos = true
                    selectedTabIndex = NAV_ITEM_INDEX_ACCOUNT
                }

                is RumbleEvent.EnterPipMode -> {
                    videoDetailsViewModel.onUpdateLayoutState(CollapsableLayoutState.Expended(animated = false))
                    contentHandler.onEnterPipMode()
                }

                else -> return@collectLatest
            }
        }
    }

    LaunchedEffect(contentHandler.userUIState) {
        contentHandler.userUIState.collectLatest {
            if (it.isLoggedIn && authHandler.state.value.uitTesting.not() && videoDetailsState.visible.not()) {
                val permission = OneSignal.Notifications.requestPermission(false)
                if (permission) OneSignal.User.pushSubscription.optIn()
            }
        }
    }

    LaunchedEffect(contentHandler.uploadsToNotifyAbout) {
        contentHandler.uploadsToNotifyAbout.collect {
            contentHandler.notifyUserAboutUploads(it)
        }
    }

    LaunchedEffect(contentHandler.eventFlow) {
        contentHandler.eventFlow.collectLatest { event ->
            when (event) {
                ContentScreenVmEvent.HideBottomSheetEvent -> {
                    coroutineScope.launch { bottomSheetState.hide() }
                }

                ContentScreenVmEvent.ShowBottomSheetEvent -> {
                    coroutineScope.launch { bottomSheetState.show() }
                }

                ContentScreenVmEvent.HideAlertDialogEvent -> {
                    activityHandler.onDismissDialog()
                }

                is ContentScreenVmEvent.ShowAlertDialogEvent -> {
                    activityHandler.onShowAlertDialog(event.reason)
                }

                is ContentScreenVmEvent.ShowSnackBarMessage -> {
                    if (event.withPadding) snackBarHostPadding = paddingGiant
                    (if (event.withPadding) snackBarHostStateWithPadding else snackBarHostState).showRumbleSnackbar(
                        message = context.getString(event.messageId),
                        title = if (event.titleId != null) context.getString(event.titleId) else null
                    )
                }

                is ContentScreenVmEvent.ShowSnackBarMessageString -> {
                    if (event.withPadding) snackBarHostPadding = paddingGiant
                    (if (event.withPadding) snackBarHostStateWithPadding else snackBarHostState).showRumbleSnackbar(
                        message = event.message,
                        title = event.title
                    )
                }

                is ContentScreenVmEvent.UserUploadNotification -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = if (event.success) {
                            String.format(
                                context.getString(R.string.upload_success),
                                if (event.uploadTitle.isEmpty()) {
                                    context.getString(R.string.upload_success_a_video)
                                } else {
                                    "\"${event.uploadTitle}\""
                                }
                            )
                        } else {
                            event.message
                                ?: context.getString(R.string.generic_error_message_try_later)
                        }
                    )
                }

                is ContentScreenVmEvent.NavigateHome -> {
                    selectedTabIndex = NAV_ITEM_INDEX_HOME
                }

                is ContentScreenVmEvent.NavigateHomeAfterSignOut -> {
                    tabScreens.forEachIndexed { index, startDestinationId ->
                        navControllers[index].popBackStack(startDestinationId, false)
                    }
                    selectedTabIndex = NAV_ITEM_INDEX_HOME
                    try {
                        navControllers[selectedTabIndex].navigate(tabScreens[selectedTabIndex]) {
                            popUpTo(navControllers[selectedTabIndex].graph.id) {
                                inclusive = true
                            }
                        }
                    } catch (e: IllegalStateException) {
                        Timber.e("NavControllerError inclusive popUpTo failed with exception: ${e.message}")
                    }
                }

                is ContentScreenVmEvent.NavigateToChannelDetails -> {
                    activityHandler.onPauseVideo()
                    navControllers[selectedTabIndex].navigate(RumbleScreens.Channel.getPath(event.channelId))
                    contentHandler.onDeepLinkNavigated()
                }

                is ContentScreenVmEvent.Error -> {
                    if (event.withPadding) snackBarHostPadding = paddingGiant
                    (if (event.withPadding) snackBarHostStateWithPadding else snackBarHostState).showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is ContentScreenVmEvent.VideoAddedToPlayList -> {
                    if (event.withPadding) snackBarHostPadding = paddingGiant
                    (if (event.withPadding) snackBarHostStateWithPadding else snackBarHostState).showRumbleSnackbar(
                        message = context.getString(R.string.video_added_to_playlist)
                    )
                }

                is ContentScreenVmEvent.VideoRemovedFromPlayList -> {
                    if (event.withPadding) snackBarHostPadding = paddingGiant
                    (if (event.withPadding) snackBarHostStateWithPadding else snackBarHostState).showRumbleSnackbar(
                        message = context.getString(R.string.video_removed_from_playlist)
                    )
                }

                ContentScreenVmEvent.VideoAlreadyInPlayList -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.video_already_in_playlist)
                    )
                }

                ContentScreenVmEvent.PlayListDeleted -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.playlist_deleted)
                    )
                }

                ContentScreenVmEvent.WatchHistoryCleared -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.watch_history_deleted)
                    )
                }

                is ContentScreenVmEvent.StartPremiumPurchase -> {
                    (context as? Activity)?.let {
                        event.billingClient.launchBillingFlow(it, event.billingParams)
                    }
                }

                is ContentScreenVmEvent.ChannelSubscriptionUpdated -> {
                    bottomSheetState.hide()
                }

                is ContentScreenVmEvent.ExpendVideoDetails -> {
                    videoDetailsViewModel.onLoadContent(
                        event.videoId,
                        event.playListId,
                        event.shuffle
                    )
                }

                is ContentScreenVmEvent.DisplayUndoRepostWarning -> {
                    activityHandler.onDisplayRepostUndoWarning(event.repostId)
                }

                is ContentScreenVmEvent.ShowRepostReportedMessage -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.the_video_has_been_reported)
                    )
                }

                is ContentScreenVmEvent.PlayListUpdated -> {}
                is ContentScreenVmEvent.PlayListCreated -> {}
                is ContentScreenVmEvent.ChannelNotificationsUpdated -> {}
                is ContentScreenVmEvent.SortFollowingTypeUpdated -> {}
                else -> {}
            }
        }
    }

    DefaultSystemBarIconsColor()

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        scrimColor = bottomSheetUiState.bottomSheetScrimColor,
        sheetContent = {
            BottomSheetOverNavBarContent(
                bottomSheetState = bottomSheetState,
                bottomSheetData = bottomSheetUiState.data,
                contentHandler = contentHandler,
                authHandler = authHandler,
                navController = navControllers[selectedTabIndex],
                activityHandler = activityHandler,
                onHideBottomSheet = {
                    hideBottomSheet(
                        coroutineScope = coroutineScope,
                        bottomSheetState = bottomSheetState,
                        contentHandler = contentHandler
                    )
                },
                onNavigateToLogin = {
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                },
                onNavigateToRegistration = { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToAgeVerification = {
                    parentController.navigate(
                        LandingScreens.AgeVerificationScreen.getPath(
                            popOnAgeVerification = true
                        )
                    )
                }
            )
        }) {
        Scaffold(
            modifier = Modifier
                .semantics { testTagsAsResourceId = true },
            bottomBar = {
                if (activityHandler.isLaunchedFromNotification.value.not()) {
                    AnimatedVisibility(
                        modifier = Modifier.systemBarsPadding(),
                        visible = (selectedTabIndex != NAV_ITEM_INDEX_CAMERA || userUIState.isLoggedIn.not())
                            && (videoDetailsState.visible.not() || videoDetailsState.collapsed),
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 }),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(NAV_BAR_ANIMATION_DURATION)
                        )
                    ) {
                        BottomNavigationBar(
                            contentHandler = contentHandler,
                            selectedTabIndex = selectedTabIndex,
                            onNavigationItemClicked = { _, index ->
                                if (selectedTabIndex == index) {
                                    if (navControllers[index].isAtTopOfNavStack()) {
                                        contentHandler.scrollToTop(index)
                                    } else {
                                        navControllers[index].popBackStack(tabScreens[index], false)
                                    }
                                }
                                selectedTabIndex = index
                                if (index == NAV_ITEM_INDEX_CAMERA && userUIState.isLoggedIn) {
                                    // pause video when camera tab is selected
                                    activityHandler.onPauseVideo()
                                }
                            },
                        )
                    }
                }
            }
        ) {
            when (selectedTabIndex) {
                NAV_ITEM_INDEX_HOME -> TabNavHost(
                    tabScreens[NAV_ITEM_INDEX_HOME],
                    parentController,
                    navControllers[NAV_ITEM_INDEX_HOME],
                    activityHandler,
                    contentHandler,
                    bottomSheetState,
                )

                NAV_ITEM_INDEX_DISCOVER -> TabNavHost(
                    tabScreens[NAV_ITEM_INDEX_DISCOVER],
                    parentController,
                    navControllers[NAV_ITEM_INDEX_DISCOVER],
                    activityHandler,
                    contentHandler,
                    bottomSheetState,
                )

                NAV_ITEM_INDEX_CAMERA -> TabNavHost(
                    tabScreens[NAV_ITEM_INDEX_CAMERA],
                    parentController,
                    navControllers[NAV_ITEM_INDEX_CAMERA],
                    activityHandler,
                    contentHandler,
                    bottomSheetState,
                )

                NAV_ITEM_INDEX_LIBRARY -> TabNavHost(
                    tabScreens[NAV_ITEM_INDEX_LIBRARY],
                    parentController,
                    navControllers[NAV_ITEM_INDEX_LIBRARY],
                    activityHandler,
                    contentHandler,
                    bottomSheetState,
                )

                NAV_ITEM_INDEX_ACCOUNT -> {
                    TabNavHost(
                        tabScreens[NAV_ITEM_INDEX_ACCOUNT],
                        parentController,
                        navControllers[NAV_ITEM_INDEX_ACCOUNT],
                        activityHandler,
                        contentHandler,
                        bottomSheetState,
                    )
                    LaunchedEffect(navigateToMyVideos) {
                        if (navigateToMyVideos) {
                            // navigation performed here as initially profile navcontroller have
                            // a empty graph.
                            navControllers[selectedTabIndex].navigate(RumbleScreens.Videos.rootName) {
                                popUpTo(navControllers[selectedTabIndex].graph.startDestinationId)
                            }
                            navigateToMyVideos = false
                        }
                    }
                }
            }
            if (videoDetailsState.visible && (selectedTabIndex != NAV_ITEM_INDEX_CAMERA || userUIState.isLoggedIn.not())) {
                VideoDetailsScreen(
                    activityHandler = activityHandler,
                    handler = videoDetailsViewModel,
                    contentHandler = contentHandler,
                    liveChatHandler = liveChatViewModel,
                    contentBottomSheetState = bottomSheetState,
                    onChannelClick = {
                        videoDetailsViewModel.onUpdateLayoutState(CollapsableLayoutState.Collapsed)
                        navControllers[selectedTabIndex].navigate(RumbleScreens.Channel.getPath(it))
                    },
                    onCategoryClick = {
                        videoDetailsViewModel.onUpdateLayoutState(CollapsableLayoutState.Collapsed)
                        navControllers[selectedTabIndex].navigate(
                            RumbleScreens.CategoryScreen.getPath(
                                it,
                                false
                            )
                        )
                    },
                    onTagClick = {
                        videoDetailsViewModel.onUpdateLayoutState(CollapsableLayoutState.Collapsed)
                        navControllers[selectedTabIndex].navigate(
                            RumbleScreens.CombinedSearchResult.getPath(
                                it
                            )
                        )
                    },
                    onNavigateBack = {
                        navControllers[selectedTabIndex].navigateUp()
                    },
                )
            }
        }
    }

    if (activityHandler.isLaunchedFromNotification.value.not()) {
        if (onboardingViewState.value is ShowOnboardingPopups) {
            val popupsList = (onboardingViewState.value as ShowOnboardingPopups).list
            if (popupsList.isNotEmpty()) {
                OnboardingPopupsOverlay(
                    handler = contentHandler,
                    list = popupsList
                )
            }
        }
    }

    RumbleSnackbarHost(snackBarHostState)
    RumbleSnackbarHost(
        snackBarHostStateWithPadding,
        modifier = Modifier.padding(bottom = snackBarHostPadding)
    )

    if (alertDialogState.show) {
        ContentScreenDialog(
            reason = alertDialogState.alertDialogReason,
            handler = activityHandler,
            contentHandler = contentHandler
        )
    }

    if (appUpdateState.value.forceUpdateRequired) {
        ForceUpdateView(
            onGoToStore = contentHandler::onGoToStore
        )
    } else if (appUpdateState.value.appUpdateSuggested) {
        AppUpdateAvailableAlertDialog(
            onDismiss = contentHandler::onSuggestedUpdateDismissed,
            onGoToStore = contentHandler::onGoToStore,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TabNavHost(
    startDestination: String,
    parentController: NavController,
    navController: NavHostController,
    activityHandler: RumbleActivityHandler,
    contentHandler: ContentHandler,
    bottomSheetState: ModalBottomSheetState,
) {
    NavHost(
        modifier = Modifier.padding(bottom = paddingNone),
        navController = navController,
        graph = createNavigationGraph(
            startDestination,
            parentController,
            navController,
            bottomSheetState,
            contentHandler,
            activityHandler,
        ),
        exitTransition = { ExitTransition.None },
        enterTransition = { EnterTransition.None }
    )
}

@Composable
private fun ContentScreenDialog(
    reason: AlertDialogReason,
    handler: RumbleActivityHandler,
    contentHandler: ContentHandler
) {
    when (reason) {
        is RumbleActivityAlertReason.VideoDetailsFromNotificationFailedReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                text = stringResource(id = R.string.something_went_wrong),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive
                    )
                )
            )
        }

        is RumbleActivityAlertReason.DeleteWatchHistoryConfirmationReason -> {
            DeleteWatchHistoryConfirmationAlertDialog(
                onCancel = handler::onDismissDialog,
                onDelete = contentHandler::onDeleteWatchHistory
            )
        }

        is RumbleActivityAlertReason.DeletePlayListConfirmationReason -> {
            DeletePlayListConfirmationAlertDialog(
                onCancel = handler::onDismissDialog,
                onDelete = { contentHandler.onDeletePlayList(reason.playListId) },
            )
        }

        is RumbleActivityAlertReason.UnfollowConfirmationReason -> {
            UnfollowConfirmationAlertDialog(
                onCancelUnfollow = contentHandler::onCancelUnfollow,
                onUnfollow = { contentHandler.onUnfollow(reason.channel) }
            )
        }

        is RumbleActivityAlertReason.PremiumPurchaseMade -> {
            PremiumPurchaseConfirmationDialog {
                handler.onDismissDialog()
                handler.onPremiumPurchased()
            }
        }

        is RumbleActivityAlertReason.SubscriptionNotAvailable -> {
            RumbleAlertDialog(
                onDismissRequest = handler::onDismissDialog,
                text = stringResource(id = R.string.subscription_not_available),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive
                    )
                )
            )
        }

        is RumbleActivityAlertReason.AppleInAppSubscription -> {
            RumbleAlertDialog(
                onDismissRequest = handler::onDismissDialog,
                title = stringResource(id = R.string.apple_in_app_subscription),
                text = stringResource(id = R.string.apple_in_app_subscription_description),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.close),
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive
                    )
                )
            )
        }

        is RumbleActivityAlertReason.UndoRepostWarning -> {
            RumbleAlertDialog(
                onDismissRequest = handler::onDismissDialog,
                title = stringResource(id = R.string.undo_repost),
                text = stringResource(id = R.string.sure_delete_repost),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.cancel),
                        dialogActionType = DialogActionType.Neutral,
                        withSpacer = true,
                        width = commentActionButtonWidth,
                        action = handler::onDismissDialog
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.delete_repost),
                        dialogActionType = DialogActionType.Destructive,
                        width = commentActionButtonWidth,
                        action = {
                            handler.onDismissDialog()
                            contentHandler.onUndoRepostConfirmed(reason.repostId)
                        }
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun hideBottomSheet(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    contentHandler: ContentHandler,
) {
    contentHandler.updateBottomSheetUiState(BottomSheetContent.HideBottomSheet)
    coroutineScope.launch {
        bottomSheetState.hide()
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun createNavigationGraph(
    startDestination: String,
    parentController: NavController,
    currentNavController: NavHostController,
    bottomSheetState: ModalBottomSheetState,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
) =
    currentNavController.createGraph(startDestination = startDestination) {
        composable(
            route = RumbleScreens.Feeds.rootName,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val feedListViewModel: HomeViewModel = hiltViewModel()
            val recommendedChannelsHandler: RecommendedChannelsViewModel = hiltViewModel()

            HomeScreen(
                activityHandler = activityHandler,
                homeHandler = feedListViewModel,
                contentHandler = contentHandler,
                recommendedChannelsHandler = recommendedChannelsHandler,
                onSearch = {
                    currentNavController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onFreshContentChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                    //Temp rollback to old navigation
//                    currentNavController.navigate(
//                        RumbleScreens.DiscoverPlayer.getPath(
//                            category = DiscoverPlayerVideoListSource.FreshContent.name,
//                            channelId = channelId
//                        )
//                    )
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        contentHandler.onOpenVideoDetails(it.id)
                    } else if (it is AdEntity) {
                        activityHandler.onOpenWebView(it.adUrl)
                    }
                },
                onViewAllRecommendedChannelsClick = {
                    currentNavController.navigate(RumbleScreens.RecommendedChannelsScreen.rootName)
                },
                onSearchIconGlobalMeasured = contentHandler::onSearchIconMeasured,
                onFollowingIconGlobalMeasured = contentHandler::onFollowingIconMeasured,
                onFollowingClicked = { currentNavController.navigate(RumbleScreens.Subscriptions.rootName) },
                onViewNotifications = {
                    currentNavController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
            )
        }
        composable(
            route = RumbleScreens.Discover.rootName,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val discoverViewModel: DiscoverViewModel = hiltViewModel()
            DiscoverScreen(
                activityHandler = activityHandler,
                discoverHandler = discoverViewModel,
                contentHandler = contentHandler,
                onSearch = {
                    currentNavController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    contentHandler.onOpenVideoDetails(it.id)
                },
                onViewCategory = {
                    currentNavController.navigate(RumbleScreens.VideoListScreen.getPath(it.name))
                },
                onViewTopChannels = {
                    currentNavController.navigate(RumbleScreens.TopChannelsScreen.rootName)
                },
                onBrowseCategory = {
                    currentNavController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                },
                onBrowseAllCategories = {
                    currentNavController.navigate(
                        RumbleScreens.BrowseAllCategories.getPath(CategoryDisplayType.CATEGORIES)
                    )
                },
                onViewNotifications = {
                    currentNavController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
            )
        }
        composable(RumbleScreens.CameraUploadStepOne.rootName) { navBackStackEntry ->
            CameraUploadStepOneScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                onSelectChannel = { currentNavController.navigate(RumbleScreens.UploadChannelSelection.rootName) },
                onSelectCategory = {
                    currentNavController.navigate(
                        RumbleScreens.UploadCategorySelection.getPath(it)
                    )
                },
                onNextStep = { currentNavController.navigate(RumbleScreens.CameraUploadStepTwo.rootName) },
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.CameraUploadStepTwo.rootName) { navBackStackEntry ->
            CameraUploadStepTwoScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                activityHandler = activityHandler,
                onSelectLicense = { currentNavController.navigate(RumbleScreens.UploadLicenseSelection.rootName) },
                onSelectVisibility = { currentNavController.navigate(RumbleScreens.UploadVisibilitySelection.rootName) },
                onSelectSchedule = { currentNavController.navigate(RumbleScreens.UploadScheduleSelection.rootName) },
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadChannelSelection.rootName) { navBackStackEntry ->
            UploadChannelSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadLicenseSelection.rootName) { navBackStackEntry ->
            CameraUploadLicenceSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadScheduleSelection.rootName) { navBackStackEntry ->
            CameraUploadScheduleSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadVisibilitySelection.rootName) { navBackStackEntry ->
            CameraUploadVisibilitySelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(
            RumbleScreens.UploadCategorySelection.rootName,
            arguments = listOf(
                navArgument(RumblePath.IS_PRIMARY_CATEGORY.path) { type = NavType.BoolType }
            )
        ) { navBackStackEntry ->
            UploadCategorySelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(
                    navBackStackEntry,
                    currentNavController
                ),
                isPrimary = navBackStackEntry.arguments?.getBoolean(RumblePath.IS_PRIMARY_CATEGORY.path) ?: true,
                onBackClick = { currentNavController.navigateUp() },
            )
        }
        composable(RumbleScreens.Videos.rootName) { backStackEntry ->
            val myVideosViewModel: MyVideosViewModel = hiltViewModel()
            val newImageUri: Uri? by backStackEntry
                .savedStateHandle
                .getLiveData<Uri?>(NEW_IMAGE_URI_KEY)
                .observeAsState()
            MyVideosScreen(
                currentDestinationRoute = currentNavController.currentDestination?.route,
                myVideosHandler = myVideosViewModel,
                onVideoClick = {
                    if (it is VideoEntity)
                        contentHandler.onOpenVideoDetails(it.id)
                },
                newImageUri = newImageUri,
                contentHandler = contentHandler,
                bottomSheetState = bottomSheetState,
            )
        }
        composable(RumbleScreens.Library.rootName) { backStackEntry ->
            val libraryViewModel: LibraryViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            val playListTypeRefresh: PlayListTypeRefresh? by backStackEntry
                .savedStateHandle
                .getLiveData<PlayListTypeRefresh?>(PLAY_LIST_TYPE_REFRESH)
                .observeAsState()
            val playListEntityRefresh: PlayListEntity? by backStackEntry
                .savedStateHandle
                .getLiveData<PlayListEntity?>(PLAY_LIST_ENTITY)
                .observeAsState()
            LibraryScreen(
                activityHandler = activityHandler,
                libraryHandler = libraryViewModel,
                contentHandler = contentHandler,
                authHandler = authViewModel,
                playListTypeRefresh = playListTypeRefresh,
                playListEntityRefresh = playListEntityRefresh,
                onSearch = {
                    currentNavController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity)
                        contentHandler.onOpenVideoDetails(it.id)
                },
                onViewAll = {
                    currentNavController.navigate(RumbleScreens.VideoListScreen.getPath(it.name))
                },
                onViewPlayLists = {
                    currentNavController.navigate(RumbleScreens.PlayListsScreen.rootName)
                },
                onViewPlayList = {
                    currentNavController.navigate(RumbleScreens.PlayListScreen.getPath(it))
                },
                bottomSheetState = bottomSheetState,
                onViewNotifications = {
                    currentNavController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
                onNavigateToRegistration = { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToAgeVerification = {
                    parentController.navigate(
                        LandingScreens.AgeVerificationScreen.getPath(
                            popOnAgeVerification = true
                        )
                    )
                },
                onNavigateToLogin = {
                    activityHandler.onPauseVideo()
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                }
            )
        }
        composable(RumbleScreens.PlayListsScreen.rootName) { backStackEntry ->
            val playListsViewModel: PlayListsViewModel = hiltViewModel()
            val playListTypeRefresh: PlayListTypeRefresh? by backStackEntry
                .savedStateHandle
                .getLiveData<PlayListTypeRefresh?>(PLAY_LIST_TYPE_REFRESH)
                .observeAsState()
            val playListEntityRefresh: PlayListEntity? by backStackEntry
                .savedStateHandle
                .getLiveData<PlayListEntity?>(PLAY_LIST_ENTITY)
                .observeAsState()
            PlayListsScreen(
                navController = currentNavController,
                playListsHandler = playListsViewModel,
                contentHandler = contentHandler,
                playListTypeRefresh = playListTypeRefresh,
                playListEntityRefresh = playListEntityRefresh,
                onViewChannel = { currentNavController.navigate(RumbleScreens.Channel.getPath(it)) },
                onViewPlayList = {
                    currentNavController.navigate(RumbleScreens.PlayListScreen.getPath(it))
                },
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.PlayListScreen.rootName,
            arguments = listOf(navArgument(RumblePath.PLAYLIST.path) { type = NavType.StringType })
        ) {
            val playListViewModel: PlayListViewModel = hiltViewModel()
            PlayListScreen(
                navController = currentNavController,
                playListHandler = playListViewModel,
                contentHandler = contentHandler,
                onVideoClick = {
                    contentHandler.onOpenVideoDetails(it.id)
                },
                onChannelClick = { currentNavController.navigate(RumbleScreens.Channel.getPath(it)) },
                onPlayAllClick = { entity, playListId ->
                    contentHandler.onOpenVideoDetails(videoId = entity.id, playListId = playListId)
                },
                onShuffleClick = { entity, playListId ->
                    contentHandler.onOpenVideoDetails(
                        videoId = entity.id,
                        playListId = playListId,
                        shuffle = true
                    )
                },
                onBackClick = {
                    currentNavController.navigateUp()
                }
            )
        }
        composable(RumbleScreens.EditProfile.rootName) {
            val editProfileViewModel: EditProfileViewModel = hiltViewModel()
            EditProfileScreen(
                editProfileHandler = editProfileViewModel,
                contentHandler = contentHandler,
                onBackClick = {
                    currentNavController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(NEW_IMAGE_URI_KEY, it)
                    currentNavController.navigateUp()
                },
            )
        }
        composable(RumbleScreens.Subscriptions.rootName) {
            val subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel()
            SubscriptionsScreen(
                subscriptionsScreenHandler = subscriptionsViewModel,
                contentHandler = contentHandler,
                bottomSheetState = bottomSheetState,
                onBackClick = { currentNavController.navigateUp() },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onSearch = { currentNavController.navigate(RumbleScreens.Search.getPath()) }
            )
        }
        composable(RumbleScreens.Referrals.rootName) {
            val referralsViewModel: ReferralsViewModel = hiltViewModel()
            ReferralsScreen(
                handler = referralsViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                }
            )
        }
        composable(
            RumbleScreens.Settings.rootName,
            arguments = listOf(navArgument(RumblePath.PARAMETER.path) {
                type = NavType.BoolType; defaultValue = false
            })
        ) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                settingsHandler = settingsViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                onNavigate = { currentNavController.navigate(it) },
            )
        }
        composable(
            RumbleScreens.Channel.rootName,
            arguments = listOf(navArgument(RumblePath.CHANNEL.path) { type = NavType.StringType })
        ) {
            val channelDetailsViewModel: ChannelDetailsViewModel = hiltViewModel()
            ChannelDetailsScreen(
                currentDestinationRoute = currentNavController.currentDestination?.route,
                channelDetailsHandler = channelDetailsViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                activityHandler = activityHandler,
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity)
                        contentHandler.onOpenVideoDetails(videoId = it.id)
                }
            )
        }
        composable(RumbleScreens.Profile.rootName) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                activityHandler = activityHandler,
                profileHandler = profileViewModel,
                authHandler = authViewModel,
                onProfileItemClicked = { navigationId ->
                    currentNavController.navigate(navigationId)
                },
                contentHandler = contentHandler,
                onNavigateToRegistration = { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToAgeVerification = {
                    parentController.navigate(
                        LandingScreens.AgeVerificationScreen.getPath(
                            popOnAgeVerification = true
                        )
                    )
                },
                onNavigateToLogin = {
                    activityHandler.onPauseVideo()
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                },
                onNavigateToSettings = {
                    currentNavController.navigate(RumbleScreens.Settings.getPath())
                },
                onViewNotifications = {
                    currentNavController.navigate(RumbleScreens.ProfileNotifications.rootName)
                }
            )
        }
        composable(
            RumbleScreens.Search.rootName,
            arguments = listOf(
                navArgument(RumblePath.QUERY.path) {
                    type = NavType.StringType; defaultValue = ""
                },
                navArgument(RumblePath.NAVIGATION.path) {
                    type = NavType.StringType; defaultValue = ""
                },
                navArgument(RumblePath.PARAMETER.path) {
                    type = NavType.StringType; defaultValue = RumbleScreens.Feeds.rootName
                }
            )
        ) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                searchHandler = searchViewModel,
                onSearch = { query, navDest, parent ->
                    currentNavController.popBackStack(parent, false)
                    if (navDest.isBlank()) currentNavController.navigate(
                        RumbleScreens.CombinedSearchResult.getPath(query)
                    )
                    else currentNavController.navigate(
                        replaceUrlParameter(
                            navDest,
                            RumblePath.QUERY.path,
                            query.navigationSafeEncode()
                        )
                    )
                },
                onViewChannel = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBrowseCategory = {
                    currentNavController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                }
            ) {
                currentNavController.navigateUp()
            }
        }
        composable(
            RumbleScreens.CombinedSearchResult.rootName,
            arguments = listOf(navArgument(RumblePath.QUERY.path) { type = NavType.StringType })
        ) {
            val combineSearchResultViewModel: CombineSearchResultViewModel = hiltViewModel()
            CombineSearchResultScreen(
                handler = combineSearchResultViewModel,
                contentHandler = contentHandler,
                onSearch = {
                    currentNavController.navigate(RumbleScreens.Search.getPath(it))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        contentHandler.onOpenVideoDetails(videoId = it.id)
                    }
                },
                onViewChannels = {
                    currentNavController.navigate(RumbleScreens.ChannelSearchScreen.getPath(it))
                },
                onViewVideos = { path, filters ->
                    currentNavController.navigate(
                        RumbleScreens.VideoSearchScreen.getPath(
                            path,
                            filters.sortSelection.name,
                            filters.filterSelection.name,
                            filters.durationSelection.name
                        )
                    )
                },
                onViewChannel = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                }) {
                currentNavController.navigateUp()
            }
        }
        composable(
            RumbleScreens.ChannelSearchScreen.rootName,
            arguments = listOf(navArgument(RumblePath.QUERY.path) { type = NavType.StringType })
        ) {
            val channelSearchViewModel: ChannelSearchViewModel = hiltViewModel()
            ChannelSearchScreen(
                handler = channelSearchViewModel,
                onSearch = { query ->
                    currentNavController.navigate(
                        RumbleScreens.Search.getPath(
                            query,
                            RumbleScreens.ChannelSearchScreen.getPath(RumblePath.QUERY.path)
                        )
                    )
                }, onViewChannel = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                }) {
                currentNavController.navigateUp()
            }
        }
        composable(
            RumbleScreens.VideoSearchScreen.rootName,
            arguments = listOf(navArgument(RumblePath.QUERY.path) { type = NavType.StringType },
                navArgument(RumblePath.SORT.path) { type = NavType.StringType },
                navArgument(RumblePath.UPLOAD_DATE.path) { type = NavType.StringType },
                navArgument(RumblePath.DURATION.path) { type = NavType.StringType })
        ) {
            val videoSearchViewModel: VideosSearchViewModel = hiltViewModel()
            VideosSearchScreen(
                handler = videoSearchViewModel,
                contentHandler = contentHandler,
                onSearch = { query ->
                    currentNavController.navigate(
                        RumbleScreens.Search.getPath(
                            query,
                            RumbleScreens.VideoSearchScreen.getPath("", "", "", "")
                        )
                    )
                },
                onViewVideo = {
                    contentHandler.onOpenVideoDetails(videoId = it.id)
                },
                onBack = { currentNavController.navigateUp() },
                onImpression = videoSearchViewModel::onVideoCardImpression
            )
        }
        composable(RumbleScreens.Credits.rootName) {
            val creditsScreenViewModel: CreditsScreenViewModel = hiltViewModel()
            CreditsScreen(
                creditsScreenHandler = creditsScreenViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                activityHandler = activityHandler,
            )
        }
        composable(RumbleScreens.ChangeEmail.rootName) {
            val changeEmailHandler: ChangeEmailViewModel = hiltViewModel()
            ChangeEmailScreen(
                changeEmailHandler = changeEmailHandler,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.ChangePassword.rootName) {
            val changePasswordHandler: ChangePasswordViewModel = hiltViewModel()
            ChangePasswordScreen(
                changePasswordHandler = changePasswordHandler,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.ChangeSubdomain.rootName) {
            val changeSubdomainViewModel: ChangeSubdomainViewModel = hiltViewModel()
            ChangeSubdomainScreen(
                changeSubdomainHandler = changeSubdomainViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.DebugAdSettings.rootName) {
            val debugAdSettingViewModel: DebugAdSettingsViewModel = hiltViewModel()
            DebugAdSettingsScreen(
                debugAdSettingsHandler = debugAdSettingViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.CloseAccount.rootName) {
            val closeAccountViewModel: CloseAccountViewModel = hiltViewModel()
            CloseAccountScreen(
                closeAccountHandler = closeAccountViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.UploadQuality.rootName) { navBackStackEntry ->
            val viewModel: SettingsViewModel = navBackStackEntry.getSharedViewModel(
                navController = currentNavController,
                parentRoute = RumbleScreens.Settings.getPath()
            )
            UploadQualityScreen(
                settingsHandler = viewModel,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.RecommendedChannelsScreen.rootName) {
            val viewModel: RecommendedChannelsViewModel = hiltViewModel()
            RecommendedChannelScreen(
                contentHandler = contentHandler,
                recommendedChannelsHandler = viewModel,
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(RumbleScreens.TopChannelsScreen.rootName) {
            val viewModel: RecommendedChannelsViewModel = hiltViewModel()
            RecommendedChannelScreen(
                contentHandler = contentHandler,
                recommendedChannelsHandler = viewModel,
                title = stringResource(id = R.string.top_channels),
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.DiscoverPlayer.rootName,
            arguments = listOf(
                navArgument(RumblePath.VIDEO_CATEGORY.path) {
                    type = NavType.StringType; defaultValue = ""
                },
                navArgument(RumblePath.CHANNEL.path) {
                    type = NavType.StringType; defaultValue = ""
                })

        ) {
            val discoverPlayerViewModel: DiscoverPlayerViewModel = hiltViewModel()
            DiscoverPlayerScreen(
                discoverPlayerHandler = discoverPlayerViewModel,
                activityHandler = activityHandler,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                onVideoClick = {
                    contentHandler.onOpenVideoDetails(videoId = it.id)
                },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                }
            )
        }
        composable(RumbleScreens.VideoListScreen.rootName) {
            val viewModel: VideoListViewModel = hiltViewModel()
            VideoListScreen(
                videoListHandler = viewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                onVideoClick = {
                    contentHandler.onOpenVideoDetails(videoId = it.id)
                },
                onChannelClick = { currentNavController.navigate(RumbleScreens.Channel.getPath(it)) }
            )
        }
        composable(
            RumbleScreens.EarningsScreen.rootName
        ) {
            val viewModel: EarningsViewModel = hiltViewModel()
            EarningsScreen(
                earningsHandler = viewModel,
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.ProfileNotifications.rootName
        ) {
            val profileNotificationsViewModel: ProfileNotificationsViewModel = hiltViewModel()
            ProfileNotificationsScreen(
                profileNotificationsHandler = profileNotificationsViewModel,
                onChannelClick = { currentNavController.navigate(RumbleScreens.Channel.getPath(it)) },
                onVideoClick = {
                    contentHandler.onOpenVideoDetails(videoId = it.id)
                },
                onBackClick = { currentNavController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.CameraGalleryScreen.rootName
        ) {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            CameraGalleryScreen(
                cameraHandler = cameraViewModel,
                contentHandler = contentHandler,
                authHandler = authViewModel,
                onOpenCameraMode = { currentNavController.navigate(RumbleScreens.CameraMode.rootName) },
                onPreviewRecording = { uri ->
                    currentNavController.navigate(RumbleScreens.VideoUploadPreview.getPath(uri))
                },
                onNavigateToRegistration = { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToAgeVerification = {
                    parentController.navigate(
                        LandingScreens.AgeVerificationScreen.getPath(
                            popOnAgeVerification = true
                        )
                    )
                },
                onNavigateToLogin = {
                    activityHandler.onPauseVideo()
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                }
            )
        }
        composable(
            RumbleScreens.CameraMode.rootName
        ) { navBackStackEntry ->
            CameraModeScreen(
                cameraHandler = getCameraUploadViewModel(navBackStackEntry, currentNavController),
                contentHandler = contentHandler,
                onPreviewRecording = { uri ->
                    currentNavController.navigate(RumbleScreens.VideoUploadPreview.getPath(uri))
                },
            ) {
                currentNavController.navigateUp()
            }
        }
        composable(
            RumbleScreens.VideoUploadPreview.rootName,
            arguments = listOf(navArgument(RumblePath.VIDEO_URL.path) { type = NavType.StringType })
        ) { navBackStackEntry ->
            VideoPreviewScreen(
                cameraHandler = getCameraUploadViewModel(navBackStackEntry, currentNavController),
                contentHandler = contentHandler,
                uri = navBackStackEntry.arguments?.getString(RumblePath.VIDEO_URL.path) ?: "",
                onNextStep = { currentNavController.navigate(RumbleScreens.CameraUploadStepOne.rootName) },
            ) {
                currentNavController.navigateUp()
            }
        }
        composable(
            RumbleScreens.CategoryScreen.rootName,
            arguments = listOf(
                navArgument(RumblePath.VIDEO_CATEGORY.path) { type = NavType.StringType },
                navArgument(RumblePath.PARAMETER.path) { type = NavType.BoolType }
            )
        ) {
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            CategoryScreen(
                categoryHandler = categoryViewModel,
                contentHandler = contentHandler,
                onSearch = { currentNavController.navigate(RumbleScreens.Search.getPath(parent = RumbleScreens.CategoryScreen.rootName)) },
                onBackClick = { currentNavController.navigateUp() },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        contentHandler.onOpenVideoDetails(videoId = it.id)
                    }
                },
                onViewCategory = {
                    currentNavController.navigate(RumbleScreens.CategoryScreen.getPath(it, false))
                }
            )
        }
        composable(
            RumbleScreens.BrowseAllCategories.rootName,
            arguments = listOf(
                navArgument(RumblePath.TYPE.path) { type = NavType.StringType }
            )
        ) {
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            BrowseCategoriesScreen(
                categoryHandler = categoryViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
                onSearch = { currentNavController.navigate(RumbleScreens.Search.getPath(parent = RumbleScreens.BrowseAllCategories.rootName)) },
                onChannelClick = { channelId ->
                    currentNavController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        contentHandler.onOpenVideoDetails(videoId = it.id)
                    }
                },
                onViewCategory = {
                    currentNavController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                }
            )
        }
        composable(
            route = RumbleScreens.NotificationSettings.rootName
        ) {
            val notificationSettingsViewModel: NotificationSettingsViewModel = hiltViewModel()
            NotificationSettingsScreen(
                handler = notificationSettingsViewModel,
                contentHandler = contentHandler,
                onBackClick = { currentNavController.navigateUp() },
            )
        }
    }

@Composable
private fun getCameraUploadViewModel(
    navBackStackEntry: NavBackStackEntry,
    navController: NavHostController
): CameraViewModel {
    val parentEntry = remember(navBackStackEntry) {
        navController.getBackStackEntry(RumbleScreens.CameraGalleryScreen.rootName)
    }
    return hiltViewModel(parentEntry)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.getSharedViewModel(
    navController: NavHostController,
    parentRoute: String
): T {
    val destinationList = navController.currentBackStack.value
    destinationList.find { it.destination.route == parentRoute }?.let {
        return hiltViewModel(it)
    } ?: run {
        return hiltViewModel()
    }
}




