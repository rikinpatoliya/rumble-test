package com.rumble.battles.library.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.analytics.CardSize
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.UserInfoView
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.content.presentation.RestrictedContentDialog
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.PlayListVideoView
import com.rumble.battles.library.presentation.playlist.PlayListHandler
import com.rumble.battles.library.presentation.playlist.PlayListScreenAlertReason
import com.rumble.battles.library.presentation.playlist.PlayListScreenVmEvent
import com.rumble.battles.library.presentation.playlist.PlayListTypeRefresh
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntityWithOptions
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.model.PlayListOption
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.network.queryHelpers.PlayListType
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXXSmall
import com.rumble.theme.logoHeaderHeight
import com.rumble.theme.logoHeaderHeightTablets
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.agoString
import com.rumble.utils.extension.collectAndHandleState
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

private const val TOP_BAR_TITLE_VISIBLE_KEY = "PlayListScreenTitleVisible"
const val PLAY_LIST_TYPE_REFRESH = "playListTypeRefresh"
const val PLAY_LIST_ENTITY = "playListEntity"

@Composable
fun PlayListScreen(
    navController: NavHostController,
    playListHandler: PlayListHandler,
    contentHandler: ContentHandler,
    onVideoClick: (VideoEntity) -> Unit,
    onChannelClick: (String) -> Unit,
    onPlayAllClick: (VideoEntity, String) -> Unit,
    onShuffleClick: (VideoEntity, String) -> Unit,
    onBackClick: () -> Unit,
) {
    val state by playListHandler.state.collectAsStateWithLifecycle()
    val playListVideos: LazyPagingItems<Feed> =
        playListHandler.playListVideosFlow.collectAndHandleState(handleLoadStates = playListHandler::handleLoadState)
    val alertDialogState by playListHandler.alertDialogState
    val listState by playListHandler.listState
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    var isTopBarTitleVisible by remember { mutableStateOf(false) }

    val listConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            isTopBarTitleVisible =
                listState.layoutInfo.visibleItemsInfo.firstOrNull()?.key != TOP_BAR_TITLE_VISIBLE_KEY
            return super.onPostScroll(consumed, available, source)
        }
    }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.PlayListDeleted) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(PLAY_LIST_TYPE_REFRESH, PlayListTypeRefresh.PlayLists)
                onBackClick()
            } else if (it is ContentScreenVmEvent.WatchHistoryCleared) {
                playListHandler.onRefreshPlayList()
                playListVideos.refresh()
            } else if (it is ContentScreenVmEvent.ChannelSubscriptionUpdated) {
                playListHandler.updateFollowStatus(it.channelDetailsEntity)
            } else if (it is ContentScreenVmEvent.PlayListUpdated) {
                playListHandler.onPlayListUpdated(it.playListEntity)
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.let { stateHandle ->
                        stateHandle[PLAY_LIST_TYPE_REFRESH] = PlayListTypeRefresh.PlayList
                        stateHandle[PLAY_LIST_ENTITY] = it.playListEntity
                    }
            }
        }
    }

    LaunchedEffect(Unit) {
        playListHandler.eventFlow.collectLatest {
            when (it) {
                is PlayListScreenVmEvent.PlayVideo -> {
                    onVideoClick(it.videoEntity)
                }

                is PlayListScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = it.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is PlayListScreenVmEvent.FollowPLayList -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(
                            if (it.following) R.string.playlist_added_to_library
                            else R.string.playlist_removed_from_library
                        )
                    )
                }

                PlayListScreenVmEvent.WatchHistoryCleared -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(PLAY_LIST_TYPE_REFRESH, PlayListTypeRefresh.WatchHistory)
                    playListVideos.refresh()
                }

                is PlayListScreenVmEvent.UpdateChannelSubscription -> {
                    contentHandler.onUpdateSubscription(it.channelDetailsEntity, it.action)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PlayListScreenTopBar(
            title = if (isTopBarTitleVisible) state.playListEntity.title else "",
            backgroundColor = if (isTopBarTitleVisible) MaterialTheme.colors.onPrimary else MaterialTheme.colors.background,
            onBackClick = { onBackClick() },
        )
        SwipeRefresh(
            modifier = Modifier
                .testTag(SwipeRefreshTag)
                .background(MaterialTheme.colors.surface),
            state = rememberSwipeRefreshState(
                playListVideos.loadState.refresh == LoadState.Loading
            ),
            onRefresh = {
                playListVideos.refresh()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .nestedScroll(listConnection),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(
                        horizontal = CalculatePaddingForTabletWidth(maxWidth = maxWidth),
                    ),
                    state = listState
                ) {
                    item(
                        key = TOP_BAR_TITLE_VISIBLE_KEY
                    ) {

                        Column(
                            modifier = Modifier.background(MaterialTheme.colors.background)
                        ) {
                            PlayListThumbnailView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = paddingSmall),
                                thumbnail = state.playListEntity.thumbnail,
                                visibility = state.playListEntity.visibility
                            )
                            Spacer(modifier = Modifier.height(paddingMedium))
                            Text(
                                text = state.playListEntity.title,
                                modifier = Modifier
                                    .padding(horizontal = paddingSmall),
                                style = h3
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.background(MaterialTheme.colors.background)
                        ) {
                            Spacer(modifier = Modifier.height(paddingSmall))
                            if (state.playListEntity.id != PlayListType.WATCH_HISTORY.id
                                && state.playListEntity.id != PlayListType.WATCH_LATER.id
                            ) {
                                UserInfoView(
                                    modifier = Modifier
                                        .padding(horizontal = paddingSmall),
                                    channelName = state.playListEntity.channelName
                                        ?: state.playListEntity.username,
                                    channelThumbnail = state.playListEntity.channelThumbnail,
                                    channelId = state.playListEntity.channelId,
                                    verifiedBadge = state.playListEntity.verifiedBadge,
                                    followers = state.playListEntity.followers,
                                    followStatus = state.followStatus,
                                    onUpdateSubscription = {
                                        playListHandler.onUpdateSubscription(it)
                                    },
                                    onChannelClick = { onChannelClick(state.playListEntity.channelId) }
                                )
                            }
                            Spacer(modifier = Modifier.height(paddingSmall))
                            PlayListDetails(
                                modifier = Modifier
                                    .padding(horizontal = paddingMedium),
                                playListEntity = state.playListEntity
                            )
                            Spacer(modifier = Modifier.height(paddingSmall))
                            PlayListSecondaryActionButtons(
                                modifier = Modifier
                                    .padding(horizontal = paddingXXSmall),
                                playListHandler = playListHandler,
                                contentHandler = contentHandler,
                                playListVisibility = state.playListEntity.visibility,
                                isFollowing = state.playListEntity.isFollowing,
                                playListEntity = state.playListEntity,
                                playListOptions = state.playListOptions,
                                followStatus = state.followStatus
                            )
                            Spacer(modifier = Modifier.height(paddingMedium))
                            PlayListMainActionButtons(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .conditional(playListVideos.itemCount == 0) {
                                        this.alpha(0.5F)
                                    }
                                    .padding(horizontal = paddingSmall),
                                onPlayAllClick = {
                                    if (playListVideos.itemCount > 0)
                                        onPlayAllClick(
                                            playListVideos[0] as VideoEntity,
                                            state.playListEntity.id
                                        )
                                },
                                onShuffleClick = {
                                    if (playListVideos.itemCount > 0)
                                        onShuffleClick(
                                            playListVideos[0] as VideoEntity,
                                            state.playListEntity.id
                                        )
                                },
                            )
                            Spacer(modifier = Modifier.height(paddingMedium))
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(paddingMedium))
                    }

                    items(
                        count = playListVideos.itemCount,
                        key = playListVideos.itemKey(),
                        contentType = playListVideos.itemContentType()
                    ) { index ->
                        val item = playListVideos[index]
                        if (item != null && item is VideoEntity) {
                            PlayListVideoView(
                                modifier = Modifier
                                    .padding(
                                        start = paddingMedium,
                                        end = paddingMedium
                                    ),
                                videoNumber = index + 1,
                                videoEntity = item,
                                onViewVideo = playListHandler::onVideoClick,
                                onMoreClick = {
                                    contentHandler.onMoreVideoOptionsClicked(
                                        item,
                                        state.playListEntity.id
                                    )
                                },
                                onImpression = {
                                    playListHandler.onVideoCardImpression(it, CardSize.COMPACT)
                                }
                            )
                            Spacer(modifier = Modifier.height(paddingMedium))
                        }
                    }

                    playListVideos.apply {
                        when {
                            loadState.refresh is LoadState.Error -> {
                                item {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(paddingMedium),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = playListVideos::refresh
                                    )
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item {
                                    PageLoadingView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                    )
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
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

                    item {
                        BottomNavigationBarScreenSpacer()
                    }
                }
            }
        }

        if (playListVideos.itemCount == 0) {
            EmptyView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingMedium),
                title = stringResource(id = R.string.no_videos_in_playlist),
                text = ""
            )
        }
    }

    if (alertDialogState.show) {
        PlayListScreenDialog(
            reason = alertDialogState.alertDialogReason,
            handler = playListHandler
        )
    }

    RumbleSnackbarHost(snackBarHostState)
}

@Composable
private fun PlayListThumbnailView(
    modifier: Modifier = Modifier,
    thumbnail: String,
    visibility: PlayListVisibility,
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (tag) = createRefs()
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusMedium))
                .aspectRatio(RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION),
            model = thumbnail.ifEmpty { R.drawable.empty_playlist_humbnail },
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )
        if (visibility != PlayListVisibility.PUBLIC) {
            Row(
                modifier = Modifier
                    .padding(start = paddingSmall, bottom = paddingSmall)
                    .clip(RoundedCornerShape(radiusXSmall))
                    .background(enforcedDarkmo.copy(alpha = 0.9F))
                    .constrainAs(tag) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(vertical = paddingXXSmall, horizontal = paddingXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = visibility.value,
                    modifier = Modifier.size(imageXXSmall),
                    tint = enforcedWhite
                )
                Text(
                    modifier = Modifier.padding(start = paddingXXSmall),
                    text = visibility.value.uppercase(),
                    style = RumbleTypography.tinyBodySemiBold,
                    color = enforcedWhite
                )
            }
        }
    }
}

@Composable
private fun PlayListScreenDialog(reason: AlertDialogReason, handler: PlayListHandler) {
    when (reason) {
        is PlayListScreenAlertReason.RestrictedContentReason ->
            RestrictedContentDialog(
                onCancelRestricted = handler::onCancelRestricted,
                onWatchRestricted = { handler.onWatchRestricted(reason.videoEntity) }
            )
    }
}

@Composable
fun PlayListMainActionButtons(
    modifier: Modifier = Modifier,
    onPlayAllClick: () -> Unit,
    onShuffleClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        ActionButton(
            modifier = Modifier
                .weight(1f),
            text = stringResource(id = R.string.play_all),
            leadingIconPainter = painterResource(id = R.drawable.ic_play_16),
            textColor = enforcedDarkmo
        ) { onPlayAllClick() }
        ActionButton(
            modifier = Modifier
                .weight(1f),
            text = stringResource(id = R.string.shuffle),
            leadingIconPainter = painterResource(id = R.drawable.ic_shuffle_16),
            backgroundColor = MaterialTheme.colors.onSurface,
            borderColor = MaterialTheme.colors.onSurface,
            textColor = MaterialTheme.colors.primary
        ) { onShuffleClick() }
    }
}

@Composable
fun PlayListSecondaryActionButtons(
    modifier: Modifier = Modifier,
    playListHandler: PlayListHandler,
    contentHandler: ContentHandler,
    playListVisibility: PlayListVisibility,
    isFollowing: Boolean,
    playListEntity: PlayListEntity,
    playListOptions: List<PlayListOption>,
    followStatus: FollowStatus? = null,
) {
    Row(
        modifier = modifier
    ) {
        followStatus?.let {
            RoundIconButton(
                painter = painterResource(id = if (isFollowing) R.drawable.ic_saved_in_playlist else R.drawable.ic_save_playlist),
                size = imageXMedium,
                backgroundColor = MaterialTheme.colors.onSurface,
                tintColor = MaterialTheme.colors.primary
            ) {
                playListHandler.onFollowPlayList(isFollowing)
            }
        }
        if (playListVisibility != PlayListVisibility.PRIVATE) {
            RoundIconButton(
                painter = painterResource(id = R.drawable.ic_share_16),
                size = imageXMedium,
                backgroundColor = MaterialTheme.colors.onSurface,
                tintColor = MaterialTheme.colors.primary
            ) {
                playListHandler.onShare()
            }
        }
        if (playListOptions.isNotEmpty()) {
            RoundIconButton(
                painter = painterResource(id = R.drawable.ic_vertical_ellipses),
                size = imageXMedium,
                backgroundColor = MaterialTheme.colors.onSurface,
                tintColor = MaterialTheme.colors.primary
            ) {
                contentHandler.onMorePlayListOptions(
                    PlayListEntityWithOptions(
                        playListEntity = playListEntity,
                        playListOptions = playListOptions
                    )
                )
            }
        }
    }
}

@Composable
fun PlayListDetails(
    modifier: Modifier = Modifier,
    playListEntity: PlayListEntity
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_video),
            contentDescription = stringResource(id = R.string.videos),
            modifier = Modifier.size(imageXXSmall),
            tint = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.width(paddingXXSmall))
        Text(
            text = "${playListEntity.videosQuantity} ${
                pluralStringResource(
                    id = R.plurals.video,
                    playListEntity.videosQuantity
                ).lowercase()
            }",
            color = MaterialTheme.colors.secondary,
            style = RumbleTypography.h6
        )
        Spacer(modifier = Modifier.width(paddingMedium))
        Text(
            text = "${stringResource(id = R.string.updated)} ${
                playListEntity.updatedDate.agoString(
                    LocalContext.current
                )
            }",
            color = MaterialTheme.colors.secondary,
            style = RumbleTypography.h6
        )
    }
}

@Composable
fun PlayListScreenTopBar(
    title: String,
    backgroundColor: Color,
    onBackClick: () -> Unit
) {
    val tablet = IsTablet()
    Row(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .height(if (tablet) logoHeaderHeightTablets else logoHeaderHeight)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onBackClick.invoke() }) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = MaterialTheme.colors.primary
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .weight(1F)
                .padding(end = paddingMedium),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = h3
        )
    }
}