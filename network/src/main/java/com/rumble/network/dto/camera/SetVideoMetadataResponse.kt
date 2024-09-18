package com.rumble.network.dto.camera

import com.google.gson.annotations.SerializedName

data class SetVideoMetadataResponse(
    @SerializedName("success")
    val success: Int,
    @SerializedName("data")
    val data: VideoMetadataDataResponse? = null,
    @SerializedName("error_msg")
    val error: ErrorMessageResponse? = null,
)

data class VideoMetadataDataResponse(
    @SerializedName("fid")
    val fid: Int? = null,
)

data class ErrorMessageResponse(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("tags")
    val tags: String? = null,
    @SerializedName("terms")
    val terms: String? = null,
    @SerializedName("form")
    val form: String? = null,
    @SerializedName("video")
    val video: String? = null,
    @SerializedName("files")
    val files: String? = null,
) {
    fun getFirstError(): String? = when {
        title.isNullOrEmpty().not() -> title
        description.isNullOrEmpty().not() -> description
        tags.isNullOrEmpty().not() -> tags
        terms.isNullOrEmpty().not() -> terms
        form.isNullOrEmpty().not() -> form
        video.isNullOrEmpty().not() -> video
        files.isNullOrEmpty().not() -> files
        else -> null
    }
}