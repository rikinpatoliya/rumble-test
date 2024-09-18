package com.rumble.player

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.rumble.R
import com.rumble.videoplayer.player.config.PlayerVideoSource
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class OptionDialog : GuidedStepSupportFragment{

    private var playerVideoSource: PlayerVideoSource? = null
    private lateinit var optionEventListener: PlayerOptionEventListener
    private var playerVideoSourceList: List<PlayerVideoSource>? = emptyList()
    private var requiredCallPlayerQualitySet: Boolean = false

    constructor():  super()
    constructor(optionEventListener: PlayerOptionEventListener,
                playerVideoSourceList: List<PlayerVideoSource>?,
                playerVideoSource: PlayerVideoSource?
    ):  super(){
        this.optionEventListener = optionEventListener
        this.playerVideoSourceList = playerVideoSourceList
        this.playerVideoSource = playerVideoSource
    }

    companion object{
        const val checkId = 10001
        const val NAME = "optionDialog"
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.stream_quality),
            getString(R.string.choose_stream_quality),
            "", null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        playerVideoSourceList?.let { playerVideoSourceList ->
            for (eachOption in playerVideoSourceList){
                val optionTitle = videoQualityText(eachOption)
                if (optionTitle.isNotEmpty()){
                    val action: GuidedAction = GuidedAction.Builder(activity)
                        .id(eachOption.resolution.toLong())
                        .title(optionTitle)
                        .hasNext(false)
                        .checkSetId(checkId)
                        .checked(eachOption.resolution == playerVideoSource?.resolution)
                        .editable(false)
                        .build()

                actions.add(action)
            }
        }
    }

    }

    private fun videoQualityText(videoSource: PlayerVideoSource): String =
        videoSource.bitrateText?.let { "${videoSource.qualityText} ($it)" }
            ?: videoSource.qualityText?.replaceFirstChar { it.uppercase() } ?: getString(R.string.auto)

    override fun onGuidedActionClicked(action: GuidedAction) {
        requiredCallPlayerQualitySet = true
        val playerVideoSource = playerVideoSourceList?.find { it.resolution == action.id.toInt()}
        optionEventListener.playerQualitySet(playerVideoSource, true)
    }

    override fun onDetach() {
        super.onDetach()
        if (requiredCallPlayerQualitySet.not()){
            optionEventListener.playerQualitySet(playerVideoSource, false)
        }
    }
}

interface PlayerOptionEventListener{
    fun playerQualitySet(playerVideoSource: PlayerVideoSource?, saveQuality: Boolean)
}