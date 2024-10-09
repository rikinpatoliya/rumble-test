package com.rumble.analytics

import android.os.Bundle

object ConversionOrganicEvent : AnalyticEvent {
    override val eventName: String = "conversion_organic"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object ConversionNonOrganicEvent : AnalyticEvent {
    override val eventName: String = "conversion_non_organic"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object ConversionFailedEvent : AnalyticEvent {
    override val eventName: String = "conversion_failed"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}
