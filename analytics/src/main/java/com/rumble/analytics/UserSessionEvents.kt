package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

data object RenewSessionEvent : AnalyticEvent {
    override val eventName: String = "renew_session"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}
