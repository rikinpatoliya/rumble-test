package com.rumble.battles.library.presentation.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleLogoSearchHeaderView
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.library.presentation.playlist.PlayListTypeRefresh
import com.rumble.battles.library.presentation.views.LibrarySectionView
import com.rumble.battles.library.presentation.views.PlayListsSectionView
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthPlaceholderScreen
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.network.queryHelpers.PlayListType
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LibraryScreen(
    activityHandler: RumbleActivityHandler,
    libraryHandler: LibraryHandler,
    contentHandler: ContentHandler,
    authHandler: AuthHandler,
    listState: LazyListState,
    playListTypeRefresh: PlayListTypeRefresh? = null,
    playListEntityRefresh: PlayListEntity? = null,
    onSearch: () -> Unit,
    onChannelClick: (id: String) -> Unit,
    onVideoClick: (id: Feed) -> Unit,
    onViewAll: (VideoList) -> Unit,
    onViewPlayLists: () -> Unit,
    onViewPlayList: (playListId: String) -> Unit,
    bottomSheetState: ModalBottomSheetState,
    onViewNotifications: () -> Unit,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {

    val state by libraryHandler.state.collectAsStateWithLifecycle()
    val userUIState by contentHandler.userUIState.collectAsStateWithLifecycle()
    val activityHandlerState by activityHandler.activityHandlerState.collectAsStateWithLifecycle()
    val alertDialogState by libraryHandler.alertDialogState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playListTypeRefresh) {
        when (playListTypeRefresh) {
            PlayListTypeRefresh.PlayLists -> libraryHandler.refreshPlayListsVideos()
            PlayListTypeRefresh.WatchHistory -> libraryHandler.refreshWatchHistory()
            PlayListTypeRefresh.PlayList -> {
                playListEntityRefresh?.let { libraryHandler.onPlayListUpdated(it) }
            }

            null -> {}
        }
    }

    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        } else {
            contentHandler.onNavigateHome()
        }
    }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.PlayListDeleted ||
                it is ContentScreenVmEvent.PlayListCreated
            ) {
                libraryHandler.refreshPlayListsVideos()
            } else if (it is ContentScreenVmEvent.WatchHistoryCleared) {
                libraryHandler.refreshWatchHistory()
            } else if (it is ContentScreenVmEvent.PlayListUpdated) {
                libraryHandler.onPlayListUpdated(it.playListEntity)
            } else if (it is ContentScreenVmEvent.ScrollToTop) {
                listState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(Unit) {
        libraryHandler.eventFlow.collectLatest {
            when (it) {
                is LibraryScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = it.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is LibraryScreenVmEvent.PlayVideo -> onVideoClick(it.videoEntity)
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        if (userUIState.isLoggedIn) {
            RumbleLogoSearchHeaderView(
                hasUnreadNotifications = activityHandlerState.hasUnreadNotifications,
                onSearch = onSearch,
                onNotifications = {
                    activityHandler.clearNotifications()
                    onViewNotifications()
                },
            )

            SwipeRefresh(
                modifier = Modifier
                    .testTag(SwipeRefreshTag)
                    .fillMaxSize(),
                state = rememberSwipeRefreshState(
                    state.watchHistoryLoading ||
                        state.purchasesLoading ||
                        state.watchLaterLoading ||
                        state.playListsLoading,// ||
                    //state.likedVideosLoading,
                ),
                onRefresh = {
                    libraryHandler.refresh()
                    activityHandler.loadNotificationState()
                },
                indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
            ) {
                BoxWithConstraints {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            top = paddingMedium,
                            start = CalculatePaddingForTabletWidth(maxWidth),
                            end = CalculatePaddingForTabletWidth(maxWidth),
                        )
                    ) {
                        items(LibraryScreenSection.values()) { section ->
                            when (section) {
                                LibraryScreenSection.WatchHistory -> {
                                    LibrarySectionView(
                                        modifier = Modifier
                                            .padding(horizontal = paddingMedium)
                                            .padding(bottom = paddingLarge),
                                        libraryScreenSection = LibraryScreenSection.WatchHistory,
                                        videoEntityList = state.watchHistoryVideos,
                                        loading = state.watchHistoryLoading,
                                        error = state.watchHistoryError,
                                        onVideoClick = libraryHandler::onVideoItemClick,
                                        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                                        onRefresh = libraryHandler::refreshWatchHistory,
                                        onViewAll = { onViewPlayList(PlayListType.WATCH_HISTORY.id) },
                                        onImpression = { libraryHandler.onVideoCardImpression(it) }
                                    )
                                }

                                LibraryScreenSection.Purchases -> {
                                    if (state.purchasesError || state.purchasesVideos.isNotEmpty()) {
                                        LibrarySectionView(
                                            modifier = Modifier
                                                .padding(horizontal = paddingMedium)
                                                .padding(bottom = paddingLarge),
                                            libraryScreenSection = LibraryScreenSection.Purchases,
                                            videoEntityList = state.purchasesVideos,
                                            loading = state.purchasesLoading,
                                            error = state.purchasesError,
                                            onVideoClick = libraryHandler::onVideoItemClick,
                                            onMoreClick = {
                                                contentHandler.onMoreVideoOptionsClicked(
                                                    it
                                                )
                                            },
                                            onRefresh = libraryHandler::refreshPurchasesVideos,
                                            onViewAll = { onViewAll(VideoList.LibraryPurchases) },
                                            onImpression = { libraryHandler.onVideoCardImpression(it) }
                                        )
                                    }
                                }

                                LibraryScreenSection.WatchLater -> {
                                    LibrarySectionView(
                                        modifier = Modifier
                                            .padding(horizontal = paddingMedium)
                                            .padding(bottom = paddingLarge),
                                        libraryScreenSection = LibraryScreenSection.WatchLater,
                                        videoEntityList = state.watchLaterVideos,
                                        loading = state.watchLaterLoading,
                                        error = state.watchLaterError,
                                        onVideoClick = libraryHandler::onVideoItemClick,
                                        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                                        onRefresh = libraryHandler::refreshWatchLaterVideos,
                                        onViewAll = { onViewPlayList(PlayListType.WATCH_LATER.id) },
                                        onImpression = { libraryHandler.onVideoCardImpression(it) }
                                    )
                                }

                                LibraryScreenSection.Playlists -> {
                                    PlayListsSectionView(
                                        modifier = Modifier
                                            .padding(horizontal = paddingMedium)
                                            .padding(bottom = paddingLarge),
                                        libraryScreenSection = LibraryScreenSection.Playlists,
                                        playListEntities = state.playListEntities,
                                        loading = state.playListsLoading,
                                        error = state.playListsError,
                                        onViewChannel = onChannelClick,
                                        onPlayListClick = onViewPlayList,
                                        onMoreClick = contentHandler::onMorePlayListOptions,
                                        onRefresh = libraryHandler::refreshPlayListsVideos,
                                        onViewAll = { onViewPlayLists() },
                                    )
                                }

                                //TODO: @LibraryFeatureWIP Hiding LikedVideos
                                LibraryScreenSection.LikedVideos -> {
//                                LibrarySectionView(
//                                    modifier = Modifier
//                                        .padding(horizontal = paddingMedium)
//                                        .padding(bottom = paddingLarge),
//                                    libraryScreenSection = LibraryScreenSection.LikedVideos,
//                                    videoEntityList = state.likedVideos,
//                                    loading = state.likedVideosLoading,
//                                    error = state.likedVideosError,
//                                    onVideoClick = libraryHandler::onVideoItemClick,
//                                    onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
//                                    onRefresh = libraryHandler::refreshLikedVideos,
//                                    onViewAll = { onViewAll(VideoList.LibraryLiked) },
//                                    onImpression = { libraryHandler.onVideoCardImpression(it) }
//                                )
                                }
                            }
                        }
                        item {
                            BottomNavigationBarScreenSpacer()
                        }
                    }
                }
            }
        } else {
            AuthPlaceholderScreen(
                modifier = Modifier.fillMaxSize(),
                authHandler = authHandler,
                onNavigateToRegistration = onNavigateToRegistration,
                onEmailLogin = onNavigateToLogin
            )
        }
    }

    if (alertDialogState.show) {
        when (val reason = alertDialogState.alertDialogReason) {
            is LibraryAlertReason.RestrictedContentReason -> {
                RumbleAlertDialog(
                    onDismissRequest = { },
                    title = stringResource(id = R.string.mature_content),
                    text = stringResource(id = R.string.must_be_18),
                    testTag = MatureContentPopupTag,
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.cancel),
                            dialogActionType = DialogActionType.Neutral,
                            withSpacer = true,
                            width = commentActionButtonWidth,
                            action = libraryHandler::onCancelRestricted
                        ),
                        DialogActionItem(
                            text = stringResource(R.string.start_watching),
                            dialogActionType = DialogActionType.Positive,
                            width = commentActionButtonWidth,
                            action = { libraryHandler.onWatchRestricted(reason.videoEntity) }
                        )
                    )
                )
            }
        }
    }
    RumbleSnackbarHost(snackBarHostState)
}