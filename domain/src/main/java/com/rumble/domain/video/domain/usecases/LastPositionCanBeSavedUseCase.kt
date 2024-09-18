package com.rumble.domain.video.domain.usecases

import com.rumble.network.dto.LiveStreamStatus
import javax.inject.Inject

class LastPositionCanBeSavedUseCase @Inject constructor() {
    operator fun invoke(status: LiveStreamStatus) =
        status == LiveStreamStatus.UNKNOWN || status == LiveStreamStatus.ENDED
}