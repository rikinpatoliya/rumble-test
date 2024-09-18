package com.rumble.domain.profile.domain

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.profile.model.repository.ProfileRepository
import javax.inject.Inject

class GetUserHasUnreadNotificationsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(): Boolean {
        val result = profileRepository.fetchHasUnreadNotificationsNotifications()
        if (result.success.not()) rumbleErrorUseCase(result.rumbleError)
        return result.hasUnreadNotifications
    }
}