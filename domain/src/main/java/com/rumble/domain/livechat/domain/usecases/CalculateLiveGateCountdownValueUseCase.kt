package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import java.time.Duration
import javax.inject.Inject
import kotlin.math.min

class CalculateLiveGateCountdownValueUseCase @Inject constructor() {

    operator fun invoke(videoEntity: VideoEntity, videoTimeCode: Long, countDownValue: Int): Int {
        return videoEntity.liveStreamedOn?.let {
            val startOfPremiumContent = it.plusSeconds(videoTimeCode)
            val difference = Duration.between(it, startOfPremiumContent).seconds
            min(difference.toInt(), countDownValue)
        } ?: countDownValue
    }
}