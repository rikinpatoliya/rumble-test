package com.rumble.battles.library.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.PlayListView
import com.rumble.battles.feed.presentation.views.VideoCompactLoadingView
import com.rumble.battles.library.presentation.playlist.PlayListTypeRefresh
import com.rumble.battles.library.presentation.playlist.PlayListsHandler
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntityWithOptions
import com.rumble.theme.RumbleTypography
import com.rumble.theme.minDefaultEmptyViewHeight
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.collectAndHandleState
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlayListsScreen(
    navController: NavHostController,
    playListsHandler: PlayListsHandler,
    contentHandler: ContentHandler,
    playListTypeRefresh: PlayListTypeRefresh? = null,
    playListEntityRefresh: PlayListEntity? = null,
    onViewChannel: (channelId: String) -> Unit,
    onViewPlayList: (playListId: String) -> Unit,
    onBackClick: () -> Unit
) {
    val playLists: LazyPagingItems<PlayListEntity> =
        playListsHandler.playListsFlow.collectAndHandleState(handleLoadStates = playListsHandler::handleLoadState)
    val listState by playListsHandler.listState

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.PlayListDeleted ||
                it is ContentScreenVmEvent.PlayListUpdated
            ) {
                playLists.refresh()
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(PLAY_LIST_TYPE_REFRESH, PlayListTypeRefresh.PlayLists)
            }
        }
    }

    LaunchedEffect(playListTypeRefresh) {
        if (playListTypeRefresh == PlayListTypeRefresh.PlayList) {
            playListEntityRefresh?.let { refreshEntity ->
                playLists.itemSnapshotList.find { entity -> entity?.id == refreshEntity.id }
                    ?.let { snapShotEntity ->
                        snapShotEntity.title = refreshEntity.title
                        snapShotEntity.description = refreshEntity.description
                        snapShotEntity.playListOwnerId = refreshEntity.playListOwnerId
                        snapShotEntity.channelName = refreshEntity.channelName
                        snapShotEntity.username = refreshEntity.username
                        snapShotEntity.visibility = refreshEntity.visibility
                    }
            }
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(PLAY_LIST_TYPE_REFRESH, PlayListTypeRefresh.PlayLists)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.playlists),
            modifier = Modifier
                .fillMaxWidth(),
            onBackClick = onBackClick,
        )
        SwipeRefresh(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SwipeRefreshTag),
            state = rememberSwipeRefreshState(
                playLists.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                playLists.refresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {
            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier.padding(
                        horizontal = CalculatePaddingForTabletWidth(
                            maxWidth = maxWidth,
                            defaultPadding = paddingMedium
                        )
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState
                ) {
                    items(
                        count = playLists.itemCount,
                        key = playLists.itemKey(),
                        contentType = playLists.itemContentType()
                    ) { index ->
                        playLists[index]?.let { playListEntity ->
                            val playListOptions =
                                playListsHandler.getPlayListOptions(playListEntity)
                            PlayListView(
                                modifier = Modifier.conditional(index != playLists.itemCount) {
                                    this.padding(bottom = paddingSmall)
                                },
                                playListOptions = playListOptions,
                                playListEntity = playListEntity,
                                onViewChannel = onViewChannel,
                                onViewPlayList = onViewPlayList,
                                onMoreClick = {
                                    contentHandler.onMorePlayListOptions(
                                        PlayListEntityWithOptions(
                                            playListEntity = playListEntity,
                                            playListOptions = playListOptions
                                        )
                                    )
                                },
                            )
                        }
                    }

                    playLists.apply {
                        when {
                            loadState.refresh is LoadState.NotLoading && playLists.itemCount == 0 -> {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.no_playlists_saved),
                                        modifier = Modifier.padding(
                                            vertical = paddingLarge,
                                            horizontal = CalculatePaddingForTabletWidth(
                                                maxWidth = maxWidth,
                                                defaultPadding = paddingMedium
                                            )
                                        ),
                                        style = RumbleTypography.smallBody,
                                        color = MaterialTheme.colors.secondary
                                    )
                                }
                            }

                            loadState.refresh is LoadState.Loading -> {
                                if (playLists.itemCount == 0)
                                    item {
                                        Column(
                                            modifier = Modifier.padding(
                                                horizontal = CalculatePaddingForTabletWidth(
                                                    maxWidth = maxWidth,
                                                )
                                            ),
                                        ) {
                                            repeat(RumbleConstants.LIBRARY_SHORT_LIST_SIZE) {
                                                VideoCompactLoadingView(
                                                    modifier = Modifier.conditional(it + 1 != RumbleConstants.LIBRARY_SHORT_LIST_SIZE) {
                                                        this.padding(bottom = paddingSmall)
                                                    }
                                                )
                                            }
                                        }
                                    }
                            }

                            loadState.refresh is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(minDefaultEmptyViewHeight),
                                        backgroundColor = Color.Transparent,
                                        onRetry = ::retry
                                    )
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(minDefaultEmptyViewHeight),
                                        backgroundColor = Color.Transparent,
                                        onRetry = ::retry
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
}