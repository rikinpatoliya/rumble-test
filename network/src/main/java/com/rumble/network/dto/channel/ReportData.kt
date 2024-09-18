package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName

/**
 *
 */
data class ReportData(
    @SerializedName("_is_test")
    val _is_test: Boolean = false,
    @SerializedName("content_type")
    val content_type: ReportContentType,
    @SerializedName("content_id")
    val content_id: Long,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("comment")
    val comment: String = ""
)
