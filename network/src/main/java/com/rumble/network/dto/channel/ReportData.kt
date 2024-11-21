package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName

/**
 *
 */
data class ReportData(
    @SerializedName("_is_test")
    val isTest: Boolean = false,
    @SerializedName("content_type")
    val contentType: ReportContentType,
    @SerializedName("content_id")
    val contentId: Long,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("comment")
    val comment: String = ""
)
