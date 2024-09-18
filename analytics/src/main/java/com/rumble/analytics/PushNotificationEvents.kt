package com.rumble.analytics

import android.os.Bundle

object PushNotificationInteractionEvent : AnalyticEvent {
    override val eventName: String = "push_ntf_interaction"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object PushNotificationHandlingFailedEvent : AnalyticEvent {
    override val eventName: String = "push_ntf_handling_failed"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}
