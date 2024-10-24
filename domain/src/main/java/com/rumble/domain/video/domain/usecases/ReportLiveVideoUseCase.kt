package com.rumble.domain.video.domain.usecases

import android.webkit.URLUtil
import com.rumble.analytics.LiveVideoPingEvent
import com.rumble.analytics.LiveVideoPingFailedEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.model.RumbleError
import com.rumble.network.api.LiveVideoApi
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.dto.livevideo.LiveReportBody
import com.rumble.network.dto.livevideo.LiveReportBodyData
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "ReportLiveVideoUseCase"

class ReportLiveVideoUseCase @Inject constructor(
    private val liveVideoApi: LiveVideoApi,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(
        videoId: Long,
        viewerId: String,
        requestLiveGateData: Boolean = false,
    ): LiveVideoReportResult? {
        val result = buildReportUrl()?.let {
            liveVideoApi.reportLiveVideo(
                url = it,
                LiveReportBody(
                    data = LiveReportBodyData(
                        videoId,
                        viewerId
                    )
                ),
                serviceName = "video.watching-now",
                requestLiveGateData = if (requestLiveGateData) 1 else null,
            )
        }
        return if (result?.isSuccessful == true) {
            analyticsEventUseCase(LiveVideoPingEvent(videoId), true)
            result.body()?.let {
                LiveVideoReportResult(
                    watchingNow = it.data.watchingNow,
                    statusCode = it.data.liveStatus,
                    isLive = it.data.liveStatus == LiveStreamStatus.LIVE.value || it.data.liveStatus == LiveStreamStatus.OFFLINE.value,
                    hasLiveGate = it.data.liveGate != null,
                    videoTimeCode = it.data.liveGate?.timeCode,
                    countDownValue = it.data.liveGate?.countdown
                )
            }
        } else {
            analyticsEventUseCase(LiveVideoPingFailedEvent(videoId))
            result?.raw()?.let { response ->
                rumbleErrorUseCase(RumbleError(tag = TAG, response = response))
            }
            null
        }
    }

    private fun buildReportUrl(): String? {
        val url = "${runBlocking { sessionManager.livePingEndpointFlow.first() }}/service.php"
        return if (URLUtil.isValidUrl(url)) url
        else null
    }
}