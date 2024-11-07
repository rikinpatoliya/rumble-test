package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject


class VideoLoadTimeTracePreRollUseCase @Inject constructor() {

    operator fun invoke(videoLoadTimeTrace: Trace, hasPreRoll: Boolean) {
        videoLoadTimeTrace.putAttribute("has_preroll", hasPreRoll.toString())
    }
}