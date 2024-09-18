package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticEvent
import com.rumble.analytics.AnalyticsManager
import com.rumble.domain.events.domain.usecases.ProvideDebugUserIdUseCase
import com.rumble.domain.events.model.repository.EventRepository
import com.rumble.network.di.IoDispatcher
import com.rumble.network.subdomain.SyncRumbleSubdomainUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AnalyticsEventUseCase"

class AnalyticsEventUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val eventRepository: EventRepository,
    private val syncRumbleSubdomainUseCase: SyncRumbleSubdomainUseCase,
    private val provideDebugUserIdUseCase: ProvideDebugUserIdUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(dispatcher)
    operator fun invoke(event: AnalyticEvent, sendDebugLog: Boolean = false) {
        analyticsManager.sendAnalyticEvent(event)
        scope.launch {
            if (sendDebugLog and syncRumbleSubdomainUseCase().isNotEmpty()) {
                try {
                    eventRepository.sendAnalyticsEvent(provideDebugUserIdUseCase(), event.eventName, event.appsFlyOps)
                } catch (t: Throwable) {
                    unhandledErrorUseCase(TAG, t)
                }
            }
        }
    }
}
