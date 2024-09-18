package com.rumble.domain.profile.domain

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(userProfileEntity: UserProfileEntity): UpdateUserProfileResult {
        val result = profileRepository.updateUserProfile(userProfileEntity)
        if (result is UpdateUserProfileResult.Error)
            rumbleErrorUseCase(result.rumbleError)

        return result
    }
}