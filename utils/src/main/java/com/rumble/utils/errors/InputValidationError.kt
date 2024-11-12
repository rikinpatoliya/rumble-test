package com.rumble.utils.errors

sealed class InputValidationError {
    object None : InputValidationError()
    object Empty : InputValidationError()
    object NotLetterOrDigit : InputValidationError()
    object NotLetterOrDigitOrUnderscore : InputValidationError()
    data class MinCharacters(val count: Int) : InputValidationError()
    data class Custom(val message: String) : InputValidationError()
}