package com.rumble.battles.subscriptions.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.R
import com.rumble.battles.SubscriptionsTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.search.presentation.views.SearchFollowingView
import com.rumble.theme.minEmptyViewHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubscriptionsScreen(
    subscriptionsScreenHandler: SubscriptionsScreenHandler,
    contentHandler: ContentHandler,
    bottomSheetState: ModalBottomSheetState,
    onBackClick: () -> Unit,
    onChannelClick: (channelId: String) -> Unit,
    onSearch: () -> Unit
) {
    val state by subscriptionsScreenHandler.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    val contentListState = rememberLazyListState()

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.loading
    )

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.ChannelSubscriptionUpdated) {
                subscriptionsScreenHandler.updateChannelDetailsEntity(it.channelDetailsEntity)
                contentHandler.updateBottomSheetUiState(BottomSheetContent.HideBottomSheet)
            } else if (it is ContentScreenVmEvent.ChannelNotificationsUpdated) {
                subscriptionsScreenHandler.updateChannelDetailsEntity(it.channelDetailsEntity)
            } else if (it is ContentScreenVmEvent.SortFollowingTypeUpdated) {
                subscriptionsScreenHandler.updateSortFollowing(it.sortFollowingType)
            }
        }
    }

    LaunchedEffect(swipeRefreshState.isSwipeInProgress) {
        if (swipeRefreshState.isSwipeInProgress) {
            if (state.searchVisible.not()) subscriptionsScreenHandler.onUpdateSearchVisible()
        }
    }

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    Column(
        modifier = Modifier
            .testTag(SubscriptionsTag)
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.onPrimary)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.following),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
        ) {
            IconButton(
                modifier = Modifier.padding(end = paddingXSmall),
                onClick = {
                    contentHandler.updateBottomSheetUiState(
                        BottomSheetContent.SortFollowingSheet(
                            state.sortFollowingType
                        )
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = stringResource(id = R.string.sort_by),
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                subscriptionsScreenHandler.onRefresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = contentListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(paddingSmall),
                contentPadding = PaddingValues(
                    horizontal = CalculatePaddingForTabletWidth(
                        configuration.screenWidthDp.dp
                    )
                )
            ) {
                item {
                    AnimatedVisibility(
                        visible = state.searchVisible,
                        enter = slideInVertically(initialOffsetY = { -it }),
                    ) {
                        SearchFollowingView(
                            modifier = Modifier.padding(horizontal = paddingXSmall),
                            query = state.query,
                            onQueryChanged = {
                                subscriptionsScreenHandler.onQueryChanged(it)
                            }
                        )
                    }
                }
                state.followedChannels.forEach { channel ->
                    item {
                        FollowingItemView(
                            modifier = Modifier.padding(
                                start = paddingSmall,
                                end = paddingSmall
                            ),
                            channelDetailsEntity = channel,
                            onChannelClick = onChannelClick,
                            onNotificationClick = {
                                contentHandler.updateBottomSheetUiState(
                                    BottomSheetContent.ChannelNotificationsSheet(
                                        channel
                                    )
                                )
                            },
                            onUpdateSubscription = {
                                contentHandler.onUpdateSubscription(
                                    channel,
                                    it
                                )
                            },
                        )
                    }
                }
                if (state.query.isNotEmpty() && state.followedChannels.isEmpty()) {
                    item {
                        EmptyView(
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(min = minEmptyViewHeight)
                                .padding(paddingMedium),
                            title = stringResource(id = R.string.no_result),
                            text = stringResource(id = R.string.try_different_keywords_filters)
                        )
                    }
                }
                item {
                    BottomNavigationBarScreenSpacer()
                }
            }
            if (state.followedChannels.isEmpty() && state.query.isEmpty()) {
                SubscriptionsEmptyView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingMedium),
                    onSearchClick = onSearch
                )
            }
        }
    }
}