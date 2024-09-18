package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

data class LiveVideoPingEvent(private val videoId: Long) : AnalyticEvent {
    override val eventName: String = "debug_live_stream_ping"
    override val firebaseOps: Bundle = bundleOf(CONTENT_ID to videoId)
    override val appsFlyOps: Map<String, String> = mapOf(CONTENT_ID to videoId.toString())
}

data class LiveVideoPingFailedEvent(private val videoId: Long) : AnalyticEvent {
    override val eventName: String = "debug_live_stream_ping_failed"
    override val firebaseOps: Bundle = bundleOf(CONTENT_ID to videoId)
    override val appsFlyOps: Map<String, String> = mapOf(CONTENT_ID to videoId.toString())
}

data class LocalsJoinButtonEvent(
    private val screenId: String,
    private val creatorId: Long
) : AnalyticEvent {
    override val eventName: String = "${screenId}_LocalsJoinButton_Tap"
    override val firebaseOps: Bundle = bundleOf(CREATOR_ID to creatorId)
    override val appsFlyOps: Map<String, String> = mapOf(CREATOR_ID to creatorId.toString())
}

data class VideoCardViewImpressionEvent(
    private val screenId: String,
    private val index: Int,
    private val cardSize: CardSize?,
    private val category: String?
) :
    AnalyticEvent {
    override val eventName: String = "video_card_view"
    override val firebaseOps: Bundle = createFirebaseParams(screenId, index, cardSize, category)
    override val appsFlyOps: Map<String, String> =
        createAppFlyParams(screenId, index, cardSize, category)
}

data class VideoViewImpressionEvent(
    private val screenId: String,
    private val index: Int? = null,
    private val cardSize: CardSize? = null,
    private val category: String? = null
) :
    AnalyticEvent {
    override val eventName: String = "video_view"
    override val firebaseOps: Bundle = createFirebaseParams(screenId, index, cardSize, category)
    override val appsFlyOps: Map<String, String> =
        createAppFlyParams(screenId, index, cardSize, category)
}

data class VideoPlayerImpressionEvent(
    private val screenId: String,
    private val index: Int? = null,
    private val cardSize: CardSize? = null,
    private val category: String? = null
) : AnalyticEvent {
    override val eventName: String = "video_player_view"
    override val firebaseOps: Bundle = createFirebaseParams(screenId, index, cardSize, category)
    override val appsFlyOps: Map<String, String> =
        createAppFlyParams(screenId, index, cardSize, category)
}

data class ContentEvent(private val contentId: String) : AnalyticEvent {
    override val eventName: String = "content_view"
    override val firebaseOps: Bundle = bundleOf(CONTENT_ID to contentId)
    override val appsFlyOps: Map<String, String> = mapOf(CONTENT_ID to contentId)
}

data class RumbleVideoEvent(
    private val screenId: String,
    private val index: Int?,
    private val cardSize: CardSize?,
    private val category: String?
) :
    AnalyticEvent {
    override val eventName: String = "rumble_video_view"
    override val firebaseOps: Bundle = createFirebaseParams(screenId, index, cardSize, category)
    override val appsFlyOps: Map<String, String> =
        createAppFlyParams(screenId, index, cardSize, category)
}

data class VideoViewEvent(
    private val screenId: String
) :
    AnalyticEvent {
    override val eventName: String = "video_view_5min"
    override val firebaseOps: Bundle = bundleOf(SCREEN to screenId)
    override val appsFlyOps: Map<String, String> = mapOf(SCREEN to screenId)
}

private fun createFirebaseParams(
    screenId: String,
    index: Int?,
    cardSize: CardSize?,
    category: String?
) =
    bundleOf(SCREEN to screenId).apply {
        index?.let {
            putInt(INDEX, it)
        }
        cardSize?.let {
            putString(SIZE, it.value)
        }
        category?.let {
            putString(CATEGORY, it)
        }
    }

private fun createAppFlyParams(
    screenId: String,
    index: Int?,
    cardSize: CardSize?,
    category: String?
) =
    mutableMapOf(SCREEN to screenId).apply {
        index?.let {
            this[INDEX] = it.toString()
        }
        cardSize?.let {
            this[SIZE] = it.value
        }
        category?.let {
            this[CATEGORY] = it
        }
    }
