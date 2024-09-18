package com.rumble.domain.events.domain.usecases

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.events.model.repository.EventRepository
import com.rumble.network.NetworkRumbleConstants.DEV_EVENT_SUBDOMAIN
import com.rumble.network.NetworkRumbleConstants.PROD_EVENT_SUBDOMAIN
import com.rumble.network.session.SessionManager
import com.rumble.network.subdomain.SyncRumbleSubdomainUseCase
import com.rumble.utils.extension.getUserId
import com.rumble.videoplayer.player.TimeRangeData
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "SendWatchProgressEventListUseCase"

class SendWatchProgressEventListUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val syncRumbleSubdomainUseCase: SyncRumbleSubdomainUseCase,
    private val sessionManager: SessionManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val isDevelopModeUseCase: IsDevelopModeUseCase,
    private val provideDebugUserIdUseCase: ProvideDebugUserIdUseCase,
) {
    suspend operator fun invoke(timeRangeList: List<TimeRangeData>) {
        try {
            var eventEndpoint = sessionManager.eventEndpointFlow.first()
            if (isDevelopModeUseCase() && eventEndpoint.startsWith(PROD_EVENT_SUBDOMAIN)) {
                eventEndpoint = eventEndpoint.replaceFirst(PROD_EVENT_SUBDOMAIN, DEV_EVENT_SUBDOMAIN)
            }
            val userId = sessionManager.userIdFlow.first()
            val subdomain = syncRumbleSubdomainUseCase()
            eventRepository.sendWatchProgressEvents(
                eventEndpoint = eventEndpoint,
                timeRangeList = timeRangeList,
                userId = if (userId.isEmpty()) null else userId.getUserId(),
                userIdString = provideDebugUserIdUseCase(),
                subdomain = subdomain
            )
        } catch (t: Throwable) {
            unhandledErrorUseCase(TAG, t)
        }
    }
}