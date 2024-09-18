package com.rumble.domain.feed.model

import com.rumble.domain.feed.domain.domainmodel.video.VideoSource
import kotlin.math.roundToInt


object PlaylistMapper {
    private const val startTag = "#EXT-X-STREAM-INF"
    private const val bandwidthTag = "BANDWIDTH"
    private const val resolutionTag = "RESOLUTION"
    private const val httpTag = "https://"
    private const val defaultTitle = "Stream"
    private const val mbLimit = 950
    private const val kbLimit = 5

    fun getVideoSourceList(input: String): List<VideoSource> {
        val videoSourceList = mutableListOf<VideoSource>()
        var currentVideoSource: VideoSource? = null
        val lines = input.lines()
        var index = 1
        lines.forEach { line ->
            if (line.startsWith(startTag)) {
                val bandwidth = getBandwidth(line)
                val bandwidthTitle = getBandwidthTitle(bandwidth)
                val resTitle = getResolutionTitle(line, index++)
                currentVideoSource = VideoSource(
                    videoUrl = "",
                    type = "",
                    resolution = getResolution(resTitle),
                    bitrate = bandwidth ?: 0,
                    qualityText = resTitle,
                    bitrateText = bandwidthTitle
                )
            } else if (line.startsWith(httpTag)) {
                currentVideoSource?.let {
                    videoSourceList.add(0, it.copy(videoUrl = line))
                }
            }
        }
        return videoSourceList
    }

    private fun getBandwidth(line: String): Int? =
        if (line.contains(bandwidthTag)) {
            val startIndex = line.indexOf(bandwidthTag) + bandwidthTag.count() + 1
            val endIndex = if (line.contains(resolutionTag)) line.indexOf(",") else line.lastIndex
            val bandwidthStr = line.substring(startIndex, endIndex)
            (bandwidthStr.toDouble() / 1000).toInt()
        } else null

    private fun getBandwidthTitle(bandwidth: Int?): String? = bandwidth?.let {
        if (bandwidth >= mbLimit) {
            "${(bandwidth.toDouble() / 100).roundToInt().toDouble() / 10} mbps"
        } else if (bandwidth >= kbLimit) {
            "${(bandwidth.toDouble() / 10).roundToInt() * 10} kbps"
        } else {
            "$bandwidth kbps"
        }
    }

    private fun getResolutionTitle(line: String, index: Int): String =
        if (line.contains(resolutionTag)) {
            val startIndex = line.indexOf(resolutionTag) + resolutionTag.count() + 1
            val resolutionStr = line.substring(startIndex)
            val resolutionWidth = resolutionStr.substring(0, resolutionStr.indexOf("x"))
            val resolutionHeight = resolutionStr.substring(resolutionStr.indexOf("x") + 1)
            "${resolutionWidth.toInt().coerceAtMost(resolutionHeight.toInt())}p"
        } else "$defaultTitle $index"

    private fun getResolution(title: String): Int =
        if (title.contains(defaultTitle)) 0
        else title.substring(0, title.indexOf("p")).toInt()
}