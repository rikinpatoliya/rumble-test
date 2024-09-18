package com.rumble.ui3.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserLoggedInViewModel"

@HiltViewModel
class UserLoggedInViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val signOutUseCase: SignOutUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
): ViewModel() {

    val userName = sessionManager.userNameFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userAvatar = sessionManager.userPictureFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    fun onLogoutClick() {
        viewModelScope.launch(errorHandler) { signOutUseCase() }
    }
}