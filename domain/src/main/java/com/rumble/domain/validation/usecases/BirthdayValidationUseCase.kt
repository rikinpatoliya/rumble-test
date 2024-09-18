package com.rumble.domain.validation.usecases

import com.rumble.utils.RumbleConstants.MINIMUM_AGE_REQUIREMENT
import com.rumble.utils.errors.InputValidationError
import java.time.*
import javax.inject.Inject

class BirthdayValidationUseCase @Inject constructor() {
    operator fun invoke(
        birthday: Long,
    ): Pair<Boolean, InputValidationError> {
        return when {
            birthday == 0L -> Pair(true, InputValidationError.Empty)
            isLessThen13Years(birthday) -> Pair(true, InputValidationError.MinCharacters)
            else -> Pair(false, InputValidationError.None)
        }
    }

    private fun isLessThen13Years(birthday: Long): Boolean {
        val birth = LocalDateTime.ofInstant(Instant.ofEpochMilli(birthday), ZoneId.systemDefault())
        return Period.between(
            LocalDate.of(birth.year, birth.monthValue, birth.dayOfMonth),
            LocalDate.now()
        ).years < MINIMUM_AGE_REQUIREMENT
    }
}