package com.rumble.battles.videolist.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.analytics.CardSize
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.VideosListTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.ListToggleView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.homeWidthRatio
import com.rumble.theme.paddingMedium
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.collectAndHandleState
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex

@Composable
fun VideoListScreen(
    videoListHandler: VideoListHandler,
    contentHandler: ContentHandler,
    onBackClick: () -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onChannelClick: (String) -> Unit,
) {
    val state by videoListHandler.state.collectAsStateWithLifecycle()
    val alertDialogState by videoListHandler.alertDialogState
    val videoPagingItems: LazyPagingItems<Feed> =
        videoListHandler.videosPagingDataFlow.collectAndHandleState(handleLoadStates = videoListHandler::handleLoadState)
    val listToggleViewStyle by videoListHandler.listToggleViewStyle.collectAsStateWithLifecycle(
        initialValue = ListToggleViewStyle.GRID
    )
    val context = LocalContext.current
    val snackBarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val listState by videoListHandler.listState
    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            videoListHandler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }
    val soundOn by videoListHandler.soundState.collectAsStateWithLifecycle(initialValue = false)
    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            videoListHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            videoListHandler.onViewResumed()
        }
    }

    val videoDetailsState by contentHandler.videoDetailsState

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) videoListHandler.onCreatePlayerForVisibleFeed()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect {
            var createPlayer = false
            val itemPosition = listState.findFirstFullyVisibleItemIndex(
                visibilityPercentage = RumbleConstants.PLAYER_MIN_VISIBILITY
            )
            val firstVisible =
                if (itemPosition == 0 && videoPagingItems.itemCount > 0) {
                    createPlayer = true
                    videoPagingItems[0]
                } else if (itemPosition >= 0 && itemPosition < videoPagingItems.itemCount) {
                    videoPagingItems[itemPosition]
                } else {
                    null
                }
            videoListHandler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) videoListHandler.onCreatePlayerForVisibleFeed()
        }
    }

    LaunchedEffect(context) {
        lifecycleOwner.lifecycle.addObserver(observer)
        videoListHandler.vmEvents.collect { event ->
            when (event) {
                is VideoListVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
                is VideoListVmEvent.PlayVideo -> {
                    onVideoClick(event.videoEntity)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoListHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val updatedEntity by videoListHandler.updatedEntity.collectAsStateWithLifecycle()
    updatedEntity?.let { updated ->
        videoPagingItems.itemSnapshotList.find { it is VideoEntity && it.id == updated.id }?.let {
            val videoEntity = it as VideoEntity
            videoEntity.userVote = updated.userVote
            videoEntity.likeNumber = updated.likeNumber
            videoEntity.dislikeNumber = updated.dislikeNumber
        }
    }

    Column(
        modifier = Modifier
            .testTag(VideosListTag)
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = getTitle(videoList = state.videoList),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
            extraContent = {
                ListToggleView(
                    modifier = Modifier
                        .padding(end = paddingMedium),
                    selectedViewStyle = listToggleViewStyle,
                    onToggleViewStyle = videoListHandler::onToggleVideoViewStyle
                )
            }
        )

        SwipeRefresh(
            modifier = Modifier.testTag(SwipeRefreshTag),
            state = rememberSwipeRefreshState(
                videoPagingItems.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                videoPagingItems.refresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(listConnection),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(paddingMedium),
                    contentPadding = PaddingValues(
                        vertical = paddingMedium,
                        horizontal = CalculatePaddingForTabletWidth(maxWidth = maxWidth),
                    ),
                    state = listState
                ) {

                    items(
                        count = videoPagingItems.itemCount,
                        key = videoPagingItems.itemKey(),
                        contentType = videoPagingItems.itemContentType()
                    ) { index ->
                        val item = videoPagingItems[index]
                        if (item != null && item is VideoEntity) {
                            if (listToggleViewStyle == ListToggleViewStyle.GRID) {
                                VideoView(
                                    modifier = Modifier
                                        .fillMaxWidth(homeWidthRatio)
                                        .padding(bottom = paddingMedium),
                                    videoEntity = item,
                                    rumblePlayer = state.rumblePlayer,
                                    soundOn = soundOn,
                                    onChannelClick = { onChannelClick(item.channelId) },
                                    onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                                    onImpression = {
                                        videoListHandler.onVideoCardImpression(it, CardSize.REGULAR)
                                    },
                                    onPlayerImpression = videoListHandler::onPlayerImpression,
                                    onClick = {
                                        videoListHandler.onVideoClick(item)
                                    },
                                    onSoundClick = videoListHandler::onSoundClick,
                                    isPremiumUser = contentHandler.isPremiumUser(),
                                )
                            } else {
                                VideoCompactView(
                                    modifier = Modifier
                                        .background(color = MaterialTheme.colors.background)
                                        .padding(
                                            start = paddingMedium,
                                            end = paddingMedium
                                        ),
                                    videoEntity = item,
                                    onViewVideo = { videoListHandler.onVideoClick(item) },
                                    onMoreClick = { contentHandler.onMoreVideoOptionsClicked(item) },
                                    onImpression = {
                                        videoListHandler.onVideoCardImpression(it, CardSize.COMPACT)
                                    }
                                )
                            }
                        }
                    }
                    videoPagingItems.apply {
                        when {
                            loadState.refresh is LoadState.NotLoading && videoPagingItems.itemCount == 0 -> {
                                item {
                                    EmptyView(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(paddingMedium),
                                        title = stringResource(id = R.string.nothing_to_see_here),
                                        text = ""
                                    )
                                }
                            }

                            loadState.refresh is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(paddingMedium),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = videoPagingItems::refresh
                                    )
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item {
                                    PageLoadingView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                    )
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = ::retry,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        BottomNavigationBarScreenSpacer()
                    }
                }
            }
        }
    }

    if (alertDialogState.show) {
        VideoListScreenDialog(
            reason = alertDialogState.alertDialogReason,
            handler = videoListHandler
        )
    }
}

@Composable
private fun VideoListScreenDialog(reason: AlertDialogReason, handler: VideoListHandler) {
    when (reason) {
        is VideoListAlertReason.RestrictedContentReason -> {
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
                        action = handler::onCancelRestricted
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.start_watching),
                        dialogActionType = DialogActionType.Positive,
                        width = commentActionButtonWidth,
                        action = { handler.onWatchRestricted(reason.videoEntity) }
                    )
                )
            )
        }
    }
}

@Composable
fun getTitle(videoList: VideoList): String {
    return stringResource(
        id = when (videoList) {
            VideoList.Viral -> R.string.viral
            VideoList.Cooking -> R.string.cooking
            VideoList.Sports -> R.string.sports
            VideoList.Gaming -> R.string.gaming
            VideoList.News -> R.string.news
            VideoList.Science -> R.string.science
            VideoList.Technology -> R.string.video_list_technology
            VideoList.Auto -> R.string.video_list_auto
            VideoList.HowTo -> R.string.video_list_howto
            VideoList.Travel -> R.string.video_list_travel
            VideoList.Music -> R.string.music
            VideoList.Vlogs -> R.string.vlogs
            VideoList.Podcasts -> R.string.podcasts
            VideoList.Entertainment -> R.string.entertainment
            VideoList.Finance -> R.string.finance
            VideoList.EditorPicks -> R.string.video_list_editor_picks
            VideoList.Live -> R.string.video_list_live
            VideoList.Popular -> R.string.video_list_popular
            VideoList.Battles -> R.string.video_list_just_for_you
            VideoList.LibraryWatchLater -> R.string.watch_later
            VideoList.LibraryWatchHistory -> R.string.watch_history
            VideoList.LibraryPurchases -> R.string.purchases
            VideoList.LibraryLiked -> R.string.liked_videos
        }
    )
}