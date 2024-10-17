package com.rumble.ui3.settings.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.DebugAdType
import com.rumble.domain.settings.domain.usecase.GetCanUseAdsDebugMode
import com.rumble.domain.settings.model.UserPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


interface SettingsHandler {
    val uiState: StateFlow<SettingsUiState>

    fun onDisableAdsChanged(disableAds: Boolean)
    fun onForceAdsChanged(forceAds: Boolean)
    fun onDisplayDebugAdChanged(displayDebugAd: Boolean)
}

data class SettingsUiState(
    val settingsVisible: Boolean = false,
    val disableAds: Boolean = false,
    val forceAds: Boolean = false,
    val displayDebugAd: Boolean = false,
    val canUseAdsDebugMode: Boolean = false,
)

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getCanUseAdsDebugMode: GetCanUseAdsDebugMode,
) : ViewModel(), SettingsHandler {

    override val uiState = MutableStateFlow(
        SettingsUiState()
    )
    
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        viewModelScope.launch(errorHandler) {
            val canUseAdsDebugMode = getCanUseAdsDebugMode()

            userPreferenceManager.disableAdsFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    settingsVisible = canUseAdsDebugMode,
                    canUseAdsDebugMode = canUseAdsDebugMode,
                    disableAds = it
                )
            }
        }

        viewModelScope.launch {
            userPreferenceManager.forceAdsFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    forceAds = it
                )
            }
        }

        viewModelScope.launch {
            userPreferenceManager.debugAdTypeFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    displayDebugAd = it == DebugAdType.DEBUG_AD
                )
            }
        }
    }

    override fun onDisableAdsChanged(disableAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveDisableAdsMode(disableAds)
        }
    }

    override fun onForceAdsChanged(forceAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveForceAds(forceAds)
        }
    }

    override fun onDisplayDebugAdChanged(displayDebugAd: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveDebugAdType(if (displayDebugAd) DebugAdType.DEBUG_AD else DebugAdType.REAL_AD)
        }
    }
}