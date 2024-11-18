package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticsManager
import com.rumble.analytics.MediaErrorData
import com.rumble.network.di.IoDispatcher
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.getUserId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SendMediaErrorReportUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val sessionManager: SessionManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(dispatcher)
    operator fun invoke(mediaErrorData: MediaErrorData) {
        scope.launch {
            val userId = sessionManager.userIdFlow.first()
            val intUserId = if (userId.isNotBlank()) userId.getUserId() else null
            analyticsManager.sendMediaErrorReport(mediaErrorData.copy(userId = intUserId))
        }
    }

}