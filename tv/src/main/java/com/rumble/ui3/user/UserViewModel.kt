package com.rumble.ui3.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
): ViewModel() {

    /***/
    private val _uiState = MutableStateFlow<UserFragmentStates>(UserFragmentStates.Loading)
    /***/
    val uiState: StateFlow<UserFragmentStates> = _uiState

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    fun getUiState() {
        viewModelScope.launch(errorHandler) {
            val isCookieExist = sessionManager.cookiesFlow.first().isNotEmpty()

            if (isCookieExist) {
                _uiState.value = UserFragmentStates.LoggedIn
            } else
                _uiState.value = UserFragmentStates.NotLoggedIn
        }
    }


}