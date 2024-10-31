package com.rumble.util

object Constant {
    const val TAG_REFRESH = "isRefreshRequired"
    const val TAG_CHANNEL = "channel"

    // offset percent for vertical grid margin and padding
    const val CHANNEL_DETAILS_OFFSET_PERCENT = 40f
    const val CHANNEL_DETAILS_OFFSET_PERCENT_COLLAPSED = 33f
    const val CHANNEL_OFFSET_PERCENT = 13f
    const val VIEW_ALL_LIVE_OFFSET_PERCENT = 27f
    const val PLAYBACK_ACTIVITY_ARGS = "video"
    const val PLAYBACK_ACTIVITY_FROM_CHANNEL = "fromChannel"
    const val REFRESH_CONTENT_DURATION = 10_000L * 60 // 10 minutes
    const val ALL_SUBSCRIPTION_REFRESH_DEBOUNCE_TIME = 15 * 1000 // 15 seconds
    const val ALL_SUBSCRIPTION_PROGRESSBAR_TIME = 300L // Hiding the loading UI after ~0.3 seconds
    const val LIVE_REFRESH_CONTENT_DELAY = 1000L
    const val REQUEST_FOCUS_DELAY = 500L
    const val LIVE_CHANNEL_LOADING_DELAY = 100L
    const val SEARCH_CURSOR_SELECTION_DELAY = 300L
    const val CATEGORY_DETAILS_OFFSET_PERCENT = 35f
    const val CATEGORY_TITLE_ANIMATION_DURATION = 600L
    const val TOP_LIVE_CATEGORIES_MAX_CARDS = 12
    const val VIEWERS_COUNT_MIN = 100
    const val OPEN_SIDEBAR_HIGHLIGHT_TINE_DELAY = 350L
    const val SEARCH_ON_BACK_PRESS_SIDE_BAR_FOCUS_DURATION = 700L
    const val CHANNEL_ITEM_ARGS = "channel"
    const val SHOW_LOGO_ARGS = "showLogo"
    const val FINISH_ACTION = "finish"
}