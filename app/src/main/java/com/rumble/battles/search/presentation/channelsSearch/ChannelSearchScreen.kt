package com.rumble.battles.search.presentation.channelsSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rumble.battles.R
import com.rumble.battles.SearchChannelsTag
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.search.presentation.views.SearchResultHeader
import com.rumble.battles.subscriptions.presentation.SubscriptionView
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall

@Composable
fun ChannelSearchScreen(
    handler: ChannelSearchHandler,
    onSearch: (String) -> Unit = {},
    onViewChannel: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val channelListItems: LazyPagingItems<ChannelDetailsEntity> =
        handler.channelList.collectAsLazyPagingItems()

    BoxWithConstraints {
        val horizontalContentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)

        Column(
            modifier = Modifier
                .testTag(SearchChannelsTag)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding()
        ) {

            SearchResultHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalContentPadding),
                query = handler.query,
                onBack = onBack,
                onSearch = onSearch
            )

            Text(
                modifier = Modifier.padding(
                    start = paddingMedium + horizontalContentPadding,
                    top = paddingMedium,
                    end = paddingMedium + horizontalContentPadding
                ),
                text = stringResource(id = R.string.channels),
                color = MaterialTheme.colors.secondary,
                style = RumbleTypography.h3
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium,
                        top = paddingMedium
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = horizontalContentPadding)
            ) {

                items(channelListItems.itemCount) {
                    channelListItems[it]?.let { channel ->
                        SubscriptionView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    bottom = paddingXSmall,
                                    top = paddingXSmall
                                ),
                            channelDetailsEntity = channel,
                            onChannelClick = onViewChannel
                        )
                    }
                }

                item {
                    channelListItems.apply {
                        when {
                            loadState.refresh is LoadState.NotLoading && itemCount == 0 -> {
                                EmptyView(
                                    modifier = Modifier
                                        .fillParentMaxSize(),
                                    title = stringResource(id = R.string.no_result),
                                    text = stringResource(id = R.string.try_different_keywords_filters)
                                )
                            }
                            loadState.refresh is LoadState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(horizontal = horizontalContentPadding)
                                ) {
                                    RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                PageLoadingView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                )
                            }

                            loadState.append is LoadState.Error -> {
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
            }
            }
        }
    }