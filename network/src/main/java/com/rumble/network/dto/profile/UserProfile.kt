package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName

/**
 * UserProfile model
 *
 * @param email User email
 * @param thumbnail Optional. URL. User thumbnail
 * @param birthday Optional. Date. User birthday
 * @param validated Integer. 0|1, email validation state
 * @param apiKey String.
 * @param rumblesScore Integer.
 * @param followersCount Integer. Amount of followers
 * @param followingCount Integer. Amount of followees
 * @param address Optional. User Address
 *
 */

data class UserProfile(
    @SerializedName("email")
    val email: String,
    @SerializedName("thumb")
    val thumbnail: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("validated")
    val validated: Int,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("rumbles_score")
    val rumblesScore: Int,
    @SerializedName("followers_count")
    val followersCount: Int,
    @SerializedName("following_count")
    val followingCount: Int,
    @SerializedName("address")
    val address: Address?,
    @SerializedName("is_premium")
    val isPremium: Boolean,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("age_verification")
    val ageVerification: AgeVerification
)