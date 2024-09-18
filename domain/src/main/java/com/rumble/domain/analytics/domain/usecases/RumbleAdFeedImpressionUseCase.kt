package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AdsCommonEvent
import com.rumble.analytics.RumbleAdFeedImpressionEvent
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RumbleAdFeedImpressionUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val rumbleAdRepository: RumbleAdRepository,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(rumbleAd: RumbleAdEntity) {
        if (rumbleAd.viewed.not()) {
            rumbleAdRepository.reportAdImpression(rumbleAd.impressionUrl)
            analyticsEventUseCase(
                event = AdsCommonEvent(rumbleAd.price, sessionManager.userIdFlow.first()),
                sendDebugLog = true
            )
            analyticsEventUseCase(
                event = RumbleAdFeedImpressionEvent(
                    rumbleAd.price,
                    sessionManager.userIdFlow.first()
                ),
                sendDebugLog = true
            )
            rumbleAd.viewed = true
        }
    }
}