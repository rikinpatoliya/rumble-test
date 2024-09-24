package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject


class VideoLoadTimeTraceStartUseCase @Inject constructor() {

    operator fun invoke(videoId: String): Trace {
        val videoLoadTimeTrace = FirebasePerformance.getInstance().newTrace("video_load_time")
        videoLoadTimeTrace.putAttribute("video_id", videoId)
        videoLoadTimeTrace.start()
        return videoLoadTimeTrace
    }
}