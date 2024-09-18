package com.rumble.util

import android.content.Context
import com.rumble.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class StringUtils @Inject constructor(@ApplicationContext val context: Context) {

    // Example: "Video results for '$dan'".
    fun getSearchVideoRowTitle(searchQuery: String): String {
        return context.getString(R.string.search_result_row_video_title_pattern, searchQuery)
    }

    // Example: "No video results for '$dan'".
    fun getSearchEmptyVideoRowTitle(searchQuery: String): String {
        return context.getString(R.string.search_result_empty_row_video_title_pattern, searchQuery)
    }

    // Example: "Channel results for '$dan'".
    fun getSearchChannelRowTitle(searchQuery: String): String {
        return context.getString(R.string.search_result_row_channel_title_pattern, searchQuery)
    }

    // Example: "No channel results for '$dan'".
    fun getSearchEmptyChannelRowTitle(searchQuery: String): String {
        return context.getString(R.string.search_result_empty_row_channel_title_pattern, searchQuery)
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}