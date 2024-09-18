package com.rumble.domain.profile.domain

import androidx.paging.PagingData
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.model.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileNotificationsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    operator fun invoke(): Flow<PagingData<ProfileNotificationEntity>> =
        profileRepository.getProfileNotifications()
}