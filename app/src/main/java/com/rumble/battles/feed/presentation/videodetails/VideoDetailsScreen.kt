package com.rumble.battles.feed.presentation.videodetails

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rumble.battles.JoinOnLocalsViewTag
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.VideoDetails
import com.rumble.battles.VideoPlayerViewTag
import com.rumble.battles.comments.CommentsView
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.ExpandableText
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.LocalsPopupBottomSheet
import com.rumble.battles.commonViews.ReportBottomSheet
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.UserInfoView
import com.rumble.battles.commonViews.VideoTimestampLabelView
import com.rumble.battles.commonViews.ViewsNumberView
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.keyboardAsState
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.feed.presentation.views.CommentAuthorChooserBottomSheet
import com.rumble.battles.feed.presentation.views.LikeDislikeView
import com.rumble.battles.feed.presentation.views.LikeDislikeViewStyle
import com.rumble.battles.feed.presentation.views.PlayListVideoView
import com.rumble.battles.feed.presentation.views.PremiumOnlyContentView
import com.rumble.battles.feed.presentation.views.PremiumOnlyThumbnailView
import com.rumble.battles.feed.presentation.views.TotalLiveTimeView
import com.rumble.battles.feed.presentation.views.VerifyEmailBottomSheet
import com.rumble.battles.feed.presentation.views.VideoCardPremiumTagView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.feed.presentation.views.VideoTagListView
import com.rumble.battles.feed.presentation.views.WatchingNowView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.landing.RumbleEvent
import com.rumble.battles.livechat.presentation.LiveChatEvent
import com.rumble.battles.livechat.presentation.LiveChatHandler
import com.rumble.battles.livechat.presentation.LiveChatView
import com.rumble.battles.livechat.presentation.content.LiveChatModerationMenu
import com.rumble.battles.livechat.presentation.content.MuteUserBottomSheet
import com.rumble.battles.livechat.presentation.rant.BuyRantSheet
import com.rumble.battles.rumbleads.presentation.RumbleAdView
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.network.queryHelpers.SubscriptionSource
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMedium
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXXMini
import com.rumble.theme.imageXXSmall
import com.rumble.theme.minDefaultEmptyViewHeight
import com.rumble.theme.miniPlayerBottomPadding
import com.rumble.theme.miniPlayerBottomThreshold
import com.rumble.theme.miniPlayerHeight
import com.rumble.theme.miniPlayerWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingNone
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXGiant
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXLarge
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.playListVideoDetailsMaxHeight
import com.rumble.theme.radiusLarge
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusNone
import com.rumble.theme.radiusXXXXMedium
import com.rumble.theme.videoHeightReduced
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.COLLAPSE_ANIMATION_DURATION
import com.rumble.utils.RumbleConstants.LIVE_CHAT_ANIMATION_DURATION
import com.rumble.utils.extension.capitalizeWords
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.RumbleVideoMode
import com.rumble.videoplayer.presentation.RumbleVideoView
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.views.MiniControllerView
import com.rumble.videoplayer.presentation.views.MiniPlayerControlsView
import com.rumble.videoplayer.presentation.views.MiniPlayerInfoView
import com.rumble.videoplayer.presentation.views.MiniPlayerView
import com.rumble.videoplayer.presentation.views.VideoSettingsBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.roundToInt

@SuppressLint("SourceLockedOrientationActivity", "InlinedApi")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideoDetailsScreen(
    activityHandler: RumbleActivityHandler,
    handler: VideoDetailsHandler,
    contentHandler: ContentHandler,
    liveChatHandler: LiveChatHandler,
    contentBottomSheetState: ModalBottomSheetState,
    onChannelClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val state by handler.state
    val liveChatState by liveChatHandler.state
    val activity = LocalContext.current as Activity
    val muteBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val liveChatBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = tween(LIVE_CHAT_ANIMATION_DURATION),
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val systemUiController = rememberSystemUiController()
    val playerTarget = state.rumblePlayer?.playerTarget
    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                activityHandler.currentPlayer = state.rumblePlayer
                handler.onEnableOrientationChangeListener()
            }

            Lifecycle.Event.ON_PAUSE -> {
                handler.onDisableOrientationChangeListener()
            }

            else -> {}
        }
    }
    val contentListState = rememberLazyListState()
    var collapsePercentage by remember { mutableFloatStateOf(0f) }
    var collapseDirection by remember { mutableStateOf(CollapseDirection.DOWN) }
    var collapsePaddingVisible by remember { mutableStateOf(false) }
    val collapseHorizontalPadding: Dp by animateDpAsState(
        if (collapsePaddingVisible) paddingXXSmall else paddingNone,
        tween(COLLAPSE_ANIMATION_DURATION),
        label = "padding"
    )
    val collapseBottomPaddingDp: Dp by animateDpAsState(
        if (collapsePaddingVisible) miniPlayerBottomPadding else paddingNone,
        tween(COLLAPSE_ANIMATION_DURATION),
        label = "bottomPadding"
    )
    val collapsingRadius: Dp by animateDpAsState(
        if (collapsePaddingVisible) radiusXXXXMedium else radiusNone,
        tween(COLLAPSE_ANIMATION_DURATION),
        label = "radius"
    )
    var collapsed by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        if (state.screenOrientationLocked.not()) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            if (state.layoutState != CollapsableLayoutState.COLLAPSED) {
                activityHandler.onPauseVideo()
            }
            lifecycleOwner.lifecycle.removeObserver(observer)
            activityHandler.disableDynamicOrientationChangeBasedOnDeviceType()
        }
    }

    LaunchedEffect(state.isFullScreen) {
        systemUiController.isSystemBarsVisible = state.isFullScreen.not()
        systemUiController.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (state.isFullScreen) {
            if (contentHandler.bottomSheetUiState.value.data is BottomSheetContent.PremiumPromo) {
                contentHandler.onClosePremiumPromo()
            }
            contentHandler.updateBottomSheetUiState(BottomSheetContent.HideBottomSheet)
            handler.onDismissBottomSheet()
        }
    }

    LaunchedEffect(state) {
        if (activityHandler.currentPlayer == null) {
            activityHandler.currentPlayer = state.rumblePlayer
        }
    }

    LaunchedEffect(configuration.orientation) {
        handler.onOrientationChanged(configuration.orientation)
    }

    LaunchedEffect(Unit) {
        handler.eventFlow.collectLatest {
            when (it) {
                is VideoDetailsEvent.VideoDetailsError -> {
                    contentHandler.onError(
                        errorMessage = it.errorMessage,
                        withPadding = false
                    )
                }

                is VideoDetailsEvent.HideKeyboard -> focusManager.clearFocus()
                is VideoDetailsEvent.ShowKeyboard -> keyboardController?.show()
                is VideoDetailsEvent.ShowBottomSheet -> {
                    coroutineScope.launch { bottomSheetState.show() }
                }

                is VideoDetailsEvent.CloseVideoDetails -> {
                    contentHandler.onCloseVideoDetails()
                    collapsed = false
                    activityHandler.currentPlayer = null
                }

                is VideoDetailsEvent.ShowCommentReportedMessage -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.the_comment_has_been_reported,
                        withPadding = false
                    )
                }

                is VideoDetailsEvent.ShowVideoReportedMessage -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.the_video_has_been_reported,
                        withPadding = false
                    )
                }

                is VideoDetailsEvent.ShowEmailVerificationSuccess -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.email_successfully_verified_message,
                        titleId = R.string.сongratulations,
                        withPadding = false
                    )
                }

                is VideoDetailsEvent.HideBottomSheet -> bottomSheetState.hide()

                is VideoDetailsEvent.OpenLiveChat -> liveChatBottomSheetState.show()

                is VideoDetailsEvent.CloseLiveChat -> liveChatBottomSheetState.hide()

                is VideoDetailsEvent.OpenComments -> liveChatBottomSheetState.show()

                is VideoDetailsEvent.CloseComments -> liveChatBottomSheetState.hide()

                is VideoDetailsEvent.InitLiveChat -> {
                    liveChatHandler.onInitLiveChat(it.videoId)
                    if (state.hasPremiumRestriction.not() && state.inComments.not() && state.inLiveChat)
                        liveChatBottomSheetState.show()
                }

                is VideoDetailsEvent.StartBuyRantFlow -> {
                    liveChatHandler.onBuyRant(it.pendingMessageInfo)
                }

                is VideoDetailsEvent.ScrollToTop -> {
                    contentListState.scrollToItem(0)
                }

                is VideoDetailsEvent.ShowPremiumPromo -> {
                    contentHandler.onShowPremiumPromo(
                        state.videoEntity?.id,
                        SubscriptionSource.Video
                    )
                }

                is VideoDetailsEvent.OpenMuteMenu -> {
                    muteBottomSheetState.show()
                }

                is VideoDetailsEvent.CloseMuteMenu -> {
                    muteBottomSheetState.hide()
                }

                is VideoDetailsEvent.OpenPremiumSubscriptionOptions -> {
                    contentHandler.onShowSubscriptionOptions(
                        videoId = state.videoEntity?.id,
                        source = SubscriptionSource.Video
                    )
                }

                is VideoDetailsEvent.SetOrientation -> {
                    activity.requestedOrientation = it.orientation
                }

                is VideoDetailsEvent.OpenAuthMenu -> {
                    contentHandler.onOpenAuthMenu()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        activityHandler.eventFlow.collectLatest {
            if (it == RumbleEvent.PipModeEntered) {
                handler.onPipModeEntered()
            } else if (it == RumbleEvent.PremiumPurchased) {
                handler.onReloadContent()
            }
        }
    }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.ChannelSubscriptionUpdated) {
                handler.updateChannelDetailsEntity(it.channelDetailsEntity)
            }
        }
    }

    LaunchedEffect(Unit) {
        liveChatHandler.eventFlow.collectLatest {
            when (it) {
                is LiveChatEvent.EnforceLiveGatePremiumRestriction -> {
                    handler.onEnforceLiveGatePremiumRestriction()
                }

                is LiveChatEvent.LiveGateStarted -> {
                    handler.onLiveGateEvent(it.liveGateEntity)
                }

                is LiveChatEvent.RedirectTo -> {
                    handler.onLoadNewVideo(it.videoUrl)
                }

                else -> return@collectLatest
            }
        }
    }

    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        } else if (contentBottomSheetState.isVisible) {
            coroutineScope.launch { contentBottomSheetState.hide() }
        } else if (state.layoutState == CollapsableLayoutState.EXPENDED) {
            handler.onUpdateLayoutState(CollapsableLayoutState.COLLAPSED)
        } else {
            onNavigateBack()
        }
    }

    BoxWithConstraints {
        val contentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)
        val boxMaxWidth = maxWidth
        val boxMaxHeight = maxHeight

        CollapsableLayout(
            modifier = Modifier
                .conditional(collapsed) {
                    padding(horizontal = collapseHorizontalPadding + contentPadding)
                }
                .conditional(collapsed.not()) {
                    padding(horizontal = collapseHorizontalPadding)
                }
                .padding(bottom = collapseBottomPaddingDp)
                .conditional(state.isFullScreen.not()) {
                    systemBarsPadding()
                },
            collapseAvailable = state.isFullScreen.not(),
            enforcedState = state.layoutState,
            bottomThreshold = miniPlayerBottomThreshold,
            cornerRadius = collapsingRadius,
            delayBeforeExpend = COLLAPSE_ANIMATION_DURATION.toLong(),
            onStateChanged = {
                collapsed = it == CollapsableLayoutState.COLLAPSED
                collapsePaddingVisible = collapsed
                handler.onUpdateLayoutState(it)
            },
            onCollapseProgress = { percentage, direction ->
                if (!state.isFullScreen) {
                    collapseDirection = direction
                    collapsePercentage = percentage
                    if (direction == CollapseDirection.UP && collapsed) {
                        collapsed = false
                        collapsePaddingVisible = false
                    }
                    if (percentage >= 0.5f && collapseHorizontalPadding == paddingNone && direction == CollapseDirection.DOWN) collapsePaddingVisible =
                        true
                    handler.onCollapsing(percentage)
                }
            }
        ) {
            if (collapsed) {
                state.rumblePlayer?.let { rumblePlayer ->
                    MiniPlayerView(
                        modifier = Modifier.fillMaxWidth(),
                        rumblePlayer = rumblePlayer,
                        onClose = {
                            if (state.currentComment.isNotEmpty()) {
                                collapsePaddingVisible = false
                                handler.onUpdateLayoutState(CollapsableLayoutState.EXPENDED)
                            }
                            handler.onClearVideo()
                        },
                        onClick = {
                            collapsePaddingVisible = false
                            handler.onUpdateLayoutState(CollapsableLayoutState.EXPENDED)
                        }
                    )
                }
            } else {
                RumbleModalBottomSheetLayout(
                    sheetState = muteBottomSheetState,
                    sheetContent = {
                        MuteUserBottomSheet(
                            userName = liveChatState.selectedMessage?.userName ?: "",
                            onMute = { period ->
                                state.videoEntity?.let {
                                    liveChatHandler.onMuteUserConfirmed(it.id, period)
                                }
                            },
                            onCancel = handler::onDismissMuteMenu
                        )
                    }
                ) {
                    RumbleModalBottomSheetLayout(
                        sheetState = bottomSheetState,
                        sheetContent = {
                            BottomSheetDialog(
                                handler = handler,
                                liveChatHandler = liveChatHandler,
                                activityHandler = activityHandler,
                                coroutineScope = coroutineScope,
                                bottomSheetState = bottomSheetState
                            )
                        }) {

                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .fillMaxSize()
                                .testTag(VideoDetails)
                        ) {
                            VideoDetailsView(
                                contentPadding = contentPadding,
                                boxMaxWidth = boxMaxWidth,
                                boxMxHeight = boxMaxHeight,
                                collapsePaddingVisible = collapsePaddingVisible,
                                handler = handler,
                                contentHandler = contentHandler,
                                activityHandler = activityHandler,
                                liveChatHandler = liveChatHandler,
                                collapsePercentage = collapsePercentage,
                                collapseDirection = collapseDirection,
                                onChannelClick = onChannelClick,
                                onCategoryClick = onCategoryClick,
                                onTagClick = onTagClick,
                                liveChatBottomSheetState = liveChatBottomSheetState,
                                contentListState = contentListState,
                                onEnforceCollapse = {
                                    handler.onUpdateLayoutState(
                                        CollapsableLayoutState.COLLAPSED
                                    )
                                }
                            )

                            if (playerTarget?.value == PlayerTarget.REMOTE) {
                                MiniControllerView(
                                    modifier = Modifier
                                        .imePadding()
                                        .fillMaxWidth()
                                        .align(BottomCenter)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideoDetailsView(
    modifier: Modifier = Modifier,
    boxMaxWidth: Dp,
    boxMxHeight: Dp,
    contentPadding: Dp,
    collapsePaddingVisible: Boolean,
    activityHandler: RumbleActivityHandler,
    handler: VideoDetailsHandler,
    contentHandler: ContentHandler,
    liveChatHandler: LiveChatHandler,
    liveChatBottomSheetState: ModalBottomSheetState,
    contentListState: LazyListState,
    collapsePercentage: Float,
    collapseDirection: CollapseDirection,
    onChannelClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    onEnforceCollapse: () -> Unit,
) {
    val state by handler.state
    val alertDialogState by handler.alertDialogState
    val coroutineScope = rememberCoroutineScope()
    var minimalHeightReached by rememberSaveable { mutableStateOf(collapsePercentage > 0f) }
    val displayPremiumOnlyContent =
        state.isFullScreen.not() && state.displayPremiumOnlyContent && collapsePaddingVisible.not()

    LaunchedEffect(Unit) {
        snapshotFlow { liveChatBottomSheetState.currentValue }
            .collectLatest {
                if (it == ModalBottomSheetValue.Hidden) handler.onLiveChatHidden()
            }
    }

    LaunchedEffect(liveChatBottomSheetState.targetValue) {
        if (liveChatBottomSheetState.targetValue == ModalBottomSheetValue.Hidden) {
            handler.onCloseLiveChat()
        }
    }

    val actualWidth: Dp by animateDpAsState(
        if (minimalHeightReached) miniPlayerWidth else boxMaxWidth,
        tween(COLLAPSE_ANIMATION_DURATION),
        label = "width"
    )

    Column(
        modifier = modifier
    ) {
        Box {
            this@Column.AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(BottomCenter),
                visible = displayPremiumOnlyContent,
                content = {
                    PremiumOnlyContentView(
                        modifier = Modifier
                            .padding(horizontal = contentPadding)
                            .fillMaxWidth(),
                        onSubscribe = handler::onSubscribeToPremium
                    )
                },
                enter = fadeIn(),
                exit = fadeOut()
            )

            Row(
                modifier = Modifier
                    .conditional(displayPremiumOnlyContent && IsTablet().not()) {
                        padding(bottom = paddingXXXLarge)
                    }
                    .conditional(displayPremiumOnlyContent && IsTablet()) {
                        padding(bottom = paddingXXLarge)
                    }
                    .align(Alignment.TopCenter),
                verticalAlignment = CenterVertically) {
                val isTablet = IsTablet()
                val isKeyboardVisible by keyboardAsState()
                val width =
                    if (isTablet && state.isFullScreen.not()) boxMaxWidth - contentPadding * 2 else boxMaxWidth
                val height = if (isKeyboardVisible) videoHeightReduced else {
                    if (state.isFullScreen) boxMxHeight
                    else if (state.videoEntity?.portraitMode == true && state.hasPremiumRestriction.not()) {
                        if (isTablet) {
                            min(width, boxMxHeight)
                        } else {
                            boxMaxWidth
                        }
                    } else width / 16 * 9
                }

                if (miniPlayerHeight >= height * (1 - collapsePercentage) && collapseDirection == CollapseDirection.DOWN) {
                    minimalHeightReached = true
                } else if (collapseDirection == CollapseDirection.UP) {
                    minimalHeightReached = false
                }

                val actualHeight = max(height * (1 - collapsePercentage), miniPlayerHeight)

                val sizeModifier =
                    Modifier
                        .conditional(state.isFullScreen.not()) { this.padding(horizontal = contentPadding) }
                        .conditional(state.isFullScreen) {
                            width(width)
                        }
                        .conditional(state.isFullScreen.not()) {
                            width(actualWidth)
                        }
                        .height(actualHeight)

                if (state.hasPremiumRestriction) {
                    PremiumOnlyThumbnailView(
                        modifier = sizeModifier
                            .aspectRatio(
                                ratio = RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION,
                            ),
                        text = stringResource(id = R.string.this_video_only_rumble_premium),
                        url = state.videoEntity?.videoThumbnail ?: "",
                        onBack = { handler.onCloseVideoDetails() },
                        onSubscribeNow = {
                            contentHandler.onShowSubscriptionOptions(
                                videoId = state.videoEntity?.id,
                                source = SubscriptionSource.Video
                            )
                        }
                    )
                } else if (state.hasLiveGateRestriction){
                    PremiumOnlyThumbnailView(
                        modifier = sizeModifier
                            .aspectRatio(
                                ratio = RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION,
                            ),
                        text = stringResource(id = R.string.rest_video_premium_only),
                        url = state.videoEntity?.videoThumbnail ?: "",
                        onBack = { handler.onCloseVideoDetails() },
                        onSubscribeNow = {
                            contentHandler.onShowSubscriptionOptions(
                                videoId = state.videoEntity?.id,
                                source = SubscriptionSource.Video
                            )
                        }
                    )
                } else {
                    VideoPlayerView(
                        modifier = sizeModifier
                            .testTag(VideoPlayerViewTag)
                            .conditional(state.inLiveChat and state.isFullScreen) {
                                Modifier.weight(3f)
                            }
                            .background(MaterialTheme.colors.primaryVariant),
                        rumblePlayer = state.rumblePlayer,
                        handler = handler,
                        fullScreen = state.isFullScreen,
                        isCollapsingMiniPlayerInProgress = state.isCollapsingMiniPlayerInProgress,
                        uiType = state.uiType,
                        liveChatDisabled = state.videoEntity?.liveChatDisabled ?: true,
                        onCollapse = onEnforceCollapse,
                    )

                    if (state.isFullScreen.not()) {
                        state.rumblePlayer?.let {
                            MiniPlayerInfoView(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = paddingMedium),
                                rumblePlayer = it
                            )
                        }

                        MiniPlayerControlsView(
                            modifier = Modifier.padding(end = paddingMedium),
                            playerTarget = state.rumblePlayer?.playerTarget?.value,
                            isPlaying = state.rumblePlayer?.isPlaying() ?: true,
                        )
                    }

                    if (state.inLiveChat and state.isFullScreen) {
                        LiveChatView(
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight(),
                            handler = handler,
                            liveChatHandler = liveChatHandler,
                            activityHandler = activityHandler
                        )
                    }
                }
            }
        }

        if (state.isFullScreen.not()) {
            Box {
                RumbleModalBottomSheetLayout(
                    sheetState = liveChatBottomSheetState,
                    sheetContent = {
                        val sheetContentModifier = Modifier
                            .fillMaxSize()
                            .conditional(IsTablet()) {
                                padding(horizontal = paddingXXMedium)
                            }
                        if (state.lastBottomSheet == LastBottomSheet.LIVECHAT) {
                            LiveChatView(
                                modifier = sheetContentModifier,
                                handler = handler,
                                liveChatHandler = liveChatHandler,
                                activityHandler = activityHandler
                            )
                        } else if (state.lastBottomSheet == LastBottomSheet.COMMENTS) {
                            CommentsView(
                                modifier = sheetContentModifier,
                                handler = handler,
                                activityHandler = activityHandler
                            )
                        }
                    }) {
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            RumbleProgressIndicator(modifier = Modifier.align(Center))
                        }
                    } else {
                        ChannelContentView(
                            modifier = Modifier.fillMaxSize(),
                            handler = handler,
                            contentHandler = contentHandler,
                            onChannelClick = onChannelClick,
                            onCategoryClick = onCategoryClick,
                            onTagClick = onTagClick,
                            coroutineScope = coroutineScope,
                            contentPadding = contentPadding,
                            activityHandler = activityHandler,
                            contentListState = contentListState
                        )
                    }
                    Box(
                        modifier
                            .fillMaxSize()
                            .background(RumbleCustomTheme.colors.background.copy(alpha = if (collapsePercentage in 0.0..1.0) collapsePercentage else 0f))
                    )
                }
            }
        }
    }

    if (alertDialogState.show) {
        VideoDetailsDialog(
            reason = alertDialogState.alertDialogReason,
            handler = handler
        )
    }
}

@Composable
private fun ChannelContentView(
    modifier: Modifier,
    handler: VideoDetailsHandler,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
    onChannelClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    coroutineScope: CoroutineScope,
    contentPadding: Dp,
    contentListState: LazyListState
) {
    val state by handler.state
    val playerTarget = state.rumblePlayer?.playerTarget
    val playListState by handler.playListState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = contentListState
    ) {

        if (state.isPlayListPlayBackMode && playListState != null) {
            playListState?.let {
                item {
                    VideoDetailsPlayListView(
                        modifier = Modifier.background(color = MaterialTheme.colors.surface),
                        handler = handler,
                        contentHandler = contentHandler,
                        playListState = it,
                        currentVideoId = state.videoEntity?.id ?: 0,
                        onLoopPlayList = handler::onLoopPlayList,
                        onShufflePlayList = handler::onShufflePlayList,
                        onPlayListVideoClick = handler::onPlayListVideoClick
                    )
                }
            }
        }
        item {
            VideoDetailsHeaderView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingSmall, bottom = paddingSmall),
                videoEntity = state.videoEntity,
                contentHandler = contentHandler,
                handler = handler,
                onChannelClick = onChannelClick,
                activityHandler = activityHandler
            )
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.secondaryVariant
            )
            state.rumbleAdEntity?.let {
                RumbleAdView(
                    modifier = Modifier.padding(
                        top = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium
                    ),
                    rumbleAdEntity = it,
                    onClick = { adEntity ->
                        activityHandler.onOpenWebView(adEntity.clickUrl)
                    },
                    onLaunch = handler::onRumbleAdImpression
                )
            }
            VideoDetailsInfoView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingMedium)
                    .clip(RoundedCornerShape(radiusMedium))
                    .background(color = MaterialTheme.colors.surface),
                handler = handler,
                contentHandler = contentHandler,
                activityHandler = activityHandler,
                coroutineScope = coroutineScope,
                videoEntity = state.videoEntity,
                onCategoryClick = onCategoryClick,
                onTagClick = onTagClick
            )
        }

        if (state.isLoading.not()) {
            if (state.videoEntity?.relatedVideoList.isNullOrEmpty().not()) {
                item {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
                state.videoEntity?.relatedVideoList?.forEach {
                    item {
                        VideoCompactView(
                            modifier = Modifier
                                .background(color = MaterialTheme.colors.background)
                                .padding(
                                    top = paddingLarge,
                                    start = paddingMedium,
                                    end = paddingMedium
                                ),
                            videoEntity = it,
                            onViewVideo = { handler.onVideoClick(it) },
                            onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                            onImpression = handler::onVideoCardImpression,
                        )
                    }
                }
                if (playerTarget?.value == PlayerTarget.REMOTE) {
                    item {
                        Spacer(modifier = Modifier.height(paddingXGiant))
                    }
                }
            } else {
                item {
                    EmptyView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = minDefaultEmptyViewHeight)
                            .padding(
                                start = paddingMedium,
                                end = paddingMedium,
                                bottom = paddingMedium
                            ),
                        title = stringResource(id = R.string.there_is_nothing_to_see_here),
                        text = stringResource(id = R.string.our_algorithm_still_looking_for_related_videos)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoDetailsPlayListView(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
    contentHandler: ContentHandler,
    playListState: PlayListState,
    currentVideoId: Long,
    onLoopPlayList: (Boolean) -> Unit,
    onShufflePlayList: (Boolean) -> Unit,
    onPlayListVideoClick: (VideoEntity, Int) -> Unit,
) {
    val playListVideos: LazyPagingItems<Feed> =
        playListState.playListVideosData.collectAsLazyPagingItems()
    val scrollState = rememberLazyListState()

    LaunchedEffect(playListVideos.itemCount) {
        handler.onPlayListVideoListUpdated(playListVideos.itemSnapshotList.items)
    }

    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(paddingXSmall))
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = paddingSmall)
                .fillMaxWidth(),
        ) {
            val (playListInfo, moreBtn) = createRefs()
            Column(
                modifier = Modifier.constrainAs(playListInfo) {
                    start.linkTo(parent.start)
                    end.linkTo(moreBtn.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = playListState.playListEntity.title,
                    color = MaterialTheme.colors.primary,
                    style = h4
                )
                val ownerTitle = playListState.playListEntity.channelName
                    ?: playListState.playListEntity.username
                Text(
                    text = "$ownerTitle - ${playListState.currentVideoNumber} / ${playListState.playListEntity.videosQuantity}",
                    color = MaterialTheme.colors.secondary,
                    style = h6
                )
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.constrainAs(moreBtn) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            ) {
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down),
                    contentDescription = stringResource(
                        id = R.string.description
                    ),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        if (expanded) {
            Row(
                modifier = Modifier.padding(start = paddingXSmall, end = paddingSmall),
                verticalAlignment = CenterVertically,
            ) {
                RoundIconButton(
                    painter = painterResource(id = R.drawable.ic_repeat),
                    size = imageXMedium,
                    backgroundColor = if (playListState.inLoopPlayListMode) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface,
                    tintColor = if (playListState.inLoopPlayListMode) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                    onClick = { onLoopPlayList(playListState.inLoopPlayListMode.not()) }
                )
                RoundIconButton(
                    painter = painterResource(id = R.drawable.ic_shuffle_16),
                    size = imageXMedium,
                    backgroundColor = if (playListState.inShuffleMode) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface,
                    tintColor = if (playListState.inShuffleMode) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                    onClick = { onShufflePlayList(playListState.inShuffleMode.not()) }
                )
                //Hiding option for current implementation as requested by Egor&Tim. But living the implementations since it might be added later.
//                    Spacer(modifier = Modifier.weight(1F))
//                    if (playListState.playListOptions.isNotEmpty()) {
//                        RoundIconButton(
//                            painter = painterResource(id = R.drawable.ic_more),
//                            size = imageXMedium,
//                            backgroundColor = MaterialTheme.colors.onSurface,
//                            tintColor = MaterialTheme.colors.primary
//                        ) {
//                            handler.onMore(playListState.playListOptions)
//                        }
//                    }
            }
        }
        Spacer(modifier = Modifier.height(paddingXSmall))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondaryVariant
        )
        if (expanded) {
            BoxWithConstraints(
                modifier = Modifier.heightIn(max = playListVideoDetailsMaxHeight)
            ) {
                LazyColumn(
                    state = scrollState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (playListVideos.itemCount > 0) {
                        item { Spacer(modifier = Modifier.height(paddingXXSmall)) }
                        items(count = playListVideos.itemCount) { index ->
                            (playListVideos[index] as? VideoEntity)?.let { videoEntity ->
                                val selectedColor = MaterialTheme.colors.onSurface
                                PlayListVideoView(
                                    modifier = Modifier
                                        .conditional(currentVideoId == videoEntity.id) {
                                            this.background(color = selectedColor)
                                        }
                                        .padding(
                                            vertical = paddingXXSmall,
                                            horizontal = paddingSmall,
                                        ),
                                    videoNumber = index + 1,
                                    videoEntity = videoEntity,
                                    onViewVideo = { onPlayListVideoClick(it, index + 1) },
                                    onMoreClick = {
                                        contentHandler.onMoreVideoOptionsClicked(
                                            videoEntity
                                        )
                                    },
                                    onImpression = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DescriptionView(
    modifier: Modifier = Modifier,
    activityHandler: RumbleActivityHandler,
    description: String?
) {
    if (!description.isNullOrEmpty()) {
        ExpandableText(
            modifier = modifier,
            text = description,
            onUriClick = activityHandler::onOpenWebView,
            onAnnotatedTextClicked = activityHandler::onAnnotatedTextClicked
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer?,
    handler: VideoDetailsHandler,
    fullScreen: Boolean,
    isCollapsingMiniPlayerInProgress: Boolean,
    uiType: UiType,
    liveChatDisabled: Boolean,
    onCollapse: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        rumblePlayer?.let {
            var height by remember { mutableFloatStateOf(0f) }
            val thresholdHeight = remember(height) {
                height * RumbleConstants.FULLSCREEN_VIDEO_DRAG_THRESHOLD
            }
            var dragOffset by remember { mutableFloatStateOf(0f) }
            val scope = rememberCoroutineScope()

            val onDragEndCancel: () -> Unit = {
                when {
                    dragOffset >= thresholdHeight -> {
                        dragOffset = 0f
                        handler.onFullScreen(fullScreen = false)
                    }

                    else -> {
                        // Animate dragOffset to 0
                        scope.launch {
                            animate(
                                initialValue = dragOffset,
                                targetValue = 0f,
                                animationSpec = tween(durationMillis = RumbleConstants.VIDEO_DRAG_CANCEL_ANIMATION_DURATION)
                            ) { value, _ ->
                                dragOffset = value
                            }
                        }
                    }
                }
            }
            RumbleVideoView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brandedPlayerBackground)
                    .onSizeChanged { height = it.height.toFloat() }
                    .conditional(uiType == UiType.FULL_SCREEN_LANDSCAPE && fullScreen) {
                        Modifier
                            .offset { IntOffset(0, dragOffset.roundToInt()) }
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragEnd = onDragEndCancel,
                                    onDragCancel = onDragEndCancel
                                ) { _, dragAmount ->
                                    dragOffset += dragAmount
                                    // Constrain dragOffset within bounds
                                    if (thresholdHeight >= 0) {
                                        dragOffset = dragOffset.coerceIn(0f, thresholdHeight)
                                    }
                                }
                            }
                    },
                rumblePlayer = rumblePlayer,
                aspectRatioMode = handler.getVideoAspectRatio(),
                uiType = uiType,
                isFullScreen = fullScreen,
                isCollapsingMiniPlayerInProgress = isCollapsingMiniPlayerInProgress,
                onChangeFullscreenMode = {
                    handler.onFullScreen(fullScreen = it)
                },
                onBack = onCollapse,
                onLiveChatClicked = handler::onOpenLiveChat,
                liveChatDisabled = liveChatDisabled,
                onSettings = handler::onVideoSettings
            )
        }
    }
}

@Composable
private fun JoinOnLocalsView(
    handler: VideoDetailsHandler,
    coroutineScope: CoroutineScope
) {
    Row(
        modifier = Modifier
            .semantics { testTag = JoinOnLocalsViewTag }
            .fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
    ) {
        Image(
            modifier = Modifier.width(imageMedium),
            painter = painterResource(id = if (MaterialTheme.colors.isLight) R.drawable.ic_locals_logo else R.drawable.ic_locals_logo_dark_mode),
            contentDescription = "",
        )

        Text(
            modifier = Modifier
                .weight(1F)
                .padding(end = paddingMedium),
            text = stringResource(id = R.string.join_on_locals),
            style = tinyBody,
            color = MaterialTheme.colors.secondary
        )
        ActionButton(
            text = stringResource(id = R.string.join),
            textModifier = Modifier
                .padding(
                    vertical = paddingXSmall,
                    horizontal = paddingXSmall + paddingXXSmall
                ),
            backgroundColor = brandedLocalsRed,
            borderColor = brandedLocalsRed,
            textColor = enforcedWhite
        ) { coroutineScope.launch { handler.onLocals() } }
    }
}

@Composable
private fun VideoDetailsInfoView(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
    contentHandler: ContentHandler,
    activityHandler: RumbleActivityHandler,
    coroutineScope: CoroutineScope,
    videoEntity: VideoEntity?,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
) {
    val state by handler.state
    val contentSate by contentHandler.userUIState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(paddingSmall)
        ) {
            state.channelDetailsEntity?.localsCommunityEntity?.let {
                if ((it.showPremiumFlow && contentSate.isPremiumUser.not()) || it.showPremiumFlow.not()) {
                    JoinOnLocalsView(
                        handler = handler,
                        coroutineScope = coroutineScope
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddingSmall),
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
            }
            videoEntity?.let {
                VideoSummaryView(
                    videoEntity = it,
                    onCategoryClick = onCategoryClick,
                    onTagClick = onTagClick,
                )
            }
            DescriptionView(
                modifier = Modifier.padding(top = paddingSmall),
                activityHandler = activityHandler,
                description = videoEntity?.description
            )
        }
    }
}

@Composable
private fun VideoSummaryView(
    videoEntity: VideoEntity,
    onCategoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
) {
    VideoTimestampView(
        modifier = Modifier.padding(
            top = paddingXXXSmall,
            bottom = paddingXXXSmall
        ),
        videoEntity = videoEntity
    )
    if (videoEntity.categoriesList.isNullOrEmpty().not()
        || videoEntity.tagList.isNullOrEmpty().not()
        || videoEntity.ppv != null
    ) {
        VideoTagListView(
            modifier = Modifier.padding(top = paddingXXSmall),
            videoEntity = videoEntity,
            onCategoryClick = onCategoryClick,
            onTagClick = onTagClick
        )
    }
}

@Composable
private fun VideoDetailsHeaderView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity?,
    contentHandler: ContentHandler,
    handler: VideoDetailsHandler,
    onChannelClick: (String) -> Unit,
    activityHandler: RumbleActivityHandler
) {
    val state by handler.state

    videoEntity?.let {
        Column(
            modifier = modifier
        ) {
            if (it.videoStatus != VideoStatus.UPLOADED && it.videoStatus != VideoStatus.STREAMED && it.videoStatus != VideoStatus.SCHEDULED) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                    WatchingNowView(
                        modifier = Modifier.padding(start = paddingMedium),
                        watchingCount = state.watchingNow,
                        videoStatus = it.videoStatus
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = paddingXSmall),
                        text = stringResource(id = R.string.vertical_separator),
                        style = h6Light,
                        color = MaterialTheme.colors.primaryVariant
                    )

                    TotalLiveTimeView(
                        startTime = videoEntity.liveStreamedOn ?: LocalDateTime.now()
                    )
                }
                Spacer(modifier = Modifier.height(paddingXXXSmall))
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingMedium),
                text = state.videoEntity?.title ?: "",
                style = RumbleTypography.h3,
                color = MaterialTheme.colors.primary
            )
            if (videoEntity.isPremiumExclusiveContent || videoEntity.hasLiveGate) {
                VideoCardPremiumTagView(
                    modifier = Modifier
                        .padding(top = paddingXXSmall, start = paddingMedium)
                )
            }
            Spacer(modifier = Modifier.height(paddingXXSmall))
            state.videoEntity?.let {
                UserInfoView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = paddingMedium),
                    channelName = it.channelName,
                    channelThumbnail = it.channelThumbnail,
                    channelId = state.channelDetailsEntity?.channelId,
                    verifiedBadge = it.verifiedBadge,
                    followers = it.channelFollowers,
                    followStatus = state.followStatus,
                    onUpdateSubscription = { action ->
                        contentHandler.onUpdateSubscription(
                            state.channelDetailsEntity,
                            action
                        )
                    },
                    onChannelClick = onChannelClick,
                )
            }

            LazyRow(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = paddingSmall),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                contentPadding = PaddingValues(horizontal = paddingMedium),
            ) {
                item {
                    VideoDetailsLikeDislikeView(
                        videoEntity = it,
                        onLike = handler::onLike,
                        onDislike = handler::onDislike,
                    )
                }
                if (it.videoStatus != VideoStatus.STREAMED
                    && it.videoStatus != VideoStatus.UPLOADED
                    && it.liveChatDisabled.not()
                ) {
                    item {
                        VideoDetailsActionButton(
                            text = stringResource(id = R.string.live_chat).capitalizeWords(),
                            leadingIconPainter = painterResource(id = R.drawable.ic_followers),
                        ) {
                            handler.onOpenLiveChat()
                        }
                    }
                }
                item {
                    VideoDetailsActionButton(
                        text = stringResource(id = R.string.comments),
                        leadingIconPainter = painterResource(id = R.drawable.ic_comments),
                    ) {
                        handler.onOpenComments()
                    }
                }

                item {
                    VideoDetailsActionButton(
                        text = stringResource(id = R.string.share),
                        leadingIconPainter = painterResource(id = R.drawable.ic_share),
                    ) {
                        activityHandler.currentPlayer = null
                        handler.onShare()
                    }
                }

                item {
                    VideoDetailsActionButton(
                        text = stringResource(id = R.string.save),
                        leadingIconPainter = painterResource(id = R.drawable.ic_save_playlist),
                    ) {
                        state.videoEntity?.let { contentHandler.onSaveToPlayList(it.id) }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoDetailsLikeDislikeView(
    videoEntity: VideoEntity,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    enabled: Boolean = true,
) {
    LikeDislikeView(
        modifier = Modifier
            .clip(RoundedCornerShape(radiusLarge)),
        style = LikeDislikeViewStyle.ActionButtonsWithBarBelow,
        likeNumber = videoEntity.likeNumber,
        dislikeNumber = videoEntity.dislikeNumber,
        userVote = videoEntity.userVote,
        onLike = onLike,
        onDislike = onDislike,
        enabled = enabled
    )
}

@Composable
fun VideoTimestampView(
    modifier: Modifier = Modifier,
    videoEntity: VideoEntity,
) {
    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_clock),
            contentDescription = "",
            modifier = Modifier.size(imageXXSmall - imageXXMini),
            tint = MaterialTheme.colors.secondary
        )
        VideoTimestampLabelView(
            modifier = Modifier.padding(start = paddingXXXSmall),
            videoEntity = videoEntity,
            textStyle = tinyBodySemiBold,
        )
        if (videoEntity.videoStatus == VideoStatus.UPLOADED || videoEntity.videoStatus == VideoStatus.STREAMED) {
            ViewsNumberView(
                modifier = Modifier.padding(start = paddingSmall),
                painterResourceId = R.drawable.ic_views_filled,
                viewsNumber = videoEntity.viewsNumber,
                textStyle = tinyBodySemiBold,
                extraText = pluralStringResource(
                    id = R.plurals.views,
                    count = videoEntity.viewsNumber.toInt()
                )
            )
        }
    }
}

@Composable
private fun VideoDetailsActionButton(
    text: String,
    leadingIconPainter: Painter,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ActionButton(
        text = text,
        contentModifier = Modifier
            .padding(
                top = paddingXXSmall,
                bottom = paddingXXSmall,
                start = paddingSmall,
                end = paddingSmall
            ),
        textModifier = Modifier
            .padding(start = paddingXXXSmall),
        leadingIconPainter = leadingIconPainter,
        backgroundColor = MaterialTheme.colors.onSurface,
        borderColor = MaterialTheme.colors.onSurface,
        textColor = MaterialTheme.colors.secondary,
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun BottomSheetDialog(
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler,
    activityHandler: RumbleActivityHandler,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    val state by handler.state
    val liveChatState by liveChatHandler.state
    var buyRantBottomSheetExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { bottomSheetState.currentValue }
            .collectLatest {
                buyRantBottomSheetExpanded =
                    it == ModalBottomSheetValue.Expanded && state.isFullScreen.not()
            }
    }

    state.bottomSheetReason?.let { reason ->
        when (reason) {
            BottomSheetReason.JoinOnLocals -> JoinLocalsSheet(
                handler = handler,
                activityHandler = activityHandler,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState
            )

            is BottomSheetReason.VideoSettingsDialog -> VideoSettingsBottomSheet(
                rumblePlayer = reason.rumblePlayer,
                settingsBottomSheetHandler = handler,
                isTablet = IsTablet()
            )

            is BottomSheetReason.ReportComment -> ReportBottomSheet(
                subtitle = stringResource(id = R.string.why_report_comment),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onReport = { handler.report(reason.commentEntity, it) })

            is BottomSheetReason.ReportVideo -> ReportBottomSheet(
                subtitle = stringResource(id = R.string.why_report_video),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onReport = { handler.reportVideo(reason.videoEntity, it) })

            BottomSheetReason.EmailVerificationComment -> VerifyEmailBottomSheet(
                subtitle = stringResource(id = R.string.verify_your_email_comments),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onRequestLink = handler::onRequestVerificationLink,
                onCheckVerificationStatus = handler::onCheckVerificationStatus,
            )

            BottomSheetReason.EmailVerificationLiveChat -> VerifyEmailBottomSheet(
                subtitle = stringResource(id = R.string.verify_your_email_live_chat),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onRequestLink = handler::onRequestVerificationLink,
                onCheckVerificationStatus = handler::onCheckVerificationStatus,
            )

            is BottomSheetReason.CommentAuthorSwitcher -> {
                CommentAuthorChooserBottomSheet(
                    coroutineScope = coroutineScope,
                    bottomSheetState = bottomSheetState,
                    onCommentAuthorChannelSelected = handler::onLiveChatAuthorSelected,
                    selectedAuthor = state.selectedLiveChatAuthor,
                    channels = reason.channels
                )
            }

            BottomSheetReason.BuyRant -> {
                BuyRantSheet(
                    handler = handler,
                    liveChatHandler = liveChatHandler,
                    activityHandler = activityHandler,
                    expanded = buyRantBottomSheetExpanded
                )
            }

            BottomSheetReason.ModerationMenu -> {
                LiveChatModerationMenu(
                    type = liveChatState.moderationMenuType,
                    onClose = liveChatHandler::onHideModerationMenu,
                    onMuteUser = handler::onMuteUser,
                    onDeleteMessage = liveChatHandler::onDeleteMessage,
                    onPinMessage = {
                        state.videoEntity?.id?.let {
                            liveChatHandler.onPinMessage(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun JoinLocalsSheet(
    handler: VideoDetailsHandler,
    activityHandler: RumbleActivityHandler,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    val state by handler.state

    state.channelDetailsEntity?.localsCommunityEntity?.let { localsCommunityEntity ->
        LocalsPopupBottomSheet(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(topStart = paddingMedium, topEnd = paddingMedium)
                ),
            localsCommunityEntity,
            onSupport = {
                activityHandler.onOpenWebView(localsCommunityEntity.channelUrl)
            },
            onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
        )
    }
}

@Composable
private fun VideoDetailsDialog(reason: AlertDialogReason, handler: VideoDetailsHandler) {
    when (reason) {
        is VideoDetailsAlertReason.DiscardReason -> {
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
                        action = { handler.onDiscard(reason.navigate) }
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.keep_writing),
                        width = commentActionButtonWidth,
                        action = handler::onKeepWriting
                    )
                )
            )
        }

        is VideoDetailsAlertReason.DeleteReason -> {
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

        is VideoDetailsAlertReason.ErrorReason -> {
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

        is VideoDetailsAlertReason.ShowEmailVerificationSent -> {
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

        VideoDetailsAlertReason.ShowYourEmailNotVerifiedYet -> {
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

        is VideoDetailsAlertReason.RestrictedContentReason -> {
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
