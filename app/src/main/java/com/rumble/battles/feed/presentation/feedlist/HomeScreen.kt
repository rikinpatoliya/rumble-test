package com.rumble.battles.feed.presentation.feedlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.FeedTag
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleLogoSearchHeaderView
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.content.presentation.UserUIState
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelsHandler
import com.rumble.battles.feed.presentation.views.FeaturedChannelListView
import com.rumble.battles.feed.presentation.views.FreshChannelListView
import com.rumble.battles.feed.presentation.views.PremiumBannerView
import com.rumble.battles.feed.presentation.views.RepostFeedView
import com.rumble.battles.feed.presentation.views.VideoCollectionSelectorView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.rumbleads.presentation.RumbleAdView
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.channel.FeaturedChannelsFeedItem
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.premium.domain.domainmodel.PremiumBanner
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.network.queryHelpers.SubscriptionSource
import com.rumble.theme.bottomBarSpacerListEmptyState
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.homeWidthRatio
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.PLAYER_MIN_VISIBILITY
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import kotlinx.coroutines.flow.collectLatest

private const val ITEMS_SHIFT_USER_LOGGED_IN = 2
private const val ITEMS_SHIFT_USER_NOT_LOGGED_IN = 1

@Composable
fun HomeScreen(
    activityHandler: RumbleActivityHandler,
    homeHandler: HomeHandler,
    contentHandler: ContentHandler,
    recommendedChannelsHandler: RecommendedChannelsHandler,
    onSearch: () -> Unit,
    onChannelClick: (id: String) -> Unit,
    onFreshContentChannelClick: (id: String) -> Unit,
    onVideoClick: (id: Feed) -> Unit,
    onViewAllRecommendedChannelsClick: () -> Unit,
    onSearchIconGlobalMeasured: (Offset) -> Unit,
    onFollowingIconGlobalMeasured: (Offset) -> Unit,
    onFollowingClicked: () -> Unit,
    onViewNotifications: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val categories by homeHandler.homeCategories.collectAsStateWithLifecycle()

    val state by homeHandler.homeScreenState.collectAsStateWithLifecycle()

    val listState by homeHandler.listState

    val userUIState by contentHandler.userUIState.collectAsStateWithLifecycle()

    val activityHandlerState by activityHandler.activityHandlerState.collectAsStateWithLifecycle()

    val videoListItems: LazyPagingItems<Feed> = state.feedList.collectAsLazyPagingItems()

    val updatedEntity by homeHandler.updatedEntity.collectAsStateWithLifecycle()

    val soundOn by homeHandler.soundState.collectAsStateWithLifecycle(initialValue = false)

    val alertDialogState by homeHandler.alertDialogState

    val videoDetailsState by contentHandler.videoDetailsState

    updatedEntity?.let { updated ->
        videoListItems.itemSnapshotList.find { it is VideoEntity && it.id == updated.id }?.let {
            val videoEntity = it as VideoEntity
            videoEntity.userVote = updated.userVote
            videoEntity.likeNumber = updated.likeNumber
            videoEntity.dislikeNumber = updated.dislikeNumber
        }
    }

    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            homeHandler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            homeHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            homeHandler.onViewResumed()
        }
    }

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) homeHandler.onCreatePlayerForVisibleFeed()
    }

    LaunchedEffect(screenWidth) {
        homeHandler.updateNumberOfGridColumns()
    }


    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            when (it) {
                is ContentScreenVmEvent.ScrollToTop -> {
                    listState.animateScrollToItem(0)
                }

                is ContentScreenVmEvent.OnRepostDeleted -> {
                    homeHandler.onRepostDeleted()
                }

                else -> return@collectLatest
            }
        }
    }

    LaunchedEffect(Unit) {
        homeHandler.eventFlow.collectLatest {
            when (it) {
                is HomeEvent.PlayVideo -> {
                    onVideoClick(it.videoEntity)
                }

                is HomeEvent.NavigateToChannelDetails -> {
                    onChannelClick(it.channelId)
                }

                is HomeEvent.Error -> {
                    contentHandler.onError(
                        errorMessage = null,
                        withPadding = true
                    )
                }
            }
        }
    }


    LaunchedEffect(listState, videoListItems.itemCount) {
        snapshotFlow { listState.layoutInfo }.collect {
            val shift =
                if (userUIState.isLoggedIn) ITEMS_SHIFT_USER_LOGGED_IN else ITEMS_SHIFT_USER_NOT_LOGGED_IN
            var createPlayer = false
            val itemPosition =
                listState.findFirstFullyVisibleItemIndex(shift, PLAYER_MIN_VISIBILITY)
            val firstVisible =
                if (itemPosition + shift == 0 && videoListItems.itemCount > 0) {
                    createPlayer = true
                    videoListItems[0]
                } else if (itemPosition >= 0 && itemPosition < videoListItems.itemCount) {
                    videoListItems[itemPosition]
                } else {
                    null
                }
            homeHandler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) homeHandler.onCreatePlayerForVisibleFeed()
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            homeHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .testTag(FeedTag)
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val (header, list, recommendedChannels) = createRefs()

        RumbleLogoSearchHeaderView(
            modifier = Modifier
                .constrainAs(header) { top.linkTo(parent.top) },
            hasUnreadNotifications = activityHandlerState.hasUnreadNotifications,
            userLoggedIn = userUIState.isLoggedIn,
            onSearch = onSearch,
            onNotifications = {
                activityHandler.clearNotifications()
                onViewNotifications()
            },
            onSearchIconGlobalMeasured = onSearchIconGlobalMeasured,
            onFollowingIconGlobalMeasured = onFollowingIconGlobalMeasured,
            onFollowing = onFollowingClicked,
            isPremiumUser = contentHandler.isPremiumUser()
        )

        SwipeRefresh(
            modifier = Modifier
                .testTag(SwipeRefreshTag)
                .constrainAs(list) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(recommendedChannels.start)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            state = rememberSwipeRefreshState(
                isRefreshing = videoListItems.loadState.refresh == LoadState.Loading || state.freshContentLoadingState == LoadingState.Loading
            ),
            onRefresh = {
                homeHandler.onRefreshAll()
                activityHandler.loadNotificationState()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {
            if (state.numberOfColumns == RumbleConstants.HOME_SCREEN_ROWS_1) {
                HomeSmallScreenContent(
                    listConnection,
                    listState,
                    userUIState,
                    state,
                    onFreshContentChannelClick,
                    onViewAllRecommendedChannelsClick,
                    categories,
                    homeHandler,
                    videoListItems,
                    contentHandler,
                    recommendedChannelsHandler,
                    onChannelClick,
                    soundOn,
                    activityHandler
                )
            } else {
                HomeBigScreenContent(
                    userUIState,
                    onFreshContentChannelClick,
                    onViewAllRecommendedChannelsClick,
                    categories,
                    homeHandler,
                    videoListItems,
                    contentHandler,
                    recommendedChannelsHandler,
                    onChannelClick,
                    soundOn,
                    activityHandler
                )

            }
        }

        Box(
            modifier = Modifier
                .constrainAs(recommendedChannels) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
                .background(MaterialTheme.colors.onPrimary),
        ) {
            if (state.numberOfColumns == RumbleConstants.HOME_SCREEN_ROWS_3) {
                FreshChannelListView(
                    freshChannels = state.freshChannels,
                    numberOfColumns = state.numberOfColumns,
                    onFreshContentChannelClick = onFreshContentChannelClick,
                    onPlusChannelsClick = onViewAllRecommendedChannelsClick
                )
            }
        }
    }

    if (alertDialogState.show) {
        HomeScreenDialog(
            reason = alertDialogState.alertDialogReason,
            handler = homeHandler
        )
    }
}

@Composable
private fun HomeSmallScreenContent(
    listConnection: NestedScrollConnection,
    listState: LazyListState,
    userUIState: UserUIState,
    state: HomeScreenState,
    onFreshContentChannelClick: (id: String) -> Unit,
    onViewAllRecommendedChannelsClick: () -> Unit,
    categories: List<VideoCollectionType>,
    homeHandler: HomeHandler,
    videoListItems: LazyPagingItems<Feed>,
    contentHandler: ContentHandler,
    recommendedChannelsHandler: RecommendedChannelsHandler,
    onChannelClick: (id: String) -> Unit,
    soundOn: Boolean,
    activityHandler: RumbleActivityHandler
) {
    BoxWithConstraints {
        val contentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(listConnection),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            if (userUIState.isLoggedIn && state.numberOfColumns < RumbleConstants.HOME_SCREEN_ROWS_3) {
                item {
                    FreshChannelListView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.onPrimary),
                        freshChannels = state.freshChannels,
                        numberOfColumns = state.numberOfColumns,
                        onFreshContentChannelClick = onFreshContentChannelClick,
                        onPlusChannelsClick = onViewAllRecommendedChannelsClick
                    )
                }
            }

            item {
                VideoCollectionSelectorView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .padding(
                            top = paddingXSmall,
                            bottom = paddingSmall
                        ),
                    videoCollections = categories,
                    onCollectionClick = {
                        homeHandler.onVideoCollectionClick(it)
                    },
                    selectedCollection = state.selectedCollection
                )
            }

            items(
                count = videoListItems.itemCount,
                key = videoListItems.itemKey(),
                contentType = videoListItems.itemContentType()
            ) { index ->
                val feed = videoListItems[index]
                feed?.let {
                    when (it) {
                        is FeaturedChannelsFeedItem -> {
                            FeaturedChannelListView(
                                modifier = Modifier
                                    .padding(horizontal = contentPadding)
                                    .padding(top = paddingLarge, bottom = paddingSmall),
                                contentHandler = contentHandler,
                                recommendedChannelsHandler = recommendedChannelsHandler,
                                onChannelClick = onChannelClick,
                                onViewAllClick = onViewAllRecommendedChannelsClick
                            )
                        }

                        is VideoEntity -> {
                            VideoView(
                                modifier = Modifier
                                    .padding(horizontal = contentPadding)
                                    .padding(
                                        top = paddingXXSmall,
                                        bottom = paddingXXSmall
                                    )
                                    .fillMaxWidth(homeWidthRatio),
                                videoEntity = it,
                                rumblePlayer = homeHandler.currentPlayerState.value,
                                soundOn = soundOn,
                                onChannelClick = { onChannelClick(it.channelId) },
                                onMoreClick = { videoEntity ->
                                    contentHandler.onMoreVideoOptionsClicked(
                                        videoEntity
                                    )
                                },
                                onImpression = homeHandler::onVideoCardImpression,
                                onPlayerImpression = homeHandler::onPlayerImpression,
                                onClick = homeHandler::onVideoClick,
                                onSoundClick = homeHandler::onSoundClick,
                                isPremiumUser = contentHandler.isPremiumUser(),
                            )
                        }

                        is RumbleAdEntity -> {
                            RumbleAdView(
                                modifier = Modifier
                                    .padding(horizontal = contentPadding)
                                    .padding(vertical = paddingXMedium)
                                    .fillMaxWidth(homeWidthRatio),
                                rumbleAdEntity = it,
                                onClick = { addEntity ->
                                    activityHandler.onOpenWebView(addEntity.clickUrl)
                                },
                                onLaunch = homeHandler::onRumbleAdImpression,
                                onResumed = homeHandler::onRumbleAdResumed
                            )
                        }

                        is PremiumBanner -> {
                            PremiumBannerView(
                                modifier = Modifier
                                    .padding(horizontal = contentPadding + paddingMedium),
                                onClick = {
                                    contentHandler.onShowSubscriptionOptions(
                                        videoId = null,
                                        source = SubscriptionSource.Home
                                    )
                                },
                                onDismiss = homeHandler::onDismissPremiumBanner
                            )
                        }

                        is RepostEntity -> {
                            RepostFeedView(
                                modifier = Modifier
                                    .padding(
                                        horizontal = contentPadding + paddingSmall,
                                        vertical = paddingXSmall
                                    ),
                                repost = it,
                                onChannelClick = { id ->
                                    onChannelClick(id)
                                },
                                onVideoClick = homeHandler::onVideoClick,
                                onMoreClick = { contentHandler.onOpenRepostMoreActions(it) }
                            )
                        }
                    }
                }
            }
            videoListItems.apply {
                when {
                    loadState.refresh is LoadState.NotLoading && videoListItems.itemCount == 0 -> {
                        item {
                            Column(modifier = Modifier.fillParentMaxSize()) {
                                EmptyView(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(paddingMedium),
                                    title = getEmptyStateTitle(state.selectedCollection),
                                    text = getEmptyStateMessage(state.selectedCollection)
                                )
                                BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                            }
                        }
                    }

                    loadState.refresh is LoadState.Error ->
                        item {
                            Column(modifier = Modifier.fillParentMaxSize()) {
                                ErrorView(
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(paddingMedium),
                                    backgroundColor = MaterialTheme.colors.onSecondary,
                                    onRetry = homeHandler::onRefreshAll
                                )
                                BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                            }
                        }

                    loadState.append is LoadState.Loading -> {
                        item {
                            PageLoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(paddingMedium)
                            )
                        }
                    }

                    loadState.append is LoadState.Error -> {
                        item {
                            Column(modifier = Modifier.fillParentMaxSize()) {
                                ErrorView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(paddingMedium),
                                    backgroundColor = MaterialTheme.colors.onSecondary,
                                    onRetry = videoListItems::retry,
                                )
                                BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                            }
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

@Composable
private fun HomeBigScreenContent(
    userUIState: UserUIState,
    onFreshContentChannelClick: (id: String) -> Unit,
    onViewAllRecommendedChannelsClick: () -> Unit,
    categories: List<VideoCollectionType>,
    homeHandler: HomeHandler,
    videoListItems: LazyPagingItems<Feed>,
    contentHandler: ContentHandler,
    recommendedChannelsHandler: RecommendedChannelsHandler,
    onChannelClick: (id: String) -> Unit,
    soundOn: Boolean,
    activityHandler: RumbleActivityHandler
) {
    val gridState: LazyGridState = rememberLazyGridState()
    val state by homeHandler.homeScreenState.collectAsStateWithLifecycle()

    LaunchedEffect(state.numberOfColumns) {
        homeHandler.onRefreshOnlyVideoList()
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(state.numberOfColumns),
        state = gridState,
        contentPadding = PaddingValues(paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        if (userUIState.isLoggedIn && state.numberOfColumns < RumbleConstants.HOME_SCREEN_ROWS_3) {
            item(
                span = { GridItemSpan(state.numberOfColumns) }
            ) {
                FreshChannelListView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onPrimary),
                    freshChannels = state.freshChannels,
                    numberOfColumns = state.numberOfColumns,
                    onFreshContentChannelClick = onFreshContentChannelClick,
                    onPlusChannelsClick = onViewAllRecommendedChannelsClick
                )
            }
        }

        item(
            span = { GridItemSpan(state.numberOfColumns) }
        ) {
            VideoCollectionSelectorView(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(
                        top = paddingXSmall,
                        bottom = paddingSmall
                    ),
                videoCollections = categories,
                onCollectionClick = {
                    homeHandler.onVideoCollectionClick(it)
                },
                selectedCollection = state.selectedCollection
            )
        }

        items(
            count = videoListItems.itemCount,
            key = videoListItems.itemKey(),
            contentType = videoListItems.itemContentType(),
            span = { index ->
                videoListItems[index]?.let { feed ->
                    when (feed) {
                        is FeaturedChannelsFeedItem, PremiumBanner -> GridItemSpan(state.numberOfColumns)
                        else -> GridItemSpan(RumbleConstants.HOME_SCREEN_ROWS_1)
                    }
                } ?: GridItemSpan(RumbleConstants.HOME_SCREEN_ROWS_1)
            }
        ) { index ->
            val feed = videoListItems[index]
            feed?.let {
                when (it) {
                    is FeaturedChannelsFeedItem -> {
                        FeaturedChannelListView(
                            modifier = Modifier
                                .padding(top = paddingLarge, bottom = paddingSmall),
                            contentHandler = contentHandler,
                            recommendedChannelsHandler = recommendedChannelsHandler,
                            onChannelClick = onChannelClick,
                            onViewAllClick = onViewAllRecommendedChannelsClick
                        )
                    }

                    is VideoEntity -> {
                        VideoView(
                            modifier = Modifier
                                .padding(
                                    top = paddingXXSmall,
                                    bottom = paddingXXSmall
                                )
                                .fillMaxWidth(homeWidthRatio),
                            videoEntity = it,
                            rumblePlayer = homeHandler.currentPlayerState.value,
                            soundOn = soundOn,
                            onChannelClick = { onChannelClick(it.channelId) },
                            onMoreClick = { videoEntity ->
                                contentHandler.onMoreVideoOptionsClicked(
                                    videoEntity
                                )
                            },
                            onImpression = homeHandler::onVideoCardImpression,
                            onPlayerImpression = homeHandler::onPlayerImpression,
                            onClick = homeHandler::onVideoClick,
                            onSoundClick = homeHandler::onSoundClick,
                            isPremiumUser = contentHandler.isPremiumUser(),
                        )
                    }

                    is RumbleAdEntity -> {
                        RumbleAdView(
                            modifier = Modifier.padding(paddingXXSmall),
                            rumbleAdEntity = it,
                            onClick = { addEntity ->
                                activityHandler.onOpenWebView(addEntity.clickUrl)
                            },
                            onLaunch = homeHandler::onRumbleAdImpression,
                            onResumed = homeHandler::onRumbleAdResumed
                        )
                    }

                    is PremiumBanner -> {
                        PremiumBannerView(
                            onClick = {
                                contentHandler.onShowSubscriptionOptions(
                                    videoId = null,
                                    source = SubscriptionSource.Home
                                )
                            },
                            onDismiss = homeHandler::onDismissPremiumBanner
                        )
                    }

                    is RepostEntity -> {
                        RepostFeedView(
                            modifier = Modifier.padding(
                                horizontal = paddingXMedium,
                                vertical = paddingXSmall
                            ),
                            repost = it,
                            onChannelClick = { id ->
                                onChannelClick(id)
                            },
                            onVideoClick = homeHandler::onVideoClick,
                            onMoreClick = { contentHandler.onOpenRepostMoreActions(it) }
                        )
                    }
                }
            }
        }
        videoListItems.apply {
            when {
                loadState.refresh is LoadState.NotLoading && videoListItems.itemCount == 0 -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(paddingMedium)
                        ) {
                            EmptyView(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(paddingMedium),
                                title = getEmptyStateTitle(state.selectedCollection),
                                text = getEmptyStateMessage(state.selectedCollection)
                            )
                            BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                        }
                    }
                }

                loadState.refresh is LoadState.Error ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(paddingMedium)
                        ) {
                            ErrorView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingMedium),
                                backgroundColor = MaterialTheme.colors.onSecondary,
                                onRetry = homeHandler::onRefreshAll
                            )
                            BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                        }
                    }

                loadState.append is LoadState.Loading -> {
                    item {
                        PageLoadingView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(paddingMedium)
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(paddingMedium)
                        ) {
                            ErrorView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(paddingMedium),
                                backgroundColor = MaterialTheme.colors.onSecondary,
                                onRetry = videoListItems::retry,
                            )
                            BottomNavigationBarScreenSpacer(bottomBarSpacerListEmptyState)
                        }
                    }
                }
            }
        }
        item {
            BottomNavigationBarScreenSpacer()
        }
    }
}

@Composable
private fun HomeScreenDialog(reason: AlertDialogReason, handler: HomeHandler) {
    when (reason) {
        is HomeAlertReason.RestrictedContentReason -> {
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
private fun getEmptyStateTitle(type: VideoCollectionType?): String =
    when (type) {
        is VideoCollectionType.Reposts -> stringResource(R.string.no_reposts)
        else -> stringResource(id = R.string.nothing_to_see_here)
    }

@Composable
private fun getEmptyStateMessage(type: VideoCollectionType?): String =
    when (type) {
        is VideoCollectionType.Reposts -> stringResource(R.string.no_reposts_from_your_categories)
        else -> ""
    }

