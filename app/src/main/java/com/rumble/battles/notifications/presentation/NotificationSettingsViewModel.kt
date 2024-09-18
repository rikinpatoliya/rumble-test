package com.rumble.battles.notifications.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.usecase.GetNotificationSettingsUseCase
import com.rumble.domain.settings.domain.usecase.UpdateNotificationSettingsUseCase
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotificationSettingsViewModel"

interface NotificationSettingsHandler {
    val state: State<NotificationSettingsState>
    val eventFlow: Flow<NotificationSettingsEvent>

    fun onToggleNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity)
}

data class NotificationSettingsState(
    val notificationSettingsEntity: NotificationSettingsEntity? = null,
    val allNotificationsEnabled: Boolean = false,
    val loading: Boolean = false,
)

sealed class NotificationSettingsEvent {
    data class Error(val message: String? = null) : NotificationSettingsEvent()
}

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), NotificationSettingsHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = state.value.copy(loading = false)
        emitVmEvent(NotificationSettingsEvent.Error())
    }

    init {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest {
                if (it.isNotEmpty()) {
                    fetchInitialSettings()
                }
            }
        }
    }

    override val state: MutableState<NotificationSettingsState> =
        mutableStateOf(NotificationSettingsState())
    override val eventFlow: MutableSharedFlow<NotificationSettingsEvent> = MutableSharedFlow()

    override fun onToggleNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity) {
        state.value = state.value.copy(loading = true)
        viewModelScope.launch(errorHandler) {
            val result =
                updateNotificationSettingsUseCase(notificationSettingsEntity)
            if (result.success) {
                state.value = state.value.copy(
                    notificationSettingsEntity = result.notificationSettingsEntity,
                    allNotificationsEnabled = hasDisabledNotification(result.notificationSettingsEntity).not(),
                    loading = false
                )
            } else {
                state.value = state.value.copy(
                    loading = false,
                    notificationSettingsEntity = state.value.notificationSettingsEntity
                )
                emitVmEvent(NotificationSettingsEvent.Error(message = result.error))
            }
        }
    }

    private fun fetchInitialSettings() {
        viewModelScope.launch(errorHandler) {
            state.value = state.value.copy(loading = true)
            val notificationSettingsResult = getNotificationSettingsUseCase()
            if (notificationSettingsResult.success) {
                state.value = state.value.copy(
                    notificationSettingsEntity = notificationSettingsResult.notificationSettingsEntity,
                    allNotificationsEnabled = !hasDisabledNotification(notificationSettingsResult.notificationSettingsEntity),
                    loading = false
                )
            } else {
                state.value = state.value.copy(loading = false)
                emitVmEvent(NotificationSettingsEvent.Error(message = notificationSettingsResult.error))
            }
        }
    }

    private fun emitVmEvent(event: NotificationSettingsEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    private fun hasDisabledNotification(notificationSettingsEntity: NotificationSettingsEntity?): Boolean {
        return notificationSettingsEntity?.let {
            if (it.moneyEarned.not())
                true
            else if (it.videoApprovedForMonetization.not())
                true
            else if (it.someoneFollowsYou.not())
                true
            else if (it.someoneTagsYou.not())
                true
            else if (it.commentsOnYourVideo.not())
                true
            else if (it.repliesToYourComments.not())
                true
            else it.newVideoBySomeoneYouFollow.not()
        } ?: false
    }
}