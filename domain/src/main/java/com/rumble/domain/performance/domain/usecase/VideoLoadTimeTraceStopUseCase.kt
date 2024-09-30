package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject


class VideoLoadTimeTraceStopUseCase @Inject constructor() {

    operator fun invoke(videoLoadTimeTrace: Trace) {
        videoLoadTimeTrace.stop()
    }
}