package com.rumble.battles.search.presentation.combinedSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SearchCombinedChannelCardTag
import com.rumble.battles.SearchCombinedEmptyStateTitleTag
import com.rumble.battles.SearchCombinedTag
import com.rumble.battles.SearchCombinedVideoCardTag
import com.rumble.battles.SearchCombinedVideoFiltersTag
import com.rumble.battles.SearchCombinedViewAllChannelsTag
import com.rumble.battles.SearchCombinedViewAllVideosTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.battles.search.presentation.views.SearchResultHeader
import com.rumble.battles.sort.SortFilterBottomSheet
import com.rumble.battles.sort.SortFilterSelection
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.homeWidthRatio
import com.rumble.theme.imageWidthXXLarge
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.searchResultChannelsHeight
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import com.rumble.utils.extension.rumbleUitTestTag
import com.rumble.utils.extension.shortString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ITEMS_SHIFT = 2

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CombineSearchResultScreen(
    handler: CombineSearchResultHandler,
    contentHandler: ContentHandler,
    onSearch: (String) -> Unit = {},
    onViewChannels: (String) -> Unit = {},
    onViewVideos: (query: String, filters: SortFilterSelection) -> Unit = { _, _ -> },
    onViewChannel: (String) -> Unit = {},
    onVideoClick: (id: Feed) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val state by handler.state
    val alertDialogState by handler.alertDialogState
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()
    val listState by handler.listState
    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            handler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            handler.onDisposed()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            handler.onViewResumed()
        }
    }
    val videoDetailsState by contentHandler.videoDetailsState

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) handler.onCreatePlayerForVisibleFeed()
    }

    LaunchedEffect(Unit) {
        handler.eventFlow.collectLatest {
            when (it) {
                is CombinedSearchEvent.PlayVideo -> {
                    onVideoClick(it.videoEntity)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            handler.onDisposed()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect {
            var createPlayer = false
            val itemPosition = listState.findFirstFullyVisibleItemIndex(
                ITEMS_SHIFT,
                RumbleConstants.PLAYER_MIN_VISIBILITY
            )
            val firstVisible =
                if (itemPosition + ITEMS_SHIFT == 0 && state.videoList.isNotEmpty()) {
                    createPlayer = true
                    state.videoList[0]
                } else if (itemPosition >= 0 && itemPosition < state.videoList.size) {
                    state.videoList[itemPosition]
                } else {
                    null
                }
            handler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) handler.onCreatePlayerForVisibleFeed()
        }
    }

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            SortFilterBottomSheet(
                bottomSheetState = bottomSheetState,
                coroutineScope = coroutineScope,
                selection = handler.selection,
            ) {
                handler.onSelectionMade(it)
            }
        }) {

        BoxWithConstraints {
            val horizontalContentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)

            Column(
                modifier = Modifier
                    .testTag(SearchCombinedTag)
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

                when (state.searchState) {
                    SearchState.LOADING -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = horizontalContentPadding)
                        ) {
                            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    SearchState.EMPTY -> {
                        EmptyView(
                            modifier = Modifier
                                .rumbleUitTestTag(SearchCombinedEmptyStateTitleTag)
                                .fillMaxSize()
                                .padding(
                                    vertical = paddingMedium,
                                    horizontal = horizontalContentPadding + paddingMedium
                                ),
                            title = stringResource(id = R.string.no_result),
                            text = stringResource(id = R.string.try_different_keywords_filters)
                        )
                    }

                    SearchState.EMPTY_CHANNELS -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = paddingLarge)
                                .nestedScroll(listConnection),
                            contentPadding = PaddingValues(horizontal = horizontalContentPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            state = listState
                        ) {
                            item { EmptyChannelListView() }
                            item {
                                VideoHeaderView(
                                    onViewVideos,
                                    handler.query,
                                    bottomSheetState,
                                    handler.selection
                                )
                            }
                            itemsIndexed(state.videoList) { index, video ->
                                VideoItem(
                                    video = video,
                                    contentHandler = contentHandler,
                                    onVideoClick = { handler.onVideoItemClick(video) },
                                    onViewChannel = onViewChannel,
                                    handler = handler,
                                    index = index
                                )
                            }
                            item {
                                RumbleTextActionButton(
                                    modifier = Modifier
                                        .padding(paddingMedium),
                                    text = stringResource(id = R.string.view_all)
                                ) {
                                    onViewVideos(handler.query, handler.selection)
                                }
                            }
                            item { BottomNavigationBarScreenSpacer() }
                        }
                    }

                    SearchState.EMPTY_VIDEOS -> {
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(top = paddingLarge)
                                .nestedScroll(listConnection),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(horizontal = horizontalContentPadding),
                            state = listState
                        ) {
                            item {
                                ChannelListView(
                                    channelList = state.channelList,
                                    onViewChannel = onViewChannel,
                                ) { onViewChannels(handler.query) }
                            }
                            item {
                                VideoHeaderView(
                                    onViewVideos,
                                    handler.query,
                                    bottomSheetState,
                                    handler.selection
                                )
                            }
                        }

                        EmptyView(
                            modifier = Modifier
                                .rumbleUitTestTag(SearchCombinedEmptyStateTitleTag)
                                .padding(paddingMedium)
                                .fillMaxSize(),
                            title = stringResource(id = R.string.no_videos_found),
                            text = stringResource(id = R.string.try_different_keywords_filters)
                        )
                    }

                    SearchState.LOADED -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = paddingLarge)
                                .nestedScroll(listConnection),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(horizontal = horizontalContentPadding),
                            state = listState
                        ) {
                            item {
                                ChannelListView(
                                    channelList = state.channelList,
                                    onViewChannel = onViewChannel,
                                ) { onViewChannels(handler.query) }
                            }
                            item {
                                VideoHeaderView(
                                    onViewVideos,
                                    handler.query,
                                    bottomSheetState,
                                    handler.selection
                                )
                            }
                            itemsIndexed(state.videoList) { index, video ->
                                VideoItem(
                                    video = video,
                                    contentHandler = contentHandler,
                                    onVideoClick = { handler.onVideoItemClick(video) },
                                    onViewChannel = onViewChannel,
                                    handler = handler,
                                    index = index
                                )
                            }
                            item {
                                RumbleTextActionButton(
                                    modifier = Modifier
                                        .padding(paddingMedium),
                                    text = stringResource(id = R.string.view_all)
                                ) {
                                    onViewVideos(handler.query, handler.selection)
                                }
                            }
                            item { BottomNavigationBarScreenSpacer() }
                        }
                    }
                }
            }
        }
    }

    if (alertDialogState.show) {
        CombinedSearchDialog(
            reason = alertDialogState.alertDialogReason,
            handler = handler
        )
    }
}

@Composable
private fun CombinedSearchDialog(reason: AlertDialogReason, handler: CombineSearchResultHandler) {
    when (reason) {
        is CombinedSearchAlertReason.RestrictedContentReason -> {
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
private fun VideoItem(
    video: VideoEntity,
    contentHandler: ContentHandler,
    onVideoClick: (id: Feed) -> Unit,
    onViewChannel: (String) -> Unit,
    handler: CombineSearchResultHandler,
    index: Int,
) {
    val soundOn by handler.soundState.collectAsStateWithLifecycle(initialValue = false)

    VideoView(
        modifier = Modifier
            .rumbleUitTestTag("$SearchCombinedVideoCardTag$index")
            .fillMaxWidth(homeWidthRatio)
            .padding(top = paddingLarge, bottom = paddingLarge),
        videoEntity = video,
        soundOn = soundOn,
        rumblePlayer = handler.state.value.rumblePlayer,
        onChannelClick = { onViewChannel(video.channelId) },
        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
        onImpression = handler::onVideoCardImpression,
        onClick = onVideoClick,
        onSoundClick = handler::onSoundClick,
        onPlayerImpression = handler::onPlayerImpression,
        isPremiumUser = contentHandler.isPremiumUser(),
    )
    Divider(
        modifier = Modifier.fillMaxWidth(homeWidthRatio),
        color = MaterialTheme.colors.secondaryVariant
    )
}

@Composable
private fun EmptyChannelListView() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingMedium,
                end = paddingMedium,
                bottom = paddingMedium
            ),
        text = stringResource(id = R.string.channels),
        style = h3
    )
    EmptyView(
        modifier = Modifier
            .rumbleUitTestTag(SearchCombinedEmptyStateTitleTag)
            .fillMaxWidth()
            .height(searchResultChannelsHeight)
            .padding(start = paddingMedium, end = paddingMedium),
        title = stringResource(id = R.string.no_channels_found),
        text = stringResource(id = R.string.try_different_keywords)
    )
}

@Composable
private fun ChannelListView(
    modifier: Modifier = Modifier,
    channelList: List<CreatorEntity>,
    onViewChannel: (String) -> Unit,
    horizontalContentPadding: Dp = 0.dp,
    onViewAll: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = paddingMedium, end = paddingMedium, bottom = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.channels),
            style = h3
        )
        RumbleTextActionButton(
            modifier = Modifier
                .rumbleUitTestTag(SearchCombinedViewAllChannelsTag),
            text = stringResource(id = R.string.view_all),
        ) {
            onViewAll()
        }
    }

    LazyRow(
        modifier = Modifier.padding(start = paddingMedium),
        contentPadding = PaddingValues(horizontal = horizontalContentPadding)
    ) {
        itemsIndexed(channelList) { index, channel ->
            Column(
                modifier = Modifier
                    .rumbleUitTestTag("$SearchCombinedChannelCardTag$index")
                    .padding(end = paddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImageComponent(
                    modifier = Modifier
                        .clickableNoRipple { onViewChannel(channel.channelId) },
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(
                        borderColor = rumbleGreen
                    ),
                    userName = channel.channelTitle,
                    userPicture = channel.thumbnail
                )

                UserNameViewSingleLine(
                    modifier = Modifier
                        .width(imageWidthXXLarge)
                        .padding(top = paddingXSmall),
                    name = channel.channelTitle,
                    verifiedBadge = channel.verifiedBadge,
                    textStyle = h6,
                    textColor = MaterialTheme.colors.secondary,
                    spacerWidth = paddingXXXSmall,
                    verifiedBadgeHeight = verifiedBadgeHeightSmall,
                    horizontalArrangement = Arrangement.Center
                )

                Text(
                    text = "${channel.followers.shortString()} ${
                        pluralStringResource(
                            id = R.plurals.followers,
                            channel.followers
                        ).lowercase()
                    }",
                    style = tinyBody,
                    color = wokeGreen
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VideoHeaderView(
    onViewAll: (String, SortFilterSelection) -> Unit,
    query: String,
    bottomSheetState: ModalBottomSheetState,
    filterSelection: SortFilterSelection,
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.videos),
            style = h3
        )

        IconButton(
            modifier = Modifier.rumbleUitTestTag(SearchCombinedVideoFiltersTag),
            onClick = {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = stringResource(id = R.string.filter),
                tint = MaterialTheme.colors.secondary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        RumbleTextActionButton(
            modifier = Modifier
                .rumbleUitTestTag(SearchCombinedViewAllVideosTag),
            text = stringResource(id = R.string.view_all),
        ) {
            onViewAll(query, filterSelection)
        }
    }
}
