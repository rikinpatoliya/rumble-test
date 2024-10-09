package com.rumble.battles.discover.presentation.discoverscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import com.rumble.battles.DiscoverPlayerTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.CommentsPopupBottomSheet
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.ReportBottomSheet
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.SwipeDirection
import com.rumble.battles.commonViews.SwipeableLikeDislikeView
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.UploadDateView
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.feed.presentation.views.VerifyEmailBottomSheet
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.RumbleTypography.textShadow
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.discoverPlayerControlsHeight
import com.rumble.theme.discoverPlayerGradientBackgroundHeight
import com.rumble.theme.discoverPlayerSwipeIconSize
import com.rumble.theme.elevation
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.imageSmall
import com.rumble.theme.imageXXMini
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.profileImageWithFollowIcon
import com.rumble.theme.radiusMedium
import com.rumble.theme.rumbleGreen
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.extension.collectAndHandleState
import com.rumble.utils.extension.shortString
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.RumbleVideoView
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playButtonSize
import com.rumble.videoplayer.presentation.internal.defaults.playControl
import com.rumble.videoplayer.presentation.views.LiveButton
import com.rumble.videoplayer.presentation.views.VideoSeekBar
import com.rumble.videoplayer.presentation.views.VideoSettingsBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DiscoverPlayerScreen(
    discoverPlayerHandler: DiscoverPlayerHandler,
    activityHandler: RumbleActivityHandler,
    onBackClick: () -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onChannelClick: (id: String) -> Unit = {},
) {
    val localView = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val coroutineScope = rememberCoroutineScope()
    val popupState by discoverPlayerHandler.popupState.collectAsStateWithLifecycle()
    val alertDialogState by discoverPlayerHandler.alertDialogState
    val userName by discoverPlayerHandler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userPicture by discoverPlayerHandler.userPictureFlow.collectAsStateWithLifecycle(initialValue = "")
    val listState = rememberLazyListState()

    val state by discoverPlayerHandler.uiState.collectAsStateWithLifecycle()
    val videoPagingItems: LazyPagingItems<Feed> = state.videoList.collectAndHandleState(handleLoadStates = discoverPlayerHandler::handleLoadState)

    val pagerState = rememberPagerState {
        videoPagingItems.itemCount
    }

    val updatedEntity by discoverPlayerHandler.updatedEntity.collectAsStateWithLifecycle()
    updatedEntity?.let { updatedVideoEntity ->
        videoPagingItems.itemSnapshotList.find { it is VideoEntity && it.id == updatedVideoEntity.id }
            ?.let {
                val videoEntity = it as VideoEntity
                videoEntity.userVote = updatedVideoEntity.userVote
                videoEntity.likeNumber = updatedVideoEntity.likeNumber
                videoEntity.dislikeNumber = updatedVideoEntity.dislikeNumber
            }
    }

    val followedVideoEntity by discoverPlayerHandler.followedVideoEntity.collectAsStateWithLifecycle()
    followedVideoEntity?.let { updatedVideoEntity ->
        videoPagingItems.itemSnapshotList.find { it is VideoEntity && it.id == updatedVideoEntity.id }
            ?.let {
                val videoEntity = it as VideoEntity
                videoEntity.channelFollowed = updatedVideoEntity.channelFollowed
            }
    }

    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        } else {
            discoverPlayerHandler.stopPlayer()
            onBackClick()
        }
    }

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            discoverPlayerHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            if (discoverPlayerHandler.currentPlayerState.value?.isPlaying() != true)
                discoverPlayerHandler.resumePlayer()
        }
    }

    LaunchedEffect(lifecycleOwner) {
        localView.keepScreenOn = true
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            localView.keepScreenOn = false
            discoverPlayerHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(videoPagingItems.itemSnapshotList) {
        val currentPage = pagerState.currentPage
        if (videoPagingItems.itemCount > 0 && state.scrollToIndex >= 0 && pagerState.pageCount > 0) {
            pagerState.animateScrollToPage(state.scrollToIndex)
            discoverPlayerHandler.updateCurrentVideo(
                state.scrollToIndex,
                videoPagingItems[state.scrollToIndex] as VideoEntity
            )
        } else if (videoPagingItems.itemCount > 0
            && currentPage != state.currentIndex
            && currentPage == 0
        ) {
            discoverPlayerHandler.updateCurrentVideo(
                currentPage,
                videoPagingItems[currentPage] as VideoEntity
            )
        }
    }

    LaunchedEffect(pagerState, videoPagingItems) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (videoPagingItems.itemCount > 0 && page != state.currentIndex && state.scrollToIndex == -1)
                discoverPlayerHandler.updateCurrentVideo(
                    page,
                    videoPagingItems[page] as VideoEntity
                )
        }
    }

    LaunchedEffect(key1 = context) {
        discoverPlayerHandler.vmEvents.collect { event ->
            when (event) {
                is DiscoverPlayerVmEvent.DiscoverPlayerError -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
                is DiscoverPlayerVmEvent.ShowBottomSheetPopup -> {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
                is DiscoverPlayerVmEvent.DismissBottomSheetPopup -> {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                }
                DiscoverPlayerVmEvent.ShowVideoReportedMessage -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.the_video_has_been_reported)
                    )
                }
                is DiscoverPlayerVmEvent.OpenVideoDetails -> {
                    onVideoClick(event.videoEntity)
                }
                DiscoverPlayerVmEvent.ShowCommentReportedMessage -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.the_comment_has_been_reported)
                    )
                }
                DiscoverPlayerVmEvent.ShowEmailVerificationSuccess -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.email_successfully_verified_message),
                        title = context.getString(R.string.сongratulations),
                    )
                }
                DiscoverPlayerVmEvent.HideKeyboard -> focusManager.clearFocus()
                DiscoverPlayerVmEvent.ShowKeyboard -> keyboardController?.show()
                is DiscoverPlayerVmEvent.ScrollCommentToIndex -> listState.animateScrollToItem((event.index + 1))
            }
        }
    }

    TransparentStatusBar()
    DarkSystemNavigationBar()

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            DiscoverPlayerScreenDialog(
                coroutineScope,
                bottomSheetState,
                popupState,
                discoverPlayerHandler,
                activityHandler,
                listState,
                userName,
                userPicture
            )
        }) {
        Box(
            modifier = Modifier
                .testTag(DiscoverPlayerTag)
                .fillMaxSize()
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.background(color = enforcedDarkmo)
            ) { index ->
                if (videoPagingItems.itemCount > 0) {
                    videoPagingItems[index]?.let {
                        if (it is VideoEntity) {
                            DiscoverPlayerItem(
                                modifier = Modifier.fillMaxSize(),
                                discoverPlayerHandler = discoverPlayerHandler,
                                videoEntity = it,
                                onChannelClick = onChannelClick
                            )
                        }
                    }
                }
            }
            TopBarView(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(top = paddingXSmall)
                    .align(Alignment.TopCenter),
                discoverPlayerHandler = discoverPlayerHandler,
                onBackClick = {
                    discoverPlayerHandler.stopPlayer()
                    onBackClick()
                },
            )
            RumbleSnackbarHost(snackBarHostState)

            if (alertDialogState.show) {
                DiscoverPlayerDialog(
                    reason = alertDialogState.alertDialogReason,
                    handler = discoverPlayerHandler
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DiscoverPlayerScreenDialog(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    dialogState: DiscoverPlayerDialog,
    handler: DiscoverPlayerHandler,
    activityHandler: RumbleActivityHandler,
    listState: LazyListState,
    userName: String,
    userPicture: String
) {
    when (dialogState) {
        DiscoverPlayerDialog.DefaultPopupState -> {}
        is DiscoverPlayerDialog.VideoSettingsDialog -> VideoSettingsBottomSheet(
            settingsBottomSheetHandler = handler,
            rumblePlayer = dialogState.rumblePlayer,
            isTablet = IsTablet()
        )
        is DiscoverPlayerDialog.OpenReportVideoPopup -> ReportBottomSheet(
            subtitle = stringResource(id = R.string.why_report_video),
            coroutineScope = coroutineScope,
            bottomSheetState = bottomSheetState,
            onReport = { handler.reportVideo(dialogState.videoEntity, it) })
        DiscoverPlayerDialog.OpenCommentsPopup -> {
            CommentsPopupBottomSheet(
                modifier = Modifier
                    .fillMaxWidth(),
                handler = handler,
                activityHandler = activityHandler,
                listState = listState,
                userName = userName,
                userPicture = userPicture
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
        }
        DiscoverPlayerDialog.OpenEmailVerificationComment -> {
            VerifyEmailBottomSheet(
                subtitle = stringResource(id = R.string.verify_your_email_comments),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onRequestLink = handler::onRequestVerificationLink,
                onCheckVerificationStatus = handler::onCheckVerificationStatus,
            )
        }
        is DiscoverPlayerDialog.OpenReportCommentPopup -> {
            ReportBottomSheet(
                subtitle = stringResource(id = R.string.why_report_comment),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onReport = { handler.report(dialogState.commentEntity, it) })
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiscoverPlayerItem(
    modifier: Modifier = Modifier,
    discoverPlayerHandler: DiscoverPlayerHandler,
    videoEntity: VideoEntity,
    onChannelClick: (channelId: String) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = SwipeDirection.Initial)
    val configuration = LocalConfiguration.current
    val swipeWidth = configuration.screenWidthDp.dp + discoverPlayerSwipeIconSize
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val swipePx = with(LocalDensity.current) { swipeWidth.toPx() }
    val iconSizeOffset = with(LocalDensity.current) { discoverPlayerSwipeIconSize.toPx() }
    val iconHeightOffset = with(LocalDensity.current) { discoverPlayerSwipeIconSize.toPx() }
    val verticalIconOffset = configuration.screenHeightDp + iconHeightOffset.roundToInt()
    val likeHorizontalOffset =
        (swipeableState.offset.value * 1.7).roundToInt() - iconSizeOffset.roundToInt()
    val dislikeHorizontalOffset =
        (swipeableState.offset.value * 1.7).roundToInt() + swipePx.roundToInt() - iconSizeOffset.roundToInt()

    val swipeBackgroundColorProvider = {
        val color =
            if (swipeableState.direction == 1F) rumbleGreen else if (swipeableState.direction == -1F) fierceRed else Color.Transparent
        color.copy(
            alpha = calculateBackgroundAlpha(
                screenWidth,
                swipeableState,
            ).coerceIn(0F, 0.8F)
        )
    }
    val playerMidYPositionHeightOffset = calculatePlayerMidYPosition(
        screenHeight = configuration.screenHeightDp.dp,
        screenWidth = configuration.screenWidthDp.dp,
        videoWidth = videoEntity.videoWidth,
        videoHeight =videoEntity.videoHeight
    )

    ConstraintLayout(
        modifier = modifier.background(color = enforcedDarkmo.copy(alpha = 0.6F))
    ) {
        val (spacer, playerMidYGuideline, stats, videoInfo, controls, gradient, player, swipe, swipeColorFeedback, playButton, likeBtn, dislikeBtn) = createRefs()
        Spacer(modifier = Modifier
            .height(playerMidYPositionHeightOffset)
            .constrainAs(spacer) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })
        Divider(modifier = Modifier
            .constrainAs(playerMidYGuideline) {
                top.linkTo(spacer.bottom)
            }, color = Color.Transparent, thickness = 1.dp)
        PlayerUIView(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(player) {
                    centerVerticallyTo(playerMidYGuideline)
                },
            videoEntity = videoEntity,
            playerBackgroundColor = enforcedDarkmo.copy(alpha = 0.6F),
            rumblePlayer = discoverPlayerHandler.currentPlayerState.value
        )
        if (discoverPlayerHandler.currentPlayerState.value?.isPaused() == true) {
            Box(
                modifier = Modifier
                    .constrainAs(playButton) {
                        centerVerticallyTo(player)
                        centerHorizontallyTo(player)
                    }
                    .size(playButtonSize)
                    .clip(CircleShape)
                    .background(enforcedCloud.copy(0.4f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_play_fullscreen),
                    contentDescription = stringResource(id = R.string.play),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(playControl)
                )
            }
        }
        SwipeableLikeDislikeView(
            modifier = Modifier
                .constrainAs(swipe) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(videoInfo.top)
                },
            swipePx = swipePx,
            swipeableState = swipeableState,
            iconSizeOffset = iconSizeOffset,
            onLike = {
                if (videoEntity.userVote != UserVote.LIKE)
                    discoverPlayerHandler.onLike(videoEntity)
            },
            onDisLike = {
                if (videoEntity.userVote != UserVote.DISLIKE)
                    discoverPlayerHandler.onDislike(videoEntity)
            },
            onClick = {
                if (discoverPlayerHandler.currentPlayerState.value?.isPaused() == true)
                    discoverPlayerHandler.resumePlayer()
                else if (discoverPlayerHandler.currentPlayerState.value?.isPlaying() == true)
                    discoverPlayerHandler.onPauseCurrentPlayer()
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(discoverPlayerGradientBackgroundHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            enforcedBlack.copy(alpha = 0F),
                            enforcedBlack
                        ),
                    )
                )
                .constrainAs(gradient) {
                    bottom.linkTo(parent.bottom)
                }
        ) {}
        StatButtonsView(
            modifier = Modifier
                .padding(
                    bottom = paddingXLarge,
                    end = paddingMedium,
                )
                .constrainAs(stats) {
                    end.linkTo(parent.end)
                    bottom.linkTo(videoInfo.top)
                },
            discoverPlayerHandler = discoverPlayerHandler,
            videoEntity = videoEntity
        )
        VideoInfoView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingMedium,
                    end = paddingMedium,
                )
                .constrainAs(videoInfo) {
                    bottom.linkTo(controls.top)
                },
            videoEntity = videoEntity,
            onFollowClicked = {
                if (it.channelFollowed.not()) discoverPlayerHandler.onFollowChannel(it.channelId)
            },
            onChannelClick = {
                onChannelClick(it)
            }
        )
        ControlsView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingMedium,
                    end = paddingMedium,
                    bottom = paddingXLarge,
                )
                .constrainAs(controls) {
                    bottom.linkTo(parent.bottom)
                },
            discoverPlayerHandler = discoverPlayerHandler
        )
        if (swipeableState.progress.fraction < 1) {
            Box(modifier = Modifier
                .fillMaxSize()
                .constrainAs(swipeColorFeedback) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(color = swipeBackgroundColorProvider.invoke())
            )
        }
        Icon(
            modifier = Modifier
                .offset { IntOffset(likeHorizontalOffset, verticalIconOffset) }
                .size(discoverPlayerSwipeIconSize),
            painter = painterResource(id = R.drawable.ic_like_filled),
            contentDescription = stringResource(id = R.string.like),
            tint = enforcedWhite
        )
        Icon(
            modifier = Modifier
                .offset { IntOffset(dislikeHorizontalOffset, verticalIconOffset) }
                .size(discoverPlayerSwipeIconSize),
            painter = painterResource(id = R.drawable.ic_dislike),
            contentDescription = stringResource(id = R.string.dislike),
            tint = enforcedWhite
        )
    }
}

@Composable
private fun TopBarView(
    modifier: Modifier = Modifier,
    discoverPlayerHandler: DiscoverPlayerHandler,
    onBackClick: () -> Unit,
) {
    RumbleBasicTopAppBar(
        title = "",
        modifier = modifier,
        onBackClick = onBackClick,
        backButtonColor = enforcedWhite,
        extraContent = {
            Row(
                modifier = Modifier.padding(end = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                CircleActionButton(
                    drawableId = R.drawable.ic_share_20,
                    contentDescriptionId = R.string.share,
                ) { discoverPlayerHandler.onShareVideoClicked() }

                Box(
                    modifier = Modifier
                        .shadow(elevation = elevation, shape = CircleShape)
                        .clip(CircleShape)
                        .background(enforcedWhite)
                        .clickable { discoverPlayerHandler.onVideoDetailsClicked() },
                ) {
                    Image(
                        modifier = Modifier.padding(paddingXXSmall),
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = stringResource(id = R.string.play),
                        colorFilter = ColorFilter.tint(enforcedDarkmo)
                    )
                }
            }
        }
    )
}

@Composable
fun PlayerUIView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    playerBackgroundColor: Color,
    rumblePlayer: RumblePlayer? = null
) {
    if (rumblePlayer?.videoId == videoEntity.id) {
        RumbleVideoView(
            modifier = modifier,
            rumblePlayer = rumblePlayer,
            playerBackgroundColor = playerBackgroundColor,
            uiType = UiType.DISCOVER,
            liveChatDisabled = true
        )
    }
}

@Composable
private fun StatButtonsView(
    modifier: Modifier = Modifier,
    discoverPlayerHandler: DiscoverPlayerHandler,
    videoEntity: VideoEntity,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        StatButton(
            drawableId = R.drawable.ic_comments_filled,
            contentDescriptionId = R.string.comments,
            text = videoEntity.commentNumber.shortString()
        ) { discoverPlayerHandler.onCommentsClicked(videoEntity) }
        StatButton(
            drawableId = R.drawable.ic_dislike,
            drawableTint = if (videoEntity.userVote == UserVote.DISLIKE) fierceRed else enforcedDarkmo,
            contentDescriptionId = R.string.dislike,
            text = videoEntity.dislikeNumber.shortString(false)
        ) { discoverPlayerHandler.onDislike(videoEntity) }
        StatButton(
            drawableId = R.drawable.ic_like_filled,
            drawableTint = if (videoEntity.userVote == UserVote.LIKE) rumbleGreen else enforcedDarkmo,
            contentDescriptionId = R.string.like,
            text = videoEntity.likeNumber.shortString(false)
        ) { discoverPlayerHandler.onLike(videoEntity) }
    }
}

@Composable
private fun StatButton(
    drawableId: Int,
    drawableTint: Color = enforcedDarkmo,
    contentDescriptionId: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleActionButton(
            drawableId = drawableId,
            drawableTint = drawableTint,
            contentDescriptionId = contentDescriptionId,
            onClick = onClick
        )
        Text(
            text = text,
            modifier = Modifier.padding(top = paddingXXXSmall),
            color = enforcedWhite,
            style = h6Heavy.copy(
                shadow = textShadow
            )
        )
    }
}

@Composable
fun VideoInfoView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
    onChannelClick: (channelId: String) -> Unit,
    onFollowClicked: (videoEntity: VideoEntity) -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .clickable {
                onChannelClick(videoEntity.channelId)
            },
        horizontalArrangement = Arrangement.spacedBy(paddingMedium),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(profileImageWithFollowIcon)
        ) {
            ProfileImageComponent(
                modifier = Modifier.align(Alignment.TopStart),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXXMediumStyle(),
                userName = videoEntity.channelName,
                userPicture = videoEntity.channelThumbnail
            )
            Box(
                modifier = Modifier
                    .size(imageSmall)
                    .clip(CircleShape)
                    .background(color = rumbleGreen)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        onFollowClicked(videoEntity)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = if (videoEntity.channelFollowed) R.drawable.ic_check_16 else R.drawable.ic_add),
                    contentDescription = stringResource(id = if (videoEntity.channelFollowed) R.string.following else R.string.follow),
                    tint = enforcedWhite
                )
            }
        }
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserNameViewSingleLine(
                    modifier = Modifier
                        .weight(1f, fill = false),
                    name = videoEntity.channelName,
                    verifiedBadge = videoEntity.verifiedBadge,
                    textStyle = RumbleTypography.h6Light,
                    textColor = enforcedWhite,
                    spacerWidth = paddingXXXSmall,
                    verifiedBadgeHeight = verifiedBadgeHeightSmall
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = "",
                    modifier = Modifier.size(imageXXMini),
                    tint = enforcedWhite
                )
                UploadDateView(
                    date = videoEntity.uploadDate,
                    textColor = enforcedWhite
                )
            }
            Text(
                text = videoEntity.title,
                color = enforcedWhite,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                style = h4
            )
        }
    }
}

@Composable
fun ControlsView(
    modifier: Modifier = Modifier,
    discoverPlayerHandler: DiscoverPlayerHandler,
) {
    Row(
        modifier = modifier
            .height(discoverPlayerControlsHeight),
        horizontalArrangement = Arrangement.spacedBy(paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        discoverPlayerHandler.currentPlayerState.value?.let { rumblePlayer ->
            if (discoverPlayerHandler.uiState.value.currentVideoEntity?.videoStatus == VideoStatus.LIVE) {
                LiveButton(
                    type = UiType.EMBEDDED,
                    currentPosition = rumblePlayer.currentPosition.value.toLong(),
                    totalDuration = rumblePlayer.totalTime.value.toLong(),
                    dvrSupported = rumblePlayer.supportsDvr
                ) {
                    rumblePlayer.seekToPercentage(1f)
                }
            }
            if (rumblePlayer.totalTime.value != 0f) {
                VideoSeekBar(
                    modifier = Modifier
                        .weight(1F)
                        .height(imageXXSmall),
                    rumblePlayer = rumblePlayer
                )
            }
        }
        IconButton(onClick = {
            discoverPlayerHandler.onVideoSettingsClicked()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more_horizontal),
                contentDescription = "",
                tint = enforcedWhite
            )
        }
    }
}

@Composable
private fun CircleActionButton(
    drawableId: Int,
    drawableTint: Color = enforcedDarkmo,
    contentDescriptionId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(elevation = elevation, shape = CircleShape)
            .clip(CircleShape)
            .background(enforcedWhite)
            .clickable {
                onClick()
            },
    ) {
        Image(
            modifier = Modifier.padding(paddingXSmall),
            painter = painterResource(id = drawableId),
            contentDescription = stringResource(id = contentDescriptionId),
            colorFilter = ColorFilter.tint(drawableTint)
        )
    }
}

@Composable
private fun DiscoverPlayerDialog(reason: AlertDialogReason, handler: DiscoverPlayerHandler) {
    when (reason) {
        is DiscoverPlayerAlertReason.DiscardReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = null,
                text = stringResource(id = R.string.sure_discard_message),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.discard),
                        dialogActionType = DialogActionType.Destructive,
                        withSpacer = true,
                        width = commentActionButtonWidth,
                        action = { handler.onDiscard(false) }
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.keep_writing),
                        width = commentActionButtonWidth,
                        action = handler::onKeepWriting
                    )
                )
            )
        }
        is DiscoverPlayerAlertReason.ShowEmailVerificationSent -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = stringResource(id = R.string.email_verification_sent),
                text = stringResource(id = R.string.email_sent_instructions, reason.email),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.ok),
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive,
                    )
                )
            )
        }
        is DiscoverPlayerAlertReason.ErrorReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = stringResource(id = R.string.unable_comment),
                text = if (reason.messageToShort) stringResource(id = R.string.comment_too_short)
                else (reason.errorMessage
                    ?: stringResource(id = R.string.generic_error_message_try_later)),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.ok),
                        width = commentActionButtonWidth,
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive,
                    )
                )
            )
        }
        is DiscoverPlayerAlertReason.DeleteReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = null,
                text = stringResource(id = R.string.sure_delete_message),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.delete),
                        dialogActionType = DialogActionType.Destructive,
                        withSpacer = true,
                        width = commentActionButtonWidth,
                        action = { handler.onDeleteAction(reason.commentEntity) }
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.cancel),
                        width = commentActionButtonWidth,
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Neutral,
                    )
                )
            )
        }
        DiscoverPlayerAlertReason.ShowYourEmailNotVerifiedYet -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = stringResource(id = R.string.email_not_verified),
                text = stringResource(id = R.string.email_not_verified_try_later),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.ok),
                        action = handler::onDismissDialog,
                        dialogActionType = DialogActionType.Positive,
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun calculateBackgroundAlpha(
    screenWidth: Float,
    swipeValue: SwipeableState<SwipeDirection>,
): Float {
    val midPoint = screenWidth / 2
    val normalizedSwipeValue = abs(swipeValue.offset.value)

    return if (swipeValue.direction != 0F) {
        if (normalizedSwipeValue <= midPoint) {
            normalizedSwipeValue / (screenWidth * 0.5f)
        } else {
            (screenWidth - normalizedSwipeValue) / (screenWidth * 0.5f)
        }
    } else {
        0F
    }
}

private fun calculatePlayerMidYPosition(
    screenHeight: Dp, screenWidth: Dp, videoWidth: Int, videoHeight: Int
): Dp {
    val height = (screenWidth * videoHeight) / videoWidth
    return (screenHeight + height) / 4
}