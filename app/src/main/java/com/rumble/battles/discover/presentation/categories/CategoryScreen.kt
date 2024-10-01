package com.rumble.battles.discover.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.rumble.battles.CategoryTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.CategoryCollapsingToolBar
import com.rumble.battles.discover.presentation.views.CategoryVideoListLoadingView
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.discover.presentation.views.LiveCategoriesView
import com.rumble.battles.discover.presentation.views.SubcategoryView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryListEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.categoryHeight
import com.rumble.theme.homeWidthRatio
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.SUBCATEGORY_ROWS_QUANTITY
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import com.rumble.utils.extension.rememberLazyListState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CategoryScreen(
    categoryHandler: CategoryHandler,
    contentHandler: ContentHandler,
    onSearch: () -> Unit,
    onBackClick: () -> Unit,
    onChannelClick: (id: String) -> Unit,
    onVideoClick: (id: Feed) -> Unit,
    onViewCategory: (String) -> Unit,
) {
    val state by categoryHandler.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val videoListItems: LazyPagingItems<Feed> = state.videoList.collectAsLazyPagingItems()
    val listState = videoListItems.rememberLazyListState()
    val gridState: LazyGridState = rememberLazyGridState()
    var isCollapsed by remember { mutableStateOf(false) }
    val soundOn by categoryHandler.soundState.collectAsStateWithLifecycle(initialValue = false)
    val alertDialogState by categoryHandler.alertDialogState
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

    val videoDetailsState by contentHandler.videoDetailsState

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) categoryHandler.onCreatePlayerForVisibleFeed()
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
            .testTag(CategoryTag)
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        CategoryCollapsingToolBar(
            modifier = Modifier.fillMaxWidth(),
            isCollapsed = isCollapsed,
            category = state.category,
            categoryDisplayType = state.displayType,
            hasSubcategories = state.subcategoryList.isEmpty().not(),
            isLoading = state.isLoading,
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
                categoryHandler.onPauseCurrentPlayer()
                categoryHandler.onDisplayTypeSelected(it)
            }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = videoListItems.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                categoryHandler.fetchCategoryData()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            if (state.displayType != CategoryDisplayType.CATEGORIES) {
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

                                is CategoryListEntity -> {
                                    LiveCategoriesView(
                                        title = stringResource(id = R.string.recommended_categories).uppercase(),
                                        titlePadding = paddingMedium,
                                        error = it.categoryList.isEmpty(),
                                        categoryList = it.categoryList,
                                        onViewCategory = { categoryEntity, index ->
                                            categoryHandler.onCategoryCardClick(
                                                categoryEntity,
                                                index
                                            )
                                            onViewCategory(categoryEntity.path)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    videoListItems.apply {
                        if (loadState.append is LoadState.Loading) {
                            item {
                                PageLoadingView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(paddingMedium)
                                )
                            }
                        }
                    }
                    if (videoListItems.itemCount in 1..2) {
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(categoryHeight * 2)
                            )
                        }
                    }
                    item {
                        BottomNavigationBarScreenSpacer()
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(SUBCATEGORY_ROWS_QUANTITY),
                    contentPadding = PaddingValues(paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                    state = gridState
                ) {
                    itemsIndexed(state.subcategoryList) { index, item ->
                        SubcategoryView(
                            modifier = Modifier
                                .clickable {
                                    categoryHandler.onCategoryCardClick(item, index)
                                    onViewCategory(item.path)
                                }
                                .conditional(state.subcategoryList.lastIndex == index) {
                                    this.padding(bottom = categoryHeight)
                                },
                            subcategory = item
                        )
                    }
                    items(SUBCATEGORY_ROWS_QUANTITY) {
                        BottomNavigationBarScreenSpacer()
                    }
                }
            }
        }

        if (videoListItems.loadState.refresh == LoadState.Loading) {
            CategoryVideoListLoadingView(
                modifier = Modifier
                    .padding(horizontal = paddingMedium)
                    .padding(top = paddingMedium)
            )
        }

        if (state.displayErrorView) {
            ErrorView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingMedium),
                backgroundColor = MaterialTheme.colors.onSecondary,
                onRetry = categoryHandler::fetchCategoryData
            )
        }

        videoListItems.apply {
            when {
                loadState.refresh is LoadState.NotLoading && videoListItems.itemCount == 0 -> {
                    EmptyView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                        title = getEmptyViewTitle(categoryDisplayType = state.displayType),
                        text = getEmptyViewMessage(categoryDisplayType = state.displayType)
                    )
                }

                loadState.refresh is LoadState.Error ->
                    ErrorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                        backgroundColor = MaterialTheme.colors.onSecondary,
                        onRetry = categoryHandler::fetchCategoryData
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


@Composable
private fun getEmptyViewTitle(categoryDisplayType: CategoryDisplayType) =
    when (categoryDisplayType) {
        CategoryDisplayType.LIVE_STREAM -> stringResource(id = R.string.no_live_streams)
        else -> stringResource(id = R.string.no_videos)
    }

@Composable
private fun getEmptyViewMessage(categoryDisplayType: CategoryDisplayType) =
    when (categoryDisplayType) {
        CategoryDisplayType.LIVE_STREAM -> stringResource(id = R.string.no_channels_live)
        else -> stringResource(id = R.string.no_videos_in_feed)
    }
