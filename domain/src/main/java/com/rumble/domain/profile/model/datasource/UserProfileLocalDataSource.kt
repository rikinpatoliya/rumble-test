package com.rumble.domain.profile.model.datasource

import com.rumble.domain.profile.domainmodel.CountryEntity

interface UserProfileLocalDataSource {
    suspend fun getCountries(): List<CountryEntity>
}