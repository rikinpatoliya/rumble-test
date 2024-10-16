package com.rumble.videoplayer.domain.ads

import com.rumble.videoplayer.domain.model.AdEntity
import com.rumble.videoplayer.domain.model.PreRollUrl
import com.rumble.videoplayer.domain.model.VideoAdDataEntity
import com.rumble.videoplayer.domain.model.VideoAdTimeCode
import com.rumble.videoplayer.domain.model.playedAtStart
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.presentation.internal.defaults.adSeekDelta
import com.rumble.videoplayer.presentation.internal.defaults.preRollDelta
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class PlayerAdsHelper {
    private val maxCount = 2
    private val preRollHash: MutableMap<Long, MutableList<AdEntity>> = mutableMapOf()
    private var currentPreRollList: MutableList<AdEntity> = mutableListOf()
    private var currentIndex: Long? = null

    var currentPreRollUrl: PreRollUrl? = null

    fun initPreRollList(rumbleVideo: RumbleVideo, preRollData: VideoAdDataEntity) {
        preRollHash.clear()
        preRollData.preRollList.forEach { preRollEntity ->
            val position: Long? = when (preRollEntity.timeCode) {
                is VideoAdTimeCode.PreRoll -> 0
                is VideoAdTimeCode.PostRoll -> TimeUnit.SECONDS.toMillis(rumbleVideo.duration)
                is VideoAdTimeCode.SecondsFromStart -> TimeUnit.SECONDS.toMillis(preRollEntity.timeCode.seconds)
                is VideoAdTimeCode.SecondsFromEnd -> TimeUnit.SECONDS.toMillis(rumbleVideo.duration - preRollEntity.timeCode.seconds)
                is VideoAdTimeCode.Percentage -> TimeUnit.SECONDS.toMillis(((rumbleVideo.duration * preRollEntity.timeCode.percentage) / 100))
                else -> null
            }
            position?.let {
                if (preRollHash[position] == null) preRollHash[position] = mutableListOf()
                if ((preRollHash[position]?.size ?: 0) < maxCount) {
                    preRollHash[position]?.add(preRollEntity)
                }
            }
        }
    }

    fun onClear() {
        preRollHash.clear()
        currentPreRollList.clear()
        currentPreRollUrl = null
    }

    fun onPreRollPlayed() {
        if (currentPreRollList.isNotEmpty()) {
            currentPreRollList.removeAt(0)
            currentIndex?.let {
                if (preRollHash[currentIndex]?.isNotEmpty() == true)
                    preRollHash[currentIndex]?.removeAt(0)
            }
        }
        if (currentPreRollList.isEmpty()) {
            currentIndex = null
        }
    }

    fun hasPreRollForPosition(position: Long, isLive: Boolean): Boolean {
        if (isLive) {
            currentIndex = 0
            currentPreRollList = preRollHash.remove(0) ?: mutableListOf()
        } else {
            currentIndex = preRollHash.keys.find { (it - position).absoluteValue <= preRollDelta }
            currentIndex?.let {
                preRollHash[it]?.let { list ->
                    currentPreRollList = list
                }
            } ?: run {
                currentPreRollList = mutableListOf()
            }
        }
        return currentIndex != null
    }

    fun getNextPreRollUrl(): PreRollUrl? {
        currentPreRollList = currentPreRollList.filter { it.urlList.isNotEmpty() }.toMutableList()
        currentPreRollUrl = if (currentPreRollList.isNotEmpty()) {
            try {
                currentPreRollList.first().urlList.removeFirstOrNull()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
        return currentPreRollUrl
    }

    fun hasPreRollAfterSeek(position: Long): Boolean {
        val candidates = preRollHash.filterKeys { key -> key <= position }
        currentIndex = candidates.keys.find { (it - position).absoluteValue <= adSeekDelta }
        currentIndex?.let {
            preRollHash[it]?.let { list ->
                currentPreRollList = list
            }
        } ?: run {
            currentPreRollList = mutableListOf()
        }
        return currentIndex != null
    }

    fun currentIsMidRoll(): Boolean =
        currentPreRollList.isNotEmpty() && currentPreRollList.first().timeCode.playedAtStart().not()

}
