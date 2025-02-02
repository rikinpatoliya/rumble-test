package com.rumble.battles.livechat.presentation.content

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.RoundTextButton
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.keyboardAsState
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsEvent
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsHandler
import com.rumble.battles.feed.presentation.views.GoPremiumToCharOrCommentView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.livechat.presentation.LiveChatEvent
import com.rumble.battles.livechat.presentation.LiveChatHandler
import com.rumble.battles.livechat.presentation.emoji.EmotePickerState
import com.rumble.battles.livechat.presentation.emoji.EmotePickerView
import com.rumble.battles.livechat.presentation.pinnedmessage.PinnedMessageView
import com.rumble.battles.livechat.presentation.premium.PremiumPurchasedMessageView
import com.rumble.battles.livechat.presentation.premium.YourPremiumGiftView
import com.rumble.battles.livechat.presentation.raid.RaidInProgressView
import com.rumble.battles.livechat.presentation.raid.RaidMessageView
import com.rumble.battles.livechat.presentation.raid.RaidPopupView
import com.rumble.battles.livechat.presentation.rant.RantMessageView
import com.rumble.battles.livechat.presentation.rant.RantView
import com.rumble.domain.livechat.domain.domainmodel.ChatMode
import com.rumble.domain.livechat.domain.domainmodel.RaidMessageType
import com.rumble.theme.RumbleTypography.h4Underlined
import com.rumble.theme.darkGreen
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.presentation.UiType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LiveChatViewContent(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler,
    activityHandler: RumbleActivityHandler,
    onChannelClick: (String) -> Unit,
) {
    val state by remember { handler.state }
    val emoteSate by remember { handler.emoteState }
    val liveChatState by liveChatHandler.state
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val rantScrollState = rememberLazyListState()
    var hasInvisibleItems by rememberSaveable { mutableStateOf(false) }
    var messagesScrolledToBottom by rememberSaveable { mutableStateOf(true) }
    var rantScrolledToLeft by rememberSaveable { mutableStateOf(true) }
    val isKeyboardVisible by keyboardAsState()
    val messagesConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (liveChatState.messageList.isNotEmpty()) {
                    hasInvisibleItems =
                        listState.layoutInfo.visibleItemsInfo.last().index < liveChatState.messageList.size - 1
                    messagesScrolledToBottom =
                        listState.layoutInfo.visibleItemsInfo.last().index == liveChatState.messageList.size - 1
                    if (hasInvisibleItems.not()) liveChatHandler.onScrolledToBottom()
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }
    val rantConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                rantScrolledToLeft =
                    rantScrollState.layoutInfo.visibleItemsInfo.last().index == liveChatState.rantList.size - 1
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (liveChatState.messageList.isNotEmpty()) listState.animateScrollToItem(liveChatState.messageList.size - 1)
        liveChatHandler.eventFlow.collectLatest {
            when (it) {
                is LiveChatEvent.ScrollLiveChat ->
                    if (messagesScrolledToBottom) listState.animateScrollToItem(it.index)

                is LiveChatEvent.ScrollRant -> if (liveChatState.rantList.isNotEmpty()) {
                    if (rantScrolledToLeft) rantScrollState.animateScrollToItem(it.index)
                }

                is LiveChatEvent.StartPurchase -> {
                    val activity = context as Activity
                    it.billingClient.launchBillingFlow(activity, it.params)
                }

                is LiveChatEvent.ScrollToBottom -> {
                    listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
                    messagesScrolledToBottom = true
                    hasInvisibleItems = false
                }


                else -> return@collectLatest
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { isKeyboardVisible }.distinctUntilChanged().collectLatest { shown ->
            if (shown) liveChatHandler.onKeyboardShown()
        }
    }

    ConstraintLayout(modifier = modifier
        .background(MaterialTheme.colors.surface)
        .clickableNoRipple { liveChatHandler.onDismissBottomSheet() }) {
        val (header, content, footer, pinned, emotes) = createRefs()

        CloseLiveChatView(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(header) {
                    top.linkTo(parent.top)
                },
            watchingNow = state.watchingNow,
            pinnedMessageHidden = liveChatState.pinnedMessageHidden && liveChatState.pinnedMessage != null,
            onClose = { handler.onCloseLiveChat() },
            onShowPinnedMessage = liveChatHandler::onDisplayPinnedMessage
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(content) {
                top.linkTo(header.bottom)
                if (state.isFullScreen) {
                    bottom.linkTo(parent.bottom)
                } else {
                    bottom.linkTo(footer.top)
                }
                height = Dimension.fillToConstraints
            }
        ) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.secondaryVariant
            )

            LazyRow(
                modifier = Modifier
                    .nestedScroll(rantConnection)
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium
                    )
                    .conditional(liveChatState.rantList.isNotEmpty()) {
                        Modifier.padding(
                            top = paddingXSmall,
                            bottom = paddingXSmall
                        )
                    },
                state = rantScrollState,
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                liveChatState.rantList.forEach {
                    item {
                        RantView(
                            modifier = Modifier.clickable {
                                handler.onRantPopupShown()
                                liveChatHandler.onRantClicked(it)
                            },
                            rantEntity = it,
                            active = liveChatState.rantPopupMessage == null || it.messageEntity == liveChatState.rantPopupMessage
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(messagesConnection)
                        .padding(
                            start = paddingSmall,
                            end = paddingMedium,
                            bottom = paddingXXXXSmall
                        ),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(paddingXXXXSmall)
                ) {
                    liveChatState.messageList.forEach { message ->
                        item {
                            if (message.giftType != null) {
                                PremiumPurchasedMessageView(
                                    modifier = Modifier.padding(
                                        top = paddingXXXSmall,
                                        start = paddingXXXSmall,
                                        end = paddingXXXSmall,
                                    ),
                                    messageEntity = message,
                                    badges = liveChatState.liveChatConfig?.badges ?: emptyMap(),
                                )
                            } else if (message.rantPrice != null) {
                                RantMessageView(
                                    modifier = Modifier.padding(top = paddingXXXSmall),
                                    messageEntity = message,
                                    badges = liveChatState.badges,
                                    liveChatConfig = liveChatState.liveChatConfig
                                )
                            } else if (message.deleted) {
                                DeletedMessageView(
                                    modifier = Modifier.padding(
                                        top = paddingXXXSmall,
                                        start = paddingXXXSmall
                                    ),
                                    messageEntity = message,
                                    badges = liveChatState.badges,
                                    onClick = liveChatHandler::onDisplayModerationMenu
                                )
                            } else if (message.isNotification) {
                                LiveChatNotificationView(
                                    modifier = Modifier.padding(top = paddingXXXSmall),
                                    messageEntity = message,
                                    badges = liveChatState.badges,
                                    liveChatConfig = liveChatState.liveChatConfig
                                )
                            } else if (message.isRaidMessage) {
                                RaidMessageView(
                                    type = message.raidMessageType
                                        ?: RaidMessageType.getRandomType(),
                                    channelName = message.userName,
                                    channelAvatar = message.userThumbnail,
                                )
                            } else {
                                LiveChatMessageView(
                                    modifier = Modifier.padding(
                                        top = paddingXXXSmall,
                                        start = paddingXXXSmall
                                    ),
                                    messageEntity = message,
                                    badges = liveChatState.badges,
                                    liveChatConfig = liveChatState.liveChatConfig,
                                    onClick = liveChatHandler::onDisplayModerationMenu,
                                    onLinkClick = activityHandler::onOpenWebView,
                                    onChannelClick = onChannelClick,
                                )
                            }
                        }
                    }
                }

                liveChatState.rantPopupMessage?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background.copy(0.7f))
                    ) {
                        RantMessageView(
                            modifier = Modifier
                                .padding(paddingMedium)
                                .align(Alignment.TopCenter),
                            messageEntity = it,
                            badges = liveChatState.badges,
                            onDismiss = liveChatHandler::onDismissBottomSheet,
                            liveChatConfig = liveChatState.liveChatConfig,
                            scrollable = true,
                        )
                    }
                }

                if (liveChatState.unreadMessageCount > 0 && hasInvisibleItems) {
                    RoundTextButton(
                        modifier = Modifier
                            .padding(paddingSmall)
                            .align(Alignment.BottomEnd),
                        text = liveChatState.unreadMessageCountText,
                        onClick = liveChatHandler::onViewUnreadMessages,
                        textColor = enforcedDarkmo
                    )
                }

                if (liveChatState.isLoadingMessages) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        RumbleProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else if (liveChatState.messageList.isEmpty()) {
                    EmptyView(
                        modifier = Modifier
                            .padding(paddingMedium)
                            .fillMaxSize()
                            .align(Alignment.Center),
                        title = stringResource(id = R.string.no_messages_yet),
                        text = stringResource(id = R.string.be_first_comment)
                    )
                }
            }
        }

        liveChatState.pinnedMessage?.let {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(pinned) {
                        top.linkTo(header.bottom)
                    },
                visible = liveChatState.pinnedMessageHidden.not(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PinnedMessageView(
                    modifier = Modifier.padding(horizontal = paddingXXXSmall),
                    message = it,
                    badges = liveChatState.badges,
                    canModerate = liveChatState.canModerate,
                    onUnpin = liveChatHandler::onUnpinMessage,
                    onHide = liveChatHandler::onHidePinnedMessage,
                    onLinkClick = activityHandler::onOpenWebView
                )
            }
        }

        liveChatState.raidEntity?.let {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingXXXSmall)
                    .constrainAs(pinned) {
                        top.linkTo(header.bottom)
                    },
                visible = liveChatState.pinnedMessageHidden.not(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (liveChatState.streamRaided) {
                    RaidInProgressView(
                        modifier = Modifier.padding(horizontal = paddingXXXSmall),
                        raidEntity = it,
                        onJoin = liveChatHandler::onJoinRaid,
                    )
                } else {
                    RaidPopupView(
                        modifier = Modifier.padding(horizontal = paddingXXXSmall),
                        raidEntity = it,
                        onJoin = liveChatHandler::onJoinRaid,
                        onOptOut = liveChatHandler::onOptOutRaid,
                    )
                }
            }
        }

        liveChatState.giftPopupMessageEntity?.let {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingXXXSmall)
                    .constrainAs(pinned) {
                        top.linkTo(header.bottom)
                    },
                visible = liveChatState.pinnedMessageHidden.not(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                YourPremiumGiftView(
                    modifier = Modifier.padding(horizontal = paddingXXXSmall),
                    giftPopupMessageEntity = it,
                    onClose = liveChatHandler::onHidePinnedMessage,
                )
            }
        }

        ContentFooter(
            modifier = Modifier
                .conditional(emoteSate.showEmoteSelector.not()) {
                    imePadding()
                }
                .fillMaxWidth()
                .constrainAs(footer) {
                    if (emoteSate.showEmoteSelector && isKeyboardVisible.not()) {
                        bottom.linkTo(emotes.top)
                    } else {
                        bottom.linkTo(parent.bottom)
                    }
                },
            handler = handler,
            liveChatHandler = liveChatHandler
        )

        AnimatedVisibility(
            visible = emoteSate.showEmoteSelector,
            modifier = Modifier
                .wrapContentHeight()
                .constrainAs(emotes) {
                    bottom.linkTo(parent.bottom)
                },
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            EmotePickerView(
                modifier = Modifier.wrapContentHeight(),
                emoteState = handler.emoteState,
                liveChatHandler = liveChatHandler,
                onSwitchToKeyboard = handler::onSwitchToKeyboard,
                onSelectEmote = handler::onEmoteSelected,
                onDelete = handler::onDeleteSymbol,
                onDismissRemoteRequest = handler::onDismissEmoteRequest,
                onFollow = handler::onFollowChannel
            )
        }
    }
}

@Composable
private fun ContentFooter(
    modifier: Modifier,
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler
) {
    val state by remember { handler.state }
    val emoteSate by remember { handler.emoteState }
    val liveChatState by liveChatHandler.state
    val userName by handler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userPicture by handler.userPictureFlow.collectAsStateWithLifecycle(initialValue = "")
    val currentComment = state.currentComment
    val selectedPosition = state.currentCursorPosition
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        handler.eventFlow.collectLatest {
            when (it) {

                is VideoDetailsEvent.RequestMessageFocus -> {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }

                else -> return@collectLatest
            }
        }
    }

    if (state.isFullScreen.not()) {
        if (state.isLoggedIn.not()) {
            Text(
                modifier = modifier
                    .clickable { handler.onSignIn() }
                    .padding(vertical = paddingMedium)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sign_in_to_live_chat),
                style = h4Underlined,
                color = darkGreen,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        } else {
            state.userProfile?.let { userProfile ->
                if ((userProfile.validated && state.hasPremiumRestriction && state.hasLiveGateRestriction.not())
                    || state.chatMode == ChatMode.PremiumOrSubscribedOnly
                ) {
                    GoPremiumToCharOrCommentView(
                        modifier = modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.go_premium_to_chat),
                        onClick = { handler.onSubscribeToPremium() }
                    )
                } else if (userProfile.validated) {
                    if (state.uiType == UiType.EMBEDDED) {
                        AddMessageView(
                            modifier = modifier
                                .fillMaxWidth(),
                            message = currentComment,
                            selectedPosition = selectedPosition,
                            placeHolder = stringResource(id = R.string.add_message),
                            userName = if (state.selectedLiveChatAuthor == null) userName else state.selectedLiveChatAuthor?.title
                                ?: "",
                            userPicture = if (state.selectedLiveChatAuthor == null) userPicture else state.selectedLiveChatAuthor?.thumbnail
                                ?: "",
                            rantsEnabled = liveChatState.liveChatConfig?.rantConfig?.rantsEnabled
                                ?: false,
                            displayEmotes = liveChatState.liveChatConfig?.emoteGroups.isNullOrEmpty()
                                .not(),
                            focusRequester = focusRequester,
                            onChange = handler::onCommentChanged,
                            onBuyRant = { handler.onSupportChannelClick(liveChatState.liveChatConfig?.premiumGiftEntity) },
                            emotePickerState = if (emoteSate.showEmoteSelector) EmotePickerState.Selected else EmotePickerState.None,
                            onProfileImageClick = {
                                handler.onLiveChatThumbnailTap(
                                    liveChatState.liveChatConfig?.channels ?: emptyList()
                                )
                            },
                            onSubmit = {
                                liveChatState.liveChatConfig?.chatId?.let {
                                    handler.onPostLiveChatMessage(it)
                                }
                            },
                            onSelectEmote = {
                                handler.onShowEmoteSelector()
                                liveChatHandler.onKeyboardShown()
                            },
                            onTextFieldClicked = {
                                if (emoteSate.showEmoteSelector) handler.onSwitchToKeyboard()
                            },
                        )
                    }
                } else {
                    Text(
                        modifier = modifier
                            .clickable { handler.onVerifyEmailForLiveChat() }
                            .padding(vertical = paddingMedium)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.verify_your_email_live_chat),
                        style = h4Underlined,
                        color = wokeGreen,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}