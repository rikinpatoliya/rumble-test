package com.rumble.domain.premium.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.numberOfYearsTillNow
import javax.inject.Inject

class FetchUserInfoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase
): RumbleUseCase {
    suspend operator fun invoke() {
        val result = profileRepository.getUserProfile()
        if (result.success) {
            result.userProfileEntity?.let {
                sessionManager.saveIsPremiumUser(it.isPremium)
                sessionManager.saveUserGender(it.gender.value)
                it.birthday?.numberOfYearsTillNow()?.let { age ->
                    sessionManager.saveUserAge(age)
                }
            }
        } else {
            rumbleErrorUseCase(result.rumbleError)
        }
    }
}