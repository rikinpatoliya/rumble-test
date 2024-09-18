package com.rumble.domain.profile.domainmodel

import java.time.LocalDate

data class UserProfileEntity(
    val apiKey: String,
    val fullName: String,
    val email: String,
    val validated: Boolean,
    val userPicture: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: CountryEntity,
    val paypalEmail: String,
    val followedChannelCount: Int,
    val isPremium: Boolean,
    val gender: Gender,
    val birthday: LocalDate?
)
