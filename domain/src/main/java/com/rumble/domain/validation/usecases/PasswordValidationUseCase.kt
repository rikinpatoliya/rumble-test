package com.rumble.domain.validation.usecases

import com.rumble.utils.RumbleConstants
import javax.inject.Inject

class PasswordValidationUseCase @Inject constructor() {
    operator fun invoke(
        password: String,
    ): Boolean = password.isNotEmpty() && password.length >= RumbleConstants.MINIMUM_PASSWORD_LENGTH
}