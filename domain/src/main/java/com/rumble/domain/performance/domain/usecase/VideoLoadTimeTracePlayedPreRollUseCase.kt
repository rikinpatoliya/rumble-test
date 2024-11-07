package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject


class VideoLoadTimeTracePlayedPreRollUseCase @Inject constructor() {

    operator fun invoke(videoLoadTimeTrace: Trace) {
        videoLoadTimeTrace.putAttribute("played_preroll", "true")
        videoLoadTimeTrace.stop()
    }
}