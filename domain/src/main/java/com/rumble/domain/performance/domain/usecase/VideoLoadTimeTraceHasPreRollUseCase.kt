package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject


class VideoLoadTimeTraceHasPreRollUseCase @Inject constructor() {

    operator fun invoke(videoLoadTimeTrace: Trace, hasPreRoll: Boolean) {
        videoLoadTimeTrace.putAttribute("has_preroll", hasPreRoll.toString())
    }
}