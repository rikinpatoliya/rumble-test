package com.rumble.battles.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.usecase.GetSensorBasedOrientationChangeEnabledUseCase
import com.rumble.domain.landing.usecases.LoginRequiredUseCase
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.usecase.PrepareAppForTestingUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LandingViewModel"

interface LandingHandler {
    val colorMode: Flow<ColorMode>
    val sensorBasedOrientationChangeEnabled: Boolean

    suspend fun shouldLogin(): Boolean
    fun onPrepareAppForTesting(uitUserName: String?, uitPassword: String?)
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    userPreferenceManager: UserPreferenceManager,
    private val getSensorBasedOrientationChangeEnabledUseCase: GetSensorBasedOrientationChangeEnabledUseCase,
    private val sessionManager: SessionManager,
    private val loginRequiredUseCase: LoginRequiredUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val prepareAppForTestingUseCase: PrepareAppForTestingUseCase
) : ViewModel(), LandingHandler {

    override val colorMode: Flow<ColorMode> = userPreferenceManager.colorMode
    override val sensorBasedOrientationChangeEnabled: Boolean
        get() = getSensorBasedOrientationChangeEnabledUseCase()

    override suspend fun shouldLogin(): Boolean = loginRequiredUseCase()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        viewModelScope.launch {
            sessionManager.saveChatEndpointUpdateForCurrentSession(false)
            sessionManager.saveLivePingEndpoint(null)
            userPreferenceManager.saveVideoCardSoundState(false)
        }
    }

    override fun onPrepareAppForTesting(
        uitUserName: String?,
        uitPassword: String?
    ) {
        viewModelScope.launch(errorHandler) {
            prepareAppForTestingUseCase(uitUserName, uitPassword)
        }
    }
}