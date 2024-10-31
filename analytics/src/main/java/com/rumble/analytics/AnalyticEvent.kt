package com.rumble.analytics

import android.os.Bundle

sealed interface AnalyticEvent {
    val eventName: String
    val firebaseOps: Bundle
    val appsFlyOps: Map<String, String?>
}
