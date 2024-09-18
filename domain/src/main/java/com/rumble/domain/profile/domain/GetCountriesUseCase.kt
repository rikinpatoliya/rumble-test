package com.rumble.domain.profile.domain

import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.model.repository.ProfileRepository
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(): List<CountryEntity> =
        profileRepository.getCountries()
}