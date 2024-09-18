package com.rumble.domain.rumbleads.model.mapping

import com.rumble.videoplayer.domain.model.VideoAdTimeCode

object TimeCodeMapper {
    fun parseTimeCode(timeCodeStr: String): VideoAdTimeCode {
        return try {
            if (timeCodeStr.contains("%").not()) {
                val code = timeCodeStr.toLong()
                when {
                    code == 0L -> VideoAdTimeCode.PreRoll
                    code == -1L -> VideoAdTimeCode.PostRoll
                    code < 0 -> VideoAdTimeCode.SecondsFromEnd(code)
                    else -> VideoAdTimeCode.SecondsFromStart(code)
                }
            } else {
                VideoAdTimeCode.Percentage(timeCodeStr.replace("%", "").toInt())
            }
        } catch (e: Exception) {
            VideoAdTimeCode.None
        }
    }
}