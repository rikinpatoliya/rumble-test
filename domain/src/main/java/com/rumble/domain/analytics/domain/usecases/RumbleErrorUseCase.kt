package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticsManager
import com.rumble.domain.common.model.RumbleError
import javax.inject.Inject

class RumbleErrorUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {

    operator fun invoke(rumbleError: RumbleError?)  {
        rumbleError?.let {
            analyticsManager.sendRumbleErrorReport(
                tag = rumbleError.tag,
                requestUrl = rumbleError.requestUrl,
                code = rumbleError.code,
                message = rumbleError.message,
                rawResponse = rumbleError.rawResponse,
                httpMethod = rumbleError.method,
                body = rumbleError.body
            )
        }
    }
}