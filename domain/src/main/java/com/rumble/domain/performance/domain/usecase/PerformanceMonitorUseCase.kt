package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.FirebasePerformance
import javax.inject.Inject


class PerformanceMonitorUseCase @Inject constructor(
    val firebasePerformance: FirebasePerformance
) {
    operator fun invoke(videoId: String) {
        // Start the trace when the user taps a video
        val videoLoadTrace = FirebasePerformance.getInstance().newTrace("video_load_time")
        videoLoadTrace.putAttribute("video_id", videoId)
        videoLoadTrace.start()
    }
}