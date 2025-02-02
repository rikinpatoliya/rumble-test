package com.rumble.battles.channels.channeldetails.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.rumble.analytics.CardSize
import com.rumble.battles.ChannelDetailsTag
import com.rumble.battles.ChannelTopBarActionMenuTag
import com.rumble.battles.ChannelTopBarTag
import com.rumble.battles.CollapsingChannelImageTag
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.ChannelDetailsBaskSplash
import com.rumble.battles.commonViews.ChannelDetailsCollapsingImage
import com.rumble.battles.commonViews.ChannelDetailsHeader
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.LocalsPopupBottomSheet
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.ReportBottomSheet
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.RumbleTabsView
import com.rumble.battles.commonViews.TitleWithBoxedCount
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.VideosCountView
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.feed.presentation.views.RepostFeedView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.DisplayScreenType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.network.queryHelpers.SubscriptionSource
import com.rumble.theme.bottomBarHeight
import com.rumble.theme.collapsedSpacerPadding
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val ITEMS_SHIFT = 2

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChannelDetailsScreen(
    currentDestinationRoute: String?,
    channelDetailsHandler: ChannelDetailsHandler,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
    onChannelClick: (id: String) -> Unit,
    onBackClick: () -> Unit,
    onVideoClick: (id: Feed) -> Unit,
) {
    val state by channelDetailsHandler.uiState.collectAsStateWithLifecycle()
    val listToggleViewStyle by channelDetailsHandler.listToggleViewStyle.collectAsStateWithLifecycle(
        initialValue = ListToggleViewStyle.GRID
    )
    val popupState by channelDetailsHandler.popupState.collectAsStateWithLifecycle()
    val alertDialogState by channelDetailsHandler.alertDialogState.collectAsStateWithLifecycle()
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()
    val itemsList: LazyPagingItems<Feed> = state.itemsList.collectAsLazyPagingItems()
    val updatedEntity by channelDetailsHandler.updatedEntity.collectAsStateWithLifecycle()
    updatedEntity?.let { updated ->
        itemsList.itemSnapshotList.find { it is VideoEntity && it.id == updated.id }?.let {
            val videoEntity = it as VideoEntity
            videoEntity.userVote = updated.userVote
            videoEntity.likeNumber = updated.likeNumber
            videoEntity.dislikeNumber = updated.dislikeNumber
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            channelDetailsHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            channelDetailsHandler.onViewResumed()
        }
    }
    val scrollState by channelDetailsHandler.listState
    val soundOn by channelDetailsHandler.soundState.collectAsStateWithLifecycle(initialValue = false)
    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            channelDetailsHandler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }
    val videoDetailsState by contentHandler.videoDetailsState

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) channelDetailsHandler.onCreatePlayerForVisibleFeed()
    }

    LaunchedEffect(scrollState, itemsList.itemCount) {
        snapshotFlow { scrollState.layoutInfo }.collect {
            var createPlayer = false
            val itemPosition = scrollState.findFirstFullyVisibleItemIndex(
                indexShift = ITEMS_SHIFT,
                visibilityPercentage = RumbleConstants.PLAYER_MIN_VISIBILITY
            )
            val firstVisible =
                if (itemPosition + ITEMS_SHIFT == 0 && itemsList.itemCount > 0) {
                    createPlayer = true
                    itemsList[0]
                } else if (itemPosition >= 0 && itemPosition < itemsList.itemCount) {
                    itemsList[itemPosition]
                } else {
                    null
                }
            channelDetailsHandler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) channelDetailsHandler.onCreatePlayerForVisibleFeed()
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        channelDetailsHandler.vmEvents.collect { event ->
            when (event) {
                is ChannelDetailsVmEvent.Error -> {
                    contentHandler.onError(event.errorMessage)
                }

                is ChannelDetailsVmEvent.ShowLocalsPopup,
                ChannelDetailsVmEvent.ShowMenuPopup -> {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }

                ChannelDetailsVmEvent.ShowChannelReportedMessage -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.the_channel_has_been_reported,
                    )
                }

                is ChannelDetailsVmEvent.PlayVideo -> {
                    onVideoClick(event.videoEntity)
                }

                is ChannelDetailsVmEvent.OpenAuthMenu -> {
                    contentHandler.onOpenAuthMenu()
                }

                is ChannelDetailsVmEvent.OpenPremiumSubscriptionOptions -> {
                    contentHandler.onShowSubscriptionOptions(
                        creatorId = event.creatorId,
                        source = SubscriptionSource.ChannelDetails,
                    )
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.ChannelSubscriptionUpdated) {
                channelDetailsHandler.updateChannelDetailsEntity(it.channelDetailsEntity)
            } else if (it is ContentScreenVmEvent.ChannelNotificationsUpdated) {
                channelDetailsHandler.updateChannelDetailsEntity(it.channelDetailsEntity)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            channelDetailsHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    TransparentStatusBar()

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            ChannelDetailsScreenDialog(
                coroutineScope,
                bottomSheetState,
                popupState,
                state.channelDetailsEntity,
                contentHandler,
                channelDetailsHandler,
                activityHandler
            )
        }) {
        Box(
            modifier = Modifier
                .testTag(ChannelDetailsTag)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            ChannelDetailsBaskSplash(state.channelDetailsEntity?.backSplash)
            ChannelDetailsTopBar(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .systemBarsPadding()
                    .testTag(ChannelTopBarTag),
                menuModifier = Modifier
                    .align(Alignment.TopEnd)
                    .systemBarsPadding(),
                onBackClick = onBackClick,
                onMenuClick = { channelDetailsHandler.onActionMenuClicked() },
            )
            ChannelDetailsCollapsingImage(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .systemBarsPadding()
                    .testTag(CollapsingChannelImageTag),
                channelName = state.channelDetailsEntity?.channelTitle ?: "",
                imageUrl = state.channelDetailsEntity?.thumbnail ?: "",
                scrollState = scrollState,
            )
            Column {
                Spacer(
                    Modifier
                        .height(collapsedSpacerPadding)
                        .systemBarsPadding()
                )
                BoxWithConstraints {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(listConnection)
                            .systemBarsPadding(),
                        state = scrollState,
                        contentPadding = PaddingValues(
                            horizontal = CalculatePaddingForTabletWidth(
                                maxWidth = maxWidth
                            )
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ChannelDetailsHeader(
                                modifier = Modifier.fillMaxWidth(),
                                currentDestinationRoute = currentDestinationRoute,
                                state = state,
                                onJoin = { channelDetailsHandler.onJoin(it) },
                                onUpdateSubscription = {
                                    contentHandler.onUpdateSubscription(
                                        channelDetailsHandler.uiState.value.channelDetailsEntity,
                                        it
                                    )
                                },
                                onChannelNotification = {
                                    channelDetailsHandler.uiState.value.channelDetailsEntity?.let { entity ->
                                        contentHandler.updateBottomSheetUiState(
                                            BottomSheetContent.ChannelNotificationsSheet(
                                                entity
                                            )
                                        )
                                    }
                                },
                            )
                        }
                        stickyHeader {
                            RumbleTabsView(
                                modifier = Modifier.background(color = MaterialTheme.colors.background),
                                tabsList = CategoryDisplayType.getChannelDetailsCategoryTypeList(),
                                initialIndex = state.displayType.channelDetailsIndex,
                                onTabSelected = channelDetailsHandler::onDisplayTypeSelected
                            )
                        }
                        if (itemsList.itemCount > 0) {
                            if (state.displayType == CategoryDisplayType.VIDEOS) {
                                item {
                                    VideosCountView(
                                        state,
                                        Modifier
                                            .fillMaxWidth()
                                            .background(color = MaterialTheme.colors.background),
                                        listToggleViewStyle
                                    ) {
                                        channelDetailsHandler.onToggleVideoViewStyle(
                                            it
                                        )
                                    }
                                }

                                state.channelDetailsEntity?.featuredVideo?.let { entity ->
                                    item {
                                        if (listToggleViewStyle == ListToggleViewStyle.LIST) {
                                            ChannelDetailsVideoCompactView(
                                                entity,
                                                channelDetailsHandler,
                                                contentHandler,
                                                true
                                            )
                                        } else {
                                            ChannelDetailsVideoView(
                                                entity,
                                                channelDetailsHandler,
                                                contentHandler,
                                                soundOn,
                                                true
                                            )
                                        }
                                    }
                                }
                            } else if (state.displayType == CategoryDisplayType.REPOSTS) {
                                item {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(color = MaterialTheme.colors.background)
                                            .padding(paddingSmall),
                                    ) {
                                        TitleWithBoxedCount(
                                            count = "${itemsList.itemCount}"
                                        )
                                    }
                                }
                            }
                            items(
                                count = itemsList.itemCount,
                                key = itemsList.itemKey(),
                                contentType = itemsList.itemContentType(
                                )
                            ) { index ->
                                val item = itemsList[index]
                                item?.let { entity ->
                                    if (entity is VideoEntity) {
                                        if (listToggleViewStyle == ListToggleViewStyle.LIST) {
                                            ChannelDetailsVideoCompactView(
                                                entity,
                                                channelDetailsHandler,
                                                contentHandler
                                            )
                                        } else {
                                            ChannelDetailsVideoView(
                                                entity,
                                                channelDetailsHandler,
                                                contentHandler,
                                                soundOn
                                            )
                                        }
                                    } else if (entity is RepostEntity) {
                                        RepostFeedView(
                                            modifier = Modifier
                                                .background(color = MaterialTheme.colors.background)
                                                .padding(
                                                    horizontal = paddingXSmall,
                                                    vertical = paddingXSmall
                                                ),
                                            repost = entity,
                                            onChannelClick = onChannelClick,
                                            onVideoClick = channelDetailsHandler::onVideoClick,
                                            onMoreClick = {
                                                contentHandler.onOpenRepostMoreActions(
                                                    entity
                                                )
                                            }
                                        )
                                    }
                                }

                            }
                            itemsList.apply {
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
                        } else if (itemsList.loadState.refresh != LoadState.Loading) {
                            item {
                                EmptyView(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingMedium),
                                    title = stringResource(id = if (state.displayType == CategoryDisplayType.VIDEOS) R.string.no_videos_yet else R.string.no_reposts_yet),
                                    text = stringResource(id = if (state.displayType == CategoryDisplayType.VIDEOS) R.string.this_channel_doesnt_have_videos_yet else R.string.this_channel_doesnt_have_reposts_yet),
                                )
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
    if (state.loading || itemsList.loadState.refresh == LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (alertDialogState.show) {
        when (val reason = alertDialogState.alertDialogReason) {
            is ChannelDetailsAlertDialogReason.SendEmailErrorDialog -> {
                RumbleAlertDialog(
                    onDismissRequest = { channelDetailsHandler.onDismissDialog() },
                    title = stringResource(id = R.string.unable_to_report),
                    text = stringResource(id = R.string.configure_email_in_settings),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(id = R.string.ok),
                            action = { channelDetailsHandler.onDismissDialog() },
                            dialogActionType = DialogActionType.Positive
                        )
                    )
                )
            }

            is ChannelDetailsAlertDialogReason.RestrictedContentReason -> {
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
                            action = channelDetailsHandler::onCancelRestricted
                        ),
                        DialogActionItem(
                            text = stringResource(R.string.start_watching),
                            dialogActionType = DialogActionType.Positive,
                            width = commentActionButtonWidth,
                            action = { channelDetailsHandler.onWatchRestricted(reason.videoEntity) }
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun ChannelDetailsVideoView(
    entity: VideoEntity,
    channelDetailsHandler: ChannelDetailsHandler,
    contentHandler: ContentHandler,
    soundOn: Boolean,
    featured: Boolean = false
) {
    VideoView(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .padding(
                top = paddingXXMedium,
                start = paddingMedium,
                bottom = paddingXXMedium,
                end = paddingMedium
            ),
        videoEntity = entity,
        rumblePlayer = channelDetailsHandler.currentPlayerState.value,
        soundOn = soundOn,
        onPlayerImpression = channelDetailsHandler::onPlayerImpression,
        onSoundClick = channelDetailsHandler::onSoundClick,
        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
        onImpression = {
            channelDetailsHandler.onVideoCardImpression(it, CardSize.REGULAR)
        },
        onClick = channelDetailsHandler::onVideoClick,
        displayScreenType = DisplayScreenType.CHANNELDETAILS,
        featured = featured,
        isPremiumUser = contentHandler.isPremiumUser(),
    )
}

@Composable
private fun ChannelDetailsVideoCompactView(
    entity: VideoEntity,
    channelDetailsHandler: ChannelDetailsHandler,
    contentHandler: ContentHandler,
    featured: Boolean = false
) {
    VideoCompactView(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .padding(
                top = paddingLarge,
                start = paddingMedium,
                end = paddingMedium
            ),
        videoEntity = entity,
        onViewVideo = channelDetailsHandler::onVideoClick,
        onMoreClick = { contentHandler.onMoreVideoOptionsClicked(entity) },
        onImpression = {
            channelDetailsHandler.onVideoCardImpression(it, CardSize.COMPACT)
        },
        displayScreenType = DisplayScreenType.CHANNELDETAILS,
        featured = featured
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ChannelDetailsScreenDialog(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    dialogState: ChannelDetailsDialog,
    channelDetailsEntity: CreatorEntity?,
    contentHandler: ContentHandler,
    channelDetailsHandler: ChannelDetailsHandler,
    activityHandler: RumbleActivityHandler,
) {
    when (dialogState) {
        is ChannelDetailsDialog.ActionMenuDialog -> {
            ActionsMenuBottomSheet(
                Modifier
                    .systemBarsPadding()
                    .padding(bottom = bottomBarHeight),
                coroutineScope,
                bottomSheetState,
                channelDetailsHandler,
                contentHandler
            )
        }

        is ChannelDetailsDialog.BlockDialog -> {
            BlockBottomSheet(
                Modifier
                    .systemBarsPadding()
                    .padding(bottom = bottomBarHeight),
                coroutineScope,
                bottomSheetState,
                channelDetailsEntity?.channelTitle ?: ""
            ) {
                contentHandler.onUpdateSubscription(
                    channel = dialogState.channelDetailsEntity,
                    action = UpdateChannelSubscriptionAction.BLOCK
                )
            }
        }

        is ChannelDetailsDialog.ReportDialog -> {
            ReportBottomSheet(
                Modifier
                    .systemBarsPadding()
                    .padding(bottom = bottomBarHeight),
                stringResource(id = R.string.report_why_reporting_account),
                coroutineScope,
                bottomSheetState
            ) {
                channelDetailsHandler.onReport(it)
            }
        }

        is ChannelDetailsDialog.LocalsPopupDialog -> {
            LocalsPopupBottomSheet(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = paddingMedium, topEnd = paddingMedium)
                    ),
                dialogState.localsCommunityEntity,
                onSupport = {
                    activityHandler.onOpenWebView(dialogState.localsCommunityEntity.channelUrl)
                },
                onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ActionsMenuBottomSheet(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    channelDetailsHandler: ChannelDetailsHandler,
    contentHandler: ContentHandler
) {
    val state by channelDetailsHandler.uiState.collectAsStateWithLifecycle()
    val sheetItems = mutableListOf(
        if (state.channelDetailsEntity?.blocked == true) {
            BottomSheetItem(
                imageResource = R.drawable.ic_block,
                text = stringResource(id = R.string.unblock)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    contentHandler.onUpdateSubscription(
                        channel = state.channelDetailsEntity,
                        action = UpdateChannelSubscriptionAction.UNBLOCK
                    )
                }
            }
        } else {
            BottomSheetItem(
                imageResource = R.drawable.ic_block,
                text = stringResource(id = R.string.block)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    channelDetailsHandler.onBlockMenuClicked()
                }
            }
        },
        BottomSheetItem(
            imageResource = R.drawable.ic_flag,
            text = stringResource(id = R.string.report)
        ) {
            coroutineScope.launch {
                bottomSheetState.hide()
                channelDetailsHandler.onReportMenuClicked()
            }
        },
    )
    if (state.shareAvailable) {
        sheetItems.add(
            index = 0,
            element = BottomSheetItem(
                imageResource = R.drawable.ic_share,
                text = stringResource(id = R.string.share)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    channelDetailsHandler.onShareChannel()
                }
            }
        )
    }

    RumbleBottomSheet(
        modifier = modifier,
        sheetItems = sheetItems,
        onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BlockBottomSheet(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    channelTitle: String,
    onBlock: () -> Unit,
) {
    RumbleBottomSheet(
        modifier = modifier,
        title = "${stringResource(id = R.string.block)} $channelTitle",
        subtitle = stringResource(id = R.string.block_account_no_longer_accessible),
        sheetItems = listOf(
            BottomSheetItem(
                imageResource = R.drawable.ic_block,
                text = stringResource(id = R.string.block)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onBlock.invoke()
                }
            },
        ),
        onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}


@Composable
private fun ChannelDetailsTopBar(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = { onBackClick.invoke() }) {
        Icon(
            Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.back),
            tint = enforcedWhite
        )
    }
    IconButton(
        modifier = menuModifier.testTag(ChannelTopBarActionMenuTag),
        onClick = { onMenuClick.invoke() }) {
        Icon(
            Icons.Filled.MoreVert,
            contentDescription = "",
            tint = enforcedWhite
        )
    }
}