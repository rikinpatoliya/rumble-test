package com.rumble.battles.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.feed.domain.usecase.GetSensorBasedOrientationChangeEnabledUseCase
import com.rumble.domain.landing.usecases.LoginRequiredUseCase
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LandingHandler {
    val colorMode: Flow<ColorMode>
    val sensorBasedOrientationChangeEnabled: Boolean

    suspend fun shouldLogin(): Boolean
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    userPreferenceManager: UserPreferenceManager,
    private val getSensorBasedOrientationChangeEnabledUseCase: GetSensorBasedOrientationChangeEnabledUseCase,
    private val sessionManager: SessionManager,
    private val loginRequiredUseCase: LoginRequiredUseCase,
): ViewModel(), LandingHandler {

    override val colorMode: Flow<ColorMode> = userPreferenceManager.colorMode
    override val sensorBasedOrientationChangeEnabled: Boolean
        get() = getSensorBasedOrientationChangeEnabledUseCase()

    override suspend fun shouldLogin(): Boolean  = loginRequiredUseCase()

    init {
        viewModelScope.launch {
            sessionManager.saveChatEndpointUpdateForCurrentSession(false)
            sessionManager.saveLivePingEndpoint(null)
            userPreferenceManager.saveVideoCardSoundState(false)
        }
    }
}