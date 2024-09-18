package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

object FollowTapEvent : AnalyticEvent {
    override val eventName: String = "debug_follow_tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object UnfollowTapEvent : AnalyticEvent {
    override val eventName: String = "debug_unfollow_tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object UnfollowConfirmedEvent : AnalyticEvent {
    override val eventName: String = "debug_unfollow_confirm"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object UnfollowCancelEvent : AnalyticEvent {
    override val eventName: String = "debug_unfollow_cancel"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object FollowRequestSentEvent : AnalyticEvent {
    override val eventName: String = "debug_follow"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object UnfollowRequestSentEvent : AnalyticEvent {
    override val eventName: String = "debug_unfollow"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}