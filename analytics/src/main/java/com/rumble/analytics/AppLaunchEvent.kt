package com.rumble.analytics

import android.os.Bundle

object AppLaunchEvent : AnalyticEvent {
    override val eventName: String = "app_launch"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}