package com.rumble.network.dto.ads.rumble

import com.google.gson.annotations.SerializedName
import com.rumble.network.queryHelpers.PublisherId

data class AdListResponse(
    @SerializedName("a")
    val metadata: AdMetadata
)

data class AdMetadata(
    @SerializedName("timeout")
    val timeout: Long,
    @SerializedName("u")
    val publisherId: PublisherId?,
    @SerializedName("aden")
    val allowedTypeList: List<Int>,
    @SerializedName("ads")
    val adPlacementList: List<AdPlacement>,
    @SerializedName("evts")
    val events: Events?
)

data class AdPlacement(
    @SerializedName("timecode")
    val timeCode: String,
    @SerializedName("linear")
    val linear: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("autoplay")
    val autoplay: Int,
    @SerializedName("waterfall")
    val adDataList: List<AdData>
)

data class AdData(
    @SerializedName("url")
    val url: String,
    @SerializedName("autoplay")
    val autoplay: Int,
    @SerializedName("evts")
    val events: AdEvents?
)

data class AdEvents(
    @SerializedName("req")
    val reqEvents: List<String>,
    @SerializedName("imp")
    val impEvents: List<String>,
    @SerializedName("pgimp")
    val pgimpEvents: List<String>,
    @SerializedName("clk")
    val clkEvents: List<String>
)

data class Events(
    @SerializedName("start")
    val startEvents: List<String>,
    @SerializedName("view")
    val viewEvents: List<String>,
    @SerializedName("pgview")
    val pgviewEvents: List<String>
)

