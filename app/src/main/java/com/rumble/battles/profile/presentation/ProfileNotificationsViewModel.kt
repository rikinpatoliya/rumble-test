package com.rumble.battles.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.profile.domain.GetProfileNotificationsUseCase
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ProfileNotificationsHandler {
    val notificationsPagingDataFlow: Flow<PagingData<ProfileNotificationEntity>>
    val uiState: StateFlow<ProfileNotificationsUIState>
}

data class ProfileNotificationsUIState(
    val loading: Boolean = false,
)

@HiltViewModel
class ProfileNotificationsViewModel @Inject constructor(
    getProfileNotificationsUseCase: GetProfileNotificationsUseCase,
) : ViewModel(), ProfileNotificationsHandler {

    override val uiState = MutableStateFlow(ProfileNotificationsUIState())

    override val notificationsPagingDataFlow: Flow<PagingData<ProfileNotificationEntity>> =
        getProfileNotificationsUseCase().cachedIn(viewModelScope)
}