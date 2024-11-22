@file:OptIn(ExperimentalComposeUiApi::class)

package com.rumble.ui3.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.theme.RumbleTvTypography
import com.rumble.theme.RumbleTvTypography.labelBoldTv
import com.rumble.theme.backButtonArrowSize
import com.rumble.theme.defaultBackgroundColor
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.recommendedChannelsGridWidth
import com.rumble.theme.refreshButtonIconSize
import com.rumble.theme.refreshButtonSize
import com.rumble.theme.rumbleGreen
import com.rumble.ui3.common.composables.ChannelCard
import com.rumble.ui3.common.views.ErrorView
import com.rumble.ui3.common.views.PageLoadingView
import com.rumble.ui3.common.views.RumbleProgressIndicator
import com.rumble.utils.RumbleConstants
import java.util.UUID

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RecommendedChannelsScreen(
    viewModel: RecommendedChannelsHandler,
    onNavigateToChannelDetails: (CreatorEntity) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val channelsPagingItems: LazyPagingItems<CreatorEntity> = viewModel.channels.collectAsLazyPagingItems()

    val gridFocusRequester = remember { FocusRequester() }

    // Trigger focus on the first item when the data is loaded or refreshed
    LaunchedEffect(channelsPagingItems.loadState.refresh) {
        if (channelsPagingItems.loadState.refresh is LoadState.NotLoading && channelsPagingItems.itemCount > 0) {
            gridFocusRequester.requestFocus()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(defaultBackgroundColor)
    ) {
        val (back, title, refresh, grid, loadingIndicator) = createRefs()

        val channelFocusRequesters = remember { mutableMapOf<UUID, FocusRequester>() }

        BackButton(
            modifier = Modifier
                .padding(paddingSmall)
                .constrainAs(back) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
            onBack = onBack
        )

        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = paddingXXMedium)
                start.linkTo(back.end)
            },
            color = enforcedWhite,
            style = RumbleTvTypography.h3Tv,
            text = stringResource(id = R.string.recommended_channels_title)
        )

        Button(
            modifier = Modifier
                .constrainAs(refresh) {
                    start.linkTo(title.end, margin = paddingXSmall)
                    top.linkTo(title.top)
                    bottom.linkTo(title.bottom)
                }
                .size(refreshButtonSize),
            onClick = {
                viewModel.onFocusedChannel(null)
                channelFocusRequesters.clear()
                channelsPagingItems.refresh()
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.colors(
                contentColor = enforcedBone,
                focusedContentColor = enforcedDarkmo,
                containerColor = enforcedWhite.copy(alpha = 0.1f),
                focusedContainerColor = rumbleGreen
            )
        ) {
            Icon(
                modifier = Modifier.size(refreshButtonIconSize),
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = stringResource(id = R.string.refresh),
            )
        }

        TvLazyVerticalGrid(
            modifier = Modifier
                .constrainAs(grid) {
                    start.linkTo(back.end)
                    top.linkTo(title.bottom, margin = paddingXSmall)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .focusProperties {
                    enter = {
                        channelFocusRequesters[state.focusedChannel?.uuid]
                            ?: channelFocusRequesters[channelsPagingItems.itemSnapshotList[0]?.uuid]
                            ?: FocusRequester.Default
                    }
                }
                .focusRequester(gridFocusRequester)
                .width(recommendedChannelsGridWidth),
            columns = TvGridCells.Fixed(RumbleConstants.TV_RECOMMENDED_CHANNELS_GRID_WIDTH),
            pivotOffsets = PivotOffsets(parentFraction = .5f, childFraction = .5f),
            verticalArrangement = Arrangement.spacedBy(paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(paddingMedium)
        ) {
            items(
                count = channelsPagingItems.itemCount,
                key = channelsPagingItems.itemKey(),
                contentType = channelsPagingItems.itemContentType()
            ) { index ->
                val item = channelsPagingItems[index]
                item?.let { channel ->
                    ChannelCard(
                        thumbnail = channel.thumbnail,
                        channelTitle = channel.channelTitle,
                        verified = channel.verifiedBadge,
                        followers = channel.followers,
                        isFollowed = channel.followed,
                        focusRequester = channelFocusRequesters.getOrPut(channel.uuid) { FocusRequester() },
                        onClick = {
                            onNavigateToChannelDetails(channel)
                        },
                        onFocused = {
                            viewModel.onFocusedChannel(channel)
                        }
                    )
                }
            }

            channelsPagingItems.apply {
                when (loadState.append) {
                    is LoadState.Loading -> {
                        item(span = { TvGridItemSpan(3) }) {
                            PageLoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(paddingMedium)
                            )
                        }
                    }

                    is LoadState.Error -> {
                        item(span = { TvGridItemSpan(3) }) {
                            ErrorView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(paddingMedium),
                            )
                        }
                    }

                    else -> {}
                }
            }
        }

        channelsPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading && this.itemCount == 0 -> {
                    RumbleProgressIndicator(modifier = Modifier.constrainAs(loadingIndicator) {
                        start.linkTo(grid.start)
                        end.linkTo(grid.end)
                        top.linkTo(grid.top)
                        bottom.linkTo(grid.bottom)
                    })
                }

                loadState.refresh is LoadState.NotLoading && this.itemCount == 0 -> {
                    ErrorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                    )
                }

                loadState.refresh is LoadState.Error -> {
                    ErrorView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingMedium),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BackButton(modifier: Modifier, onBack: () -> Unit) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.colors(
            containerColor = enforcedWhite.copy(alpha = .1f),
            focusedContainerColor = enforcedWhite.copy(alpha = .1f),
            pressedContainerColor = enforcedWhite.copy(alpha = .1f),
            contentColor = enforcedWhite,
            focusedContentColor = rumbleGreen
        ),
        scale = ButtonDefaults.scale(focusedScale = 1f),
        onClick = onBack
    ) {
        Icon(
            modifier = Modifier
                .size(backButtonArrowSize),
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = stringResource(id = R.string.back),
            tint = LocalContentColor.current
        )

        Spacer(
            modifier = Modifier
                .size(paddingXSmall10)
        )

        Text(
            text = stringResource(id = R.string.back),
            style = labelBoldTv
        )
    }
}