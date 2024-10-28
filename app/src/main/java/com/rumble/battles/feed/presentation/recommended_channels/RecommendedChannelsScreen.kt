package com.rumble.battles.feed.presentation.recommended_channels

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.R
import com.rumble.battles.RecommendedChannelsTag
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.RecommendedChannelCard
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants.RECOMMENDED_CHANNELS_COLUMNS_QUANTITY

@Composable
fun RecommendedChannelScreen(
    contentHandler: ContentHandler,
    recommendedChannelsHandler: RecommendedChannelsHandler,
    title: String = stringResource(id = R.string.may_we_recommend_channels),
    onChannelClick: (id: String) -> Unit,
    onBackClick: () -> Unit,
) {

    val channelPagingItems: LazyPagingItems<ChannelDetailsEntity> =
        recommendedChannelsHandler.channels.collectAsLazyPagingItems()
    val gridState by recommendedChannelsHandler.gridState

    Column(
        modifier = Modifier
            .testTag(RecommendedChannelsTag)
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = title,
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )

        SwipeRefresh(
            modifier = Modifier.testTag(SwipeRefreshTag),
            state = rememberSwipeRefreshState(
                channelPagingItems.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                channelPagingItems.refresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {
                val horizontalContentPadding =
                    CalculatePaddingForTabletWidth(maxWidth, defaultPadding = paddingSmall)

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = gridState,
                    columns = GridCells.Fixed(RECOMMENDED_CHANNELS_COLUMNS_QUANTITY),
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                    contentPadding = PaddingValues(
                        vertical = paddingSmall,
                        horizontal = horizontalContentPadding
                    )
                ) {

                    items(channelPagingItems.itemCount) { index ->

                        channelPagingItems[index]?.let {
                            RecommendedChannelCard(
                                channel = it,
                                onChannelClick = onChannelClick,
                                onSubscriptionUpdate = contentHandler::onUpdateSubscription
                            )
                        }
                    }
                    channelPagingItems.apply {
                        when {
                            loadState.refresh is LoadState.NotLoading && channelPagingItems.itemCount == 0 -> {
                                item {
                                    EmptyView(
                                        modifier = Modifier
                                            .fillMaxSize()
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
                                            .fillMaxSize()
                                            .padding(paddingMedium),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = channelPagingItems::refresh
                                    )
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
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(paddingMedium),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = ::retry,
                                    )
                                }
                            }
                        }
                    }
                    items(RECOMMENDED_CHANNELS_COLUMNS_QUANTITY) {
                        BottomNavigationBarScreenSpacer()
                    }
                }
            }
        }
    }
}