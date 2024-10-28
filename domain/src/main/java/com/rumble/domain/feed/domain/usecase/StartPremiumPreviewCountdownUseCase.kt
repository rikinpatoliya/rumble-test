package com.rumble.domain.feed.domain.usecase

import com.rumble.utils.RumbleConstants.WATCHED_TIME_OFFSET
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.CountDownType
import java.lang.Long.max
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StartPremiumPreviewCountdownUseCase @Inject constructor() {

    operator fun invoke(rumblePlayer: RumblePlayer, actualDuration: Long) {
        val actualDurationThreshold = max(0, actualDuration - TimeUnit.SECONDS.toMillis(WATCHED_TIME_OFFSET.toLong()))
        if (rumblePlayer.currentPositionValue >= actualDurationThreshold) {
            rumblePlayer.seekTo(0L)
            rumblePlayer.startPremiumCountDown(
                seconds = TimeUnit.MILLISECONDS.toSeconds(actualDuration),
                type = CountDownType.FreePreview
            )
        } else {
            val countDownStartPosition = actualDuration - rumblePlayer.currentPositionValue
            rumblePlayer.startPremiumCountDown(
                seconds = TimeUnit.MILLISECONDS.toSeconds(countDownStartPosition),
                type = CountDownType.FreePreview
            )
        }
    }
}