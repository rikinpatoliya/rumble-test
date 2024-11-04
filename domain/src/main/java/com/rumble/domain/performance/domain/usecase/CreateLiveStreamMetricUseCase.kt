package com.rumble.domain.performance.domain.usecase

import com.google.firebase.perf.FirebasePerformance
import javax.inject.Inject

class CreateLiveStreamMetricUseCase @Inject constructor() {
    operator fun invoke() = FirebasePerformance.getInstance().newHttpMetric(
        "https://rumble.com/chat/api/chat/stream",
        FirebasePerformance.HttpMethod.GET,
    )
}