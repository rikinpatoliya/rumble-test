package com.rumble.ui3.user.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.domainmodel.TvPairingCodeResult
import com.rumble.domain.login.domain.domainmodel.LoginResultStatus
import com.rumble.domain.login.domain.usecases.RequestTvPairingCodeUseCase
import com.rumble.domain.login.domain.usecases.VerifyTvPairingCodeUseCase
import com.rumble.network.dto.login.TvPairingCodeData
import com.rumble.ui3.user.UserFragmentStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "UserNotLoggedInViewModel"

// TODO - request new code on error and on duration timeout
@HiltViewModel
class UserNotLoggedInViewModel @Inject constructor(
    private val requestTvPairingCodeUseCase: RequestTvPairingCodeUseCase,
    private val verifyTvPairingCodeUseCase: VerifyTvPairingCodeUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
): ViewModel() {

    companion object {
        private const val DEFAULT_EMPTY_CODE = "      " // 6 spaces
    }

    /***/
    val liveData = MutableLiveData(DEFAULT_EMPTY_CODE)
    /***/
    private val _uiState = MutableStateFlow<UserFragmentStates>(UserFragmentStates.Loading)
    /***/
    val uiState: StateFlow<UserFragmentStates> = _uiState
    /***/
    private var tvPairingCodeData = TvPairingCodeData()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        error()
    }

    fun requestCodeIfNeeded() {
        Timber.d("requestCodeIfNeeded")
        viewModelScope.launch(errorHandler) {
            _uiState.value = UserFragmentStates.Loading
            updatePairingCode()
            if (tvPairingCodeData.regCode.isNotEmpty()) {
                liveData.value = tvPairingCodeData.regCode
                _uiState.value = UserFragmentStates.NotLoggedIn
                verifyTvDevicePairingCode()
            } else {
                error()
            }
        }
    }

    private suspend fun verifyTvDevicePairingCode() = withContext(viewModelScope.coroutineContext) {
        // lets repeat here every 2 seconds, but not longer than requestCode.retryDuration
        repeat((tvPairingCodeData.retryDuration / 2).toInt()) { // /2 because we want to repeat every 2 seconds
            delay(TimeUnit.SECONDS.toMillis(2)) // 2 seconds delay

            val regCode = tvPairingCodeData.regCode

            val result = verifyTvPairingCodeUseCase(regCode)
            Timber.d("verifyTvDevicePairingCode result: $result")
            when (result) {
                LoginResultStatus.SUCCESS -> {
                    loggedIn()
                    cancel()
                }
                LoginResultStatus.FAILURE -> {
                    cancel()
                    requestCodeIfNeeded()
                }
                LoginResultStatus.INCOMPLETE -> {
                }
            }
        }

    }

    private suspend fun updatePairingCode() {
        if (isPairingCodeExpired()) {
            Timber.d("requestCode: expired, let's get a new one")
            try {
                val result = requestTvPairingCodeUseCase()
                when (result) {
                    is TvPairingCodeResult.Failure -> {
                        Timber.e("requestCode: failure")
                        error()
                    }

                    is TvPairingCodeResult.Success -> {
                        Timber.d("requestCode: success")
                        tvPairingCodeData = result.tvPairingCodeData
                    }
                }
            } catch (e: Exception) {
                Timber.e("requestCode: exception")
                unhandledErrorUseCase(TAG, e)
                error()
            }
        } else {
            Timber.d("requestCode: not expired, let's use the old one")
        }
    }

    private fun loggedIn() {
        _uiState.value = UserFragmentStates.LoggedIn
        tvPairingCodeData = TvPairingCodeData() // reset
        liveData.value = DEFAULT_EMPTY_CODE
    }

    private fun error() {
        _uiState.value = UserFragmentStates.Error
        tvPairingCodeData = TvPairingCodeData() // reset
        liveData.value = DEFAULT_EMPTY_CODE
    }

    private fun isPairingCodeExpired() : Boolean {

        val creationTime = tvPairingCodeData.creationTime
        val retryDuration = TimeUnit.SECONDS.toMillis(tvPairingCodeData.retryDuration)
        val currentTime = System.currentTimeMillis()

        Timber.d("$creationTime + $retryDuration ${(creationTime + retryDuration <= currentTime)} $currentTime")

        return creationTime + retryDuration <= currentTime
    }
}