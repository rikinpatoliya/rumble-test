package com.rumble.battles.videos.presentation

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rumble.analytics.CardSize
import com.rumble.battles.CollapsingChannelImageTag
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.MyVideosTag
import com.rumble.battles.MyVideosTopBarTag
import com.rumble.battles.R
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsVmEvent
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.ChannelDetailsBaskSplash
import com.rumble.battles.commonViews.ChannelDetailsCollapsingImage
import com.rumble.battles.commonViews.ChannelDetailsHeader
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.VideosCountView
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.collapsedSpacerPadding
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.imageLarge
import com.rumble.theme.imageMini
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.treePoppy
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.findFirstFullyVisibleItemIndex
import kotlinx.coroutines.launch

private const val ITEMS_SHIFT = 2

@OptIn(ExperimentalMaterialApi::class)
@Suppress("DEPRECATION")
@Composable
fun MyVideosScreen(
    currentDestinationRoute: String?,
    myVideosHandler: MyVideosHandler,
    onVideoClick: (id: Feed) -> Unit,
    newImageUri: Uri?,
    contentHandler: ContentHandler,
    bottomSheetState: ModalBottomSheetState,
) {

    val state by myVideosHandler.uiState.collectAsStateWithLifecycle()
    val alertDialogState by myVideosHandler.alertDialogState.collectAsStateWithLifecycle()
    val userUploadChannelsState by myVideosHandler.userUploadChannels.collectAsStateWithLifecycle()
    val userUploadsState by myVideosHandler.userUploads.collectAsStateWithLifecycle(initialValue = emptyList())
    val userSuccessfulUploads by myVideosHandler.userSuccessfulUploads.collectAsStateWithLifecycle(initialValue = emptyList())
    val listToggleViewStyle by myVideosHandler.listToggleViewStyle.collectAsStateWithLifecycle(
        initialValue = ListToggleViewStyle.GRID
    )
    val context = LocalContext.current

    val videoListItems: LazyPagingItems<Feed> = state.videoList.collectAsLazyPagingItems()
    val updatedEntity by myVideosHandler.updatedEntity.collectAsStateWithLifecycle()
    updatedEntity?.let { updated ->
        videoListItems.itemSnapshotList.find { it is VideoEntity && it.id == updated.id }?.let {
            val videoEntity = it as VideoEntity
            videoEntity.userVote = updated.userVote
            videoEntity.likeNumber = updated.likeNumber
            videoEntity.dislikeNumber = updated.dislikeNumber
        }
    }
    val bitmap = if (newImageUri != null) {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images
                .Media.getBitmap(context.contentResolver, newImageUri)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, newImageUri)
            ImageDecoder.decodeBitmap(source)
        }
    } else null

    val scrollState by myVideosHandler.listState

    val listConnection = object : NestedScrollConnection {
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            myVideosHandler.onCreatePlayerForVisibleFeed()
            return super.onPostFling(consumed, available)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            myVideosHandler.onViewPaused()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            myVideosHandler.onViewResumed()
        }
    }

    val soundOn by myVideosHandler.soundState.collectAsStateWithLifecycle(initialValue = false)

    val coroutineScope = rememberCoroutineScope()

    val videoDetailsState by contentHandler.videoDetailsState

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    LaunchedEffect(videoDetailsState) {
        if (videoDetailsState.visible.not()) myVideosHandler.onCreatePlayerForVisibleFeed()
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo }.collect {
            var createPlayer = false
            val itemPosition = scrollState.findFirstFullyVisibleItemIndex(
                ITEMS_SHIFT,
                RumbleConstants.PLAYER_MIN_VISIBILITY
            )
            val firstVisible =
                if (itemPosition + ITEMS_SHIFT == 0 && videoListItems.itemCount > 0) {
                    createPlayer = true
                    videoListItems[0]
                } else if (itemPosition >= 0 && itemPosition < videoListItems.itemCount) {
                    videoListItems[itemPosition]
                } else {
                    null
                }
            myVideosHandler.onFullyVisibleFeedChanged(firstVisible)
            if (createPlayer) myVideosHandler.onCreatePlayerForVisibleFeed()
        }
    }

    LaunchedEffect(key1 = userSuccessfulUploads) {
        myVideosHandler.onDeleteSuccededUploads(userSuccessfulUploads)
    }

    LaunchedEffect(key1 = context) {
        lifecycleOwner.lifecycle.addObserver(observer)
        myVideosHandler.vmEvents.collect { event ->
            when (event) {
                is ChannelDetailsVmEvent.Error -> {
                    contentHandler.onError(event.errorMessage)
                }

                is ChannelDetailsVmEvent.ShowEmailVerifiedMessage -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.email_successfully_verified_message,
                        titleId = R.string.сongratulations,
                    )
                }

                is ChannelDetailsVmEvent.ShowMoreUploadOptionsBottomSheet -> {
                    contentHandler.updateBottomSheetUiState(
                        BottomSheetContent.MoreUploadOptionsSheet(
                            title = if (event.uploadVideoEntity.status == UploadStatus.EMAIL_VERIFICATION_NEEDED) context.getString(
                                R.string.email_verification_required
                            ) else null,
                            subtitle = if (event.uploadVideoEntity.status == UploadStatus.EMAIL_VERIFICATION_NEEDED) context.getString(
                                R.string.verify_email_to_upload_videos
                            ) else null,
                            bottomSheetItems = getBottomSheetItems(
                                uploadVideoEntity = event.uploadVideoEntity,
                                context = context,
                                contentHandler = contentHandler,
                                myVideosHandler = myVideosHandler,
                            )
                        )
                    )
                }

                is ChannelDetailsVmEvent.PlayVideo -> {
                    onVideoClick(event.videoEntity)
                }

                else -> {}
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            myVideosHandler.onViewPaused()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    TransparentStatusBar()

    Column(modifier = Modifier
        .testTag(MyVideosTag)
        .fillMaxSize()
        .background(MaterialTheme.colors.background)) {
        Box(modifier = Modifier.weight(1f)) {
            ChannelDetailsBaskSplash(state.channelDetailsEntity?.backSplash)
            MyVideosTopBar(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .systemBarsPadding()
                    .testTag(MyVideosTopBarTag),
                userUploadChannelsState = userUploadChannelsState,
                onShowChannelSwitch = { uploadChannels ->
                    contentHandler.updateBottomSheetUiState(
                        BottomSheetContent.UserUploadChannelSwitcher(
                            uploadChannels
                        )
                    )
                },
            )
            ChannelDetailsCollapsingImage(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .systemBarsPadding()
                    .testTag(CollapsingChannelImageTag),
                channelName = state.channelDetailsEntity?.channelTitle ?: "",
                imageUrl = state.channelDetailsEntity?.thumbnail ?: "",
                bitmap = bitmap,
                scrollState = scrollState,
            )
            Column {
                Spacer(
                    Modifier
                        .height(collapsedSpacerPadding)
                        .systemBarsPadding()
                )
                if (videoListItems.itemCount > 0) {
                    BoxWithConstraints {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()
                                .nestedScroll(listConnection)
                                .padding(
                                    horizontal = CalculatePaddingForTabletWidth(
                                        maxWidth = maxWidth
                                    )
                                ),
                            state = scrollState,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                ChannelDetailsHeader(
                                    modifier = Modifier.fillMaxWidth(),
                                    currentDestinationRoute = currentDestinationRoute,
                                    state = state,
                                )
                            }
                            items(items = userUploadsState) {
                                UploadVideoCardView(
                                    uploadVideoEntity = it,
                                    onMoreUploadOptionsClicked = myVideosHandler::onMoreUploadOptionsClicked,
                                    onCancelUpload = myVideosHandler::onCancelUploadClicked
                                )
                            }
                            item {
                                VideosCountView(
                                    state,
                                    Modifier
                                        .fillMaxWidth()
                                        .background(color = MaterialTheme.colors.background),
                                    listToggleViewStyle
                                ) {
                                    myVideosHandler.onToggleVideoViewStyle(it)
                                }
                            }
                            items(videoListItems.itemCount) {
                                videoListItems[it]?.let { entity ->
                                    if (entity is VideoEntity) {
                                        if (listToggleViewStyle == ListToggleViewStyle.LIST) {
                                            VideoCompactView(
                                                modifier = Modifier
                                                    .background(color = MaterialTheme.colors.background)
                                                    .padding(
                                                        top = paddingLarge,
                                                        start = paddingMedium,
                                                        end = paddingMedium
                                                    ),
                                                videoEntity = entity,
                                                onViewVideo = { myVideosHandler.onVideoClick(entity) },
                                                onMoreClick = { contentHandler.onMoreVideoOptionsClicked(entity) },
                                                onImpression = { video ->
                                                    myVideosHandler.onVideoCardImpression(video, CardSize.COMPACT)
                                                }
                                            )
                                        } else {
                                            VideoView(
                                                modifier = Modifier
                                                    .background(color = MaterialTheme.colors.background)
                                                    .padding(
                                                        top = paddingLarge,
                                                        start = paddingMedium,
                                                        bottom = paddingLarge,
                                                        end = paddingMedium
                                                    ),
                                                videoEntity = entity,
                                                soundOn = soundOn,
                                                rumblePlayer = myVideosHandler.currentPlayerState.value,
                                                onPlayerImpression = myVideosHandler::onPlayerImpression,
                                                onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                                                onSoundClick = myVideosHandler::onSoundClick,
                                                onImpression = { video ->
                                                    myVideosHandler.onVideoCardImpression(video, CardSize.REGULAR)
                                                },
                                                onClick = {
                                                    myVideosHandler.onVideoClick(entity)
                                                },
                                                isPremiumUser = contentHandler.isPremiumUser(),
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                videoListItems.apply {
                                    when {
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
                            item {
                                BottomNavigationBarScreenSpacer()
                            }
                        }
                    }
                } else {
                    BoxWithConstraints {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = CalculatePaddingForTabletWidth(
                                        maxWidth = maxWidth
                                    )
                                )
                        ) {
                            ChannelDetailsHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .systemBarsPadding(),
                                currentDestinationRoute = currentDestinationRoute,
                                state = state,
                            )
                            LazyColumn {
                                items(items = userUploadsState) {
                                    UploadVideoCardView(
                                        uploadVideoEntity = it,
                                        onMoreUploadOptionsClicked = myVideosHandler::onMoreUploadOptionsClicked,
                                        onCancelUpload = myVideosHandler::onCancelUploadClicked
                                    )
                                }
                            }
                            EmptyView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingMedium),
                                title = stringResource(id = R.string.my_videos_have_not_uploaded_title),
                                text = stringResource(id = R.string.my_videos_have_not_uploaded_description)
                            )
                            BottomNavigationBarScreenSpacer()
                        }
                    }
                }
            }
        }
    }

    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (alertDialogState.show) {
        when (val reason = alertDialogState.alertDialogReason) {
            is MyVideosScreenAlertDialogReason.CancelUploadDialogReason -> {
                RumbleAlertDialog(
                    onDismissRequest = myVideosHandler::onDismissDialog,
                    title = stringResource(id = R.string.cancel_uploading),
                    text = stringResource(id = R.string.sure_want_to_cancel_upload),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(id = R.string.keep_it),
                            dialogActionType = DialogActionType.Neutral,
                            withSpacer = true,
                            action = myVideosHandler::onDismissDialog,
                        ),
                        DialogActionItem(
                            text = "${stringResource(id = R.string.yes)}, ${stringResource(id = R.string.cancel).lowercase()}",
                            dialogActionType = DialogActionType.Destructive,
                            action = { myVideosHandler.onCancelUpload(reason.uploadVideoEntity) },
                        )
                    ),
                )
            }

            is MyVideosScreenAlertDialogReason.ShowEmailVerificationSent -> {
                RumbleAlertDialog(
                    onDismissRequest = myVideosHandler::onDismissDialog,
                    title = stringResource(id = R.string.email_verification_sent),
                    text = stringResource(id = R.string.email_sent_instructions, reason.email),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.ok),
                            action = myVideosHandler::onDismissDialog,
                            dialogActionType = DialogActionType.Neutral,
                        )
                    )
                )
            }

            is MyVideosScreenAlertDialogReason.ShowYourEmailNotVerifiedYet -> {
                RumbleAlertDialog(
                    onDismissRequest = myVideosHandler::onDismissDialog,
                    title = stringResource(id = R.string.email_not_verified),
                    text = stringResource(id = R.string.email_not_verified_try_later),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.ok),
                            action = myVideosHandler::onDismissDialog,
                            dialogActionType = DialogActionType.Neutral,
                        )
                    )
                )
            }

            is MyVideosScreenAlertDialogReason.RestrictedContentReason -> {
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
                            action = myVideosHandler::onCancelRestricted
                        ),
                        DialogActionItem(
                            text = stringResource(R.string.start_watching),
                            dialogActionType = DialogActionType.Positive,
                            width = commentActionButtonWidth,
                            action = { myVideosHandler.onWatchRestricted(reason.videoEntity) }
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun MyVideosTopBar(
    modifier: Modifier = Modifier,
    userUploadChannelsState: List<UserUploadChannelEntity>?,
    onShowChannelSwitch: (userUploadChannels: List<UserUploadChannelEntity>) -> Unit,
) {
    Row(
        modifier = modifier
            .wrapContentWidth()
            .padding(top = paddingXSmall, end = paddingMedium),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!userUploadChannelsState.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(enforcedDarkmo.copy(alpha = 0.6F))
                    .clickable { onShowChannelSwitch(userUploadChannelsState) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tv_quality_24),
                    contentDescription = stringResource(id = R.string.channels),
                    modifier = Modifier.padding(paddingXXXSmall),
                    tint = enforcedWhite
                )
            }
        }
    }
}

@Composable
private fun UploadVideoCardView(
    modifier: Modifier = Modifier,
    uploadVideoEntity: UploadVideoEntity,
    onMoreUploadOptionsClicked: (uploadVideoEntity: UploadVideoEntity) -> Unit,
    onCancelUpload: (uploadVideoEntity: UploadVideoEntity) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = enforcedDarkmo)
    ) {
        Row(
            modifier = Modifier
                .padding(paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = uploadVideoEntity.thumbnail,
                contentDescription = uploadVideoEntity.title,
                modifier = Modifier
                    .width(imageLarge)
                    .aspectRatio(RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION)
                    .clip(RoundedCornerShape(radiusSmall))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = paddingSmall, end = paddingSmall)
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = getUploadStatusIcon(uploadVideoEntity.status)),
                        contentDescription = stringResource(id = uploadVideoEntity.status.titleId),
                        modifier = Modifier.size(imageXXSmall),
                        tint = getUploadStatusTintColor(uploadVideoEntity.status)
                    )
                    Text(
                        text = stringResource(id = uploadVideoEntity.status.titleId),
                        modifier = Modifier.padding(start = paddingXXXSmall),
                        style = h6Light,
                        color = enforcedWhite,
                    )
                }
                Text(
                    text = uploadVideoEntity.title.ifEmpty { stringResource(id = R.string.untitled) },
                    style = tinyBody,
                    color = enforcedWhite,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
            when (uploadVideoEntity.status) {
                UploadStatus.EMAIL_VERIFICATION_NEEDED, UploadStatus.WAITING_WIFI, UploadStatus.UPLOADING_FAILED -> {
                    IconButton(onClick = {
                        onMoreUploadOptionsClicked(uploadVideoEntity)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_elipsis),
                            contentDescription = stringResource(id = R.string.more),
                            tint = enforcedWhite
                        )
                    }
                }

                else -> {
                    RumbleTextActionButton(
                        text = stringResource(id = R.string.cancel),
                        textColor = enforcedWhite,
                    ) {
                        onCancelUpload(uploadVideoEntity)
                    }
                }
            }
        }
        androidx.compose.material3.LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageMini),
            progress = uploadVideoEntity.progress,
            color = rumbleGreen,
            trackColor = enforcedWhite.copy(0.6f)
        )
    }
}

private fun getUploadStatusIcon(status: UploadStatus): Int =
    when (status) {
        UploadStatus.UPLOADING -> R.drawable.ic_upload_cloud
        else -> R.drawable.ic_info
    }


private fun getUploadStatusTintColor(status: UploadStatus): Color =
    when (status) {
        UploadStatus.UPLOADING_FAILED -> fierceRed
        UploadStatus.WAITING_WIFI, UploadStatus.WAITING_CONNECTION -> treePoppy
        else -> enforcedBone
    }

private fun getBottomSheetItems(
    uploadVideoEntity: UploadVideoEntity,
    context: Context,
    contentHandler: ContentHandler,
    myVideosHandler: MyVideosHandler,
): List<BottomSheetItem> =
    when (uploadVideoEntity.status) {
        UploadStatus.EMAIL_VERIFICATION_NEEDED -> {
            listOf(
                BottomSheetItem(
                    imageResource = R.drawable.ic_email,
                    text = context.getString(R.string.resend_verification_link)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.resendVerificationEmail()
                    }
                },
                BottomSheetItem(
                    imageResource = R.drawable.ic_replay,
                    text = context.getString(R.string.check_verification_and_retry)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.checkEmailValidationAndRetry(uploadVideoEntity)
                    }
                },
                BottomSheetItem(
                    imageResource = R.drawable.ic_trash,
                    text = context.getString(R.string.cancel_uploading)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.removeUploading(uploadVideoEntity)
                    }
                },
            )
        }

        UploadStatus.WAITING_WIFI -> {
            listOf(
                BottomSheetItem(
                    imageResource = R.drawable.ic_replay,
                    text = context.getString(R.string.upload_over_cellular)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.uploadOverCellular(uploadVideoEntity)
                    }
                },
                BottomSheetItem(
                    imageResource = R.drawable.ic_trash,
                    text = context.getString(R.string.cancel_uploading)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.removeUploading(uploadVideoEntity)
                    }
                },
            )
        }

        UploadStatus.UPLOADING_FAILED -> {
            listOf(
                BottomSheetItem(
                    imageResource = R.drawable.ic_replay,
                    text = context.getString(R.string.retry_uploading)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.retryUploading(uploadVideoEntity)
                    }
                },
                BottomSheetItem(
                    imageResource = R.drawable.ic_trash,
                    text = context.getString(R.string.cancel_uploading)
                ) {
                    dismissBottomSheetAndExecuteItemAction(contentHandler) {
                        myVideosHandler.removeUploading(uploadVideoEntity)
                    }
                },
            )
        }

        else -> emptyList()
    }

fun dismissBottomSheetAndExecuteItemAction(
    contentHandler: ContentHandler,
    action: () -> Unit,
) {
    contentHandler.updateBottomSheetUiState(BottomSheetContent.HideBottomSheet)
    action()
}