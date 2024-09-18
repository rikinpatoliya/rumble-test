package com.rumble.analytics

import android.os.Bundle

object MatureContentCancelEvent : AnalyticEvent {
    override val eventName: String = "MatureContentPopup_CancelButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object MatureContentWatchEvent : AnalyticEvent {
    override val eventName: String = "MatureContentPopup_StartWatchingButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}
