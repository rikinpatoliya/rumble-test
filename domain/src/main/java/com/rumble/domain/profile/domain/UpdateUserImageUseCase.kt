package com.rumble.domain.profile.domain

import android.net.Uri
import com.rumble.domain.profile.model.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserImageUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(uri: Uri) = profileRepository.updateUserImage(uri)
}