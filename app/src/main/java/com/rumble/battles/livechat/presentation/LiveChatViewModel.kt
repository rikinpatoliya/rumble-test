package com.rumble.battles.livechat.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.rumble.analytics.IAP_FAILED
import com.rumble.analytics.RantBuyButtonTapEvent
import com.rumble.analytics.RantTermsLinkTapEvent
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.billing.domain.usecase.BuildProductDetailsParamsUseCase
import com.rumble.domain.billing.domain.usecase.FetchRantProductDetailsUseCase
import com.rumble.domain.billing.model.PurchaseHandler
import com.rumble.domain.billing.model.PurchaseResult
import com.rumble.domain.billing.model.RumblePurchaseUpdateListener
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.DeleteMessageResult
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatResult
import com.rumble.domain.livechat.domain.domainmodel.MessageModerationResult
import com.rumble.domain.livechat.domain.domainmodel.MutePeriod
import com.rumble.domain.livechat.domain.domainmodel.MuteUserResult
import com.rumble.domain.livechat.domain.domainmodel.PaymentProofResult
import com.rumble.domain.livechat.domain.domainmodel.PendingMessageInfo
import com.rumble.domain.livechat.domain.domainmodel.RantEntity
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.domain.usecases.DeleteMessageUseCase
import com.rumble.domain.livechat.domain.usecases.GetLiveChatEventsUseCase
import com.rumble.domain.livechat.domain.usecases.GetRantListUseCase
import com.rumble.domain.livechat.domain.usecases.GetUnreadMessageCountTextUseCase
import com.rumble.domain.livechat.domain.usecases.InitAtMentionUseCase
import com.rumble.domain.livechat.domain.usecases.MuteUserUseCase
import com.rumble.domain.livechat.domain.usecases.PinMessageUseCase
import com.rumble.domain.livechat.domain.usecases.PostPaymentProofUseCase
import com.rumble.domain.livechat.domain.usecases.UnpinMessageUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.LIVE_CHAT_MAX_MESSAGE_COUNT
import com.rumble.utils.RumbleConstants.RANT_STATE_UPDATE_RATIO
import com.rumble.utils.extension.getComposeColor
import com.rumble.utils.extension.getUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.max

private const val TAG = "LiveChatViewModel"

interface LiveChatHandler {
    val state: State<LiveChatState>
    val eventFlow: SharedFlow<LiveChatEvent>
    val alertDialogState: State<AlertDialogState>

    fun onInitLiveChat(videoId: Long)
    fun onRantClicked(rantEntity: RantEntity)
    fun onDismissBottomSheet()
    fun onReportRantTermsEvent()
    fun onBuyRant(pendingMessageInfo: PendingMessageInfo)
    fun onRantLevelSelected(rantLevel: RantLevel)
    fun onScrolledToBottom()
    fun onViewUnreadMessages()
    fun onHidePinnedMessage()
    fun onDisplayPinnedMessage()
    fun onDisplayModerationMenu(message: LiveChatMessageEntity)
    fun onHideModerationMenu()
    fun onPinMessage(videoId: Long)
    fun onUnpinMessage()
    fun onConfirmUnpinMessage(videoId: Long)
    fun onDismiss()
    fun onDeleteMessage()
    fun onConfirmDeleteMessage()
    fun onMuteUserConfirmed(videoId: Long, mutePeriod: MutePeriod)
}

data class LiveChatState(
    val messageList: List<LiveChatMessageEntity> = emptyList(),
    val badges: Map<String, BadgeEntity> = emptyMap(),
    val rantList: List<RantEntity> = emptyList(),
    val rantPopupMessage: LiveChatMessageEntity? = null,
    val liveChatConfig: LiveChatConfig? = null,
    val connectionState: InternetConnectionState = InternetConnectionState.CONNECTED,
    val observationStarted: Boolean = false,
    val pendingMessageInfo: PendingMessageInfo? = null,
    val rantSelected: RantLevel? = null,
    val unreadMessageCount: Int = 0,
    val unreadMessageCountText: String = "",
    val pinnedMessage: LiveChatMessageEntity? = null,
    val pinnedMessageHidden: Boolean = false,
    val canModerate: Boolean = false,
    val selectedMessage: LiveChatMessageEntity? = null,
    val moderationMenuType: ModerationMenuType = ModerationMenuType.Generic
)

sealed class LiveChatEvent {
    object Error : LiveChatEvent()
    data class ScrollLiveChat(val index: Int) : LiveChatEvent()
    data class ScrollRant(val index: Int) : LiveChatEvent()
    object CloseBottomSheet : LiveChatEvent()
    data class StartPurchase(val billingClient: BillingClient, val params: BillingFlowParams) :
        LiveChatEvent()

    object ScrollToBottom : LiveChatEvent()
    data class RantPurchaseSucceeded(val rantLevel: RantLevel) : LiveChatEvent()
    object OpenModerationMenu : LiveChatEvent()
    object HideModerationMenu : LiveChatEvent()
}

sealed class LiveChatAlertReason : AlertDialogReason {
    object UnpinMessage : LiveChatAlertReason()
    object DeleteMessage : LiveChatAlertReason()
    data class ErrorMessage(val errorMessage: String) : LiveChatAlertReason()
}

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    private val fetchRantProductDetailsUseCase: FetchRantProductDetailsUseCase,
    private val getUnreadMessageCountTextUseCase: GetUnreadMessageCountTextUseCase,
    private val getLiveChatEventsUseCase: GetLiveChatEventsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getRantListUseCase: GetRantListUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val buildProductDetailsParamsUseCase: BuildProductDetailsParamsUseCase,
    private val billingClient: BillingClient,
    private val initAtMentionUseCase: InitAtMentionUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val postPaymentProofUseCase: PostPaymentProofUseCase,
    private val purchaseUpdateListener: RumblePurchaseUpdateListener,
    private val pinMessageUseCase: PinMessageUseCase,
    private val unpinMessageUseCase: UnpinMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val muteUseCase: MuteUserUseCase,
    private val sessionManager: SessionManager,
    private val rumbleErrorUseCase: RumbleErrorUseCase,
) : ViewModel(), LiveChatHandler, PurchaseHandler {

    private var currentUserid: Long? = null
    private var purchaseInProgress: Boolean = false
    private var liveChatConfig: LiveChatConfig? = null
    private var messageList = listOf<LiveChatMessageEntity>()
    private var rantUpdateJob: Job = Job()
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = state.value.copy(selectedMessage = null)
    }

    override val state: MutableState<LiveChatState> = mutableStateOf(LiveChatState())
    override val eventFlow: MutableSharedFlow<LiveChatEvent> = MutableSharedFlow()
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())

    init {
        purchaseUpdateListener.subscribeToPurchaseUpdate(this)
        viewModelScope.launch {
            currentUserid = sessionManager.userIdFlow.firstOrNull()?.getUserId()
        }
    }

    override fun onPurchaseFinished(result: PurchaseResult) {
        if (purchaseInProgress) {
            purchaseInProgress = false
            if (result is PurchaseResult.Success) {
                viewModelScope.launch(errorHandler) {
                    state.value.rantSelected?.let {
                        emitEvent(LiveChatEvent.RantPurchaseSucceeded(it))
                    }
                    state.value.pendingMessageInfo?.let {
                        when (postPaymentProofUseCase(it, result.purchaseToken)) {
                            is PaymentProofResult.Success -> {
                                state.value = state.value.copy(pendingMessageInfo = null)
                            }

                            is PaymentProofResult.Failure -> {
                                emitEvent(LiveChatEvent.Error)
                            }
                        }
                    }
                }
            } else if (result is PurchaseResult.Failure) {
                rumbleErrorUseCase(RumbleError(IAP_FAILED, result.errorMessage, result.code))
                emitEvent(LiveChatEvent.Error)
            }
        }
    }

    override fun onInitLiveChat(videoId: Long) {
        if (state.value.observationStarted.not()) {
            state.value = state.value.copy(observationStarted = true)
            observeEventFlow(videoId)
            observeConnectionState(videoId)
        }
    }

    override fun onRantClicked(rantEntity: RantEntity) {
        state.value = state.value.copy(rantPopupMessage = rantEntity.messageEntity)
    }

    override fun onDismissBottomSheet() {
        emitEvent(LiveChatEvent.CloseBottomSheet)
        state.value = state.value.copy(
            rantPopupMessage = null,
            selectedMessage = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        rantUpdateJob.cancel()
        purchaseUpdateListener.unsubscribeFromPurchaseUpdate(this)
    }

    override fun onReportRantTermsEvent() {
        analyticsEventUseCase(RantTermsLinkTapEvent)
    }

    override fun onBuyRant(pendingMessageInfo: PendingMessageInfo) {
        state.value.rantSelected?.let { rantSelected ->
            analyticsEventUseCase(RantBuyButtonTapEvent(rantSelected.rantPrice))
            viewModelScope.launch(errorHandler) {
                rantSelected.productDetails?.let {
                    state.value = state.value.copy(pendingMessageInfo = pendingMessageInfo)
                    purchaseInProgress = true
                    emitEvent(
                        LiveChatEvent.StartPurchase(
                            billingClient,
                            buildProductDetailsParamsUseCase(it)
                        )
                    )
                } ?: run {
                    emitEvent(LiveChatEvent.Error)
                }
            }
        }
    }

    override fun onRantLevelSelected(rantLevel: RantLevel) {
        state.value = state.value.copy(rantSelected = rantLevel)
    }

    override fun onScrolledToBottom() {
        state.value = state.value.copy(unreadMessageCount = 0)
    }

    override fun onViewUnreadMessages() {
        state.value = state.value.copy(unreadMessageCount = 0)
        emitEvent(LiveChatEvent.ScrollToBottom)
    }

    override fun onHidePinnedMessage() {
        state.value = state.value.copy(pinnedMessageHidden = true)
    }

    override fun onDisplayPinnedMessage() {
        state.value = state.value.copy(pinnedMessageHidden = false)
    }

    override fun onDisplayModerationMenu(message: LiveChatMessageEntity) {
        val type = if (message.deleted) ModerationMenuType.Deleted
        else if (message.userId == currentUserid) ModerationMenuType.Self
        else ModerationMenuType.Generic
        if (state.value.canModerate) {
            state.value = state.value.copy(
                selectedMessage = message,
                moderationMenuType = type
            )
            emitEvent(LiveChatEvent.OpenModerationMenu)
        }
    }

    override fun onHideModerationMenu() {
        state.value = state.value.copy(selectedMessage = null)
        emitEvent(LiveChatEvent.HideModerationMenu)
    }

    override fun onPinMessage(videoId: Long) {
        emitEvent(LiveChatEvent.HideModerationMenu)
        state.value.selectedMessage?.let {
            viewModelScope.launch(errorHandler) {
                val result = pinMessageUseCase(videoId = videoId, messageId = it.messageId)
                if (result is MessageModerationResult.Failure) {
                    emitEvent(LiveChatEvent.Error)
                }
                state.value = state.value.copy(
                    selectedMessage = null
                )
            }
        }
    }

    override fun onUnpinMessage() {
        alertDialogState.value = alertDialogState.value.copy(
            show = true,
            alertDialogReason = LiveChatAlertReason.UnpinMessage
        )
    }

    override fun onConfirmUnpinMessage(videoId: Long) {
        onDismiss()
        state.value.pinnedMessage?.let {
            viewModelScope.launch(errorHandler) {
                val result = unpinMessageUseCase(videoId = videoId, messageId = it.messageId)
                if (result is MessageModerationResult.Failure) {
                    emitEvent(LiveChatEvent.Error)
                }
                state.value = state.value.copy(
                    selectedMessage = null
                )
            }
        }
    }

    override fun onDismiss() {
        alertDialogState.value = alertDialogState.value.copy(
            show = false,
            alertDialogReason = AlertDialogReason.None
        )
    }

    override fun onDeleteMessage() {
        alertDialogState.value = alertDialogState.value.copy(
            show = true,
            alertDialogReason = LiveChatAlertReason.DeleteMessage
        )
    }

    override fun onConfirmDeleteMessage() {
        onDismiss()
        emitEvent(LiveChatEvent.HideModerationMenu)
        viewModelScope.launch(errorHandler) {
            state.value.liveChatConfig?.chatId?.let { chatId ->
                state.value.selectedMessage?.let { message ->
                    val result = deleteMessageUseCase(chatId = chatId, message.messageId)
                    if (result is DeleteMessageResult.Failure) {
                        emitEvent(LiveChatEvent.Error)
                    }
                    state.value = state.value.copy(
                        selectedMessage = null
                    )
                }
            }
        }
    }

    override fun onMuteUserConfirmed(videoId: Long, mutePeriod: MutePeriod) {
        emitEvent(LiveChatEvent.HideModerationMenu)
        viewModelScope.launch(errorHandler) {
            state.value.selectedMessage?.let {
                val result = muteUseCase(
                    liveChatMessageEntity = it,
                    videoId = videoId,
                    mutePeriod = mutePeriod
                )
                if (result is MuteUserResult.Failure) {
                    emitEvent(LiveChatEvent.Error)
                } else if (result is MuteUserResult.MuteFailure) {
                    alertDialogState.value = alertDialogState.value.copy(
                        show = true,
                        alertDialogReason = LiveChatAlertReason.ErrorMessage(result.errorMessage)
                    )
                }
            }
            state.value = state.value.copy(
                selectedMessage = null
            )
        }
    }

    private fun emitEvent(event: LiveChatEvent) {
        viewModelScope.launch { eventFlow.emit(event) }
    }

    private fun observeEventFlow(videoId: Long) {
        viewModelScope.launch(errorHandler) {
            getLiveChatEventsUseCase(videoId).collect { result ->
                if (liveChatConfig == null) {
                    result.liveChatConfig?.let {
                        val rantConfig = it.rantConfig
                        liveChatConfig = it.copy(
                            rantConfig = rantConfig.copy(
                                levelList = fetchRantProductDetailsUseCase(rantConfig.levelList)
                            )
                        )
                    }
                    state.value = state.value.copy(liveChatConfig = liveChatConfig)
                    startUpdateRantList()
                }
                liveChatConfig?.let { config ->
                    messageList = handleDeletedMessages(result)
                    messageList =
                        messageList + updateRantColor(initAtMentionUseCase(result.messageList))
                    val messageCount = state.value.unreadMessageCount + result.messageList.size
                    val currentSelection = state.value.rantSelected

                    if (messageList.size > LIVE_CHAT_MAX_MESSAGE_COUNT) {
                        messageList = messageList.takeLast(LIVE_CHAT_MAX_MESSAGE_COUNT)
                    }

                    state.value = state.value.copy(
                        messageList = messageList,
                        rantList = getRantListUseCase(messageList, config.rantConfig),
                        badges = config.badges,
                        rantSelected = currentSelection
                            ?: config.rantConfig.levelList.firstOrNull(),
                        unreadMessageCount = messageCount,
                        unreadMessageCountText = getUnreadMessageCountTextUseCase(messageCount),
                        pinnedMessage = getPinnedMessage(result),
                        canModerate = (if (result.canModerate != null) result.canModerate else state.value.canModerate)
                            ?: false
                    )
                    delay(RumbleConstants.LIVE_CHAT_ANIMATION_DURATION.toLong())
                    emitEvent(LiveChatEvent.ScrollLiveChat(max(messageList.size - 1, 0)))
                }
            }
        }
    }

    private fun handleDeletedMessages(result: LiveChatResult) =
        messageList.map { messageEntity ->
            messageEntity.copy(
                deleted = result.deletedMessageIdList.contains(messageEntity.messageId)
                    || result.mutedUserIdList.contains(messageEntity.userId)
                    || messageEntity.deleted
            )
        }

    private fun getPinnedMessage(result: LiveChatResult): LiveChatMessageEntity? =
        if (result.unpinnedMessageId != null && result.unpinnedMessageId == state.value.pinnedMessage?.messageId) {
            null
        } else if (result.pinnedMessageId != null) {
            messageList.find { it.messageId == result.pinnedMessageId }
        } else {
            state.value.pinnedMessage
        }

    private fun updateRantColor(entities: List<LiveChatMessageEntity>) =
        entities.map { entity ->
            val rant = liveChatConfig?.rantConfig?.levelList?.find {
                it.rantPrice.compareTo(entity.rantPrice ?: BigDecimal.ZERO) == 0
            }
            entity.copy(
                background = rant?.mainColor?.getComposeColor(),
                titleBackground = rant?.backgroundColor?.getComposeColor(),
                textColor = rant?.foregroundColor?.getComposeColor()
            )
        }

    private fun startUpdateRantList() {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            while (isActive) {
                delay(RANT_STATE_UPDATE_RATIO)
                liveChatConfig?.let { config ->
                    state.value = state.value.copy(
                        rantList = getRantListUseCase(messageList, config.rantConfig)
                    )
                    if (state.value.rantList.isNotEmpty()) {
                        emitEvent(LiveChatEvent.ScrollRant(state.value.rantList.size - 1))
                    }
                }
            }
        }
    }

    private fun observeConnectionState(videoId: Long) {
        rantUpdateJob = viewModelScope.launch(errorHandler) {
            internetConnectionObserver.connectivityFlow.collectLatest {
                if ((it == InternetConnectionState.CONNECTED) and (state.value.connectionState == InternetConnectionState.LOST)) {
                    messageList = emptyList()
                    observeEventFlow(videoId)
                } else {
                    state.value = state.value.copy(connectionState = it)
                }
            }
        }
    }
}