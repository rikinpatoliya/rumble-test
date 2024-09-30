package com.rumble.battles.content.presentation

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
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
import com.rumble.battles.camera.presentation.UploadChannelSelectionScreen
import com.rumble.battles.camera.presentation.VideoPreviewScreen
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsScreen
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsViewModel
import com.rumble.battles.commonViews.DefaultSystemBarIconsColor
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleWebView
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
import com.rumble.battles.library.presentation.views.LibraryOnboardingView
import com.rumble.battles.library.presentation.views.PLAY_LIST_ENTITY
import com.rumble.battles.library.presentation.views.PLAY_LIST_TYPE_REFRESH
import com.rumble.battles.library.presentation.views.PlayListScreen
import com.rumble.battles.library.presentation.views.PlayListsScreen
import com.rumble.battles.livechat.presentation.LiveChatViewModel
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthViewModel
import com.rumble.battles.navigation.LandingScreens
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
import com.rumble.domain.onboarding.domain.domainmodel.ShowLibraryOnboarding
import com.rumble.domain.onboarding.domain.domainmodel.ShowOnboardingPopups
import com.rumble.theme.paddingGiant
import com.rumble.utils.extension.navigationSafeEncode
import com.rumble.utils.replaceUrlParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ContentScreen(
    activityHandler: RumbleActivityHandler,
    contentHandler: ContentHandler,
    authHandler: AuthHandler,
    parentController: NavController
) {
    val navController = rememberNavController()
    val displaySearch = rememberSaveable { mutableStateOf(true) }
    val displayModeSwitch = rememberSaveable { mutableStateOf(false) }
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
            if (it is RumbleEvent.NavigateToVideoDetailsFromNotification) {
                navController.navigate(
                    RumbleScreens.VideoDetailsScreen.getPath(
                        it.videoEntity.id,
                        (context as Activity).requestedOrientation
                    )
                )
            } else if (it is RumbleEvent.UnexpectedError) {
                snackBarHostState.showRumbleSnackbar(
                    context.getString(R.string.generic_error_message_try_later)
                )
            } else if (it is RumbleEvent.OpenWebView) {
                navController.navigate(
                    RumbleScreens.RumbleWebViewScreen.getPath(
                        it.url
                    )
                )
            }
        }
    }

    LaunchedEffect(contentHandler.userUIState) {
        contentHandler.userUIState.collectLatest {
            if (it.isLoggedIn) {
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
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(event.messageId)
                    )
                }

                ContentScreenVmEvent.NavigateToLibrary -> {
                    navController.navigate(
                        route = RumbleScreens.Library.rootName,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(navController.graph.findStartDestination().id, true)
                            .build()
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

                is ContentScreenVmEvent.NavigateToChannelDetails -> {
                    activityHandler.onDeepLinkNavigated()
                    navController.navigate(RumbleScreens.Channel.getPath(event.channelId))
                    contentHandler.onDeepLinkNavigated()
                }

                is ContentScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
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

                is ContentScreenVmEvent.PlayListUpdated -> {}
                is ContentScreenVmEvent.PlayListCreated -> {}
                is ContentScreenVmEvent.ChannelNotificationsUpdated -> {}
                is ContentScreenVmEvent.SortFollowingTypeUpdated -> {}
            }
        }
    }

    DefaultSystemBarIconsColor()

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetOverNavBarContent(
                bottomSheetState = bottomSheetState,
                bottomSheetData = bottomSheetUiState.data,
                contentHandler = contentHandler,
                authHandler = authHandler,
                activityHandler = activityHandler,
                navController = navController,
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
                onNavigateToRegistration =  { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
            )
        }) {
        Scaffold(
            modifier = Modifier
                .navigationBarsPadding()
                .semantics { testTagsAsResourceId = true }
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                graph = createNavigationGraph(
                    parentController,
                    navController,
                    bottomSheetState,
                    contentHandler,
                    activityHandler,
                    contentHandler::onDiscoverIconMeasured,
                    contentHandler::onLibraryIconMeasured,
                ) { screen ->
                    displayModeSwitch.value = screen == RumbleScreens.Profile.rootName
                    displaySearch.value = screen == RumbleScreens.Feeds.rootName
                    navController.navigate(screen) {
                        if (screen == RumbleScreens.Feeds.rootName)
                            navController.popBackStack()
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                exitTransition = { ExitTransition.None },
                enterTransition = { EnterTransition.None }
            )
        }
    }

    if (activityHandler.isLaunchedFromNotification.not()) {
        if (onboardingViewState.value == ShowLibraryOnboarding) {
            LibraryOnboardingView(
                onClose = contentHandler::onLibrary,
                onLibrary = { contentHandler.onLibrary(true) }
            )
        } else if (onboardingViewState.value is ShowOnboardingPopups) {
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
    RumbleSnackbarHost(snackBarHostStateWithPadding, modifier = Modifier.padding(bottom = snackBarHostPadding))

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
    parentController: NavController,
    navController: NavHostController,
    bottomSheetState: ModalBottomSheetState,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
    onDiscoverIconCenter: ((Offset) -> Unit),
    onLibraryIconCenter: ((Offset) -> Unit),
    onNavigationItemClicked: (String) -> Unit,
) =
    navController.createGraph(startDestination = RumbleScreens.Feeds.rootName) {
        composable(
            route = RumbleScreens.Feeds.rootName,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { navBackStackEntry ->
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val feedListViewModel: HomeViewModel = hiltViewModel()
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(RumbleScreens.Feeds.rootName)
            }
            val recommendedChannelsHandler =
                hiltViewModel<RecommendedChannelsViewModel>(parentEntry)

            HomeScreen(
                activityHandler = activityHandler,
                homeHandler = feedListViewModel,
                contentHandler = contentHandler,
                recommendedChannelsHandler = recommendedChannelsHandler,
                onSearch = {
                    navController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onFreshContentChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                    //Temp rollback to old navigation
//                    navController.navigate(
//                        RumbleScreens.DiscoverPlayer.getPath(
//                            category = DiscoverPlayerVideoListSource.FreshContent.name,
//                            channelId = channelId
//                        )
//                    )
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                    } else if (it is AdEntity) navController.navigate(
                        RumbleScreens.RumbleWebViewScreen.getPath(
                            it.adUrl
                        )
                    )
                },
                onViewAllRecommendedChannelsClick = {
                    navController.navigate(RumbleScreens.RecommendedChannelsScreen.rootName)
                },
                onSearchIconGlobalMeasured = contentHandler::onSearchIconMeasured,
                onFollowingIconGlobalMeasured = contentHandler::onFollowingIconMeasured,
                onNavigationItemClicked = onNavigationItemClicked,
                onDiscoverIconCenter = onDiscoverIconCenter,
                onLibraryIconCenter = onLibraryIconCenter,
                onViewNotifications = {
                    navController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
            )
        }
        composable(
            route = RumbleScreens.Discover.rootName,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val discoverViewModel: DiscoverViewModel = hiltViewModel()
            DiscoverScreen(
                activityHandler = activityHandler,
                discoverHandler = discoverViewModel,
                contentHandler = contentHandler,
                onSearch = {
                    navController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onViewCategory = {
                    navController.navigate(RumbleScreens.VideoListScreen.getPath(it.name))
                },
                onViewTopChannels = {
                    navController.navigate(RumbleScreens.TopChannelsScreen.rootName)
                },
                onBrowseCategory = {
                    navController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                },
                onBrowseAllCategories = {
                    navController.navigate(
                        RumbleScreens.BrowseAllCategories.getPath(CategoryDisplayType.CATEGORIES)
                    )
                },
                onNavigationItemClicked = onNavigationItemClicked,
                onViewNotifications = {
                    navController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
                onNavigateToSettings = {
                    navController.navigate(RumbleScreens.Settings.getPath())
                },
            )
        }
        composable(RumbleScreens.CameraUploadStepOne.rootName) { navBackStackEntry ->
            CameraUploadStepOneScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onSelectChannel = { navController.navigate(RumbleScreens.UploadChannelSelection.rootName) },
                onNextStep = { navController.navigate(RumbleScreens.CameraUploadStepTwo.rootName) },
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.CameraUploadStepTwo.rootName) { navBackStackEntry ->
            CameraUploadStepTwoScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                activityHandler = activityHandler,
                onPublishClick = {
                    navController.navigate(
                        route = RumbleScreens.Library.rootName,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(navController.graph.findStartDestination().id, true).build()
                    )
                },
                onSelectLicense = { navController.navigate(RumbleScreens.UploadLicenseSelection.rootName) },
                onSelectVisibility = { navController.navigate(RumbleScreens.UploadVisibilitySelection.rootName) },
                onSelectSchedule = { navController.navigate(RumbleScreens.UploadScheduleSelection.rootName) },
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadChannelSelection.rootName) { navBackStackEntry ->
            UploadChannelSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadLicenseSelection.rootName) { navBackStackEntry ->
            CameraUploadLicenceSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadScheduleSelection.rootName) { navBackStackEntry ->
            CameraUploadScheduleSelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.UploadVisibilitySelection.rootName) { navBackStackEntry ->
            CameraUploadVisibilitySelectionScreen(
                cameraUploadHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onBackClick = { navController.navigateUp() },
            )
        }
        composable(RumbleScreens.Videos.rootName) { backStackEntry ->
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val myVideosViewModel: MyVideosViewModel = hiltViewModel()
            val newImageUri: Uri? by backStackEntry
                .savedStateHandle
                .getLiveData<Uri?>(NEW_IMAGE_URI_KEY)
                .observeAsState()
            MyVideosScreen(
                currentDestinationRoute = navController.currentDestination?.route,
                myVideosHandler = myVideosViewModel,
                onVideoClick = {
                    if (it is VideoEntity)
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                },
                newImageUri = newImageUri,
                contentHandler = contentHandler,
                bottomSheetState = bottomSheetState,
            )
        }
        composable(RumbleScreens.Library.rootName) { backStackEntry ->
            val orientation = (LocalContext.current as Activity).requestedOrientation
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
                    navController.navigate(RumbleScreens.Search.getPath())
                },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity)
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                },
                onViewAll = {
                    navController.navigate(RumbleScreens.VideoListScreen.getPath(it.name))
                },
                onViewPlayLists = {
                    navController.navigate(RumbleScreens.PlayListsScreen.rootName)
                },
                onViewPlayList = {
                    navController.navigate(RumbleScreens.PlayListScreen.getPath(it))
                },
                bottomSheetState = bottomSheetState,
                onNavigationItemClicked = onNavigationItemClicked,
                onViewNotifications = {
                    navController.navigate(RumbleScreens.ProfileNotifications.rootName)
                },
                onNavigateToSettings = {
                    navController.navigate(RumbleScreens.Settings.getPath())
                },
                onNavigateToRegistration =  { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToLogin = {
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
                navController = navController,
                playListsHandler = playListsViewModel,
                contentHandler = contentHandler,
                playListTypeRefresh = playListTypeRefresh,
                playListEntityRefresh = playListEntityRefresh,
                onViewChannel = { navController.navigate(RumbleScreens.Channel.getPath(it)) },
                onViewPlayList = {
                    navController.navigate(RumbleScreens.PlayListScreen.getPath(it))
                },
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.PlayListScreen.rootName,
            arguments = listOf(navArgument(RumblePath.PLAYLIST.path) { type = NavType.StringType })
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val playListViewModel: PlayListViewModel = hiltViewModel()
            PlayListScreen(
                navController = navController,
                playListHandler = playListViewModel,
                contentHandler = contentHandler,
                onVideoClick = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onChannelClick = { navController.navigate(RumbleScreens.Channel.getPath(it)) },
                onPlayAllClick = { entity, playListId ->
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            videoId = entity.id,
                            orientation = orientation,
                            playListId = playListId
                        )
                    )
                },
                onShuffleClick = { entity, playListId ->
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            videoId = entity.id,
                            orientation = orientation,
                            playListId = playListId,
                            shufflePlayList = true
                        )
                    )
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable(RumbleScreens.EditProfile.rootName) {
            val editProfileViewModel: EditProfileViewModel = hiltViewModel()
            EditProfileScreen(
                editProfileHandler = editProfileViewModel,
                onBackClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(NEW_IMAGE_URI_KEY, it)
                    navController.navigateUp()
                },
            )
        }
        composable(RumbleScreens.Subscriptions.rootName) {
            val subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel()
            SubscriptionsScreen(
                subscriptionsScreenHandler = subscriptionsViewModel,
                contentHandler = contentHandler,
                bottomSheetState = bottomSheetState,
                onBackClick = { navController.navigateUp() },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onSearch = { navController.navigate(RumbleScreens.Search.getPath()) }
            )
        }
        composable(RumbleScreens.Referrals.rootName) {
            val referralsViewModel: ReferralsViewModel = hiltViewModel()
            ReferralsScreen(
                handler = referralsViewModel,
                onBackClick = { navController.navigateUp() },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
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
                onBackClick = { navController.navigateUp() },
                onNavigate = { navController.navigate(it) },
            )
        }
        composable(
            RumbleScreens.Channel.rootName,
            arguments = listOf(navArgument(RumblePath.CHANNEL.path) { type = NavType.StringType })
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val channelDetailsViewModel: ChannelDetailsViewModel = hiltViewModel()
            ChannelDetailsScreen(
                currentDestinationRoute = navController.currentDestination?.route,
                channelDetailsHandler = channelDetailsViewModel,
                contentHandler = contentHandler,
                activityHandler = activityHandler,
                onBackClick = { navController.navigateUp() },
                onVideoClick = {
                    if (it is VideoEntity)
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
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
                    navController.navigate(navigationId)
                },
                contentHandler = contentHandler,
                onNavigationItemClicked = onNavigationItemClicked,
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
                onNavigateToLogin = {
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                },
                onNavigateToSettings = {
                    navController.navigate(RumbleScreens.Settings.getPath())
                },
                onViewNotifications = {
                    navController.navigate(RumbleScreens.ProfileNotifications.rootName)
                }
            )
        }
        composable(
            RumbleScreens.RumbleWebViewScreen.rootName,
            arguments = listOf(navArgument(RumblePath.URL.path) { type = NavType.StringType })
        ) { backStackEntry ->
            RumbleWebView(url = backStackEntry.arguments?.getString(RumblePath.URL.path) ?: "")
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
                    navController.popBackStack(parent, false)
                    if (navDest.isBlank()) navController.navigate(
                        RumbleScreens.CombinedSearchResult.getPath(query)
                    )
                    else navController.navigate(
                        replaceUrlParameter(
                            navDest,
                            RumblePath.QUERY.path,
                            query.navigationSafeEncode()
                        )
                    )
                },
                onViewChannel = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBrowseCategory = {
                    navController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                }
            ) {
                navController.navigateUp()
            }
        }
        composable(
            RumbleScreens.CombinedSearchResult.rootName,
            arguments = listOf(navArgument(RumblePath.QUERY.path) { type = NavType.StringType })
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val combineSearchResultViewModel: CombineSearchResultViewModel = hiltViewModel()
            CombineSearchResultScreen(
                handler = combineSearchResultViewModel,
                contentHandler = contentHandler,
                onSearch = {
                    navController.navigate(RumbleScreens.Search.getPath(it))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                    }
                },
                onViewChannels = {
                    navController.navigate(RumbleScreens.ChannelSearchScreen.getPath(it))
                },
                onViewVideos = { path, filters ->
                    navController.navigate(
                        RumbleScreens.VideoSearchScreen.getPath(
                            path,
                            filters.sortSelection.name,
                            filters.filterSelection.name,
                            filters.durationSelection.name
                        )
                    )
                },
                onViewChannel = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                }) {
                navController.navigateUp()
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
                    navController.navigate(
                        RumbleScreens.Search.getPath(
                            query.navigationSafeEncode(),
                            RumbleScreens.ChannelSearchScreen.getPath(RumblePath.QUERY.path)
                        )
                    )
                }, onViewChannel = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                }) {
                navController.navigateUp()
            }
        }
        composable(
            RumbleScreens.VideoSearchScreen.rootName,
            arguments = listOf(navArgument(RumblePath.QUERY.path) { type = NavType.StringType },
                navArgument(RumblePath.SORT.path) { type = NavType.StringType },
                navArgument(RumblePath.UPLOAD_DATE.path) { type = NavType.StringType },
                navArgument(RumblePath.DURATION.path) { type = NavType.StringType })
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val videoSearchViewModel: VideosSearchViewModel = hiltViewModel()
            VideosSearchScreen(
                handler = videoSearchViewModel,
                contentHandler = contentHandler,
                onSearch = { query ->
                    navController.navigate(
                        RumbleScreens.Search.getPath(
                            query.navigationSafeEncode(),
                            RumbleScreens.VideoSearchScreen.getPath("", "", "", "")
                        )
                    )
                },
                onViewVideo = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onBack = { navController.navigateUp() },
                onImpression = videoSearchViewModel::onVideoCardImpression
            )
        }
        composable(RumbleScreens.Credits.rootName) {
            val creditsScreenViewModel: CreditsScreenViewModel = hiltViewModel()
            CreditsScreen(
                creditsScreenHandler = creditsScreenViewModel,
                activityHandler = activityHandler,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.ChangeEmail.rootName) {
            val changeEmailHandler: ChangeEmailViewModel = hiltViewModel()
            ChangeEmailScreen(
                changeEmailHandler = changeEmailHandler,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.ChangePassword.rootName) {
            val changePasswordHandler: ChangePasswordViewModel = hiltViewModel()
            ChangePasswordScreen(
                changePasswordHandler = changePasswordHandler,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.ChangeSubdomain.rootName) {
            val changeSubdomainViewModel: ChangeSubdomainViewModel = hiltViewModel()
            ChangeSubdomainScreen(
                changeSubdomainHandler = changeSubdomainViewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.CloseAccount.rootName) {
            val closeAccountViewModel: CloseAccountViewModel = hiltViewModel()
            CloseAccountScreen(
                closeAccountHandler = closeAccountViewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.UploadQuality.rootName) { navBackStackEntry ->
            val viewModel: SettingsViewModel = navBackStackEntry.getSharedViewModel(
                navController = navController,
                parentRoute = RumbleScreens.Settings.getPath()
            )
            UploadQualityScreen(
                settingsHandler = viewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.RecommendedChannelsScreen.rootName) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(RumbleScreens.Feeds.rootName)
            }
            val viewModel =
                hiltViewModel<RecommendedChannelsViewModel>(parentEntry)
            RecommendedChannelScreen(
                contentHandler = contentHandler,
                recommendedChannelsHandler = viewModel,
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(RumbleScreens.TopChannelsScreen.rootName) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(RumbleScreens.Feeds.rootName)
            }
            val viewModel =
                hiltViewModel<RecommendedChannelsViewModel>(parentEntry)
            RecommendedChannelScreen(
                contentHandler = contentHandler,
                recommendedChannelsHandler = viewModel,
                title = stringResource(id = R.string.top_channels),
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onBackClick = { navController.navigateUp() }
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
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val discoverPlayerViewModel: DiscoverPlayerViewModel = hiltViewModel()
            DiscoverPlayerScreen(
                discoverPlayerHandler = discoverPlayerViewModel,
                onBackClick = { navController.navigateUp() },
                onVideoClick = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                }
            )
        }
        composable(RumbleScreens.VideoListScreen.rootName) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val viewModel: VideoListViewModel = hiltViewModel()
            VideoListScreen(
                videoListHandler = viewModel,
                contentHandler = contentHandler,
                onBackClick = { navController.navigateUp() },
                onVideoClick = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onChannelClick = { navController.navigate(RumbleScreens.Channel.getPath(it)) }
            )
        }
        composable(
            RumbleScreens.VideoDetailsScreen.rootName,
            arguments = listOf(
                navArgument(RumblePath.VIDEO.path) { type = NavType.LongType },
                navArgument(RumblePath.ORIENTATION.path) { type = NavType.IntType },
                navArgument(RumblePath.PLAYLIST.path) { type = NavType.StringType },
                navArgument(RumblePath.PLAYLIST_SHUFFLE.path) { type = NavType.BoolType }
            )
        ) {
            val videoDetailsViewModel: VideoDetailsViewModel = hiltViewModel()
            val liveChatViewModel: LiveChatViewModel = hiltViewModel()
            VideoDetailsScreen(
                activityHandler = activityHandler,
                handler = videoDetailsViewModel,
                contentHandler = contentHandler,
                liveChatHandler = liveChatViewModel,
                contentBottomSheetState = bottomSheetState,
                onBackClick = { navController.navigateUp() },
                onChannelClick = { navController.navigate(RumbleScreens.Channel.getPath(it)) },
                onCategoryClick = {
                    navController.navigate(
                        RumbleScreens.CategoryScreen.getPath(
                            it,
                            false
                        )
                    )
                },
                onTagClick = { navController.navigate(RumbleScreens.Search.getPath(it)) },
            )
        }
        composable(
            RumbleScreens.EarningsScreen.rootName
        ) {
            val viewModel: EarningsViewModel = hiltViewModel()
            EarningsScreen(
                earningsHandler = viewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.ProfileNotifications.rootName
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val profileNotificationsViewModel: ProfileNotificationsViewModel = hiltViewModel()
            ProfileNotificationsScreen(
                profileNotificationsHandler = profileNotificationsViewModel,
                onChannelClick = { navController.navigate(RumbleScreens.Channel.getPath(it)) },
                onVideoClick = {
                    navController.navigate(
                        RumbleScreens.VideoDetailsScreen.getPath(
                            it.id,
                            orientation
                        )
                    )
                },
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(
            RumbleScreens.CameraGalleryScreen.rootName
        ) {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            CameraGalleryScreen(
                cameraHandler = cameraViewModel,
                authHandler = authViewModel,
                onClose = { navController.navigateUp() },
                onOpenCameraMode = { navController.navigate(RumbleScreens.CameraMode.rootName) },
                onPreviewRecording = { uri ->
                    navController.navigate(RumbleScreens.VideoUploadPreview.getPath(uri))
                },
                onNavigationItemClicked = onNavigationItemClicked,
                onNavigateToRegistration =  { loginType, userId, token, email ->
                    parentController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            loginType,
                            userId,
                            token,
                            email
                        )
                    )
                },
                onNavigateToLogin = {
                    parentController.navigate(LandingScreens.LoginScreen.getPath())
                }
            )
        }
        composable(
            RumbleScreens.CameraMode.rootName
        ) { navBackStackEntry ->
            CameraModeScreen(
                cameraHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                onPreviewRecording = { uri ->
                    navController.navigate(RumbleScreens.VideoUploadPreview.getPath(uri))
                },
            ) {
                navController.navigateUp()
            }
        }
        composable(
            RumbleScreens.VideoUploadPreview.rootName,
            arguments = listOf(navArgument(RumblePath.VIDEO_URL.path) { type = NavType.StringType })
        ) { navBackStackEntry ->
            VideoPreviewScreen(
                cameraHandler = getCameraUploadViewModel(navBackStackEntry, navController),
                uri = navBackStackEntry.arguments?.getString(RumblePath.VIDEO_URL.path) ?: "",
                onNextStep = { navController.navigate(RumbleScreens.CameraUploadStepOne.rootName) },
            ) {
                navController.navigateUp()
            }
        }
        composable(
            RumbleScreens.CategoryScreen.rootName,
            arguments = listOf(
                navArgument(RumblePath.VIDEO_CATEGORY.path) { type = NavType.StringType },
                navArgument(RumblePath.PARAMETER.path) { type = NavType.BoolType }
            )
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            CategoryScreen(
                categoryHandler = categoryViewModel,
                contentHandler = contentHandler,
                onSearch = { navController.navigate(RumbleScreens.Search.getPath(parent = RumbleScreens.CategoryScreen.rootName)) },
                onBackClick = { navController.navigateUp() },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                    }
                },
                onViewCategory = {
                    navController.navigate(RumbleScreens.CategoryScreen.getPath(it, false))
                }
            )
        }
        composable(
            RumbleScreens.BrowseAllCategories.rootName,
            arguments = listOf(
                navArgument(RumblePath.TYPE.path) { type = NavType.StringType }
            )
        ) {
            val orientation = (LocalContext.current as Activity).requestedOrientation
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            BrowseCategoriesScreen(
                categoryHandler = categoryViewModel,
                contentHandler = contentHandler,
                onBackClick = { navController.navigateUp() },
                onSearch = { navController.navigate(RumbleScreens.Search.getPath(parent = RumbleScreens.BrowseAllCategories.rootName)) },
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onVideoClick = {
                    if (it is VideoEntity) {
                        navController.navigate(
                            RumbleScreens.VideoDetailsScreen.getPath(
                                it.id,
                                orientation
                            )
                        )
                    }
                },
                onViewCategory = {
                    navController.navigate(RumbleScreens.CategoryScreen.getPath(it, true))
                }
            )
        }
        composable(
            route = RumbleScreens.NotificationSettings.rootName
        ) {
            val notificationSettingsViewModel: NotificationSettingsViewModel = hiltViewModel()
            NotificationSettingsScreen(
                handler = notificationSettingsViewModel,
                onBackClick = { navController.navigateUp() },
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




