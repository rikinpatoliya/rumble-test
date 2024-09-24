package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class UpdateUserProfileResult {
    data class Success(val requiresConfirmation: Boolean) : UpdateUserProfileResult()
    data class Error(val rumbleError: RumbleError) : UpdateUserProfileResult()
    data class FormError(
        val fullNameError: Boolean,
        val fullNameErrorMessage: String,
        val cityError: Boolean,
        val cityErrorMessage: String,
        val stateError: Boolean,
        val stateErrorMessage: String,
        val postalCodeError: Boolean,
        val postalCodeErrorMessage: String,
        val birthdayErrorMessage:String
    ) : UpdateUserProfileResult()
}