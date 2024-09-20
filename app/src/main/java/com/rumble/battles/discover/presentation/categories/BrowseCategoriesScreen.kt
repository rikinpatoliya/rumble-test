package com.rumble.battles.discover.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
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
import com.rumble.battles.CategoriesBrowse
import com.rumble.battles.R
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.BrowseCategoriesCollapsingToolBar
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.discover.presentation.views.SubcategoryView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.homeWidthRatio
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingNone
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BrowseCategoriesScreen(
    categoryHandler: CategoryHandler,
    contentHandler: ContentHandler,
    onBackClick: () -> Unit,
    onSearch: () -> Unit,
    onChannelClick: (id: String) -> Unit,
    onVideoClick: (id: Feed) -> Unit,
    onViewCategory: (String) -> Unit,
) {
    val context = LocalContext.current
    val state by categoryHandler.state.collectAsStateWithLifecycle()
    val alertDialogState by categoryHandler.alertDialogState
    val videoListItems: LazyPagingItems<Feed> = state.liveVideoList.collectAsLazyPagingItems()
    val configuration = LocalConfiguration.current
    var isCollapsed by remember { mutableStateOf(false) }
    val soundOn by categoryHandler.soundState.collectAsStateWithLifecycle(initialValue = false)
    val gridState: LazyGridState = remember { LazyGridState() }
    val listState: LazyListState = remember { LazyListState() }
    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            categoryHandler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }

    state.updatedVideo?.let { updated ->
        videoListItems.itemSnapshotList.find { it is VideoEntity && it.id == updated.id }?.let {
            val videoEntity = it as VideoEntity
            videoEntity.userVote = updated.userVote
            videoEntity.likeNumber = updated.likeNumber
            videoEntity.dislikeNumber = updated.dislikeNumber
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            categoryHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            categoryHandler.onViewResumed()
        }
    }

    LaunchedEffect(Unit) {
        categoryHandler.eventFlow.collectLatest {
            when (it) {
                is CategoryEvent.PlayVideo -> {
                    onVideoClick(it.videoEntity)
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect {
            if (state.displayType != CategoryDisplayType.CATEGORIES)
                isCollapsed = listState.firstVisibleItemScrollOffset > 0

            var createPlayer = false
            val itemPosition = listState.findFirstFullyVisibleItemIndex(
                visibilityPercentage = RumbleConstants.PLAYER_MIN_VISIBILITY
            )
            val firstVisible =
                if (itemPosition == 0 && videoListItems.itemCount > 0) {
                    createPlayer = true
                    videoListItems[0]
                } else if (itemPosition >= 0 && itemPosition < videoListItems.itemCount) {
                    videoListItems[itemPosition]
                } else {
                    null
                }
            categoryHandler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) categoryHandler.onCreatePlayerForVisibleFeed()
        }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }.collect {
            if (state.displayType == CategoryDisplayType.CATEGORIES)
                isCollapsed = gridState.firstVisibleItemScrollOffset > 0
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            categoryHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .testTag(CategoriesBrowse)
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        BrowseCategoriesCollapsingToolBar(
            modifier = Modifier.fillMaxWidth(),
            isCollapsed = isCollapsed,
            categoryDisplayType = state.displayType,
            onSearch = {
                categoryHandler.onSearch()
                onSearch()
            },
            onBackClick = {
                categoryHandler.onBackClick()
                onBackClick()
            },
            onTabSelected = {
                isCollapsed = false
                categoryHandler.onDisplayTypeSelected(it)
            },
            onCategoryClick = {
                categoryHandler.onCategoryButtonClick(context.getString(it.label))
                onViewCategory(it.path)
            }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = videoListItems.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                categoryHandler.onRefresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            if (state.displayType == CategoryDisplayType.CATEGORIES) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(RumbleConstants.SUBCATEGORY_ROWS_QUANTITY),
                    contentPadding = if (state.categoryList.isNotEmpty()) PaddingValues(paddingSmall) else PaddingValues(paddingNone),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                    state = gridState
                ) {
                    itemsIndexed(state.categoryList) { index, item ->
                        SubcategoryView(
                            modifier = Modifier.clickable {
                                categoryHandler.onCategoryCardClick(item, index)
                                onViewCategory(item.path)
                            },
                            subcategory = item
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(listConnection),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState,
                    contentPadding = PaddingValues(
                        horizontal = CalculatePaddingForTabletWidth(
                            configuration.screenWidthDp.dp
                        )
                    )
                ) {
                    items(
                        count = videoListItems.itemCount,
                        key = videoListItems.itemKey(),
                        contentType = videoListItems.itemContentType()
                    ) { index ->
                        videoListItems[index]?.let {
                            when (it) {
                                is VideoEntity -> {
                                    VideoView(
                                        modifier = Modifier
                                            .fillMaxWidth(homeWidthRatio)
                                            .padding(top = paddingMedium, bottom = paddingMedium),
                                        videoEntity = it,
                                        rumblePlayer = categoryHandler.currentPlayerState.value,
                                        soundOn = soundOn,
                                        onChannelClick = { onChannelClick(it.channelId) },
                                        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                                        onImpression = categoryHandler::onVideoCardImpression,
                                        onPlayerImpression = categoryHandler::onPlayerImpression,
                                        onClick = { feed ->
                                            categoryHandler.onVideoClick(feed, index)
                                        },
                                        onSoundClick = categoryHandler::onSoundClick,
                                        isPremiumUser = contentHandler.isPremiumUser(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.displayErrorView) {
            ErrorView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingMedium),
                backgroundColor = MaterialTheme.colors.onSecondary,
                onRetry = categoryHandler::onRefresh
            )
        }

        videoListItems.apply {
            when {
                loadState.refresh is LoadState.NotLoading && videoListItems.itemCount == 0 -> {
                    EmptyView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                        title = stringResource(id = R.string.no_live_streams),
                        text = stringResource(id = R.string.no_channels_live)
                    )
                }

                loadState.refresh is LoadState.Error ->
                    ErrorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                        backgroundColor = MaterialTheme.colors.onSecondary,
                        onRetry = categoryHandler::onRefresh
                    )

                loadState.append is LoadState.Error -> {
                    ErrorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                        backgroundColor = MaterialTheme.colors.onSecondary,
                        onRetry = videoListItems::retry,
                    )
                }
                loadState.append is LoadState.Loading -> {
                    PageLoadingView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(paddingMedium)
                    )
                }
            }
        }
    }

    if (alertDialogState.show) {
        CategoryDialog(
            reason = alertDialogState.alertDialogReason,
            handler = categoryHandler
        )
    }
}
