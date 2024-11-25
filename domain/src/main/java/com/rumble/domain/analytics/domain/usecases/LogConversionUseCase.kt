package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticEvent
import com.rumble.network.di.IoDispatcher
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogConversionUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(dispatcher)
    operator fun invoke(event: AnalyticEvent) {
        scope.launch {
            if (!sessionManager.conversionLoggedKeyFlow.first()) {
                sessionManager.saveConversionLoggedState(true)
                analyticsEventUseCase(event)
            }
        }
    }
}