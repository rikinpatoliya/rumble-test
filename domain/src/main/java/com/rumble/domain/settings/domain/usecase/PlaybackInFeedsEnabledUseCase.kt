package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.PlaybackInFeedsMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.connection.NetworkType
import com.rumble.network.connection.NetworkTypeResolver
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaybackInFeedsEnabledUseCase @Inject constructor(
    private val networkTypeResolver: NetworkTypeResolver,
    private val userPreferenceManager: UserPreferenceManager,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(): Boolean {
        val networkType = networkTypeResolver.typeOfNetwork()
        val playbackMode = userPreferenceManager.playbackInFeedsModeModeFlow.first()
        val videoDetailsVisible = sessionManager.videDetailsOpenedFlow.first()
        return ((playbackMode == PlaybackInFeedsMode.ALWAYS_ON) ||
            (playbackMode == PlaybackInFeedsMode.WIFI_ONLY && networkType == NetworkType.WI_FI)) &&
            videoDetailsVisible.not()
    }
}