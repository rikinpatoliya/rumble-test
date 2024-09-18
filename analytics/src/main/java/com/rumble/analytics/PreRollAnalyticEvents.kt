package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

object ImaVideoLoadedEvent : AnalyticEvent {
    override val eventName: String = "ima_video_view"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaVideoNoAutoplayLoadedEvent : AnalyticEvent {
    override val eventName: String = "ima_video_view_no_autoplay"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaVideoStartedEvent : AnalyticEvent {
    override val eventName: String = "ima_video_start"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

data class ImaRequestedEvent(private val userId: String, private val creatorId: String) : AnalyticEvent {
    override val eventName: String = "ima_requested"
    override val firebaseOps: Bundle = bundleOf(
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
}

data class ImaImpressionEvent(private val userId: String, private val creatorId: String) : AnalyticEvent {
    override val eventName: String = "ima_impression"
    override val firebaseOps: Bundle = bundleOf(
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
}

object ImaImpressionNoAutoplayEvent : AnalyticEvent {
    override val eventName: String = "ima_impression_no_autoplay"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaClickedEvent : AnalyticEvent {
    override val eventName: String = "ima_clicked"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaSkippedEvent : AnalyticEvent {
    override val eventName: String = "ima_skipped"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaCompletedEvent : AnalyticEvent {
    override val eventName: String = "ima_completed"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaFailedEvent : AnalyticEvent {
    override val eventName: String = "ima_failed"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

data class ImaFetchEvent(private val watchedTime: Long): AnalyticEvent {
    override val eventName: String = "ima_fetch_ads"
    override val firebaseOps: Bundle = bundleOf(
        WATCHED_TIME to watchedTime,
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        WATCHED_TIME to watchedTime.toString(),
    )
}

object ImaFetchFailedEvent: AnalyticEvent {
    override val eventName: String = "ima_fetch_failed"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaFetchFilledEvent: AnalyticEvent {
    override val eventName: String = "ima_fetched_filled"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaFetchEmptyEvent: AnalyticEvent {
    override val eventName: String = "ima_fetched_empty"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object ImaDestroyedEvent: AnalyticEvent {
    override val eventName: String = "ima_destroyed"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}
